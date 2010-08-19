/****

    activequant - activestocks.eu

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

	
	contact  : contact@activestocks.eu
    homepage : http://www.activestocks.eu

 ****/
package org.activequant.tradesystem.broker.integration.ib;

import static org.activequant.tradesystem.domainmodel.Order.NOT_SET;

import java.util.HashMap;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Quote;
import org.activequant.core.util.TimeStampFormat;
import org.activequant.data.retrieval.IQuoteSubscriptionSource;
import org.activequant.data.retrieval.integration.ib.IBEventListener;
import org.activequant.data.retrieval.integration.ib.IBTwsConnection;
import org.activequant.tradesystem.broker.BrokerBase;
import org.activequant.tradesystem.broker.integration.QuoteTrackingBrokerBase;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.domainmodel.Position;
import org.activequant.tradesystem.types.OrderSide;
import org.activequant.tradesystem.types.OrderState;
import org.activequant.tradesystem.types.OrderType;
import org.apache.log4j.Logger;

import com.ib.client.Contract;
import com.ib.client.Execution;

/**
 * a broker implementation for trading through ib. <br>
 * <br>
 * <b>History:</b><br> 
 * - [05.07.2007] Created (Ulrich Staudinger)<br> 
 * - [06.11.2007] adding order id mapping (Ulrich Staudinger)<br>
 * - [06.11.2007] Adding manual trailing stop facility (Ulrich Staudinger)<br>
 * - [13.11.2007] Order id cleanup (Erik Nijkamp)<br>
 * - [14.11.2007] Move to latest broker code (Mike Kroutikov) <br>
 * 
 * @author Ulrich Staudinger
 * @author Mike Kroutikov
 */
public class IBBroker extends BrokerBase implements IBEventListener {

	private IBTwsConnection connection = null;
	protected final static Logger log = Logger.getLogger(IBBroker.class);

	private final QuoteTrackingBrokerBase quoteTracker = new QuoteTrackingBrokerBase() {
		@Override
		protected void processOrder(Order order, Quote quote) throws Exception {
			processQuote(order, quote);
		}
	};
	
	private HashMap<Integer, com.ib.client.Order> twsOrders = new HashMap<Integer, com.ib.client.Order>();
	
	public IBBroker(IBTwsConnection connection, Account account) {
		super(account);
		this.connection = connection;
		connection.connect();
		connection.addIBEventListener(this);
	}

	public IBBroker(IBTwsConnection connection, Account account, IQuoteSubscriptionSource source) {
		super(account);
		this.connection = connection;
		connection.connect();
		connection.addIBEventListener(this);
		quoteTracker.setQuoteSubscriptionSource(source);
	}

	/**
	 * Initializes the broker by going thru the list of
	 * opened orders and picking the ones that it will manage internally (TRAILING_STOP
	 * ones). For every managed order, it subscribes to the quote events.
	 * <p>
	 * Call this method after all properties are set, but before using the
	 * methods of this broker. If using Spring, mark this method as bean's "init-method".
	 */
	public void init() throws Exception {
		
		Order [] openOrders = getBrokerAccount().getOrderBook().getOpenOrders();
		
		for(Order o : openOrders) {
			if(o.getType() == OrderType.TRAILING_STOP) {
				quoteTracker.addToManagedOrders(o);
			}
		}
	}
	
	/**
	 * De-initializes the broker by releasing all subscriptions.
	 */
	public void destroy() {
		quoteTracker.destroy();
	}

	@Override
	public void placeOrder(Order o) throws Exception {

		super.placeOrder(o);

		// need to convert the order into an IB order object.

		com.ib.client.Order twsOrder = convertToTwsOrder(o);
		
		if (o.getType().equals(OrderType.MARKET) || o.getType().equals(OrderType.LIMIT) || o.getType().equals(OrderType.STOP) 
				|| o.getType().equals(OrderType.STOP_LIMIT)){
			connection.placeOrder(o.getInstrumentSpecification(), twsOrder);
			// have to save it. 
			twsOrders.put(twsOrder.m_orderId, twsOrder);
		}
		else if (o.getType().equals(OrderType.TRAILING_STOP)) {
			quoteTracker.addToManagedOrders(o);
		} else {
			throw new Exception("Unsupported order type.");
		}

		log.info("[placeOrder] ORDER PLACED : " + o.toString());

	}

