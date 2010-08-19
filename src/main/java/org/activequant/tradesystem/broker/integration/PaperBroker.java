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
import org.activequant.core.types.TimeStamp;
import org.activequant.data.retrieval.IQuoteSubscriptionSource;
import org.activequant.tradesystem.broker.BrokerBase;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.BrokerAccount;
import org.activequant.tradesystem.domainmodel.Execution;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.types.OrderSide;
import org.apache.log4j.Logger;


/**
 * PaperBroker recieves price indications from some source. 
 * These event sources need to be configured in spring. Same documentation as in BrokerBase
 *  applies, but: 
 * PaperBroker does check stops in orders. 
 * 
 * The paper broker stops on quotes. TradeIndications are internally converted to quotes for 
 * stop checking. 
 * 
 * <br>
 * <b>History:</b><br>
 *  - [29.05.2007] Created (Ulrich.)<br>
 *  - [09.06.2007] moved to subaccount model (Ulrich)<br>
 *  - [10.06.2007] Refactored processPriceIndication(...) + cleanup (Erik Nijkamp)<br>
 *  - [03.09.2007] added order type limit support (Ulrich Staudinger)<br>
 *  - [02.11.2007] Added trailing stop support, various small fixes in stop rules (Ulrich Staudinger)<br>
 *  - [04.11.2007] Adding trade indication to quote converter. (Ulrich Staudinger)<br> 
 *  - [04.11.2007] Adding partial fills based on available quantity (Ulrich Staudinger) <br>
 *  - [06.11.2007] Adding cancelOrder support (Ulrich Staudinger)<br>  
 *  - [10.11.2007] Added Subscription sources (Mke Kroutikov)<br>
 *  - [13.11.2007] Order id cleanup (Erik Nijkamp)<br>
 *  
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 *  @author Mike Kroutikov
 */
public class PaperBroker extends BrokerBase {
    
	private final static Logger log = Logger.getLogger(PaperBroker.class);
	
    private double commission = 0.0;
    // the last order id. 
    private long orderId = 0;

    // component to take care of tracking market quotes 
    private final QuoteTrackingBrokerBase quoteTracker = new QuoteTrackingBrokerBase() {
		@Override
		protected void processOrder(Order order, Quote quote) throws Exception {
			checkOrder(order, quote);
		}
    };
    
    public PaperBroker() {
    	super();
    }
    
    public PaperBroker(Account account) {    	
    	super(account);
    }    

    public PaperBroker(Account account, IQuoteSubscriptionSource quoteSource) {    	
    	super(account);
    	quoteTracker.setQuoteSubscriptionSource(quoteSource);
    }    
    
    public PaperBroker(BrokerAccount account) {
    	super(account);
    }

    /**
     * Call this method to initialize the object (all properties must be already set).
     * When using Spring, declare this as bean's "init-method".
     * 
     * @throws Exception if something goes wrong.
     */
    public void init() throws Exception {
		Order[] openOrders = this.getBrokerAccount().getOrderBook().getOpenOrders();
		for(Order o : openOrders) {
			quoteTracker.addToManagedOrders(o);
		}
    }
    
    /**
     * Call this method to de-initialize the object to release outstanding quote subscriptions.
     * When using Spring, declare this as bean's "destroy-method".
     */
    public void destroy() {
    	quoteTracker.destroy();
    }
    
