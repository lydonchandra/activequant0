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
package org.activequant.tradesystem.broker;

import org.activequant.core.types.TimeStamp;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.BrokerAccount;
import org.activequant.tradesystem.domainmodel.Execution;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.domainmodel.Position;
import org.activequant.tradesystem.types.BrokerId;
import org.activequant.tradesystem.types.OrderSide;
import org.activequant.tradesystem.types.OrderState;
import org.activequant.util.exceptions.NotImplementedException;
import org.activequant.util.pattern.events.Event;
import org.apache.log4j.Logger;


/**
 * Orders are stored in a queue. 
 * Once a new market data set is received, PaperBroker works out orders in the order queue. 
 * Order queue is a linked list. <br>
 * Once an order has been filled, even when done only partially, an execution report is sent
 * out. <br>
 * <br>
 * <b>History:</b><br>
 *  - [26.04.2007] Created (Erik N.)<br>
 *  - [04.05.2007] adding buy and sell methods (us)<br>
 *  - [10.06.2007] Adding stop loss + limit code. (Ulrich Staudinger) <br>
 *  - [28.06.2007] Adding executionReceived method (Ulrich Staudinger) <br>
 *  - [10.06.2007] Kicked unused code, cleanup (Erik Nijkamp) <br>  
 *  - [06.11.2007] Adding cancelOrder support (Ulrich Staudinger)<br>
 *  - [13.11.2007] Order id cleanup + polished portfolio access (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 *  @author Ulrich Staudinger
 */
public abstract class BrokerBase implements IBroker {
	
    private final static Logger log = Logger.getLogger(BrokerBase.class);
    
    
    private BrokerId brokerID = new BrokerId(getClass().getName());

    protected BrokerAccount brokerAccount = null;
    
    protected Event<Execution> onNewExecution = new Event<Execution>();
    
    protected Event<Order> onNewOrder = new Event<Order>();
    
    protected Event<BrokerAccount> onAccountUpdate = new Event<BrokerAccount>();
    
    
    public BrokerBase() {
    	
    }
    
    public BrokerBase(Account account) {
    	this.brokerAccount = account.getBrokerAccount(brokerID);
    }
    
    public BrokerBase(BrokerAccount account) {
    	this.brokerAccount = account;
    }
    
    /**
     * returns the specific broker id of this implementation. 
     * @return
     */
    public BrokerId getBrokerID() {
    	return brokerID;
    }
    
    public BrokerAccount getBrokerAccount() {
		return brokerAccount;
	}
    
    public void setBrokerAccount(BrokerAccount account) {
		this.brokerAccount = account;
	}
    
    protected void updateBalance(double balance, TimeStamp date) {
    	brokerAccount.getBalanceBook().addBalanceEntry(balance, date);
	}
    
	/**
	 * TODO could makes more sense to use quickfixj here. ???? (us)
	 * can't remember what i wanted to use it for. 
	 * 
	 * @param execution
	 */
	protected void processExecution(Execution execution) throws Exception {

		
		// check if there are open orders belonging to this execution. 
		Order order = execution.getOrder();

		// work out the execution. 
		{			
			double sum = order.getExecutedQuantity() * order.getAveragePrice();		
			
			sum += execution.getExecutionQuantity() * execution.getExecutionPrice();
			double avgPx = sum / (order.getExecutedQuantity() + execution.getExecutionQuantity());

			order.setExecutedQuantity(order.getExecutedQuantity() + execution.getExecutionQuantity());
			order.setOpenQuantity(order.getOpenQuantity() - execution.getExecutionQuantity());

			
			// set the average price.
			order.setAveragePrice(avgPx);
		}

		// check if the order is completely filled.
		if (order.getOpenQuantity() == 0) {
			// filled completely.
			// TODO hmm? (en)
		}

		// work out resulting portfolio positions
		updatePortfolio(order, execution);
		
		// update the executions / execution book. 
		updateExecutionBook(order, execution);

		// distribute the account update. 
		onAccountUpdate.fire(brokerAccount);
		
		// distribute the execution further ???
		onNewExecution.fire(execution);


	}
	