	/**
	 * this method converts an AQ order to a broker specific TWS order object.  
	 * @param o
	 * @return
	 */
	private com.ib.client.Order convertToTwsOrder(Order o){
		// need to convert the order into an IB order object.

		com.ib.client.Order twsOrder = new com.ib.client.Order();
		// get the next implementation specific order id and set it in the order
		// object.
		twsOrder.m_orderId = connection.getNextOrderId();
		o.setBrokerAssignedId(Integer.toString(twsOrder.m_orderId));

		twsOrder.m_clientId = connection.getClientId();
		twsOrder.m_transmit = true;
		if (o.getSide().equals(OrderSide.BUY)) {
			twsOrder.m_action = "BUY";
		} else if (o.getSide().equals(OrderSide.SELL)) {
			twsOrder.m_action = "SELL";
		} else if (o.getSide().equals(OrderSide.SHORT_SELL)) {
			twsOrder.m_action = "SSHORT";
		} else if (o.getSide().equals(OrderSide.SHORT_SELL_EXEMPT)) {
			twsOrder.m_action = "SSHORT";
		}

		// set the quantity. IB supports only integers.
		twsOrder.m_totalQuantity = (int) o.getQuantity();

		if (o.getType().equals(OrderType.MARKET)) {
			// placing a market order.
			twsOrder.m_orderType = "MKT";
		} else if (o.getType().equals(OrderType.LIMIT)) {
			twsOrder.m_orderType = "LMT";
			twsOrder.m_lmtPrice = o.getLimitPrice();
		} else if (o.getType().equals(OrderType.STOP)) {
			twsOrder.m_orderType = "STP";
			twsOrder.m_auxPrice = o.getStopPrice();
		}
		else if(o.getType().equals(OrderType.STOP_LIMIT)){
			twsOrder.m_orderType = "STPLMT";
			twsOrder.m_lmtPrice = o.getLimitPrice();
		}
		
		return twsOrder;
	}
	
	/**
	 * called to process a quote. This method checks all trailing stops, as trailing stops with an entry level are not supported by IB
	 * and thus are simulated by AQ. 
	 * 
	 * @param order
	 * @param quote
	 * @throws Exception
	 */
	private void processQuote(Order order, Quote quote) throws Exception {
		log.debug("processing order: " + order + " on quote: " + quote);

		// check through all the trailing stops.
		if (order.getType().equals(OrderType.TRAILING_STOP)) {
			if (order.getSide().equals(OrderSide.SELL) || order.getSide().equals(OrderSide.SHORT_SELL) || order.getSide().equals(OrderSide.SHORT_SELL_EXEMPT)
					&& quote.getBidPrice() != Quote.NOT_SET) {

				boolean processOrder = false;
				if (order.getLimitPrice() != NOT_SET) {
					if (quote.getBidPrice() > order.getLimitPrice() || (order.getStopPrice() != NOT_SET && quote.getBidPrice() < order.getStopPrice())) {
						// activate order processing
						processOrder = true;
					}
				} else
					processOrder = true;

				if (processOrder) {
					if (order.getStopPrice() == NOT_SET) {
						// initialize it.
						order.setStopPrice(quote.getBidPrice() - order.getTrailingDistance());
							
						// convert it into a tws order. 
							
						// send the order. 
						order.setState(OrderState.PLACED);
						com.ib.client.Order twsOrder = convertToTwsOrder(order);
						twsOrder.m_orderType = "STP";
						twsOrder.m_auxPrice = order.getStopPrice();
						//
						connection.placeOrder(order.getInstrumentSpecification(), twsOrder);
						twsOrders.put(twsOrder.m_orderId, twsOrder);
						log.info("order initialized at stop=" + order.getStopPrice() + " and submitted to tws as stop order #" + twsOrder.m_orderId );
					}

					// trail it.
					if (order.getStopPrice() + order.getTrailingDistance() < quote.getBidPrice()) {
						order.setStopPrice(quote.getBidPrice() - order.getTrailingDistance());

						// update the corresponding ib order 
						com.ib.client.Order twsOrder = twsOrders.get(Integer.parseInt(order.getBrokerAssignedId()));
						twsOrder.m_auxPrice = order.getStopPrice();
						connection.placeOrder(order.getInstrumentSpecification(), twsOrder);
						log.info("Trailing order #" + order.getBrokerAssignedId() + ", new stop : " + order.getStopPrice());
					}
				}
			} else if (order.getSide().equals(OrderSide.BUY) && quote.getAskPrice() != Quote.NOT_SET) {
				boolean processOrder = false;
				if (order.getLimitPrice() != NOT_SET) {
					if (quote.getAskPrice() < order.getLimitPrice()) {
						// activate order processing
						processOrder = true;
					}
				} else
					processOrder = true;
				if (processOrder) {
					if (order.getStopPrice() == NOT_SET) {
						// initialize it.
						order.setStopPrice(quote.getAskPrice() + order.getTrailingDistance());

						// send the order. 
						order.setState(OrderState.PLACED);
						com.ib.client.Order twsOrder = convertToTwsOrder(order);
						twsOrder.m_orderType = "STP";
						twsOrder.m_auxPrice = order.getStopPrice();
						//
						connection.placeOrder(order.getInstrumentSpecification(), twsOrder);
						twsOrders.put(twsOrder.m_orderId, twsOrder);
						log.info("order initialized at stop=" + order.getStopPrice() + " and submitted to tws as stop order #" + twsOrder.m_orderId );
					}

					// trail it.
					if (order.getStopPrice() - order.getTrailingDistance() > quote.getAskPrice()) {
						order.setStopPrice(quote.getAskPrice() + order.getTrailingDistance());
							
						// update the corresponding ib order 
						com.ib.client.Order twsOrder = twsOrders.get(Integer.parseInt(order.getBrokerAssignedId()));
						twsOrder.m_auxPrice = order.getStopPrice();
						connection.placeOrder(order.getInstrumentSpecification(), twsOrder);
						log.info("Trailing order #" + order.getBrokerAssignedId() + ", new stop : " + order.getStopPrice());
					}
				}
			}
		}
	}