    public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}
    
    /**
     * order is executed at next price event. 
     */
	@Override
    public void placeOrder(Order order) throws Exception {
    	order.setBrokerAssignedId("" + orderId);
    	orderId++; 
    	super.placeOrder(order);
    	quoteTracker.addToManagedOrders(order);
    }
	
	@Override
	public void cancelOrder(Order order) throws Exception {
		if(quoteTracker.removeFromManagedOrders(order)) {
			super.cancelOrder(order);
		}
	}

    private void checkStopOrder(Order order, Quote quote) throws Exception {
		if (order.getSide().equals(OrderSide.BUY) 
				&& quote.getAskPrice() != Quote.NOT_SET) {
			// ok, need to check if the price is lower.
			if (quote.getAskPrice() > order.getStopPrice()) {
				// execute the stop.
				executeOrder(order, quote.getTimeStamp(), quote.getAskPrice(), quote.getAskQuantity());
			}
		} else if ((order.getSide().equals(OrderSide.SELL)
				|| order.getSide().equals(OrderSide.SHORT_SELL)
				|| order.getSide().equals(OrderSide.SHORT_SELL_EXEMPT))
			&& quote.getBidPrice() != Quote.NOT_SET) {
			// ok, need to check if the price is higher.
			if (quote.getBidPrice() < order.getStopPrice()) {
				// execute the stop.
				executeOrder(order, quote.getTimeStamp(), quote.getBidPrice(), quote.getBidQuantity());
			}
		}
    }
    
    private void checkTrailingStopOrder(Order order, Quote quote) throws Exception {
		if ((order.getSide().equals(OrderSide.SELL)
				|| order.getSide().equals(OrderSide.SHORT_SELL)
				|| order.getSide().equals(OrderSide.SHORT_SELL_EXEMPT))
				&& quote.getBidPrice() != Quote.NOT_SET) {
				
			boolean processOrder = false; 
			if(order.getLimitPrice() != NOT_SET) {
				if(quote.getBidPrice() > order.getLimitPrice()) {
					// activate order processing
					processOrder = true; 
				} else if(order.getStopPrice() != NOT_SET && quote.getBidPrice() < order.getStopPrice()) {
					// activate order processing
					processOrder = true; 
				}
			} else {
				processOrder = true; 
			}
					
			if(processOrder){
				if(order.getStopPrice() == NOT_SET){
					// initialize it.
					order.setStopPrice(quote.getBidPrice() - order.getTrailingDistance());
				}
						
				// trail it.
				if(order.getStopPrice() + order.getTrailingDistance() < quote.getBidPrice()){
					order.setStopPrice(quote.getBidPrice() - order.getTrailingDistance());
					log.info("Trailing order, new stop : "+order.getStopPrice());
				}
					
				// ok, need to check if the price is lower.
				if (quote.getBidPrice() < order.getStopPrice()) {
					// execute the stop.
					executeOrder(order, quote.getTimeStamp(), quote.getBidPrice(), quote.getBidQuantity());
				}
			}
		} else if (order.getSide().equals(OrderSide.BUY) 
				&& quote.getAskPrice() != Quote.NOT_SET) {
			boolean processOrder = false; 
			if(order.getLimitPrice() != NOT_SET){
				if( quote.getAskPrice() < order.getLimitPrice()){
					// activate order processing
					processOrder = true; 
				}
			}
			else processOrder = true; 
			
			if(processOrder){
				if(order.getStopPrice() == NOT_SET){
					// initialize it.
					order.setStopPrice(quote.getAskPrice() + order.getTrailingDistance());
				}
						
				// trail it.
				if(order.getStopPrice() - order.getTrailingDistance() > quote.getAskPrice()){
					order.setStopPrice(quote.getAskPrice() + order.getTrailingDistance());
					log.info("Trailing order, new stop : "+order.getStopPrice());
				}

						
				// ok, need to check if the price is higher.
				if (quote.getAskPrice() > order.getStopPrice()) {
					// execute the stop.
					executeOrder(order, quote.getTimeStamp(), quote.getAskPrice(), quote.getAskQuantity());
				}
			}
		}
    }
    
    private void checkMarketOrder(Order order, Quote quote) throws Exception {
		if(order.getSide().equals(OrderSide.BUY) 
				&& quote.getAskPrice() != Quote.NOT_SET ) {
			// execute at ask 
			executeOrder(order, quote.getTimeStamp(), quote.getAskPrice(), quote.getAskQuantity());
		}
		else if(quote.getBidPrice() != Quote.NOT_SET ) {
			executeOrder(order, quote.getTimeStamp(), quote.getBidPrice(), quote.getBidQuantity());
		}
    }

    private void checkLimitOrder(Order order, Quote quote) throws Exception {
		
		if (order.getSide().equals(OrderSide.BUY) 
				&& quote.getAskPrice() != Quote.NOT_SET) {
			// ok, need to check if the price is lower.
			if (quote.getAskPrice() < order.getLimitPrice()) {
				// execute the stop.
				executeOrder(order, quote.getTimeStamp(), quote.getAskPrice(), quote.getAskQuantity());
			}
		} else if ((order.getSide().equals(OrderSide.SELL)
				|| order.getSide().equals(OrderSide.SHORT_SELL)
				|| order.getSide().equals(OrderSide.SHORT_SELL_EXEMPT))
				&& quote.getBidPrice() != Quote.NOT_SET ) {
			// ok, need to check if the price is higher.
			if (quote.getBidPrice() > order.getLimitPrice()) {
				// execute the stop.
				executeOrder(order, quote.getTimeStamp(), quote.getBidPrice(), quote.getBidQuantity());
			}
		}

    }

    private void checkOrder(Order order, Quote quote) throws Exception {

		switch(order.getType()) {
		case STOP:
			checkStopOrder(order, quote);
			break;
		case TRAILING_STOP:
			checkTrailingStopOrder(order, quote);
			break;
		case MARKET:
			checkMarketOrder(order, quote);
			break;
		case LIMIT:
			checkLimitOrder(order, quote);
			break;
		default:
			throw new Exception("Unsupported order type " + order);
		}
	}
	
	/**
	 * method to fully execute an order at a given quotation. 
	 * Only important for PaperBroker!!!!
	 * 
	 * @param order
	 * @param priceIndication
	 */
	private void executeOrder(Order order, TimeStamp date, double price, double quantity) throws Exception {
		
		if(quantity > order.getOpenQuantity()){
			quantity = order.getOpenQuantity();
		}
		
		
		// create a new execution report. 
		Execution execution = new Execution();
		execution.setExecutionTimeStamp(date);
		execution.setExecutionQuantity(quantity);
		execution.setExecutionPrice(price);
		execution.setOrder(order);
		log.info("" + order.getOrderTimeStamp().getDate().toLocaleString()  + " " + order.getSide() + "  " +order.getQuantity() + "  " + 
				order.getSymbol() + " " +  this.getBrokerAccount().getBalanceBook().getCurrentBalance() );
		// bill the commission. 
		this.getBrokerAccount().getBalanceBook().addBalanceEntry(-commission, execution.getExecutionTimeStamp());
		
		// call the super class execution method. 
		super.processExecution(execution);
	
	}
}