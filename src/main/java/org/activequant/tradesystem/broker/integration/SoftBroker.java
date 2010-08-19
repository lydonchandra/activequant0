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
package org.activequant.tradesystem.broker.integration;

import static org.activequant.tradesystem.domainmodel.Order.NOT_SET;

import org.activequant.core.domainmodel.Quote;
import org.activequant.data.retrieval.IQuoteSubscriptionSource;
import org.activequant.tradesystem.broker.IBroker;
import org.activequant.tradesystem.domainmodel.BrokerAccount;
import org.activequant.tradesystem.domainmodel.Execution;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.types.BrokerId;
import org.activequant.tradesystem.types.OrderSide;
import org.activequant.tradesystem.types.OrderState;
import org.activequant.tradesystem.types.OrderType;
import org.activequant.util.pattern.events.IEventSource;
import org.apache.log4j.Logger;

/**
 * SoftBroker receives quotes from some source, and implements order placing strategies
 * (STOP, LIMIT, TRAILING_STOP) by watching the market events.
 * Once it decides to submit the order, it sends a MARKET order to the underlying broker
 * engine. Incoming orders of MARKET type are never intercepted, and always passed
 * down to the physical broker engine.
 * 
 * <br>
 * <b>History:</b><br> 
 * - [29.05.2007] Created (Ulrich.)<br> 
 * - [09.06.2007] moved to subaccount model (Ulrich)<br> 
 * - [10.06.2007] Refactored processPriceIndication(...) + cleanup (Erik Nijkamp)<br> 
 * - [03.09.2007] added order type limit support (Ulrich Staudinger)<br>
 * - [02.11.2007] Added trailing stop support, various small fixes in stop rules (Ulrich Staudinger)<br> 
 * - [04.11.2007] Adding trade indication to quote converter. (Ulrich Staudinger)<br> 
 * - [04.11.2007] Adding partial fills based on available quantity (Ulrich Staudinger) <br> 
 * - [06.11.2007] Adding cancelOrder support (Ulrich Staudinger)<br>
 * - [11.11.2007] Split out the SoftBroker part (Mike Kroutikov)<br>
 * 
 * @author Ulrich Staudinger
 * @author Erik Nijkamp
 * @author Mike Kroutikov
 */
public class SoftBroker implements IBroker {

	private final static Logger log = Logger.getLogger(SoftBroker.class);

	private final BrokerAccount brokerAccount;
	private final IBroker engine;
	private boolean interceptTrailStop = true;
	private boolean interceptStop = true;
	private boolean interceptLimit = true;

    // component to take care of tracking market quotes 
    private final QuoteTrackingBrokerBase quoteTracker = new QuoteTrackingBrokerBase() {
		@Override
		protected void processOrder(Order order, Quote quote) throws Exception {
			if(checkOrder(order, quote)) {
				Order clonedOrder = order.clone();
				clonedOrder.setType(OrderType.MARKET);
				engine.placeOrder(clonedOrder);
				removeFromManagedOrders(order);
			} 
		}
    };

	public SoftBroker(BrokerAccount brokerAccount, IBroker engine) {
		this.brokerAccount = brokerAccount;
		this.engine        = engine;
	}
	
	public void setQuoteSubscriptionSource(IQuoteSubscriptionSource val) {
		quoteTracker.setQuoteSubscriptionSource(val);
	}
	
	public IQuoteSubscriptionSource getQuoteSubscriptionSource() {
		return quoteTracker.getQuoteSubscriptionSource();
	}

	/**
	 * Determines if this class intercepts and manages orders of type
	 * 'STOP'. Default is <code>true</code>.
	 * 
	 * @return true or false.
	 */
	public boolean isInterceptStop() {
		return interceptStop;
	}
	
	public void setInterceptStop(boolean val) {
		interceptStop= val;	
	}

	/**
	 * Determines if this class intercepts and manages orders of type
	 * 'LIMIT'. Default is <code>true</code>.
	 * 
	 * @return true or false.
	 */
	public boolean isInterceptLimit() {
		return interceptLimit;
	}
	
	public void setInterceptLimit(boolean val) {
		interceptLimit = val;	
	}

	/**
	 * Determines if this class intercepts and manages orders of type
	 * 'TRAILING_STOP'. Default is <code>true</code>.
	 * 
	 * @return true or false.
	 */
	public boolean isInterceptTrailingStop() {
		return interceptTrailStop;
	}
	
	public void setInterceptTrailingStop(boolean val) {
		interceptTrailStop = val;	
	}
	
	/**
	 * This method must be called to complete object initialization. If using
	 * Spring, declare it as bean's "init-method".
	 */
	public void init() throws Exception {
		
		Order[] openOrders = brokerAccount.getOrderBook().getOpenOrders();
		
		for(Order o : openOrders) {
			if(interceptOrder(o.getType())) {
				quoteTracker.addToManagedOrders(o);
			}
		}
	}
	
	/**
	 * This method must be called to release resources before destroying the object.
	 * If using Spring, declare it as bean's "destroy-method".
	 */
	public void destroy() throws Exception {
		quoteTracker.destroy();
	}