	/**
	 * method to update the portfolio. 
	 * @param order
	 * @param execution
	 */
	protected void updatePortfolio(Order order, Execution execution){
		// get position for execution
		Position position; 
		if (getBrokerAccount().getPortfolio().hasPosition(execution.getInstrumentSpecification())) {
			position = getBrokerAccount().getPortfolio().getPosition(execution.getInstrumentSpecification());
		} else {
			position = new Position();
			position.setInstrumentSpecification(execution.getInstrumentSpecification());
			getBrokerAccount().getPortfolio().addPosition(position);
		}
		
		// update the average price		
		double oldPosSize = position.getQuantity();
		double oldAvgPx = position.getAveragePrice();
		double positionChange = 0; 
		double cashFlow = 0;
		
		if (order.getSide().equals(OrderSide.SELL)
				|| order.getSide().equals(OrderSide.SHORT_SELL)
				|| order.getSide().equals(OrderSide.SHORT_SELL_EXEMPT)) {
			// decrease the amount of the position.
			position.setQuantity(position.getQuantity()
					- execution.getExecutionQuantity());
			// 
			positionChange = -execution.getExecutionQuantity();
			cashFlow = (execution.getExecutionPrice() - oldAvgPx)
					* execution.getExecutionQuantity();
		} else if (order.getSide().equals(OrderSide.BUY)) {
			// increase the position size.
			position.setQuantity(position.getQuantity()
					+ execution.getExecutionQuantity());
			//
			positionChange = +execution.getExecutionQuantity();
			//

			cashFlow = (oldAvgPx - execution.getExecutionPrice())
					* execution.getExecutionQuantity();
		} else {
			throw new NotImplementedException("Unknown order type.");
		}
		
		
		// clean out empty positions.
		if (position.getQuantity() == 0.0) {
			this.getBrokerAccount().getPortfolio().removePosition(position);

			this.getBrokerAccount().getBalanceBook().addBalanceEntry(cashFlow,
					execution.getExecutionTimeStamp());
		} else {
			// calculate the new avg price.
			position.setAveragePrice(Math
					.abs(((oldPosSize * oldAvgPx) + (positionChange * execution
							.getExecutionPrice()))
							/ position.getQuantity()));
		}

		// TODO (en)
//		 update the balance book with the execution.
		//double cashFlow = (-1)*positionChange * execution.getExecutionPrice(); 
		//this.getBrokerAccount().getBalanceBook().addBalanceEntry(cashFlow, execution.getExecutionDate());
		
	}
	
	protected void updateExecutionBook(Order order, Execution execution){
		this.getBrokerAccount().getExecutionBook().addExecution(execution);
		log.info("[updateExecutionBook] Execution id: " + execution.getId() + 
				" / corresponding order id: "+execution.getOrder().getBrokerAssignedId()+ 
				" / executions so far : "+getBrokerAccount().getExecutionBook().getExecutions().length);
	}
	
	/**
	 * method to place an order. 
	 * 
	 * your broker specific implementation should override this method and call
	 * this method at the end of your method through super.placeOrder(..).
	 * 
	 * It is good practice for your implementation to throw an exception 
	 * upon error, for example when your implementation receives an order with an unsupported
	 * order type from a trade system.  
	 * 
	 */
	public void placeOrder(Order order) throws Exception {
		log.info("[placeOrder] "+order.toString());
		
		// safety set. 
		if(order.getOrderTimeStamp() == null){
			order.setOrderTimeStamp(new TimeStamp());
		}
		
		this.getBrokerAccount().getOrderBook().addOrder(order);
		onNewOrder.fire(order);
	}
	
	public void placeOrders(Order... orders) throws Exception {
		for(Order order : orders){
			placeOrder(order);
		}
	}
	
    /**
     * method hook to cancel an order
     * @param orderId
     * @throws Exception
     */
    public void cancelOrder(Order order) throws Exception {
    	order.setState(OrderState.CANCELED);
    	// TODO: send out corresponding event
    }
    
    /**
     * method hook to cancel an array of order ids.
     * @param orderId
     * @throws Exception
     */
    public void cancelOrders(Order... orders) throws Exception {
    	for(Order order : orders){
    		cancelOrder(order);
    	}
    }
	
	protected Order findOrder(String orderId) {
		for(Order order : brokerAccount.getOrderBook().getOpenOrders()) {
			if(order.getBrokerAssignedId().equals(orderId)){
				return order; 
			}
		}
		throw new IllegalArgumentException();
	}

	public Event<Execution> getOnNewExecution() {
		return onNewExecution;
	}

	public Event<Order> getOnNewOrder() {
		return onNewOrder;
	}

	public Event<BrokerAccount> getOnAccountUpdate() {
		return onAccountUpdate;
	}
}