	@Override
	public void cancelOrder(Order order) throws Exception {
		log.info("Cancelling order " + order);
		
		com.ib.client.Order twsOrder = twsOrders.remove(Integer.parseInt(order.getBrokerAssignedId()));
		if(twsOrder != null) {
			connection.cancelOrder(twsOrder.m_orderId);
		}
		quoteTracker.removeFromManagedOrders(order);
		order.setState(OrderState.CANCELED);
	}

	public IBTwsConnection getConnection() {
		return connection;
	}

	public void setConnection(IBTwsConnection connection) {
		this.connection = connection;
	}

	/**
	 * method to receive information about order status through callback from
	 * IB.
	 */
	public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId) {
		try {

			log.info("[orderStatus] " + orderId + " / " + filled);
			if (status.equals("filled")) {
				// partial fill.
			} else if (status.equals("Filled")) {
				// complete fill.
				Order order = findOrder(Integer.toString(orderId));
				org.activequant.tradesystem.domainmodel.Execution execution = new org.activequant.tradesystem.domainmodel.Execution();
				execution.setExecutionPrice(lastFillPrice);
				execution.setExecutionQuantity(filled);
				execution.setOrder(order);
				// take care of the execution.
				super.processExecution(execution);

			} else if (status.equals("Submitted")) {
				// order accepted through IB and working.
			} else if (status.equals("Cancelled")) {
				// cancelled from IB.
			}
		} catch (Exception x) {
			log.debug("[orderStatus] details about an already filled order received. Could have been sent from another or and old IB connection.");
		}
	}

	/**
	 * see orderStatus method. 
	 */
	public void execDetails(int orderId, Contract ibContract, Execution ibExec) {
		// get the corresponding order.
		try {
			Order order = findOrder(Integer.toString(orderId));
			org.activequant.tradesystem.domainmodel.Execution execution = new org.activequant.tradesystem.domainmodel.Execution();
			// parse the execution date.
			TimeStampFormat sdf = new TimeStampFormat("yyyyMMdd  HH:mm:ss");
			execution.setExecutionTimeStamp(sdf.parse(ibExec.m_time));
			execution.setExecutionPrice(ibExec.m_price);
			execution.setExecutionQuantity(ibExec.m_shares);
			execution.setOrder(order);
			// take care of the execution.
			super.processExecution(execution);
		} catch (IllegalArgumentException x) {
			log.warn("[execDetails] details about a not existant order received. Could have been sent from another or and old IB connection");
			
			// building a new portfolio position / execution for it. 
			
		} catch (Exception x) {
			log.warn("[execDetails] ", x);
		}
	}

	public void updateAccountValue(String informationType, String amount,
			String currency, String account) {
		// TODO Auto-generated method stub
		
	}

	public void updatePortfolio(Contract contract, int positionCount,
			double marketPrice, double marketValue, double avgCost,
			double unrealizedPNL, double realizedPNL, String accountName) {
		try{
			InstrumentSpecification spec = IBTwsConnection.convertToInstrument(contract);
			
			// building or fetching a new position 
			Position position; 
			if (getBrokerAccount().getPortfolio().hasPosition(spec)) {
				position = getBrokerAccount().getPortfolio().getPosition(spec);
			} else {
				position = new Position();
				position.setInstrumentSpecification(spec);
				getBrokerAccount().getPortfolio().addPosition(position);
			}
			
			//
			position.setAveragePrice(avgCost);
			position.setQuantity(positionCount);
			
			// check if i have to remove this position. 
			if (position.getQuantity() == 0.0) {
				this.getBrokerAccount().getPortfolio().removePosition(position);
			}

			
		}
		catch(Exception x){
			log.warn("[updatePortfolio] ", x);
		}
		
		
	}

	public void error(int orderId, int errorCode, String message) {
		// TODO Auto-generated method stub
	}

	public void openOrder(int orderId, Contract contract,
			com.ib.client.Order order) {
		// TODO Auto-generated method stub
	}
}