	private boolean checkOrder(Order order, Quote quote) throws Exception {
		
		// check for stops, etc.
		// in case a stop grips, simply fully execute it.
		// /
		// check if it is a stop order.
		if (order.getType().equals(OrderType.STOP)) {
			if (order.getSide().equals(OrderSide.BUY) && quote.getAskPrice() != Quote.NOT_SET) {
				// ok, need to check if the price is lower.
				if (quote.getAskPrice() > order.getStopPrice()) {
					// execute the stop.
					log.info("STOP order sent for execution as MARKET order: " + order);
					return true;
				}
			} else if (order.getSide().equals(OrderSide.SELL) || order.getSide().equals(OrderSide.SHORT_SELL) || order.getSide().equals(OrderSide.SHORT_SELL_EXEMPT)
					&& quote.getBidPrice() != Quote.NOT_SET) {
				// ok, need to check if the price is higher.
				if (quote.getBidPrice() < order.getStopPrice()) {
					// execute the stop.
					log.info("STOP order sent for execution as MARKET order: " + order);
					return true;
				}
			}
		} else if (order.getType().equals(OrderType.TRAILING_STOP)) {
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
					}

					// trail it.
					if (order.getStopPrice() + order.getTrailingDistance() < quote.getBidPrice()) {
						order.setStopPrice(quote.getBidPrice() - order.getTrailingDistance());
						log.info("Trailing order, new stop : " + order.getStopPrice());
					}

					// ok, need to check if the price is lower.
					if (quote.getBidPrice() < order.getStopPrice()) {
						// execute the stop.
						log.info("TRAILING_STOP order sent for execution as MARKET order: " + order);
						return true;
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
					}

					// trail it.
					if (order.getStopPrice() - order.getTrailingDistance() > quote.getAskPrice()) {
						order.setStopPrice(quote.getAskPrice() + order.getTrailingDistance());
						log.info("Trailing order, new stop : " + order.getStopPrice());
					}

					// ok, need to check if the price is higher.
					if (quote.getAskPrice() > order.getStopPrice()) {
						// execute the stop.
						log.info("TRAILING_STOP order sent for execution as MARKET order: " + order);
						return true;
					}
				}
			}
		}
		// check if it is a market order
		else if (order.getType().equals(OrderType.MARKET)) {
			// FIXME: not reached(left for now for documentation purposes only)

			if (order.getSide().equals(OrderSide.BUY) && quote.getAskPrice() != Quote.NOT_SET) {
				// execute at ask
				log.info("MARKET order sent for execution as MARKET order: " + order);
				return true;
			} else if (quote.getBidPrice() != Quote.NOT_SET) {
				log.info("MARKET order sent for execution as MARKET order: " + order);
				return true;
			}
		}
		// check if it is a limit order
		else if (order.getType().equals(OrderType.LIMIT)) {
			if (order.getSide().equals(OrderSide.BUY) && quote.getAskPrice() != Quote.NOT_SET) {
				// ok, need to check if the price is lower.
				if (quote.getAskPrice() < order.getLimitPrice()) {
					// execute the stop.
					log.info("LIMIT order sent for execution as MARKET order: " + order);
					return true;
				}
			} else if (order.getSide().equals(OrderSide.SELL) || order.getSide().equals(OrderSide.SHORT_SELL) || order.getSide().equals(OrderSide.SHORT_SELL_EXEMPT)
					&& quote.getBidPrice() != Quote.NOT_SET) {
				// ok, need to check if the price is higher.
				if (quote.getBidPrice() > order.getLimitPrice()) {
					// execute the stop.
					log.info("LIMIT order sent for execution as MARKET order: " + order);
					return true;
				}
			}
		}
		// discard all other order types with an unsupported exception.
		else {
			throw new Exception("Unsupported order type");
		}

		return false; // do not execute now!
	}

	public void cancelOrder(Order order) throws Exception {
		if(quoteTracker.removeFromManagedOrders(order)) {
			order.setState(OrderState.CANCELED); // canceled locally-managed order
		} else {
			// already sent to the engine
			engine.cancelOrder(order);
		}
	}
	
	// do we intercept this order or just pass it down to the engine?
	private boolean interceptOrder(OrderType type) {
		switch(type) {
		case LIMIT: 
			return interceptLimit;
		case STOP: 
			return interceptStop;
		case TRAILING_STOP:
			return interceptTrailStop;
		default:
			return false;
		}
	}
	
	public void placeOrder(Order order) throws Exception {

		if(interceptOrder(order.getType())) {
			quoteTracker.addToManagedOrders(order);
		} else {
			// not ours
			engine.placeOrder(order);
			return;
		}
	}
	
	public void cancelOrders(Order... orders) throws Exception {
		for(Order o : orders) {
			cancelOrder(o);
		}
	}

	public BrokerId getBrokerID() {
		return brokerAccount.getBrokerID();
	}

	public IEventSource<BrokerAccount> getOnAccountUpdate() {
		return engine.getOnAccountUpdate();
	}

	public IEventSource<Execution> getOnNewExecution() {
		return engine.getOnNewExecution();
	}

	public IEventSource<Order> getOnNewOrder() {
		return engine.getOnNewOrder();
	}

	public void placeOrders(Order ... orders) throws Exception {
		for(Order o : orders) {
			placeOrder(o);
		}
	}
}