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
package org.activequant.tradesystem.system;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.MarketParameter;
import org.activequant.core.domainmodel.Quote;
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.Execution;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.domainmodel2.event.OrderEvent;


/**
 * Open-Trade-System-Alliance (OTSA) interface for a trade-system.<br>
 * <br>
 * <b>History:</b><br>
 *  - [05.12.2007] Created (Ulrich Staudinger)<br>
 *  - [06.05.2007] Cleanup (Erik Nijkamp)<br>
 *  - [12.11.2007] Removing instrument specifications from trade system interface methods (Ulrich Staudinger)<br>
 *  - [05.12.2007] Refactored interface (Erik Nijkamp)<br>
 *  - [10.12.2007] Added cancel/placeOrder (Erik Nijkamp)<br>
 *  - [12.12.2007] Adding order state (Ulrich Staudinger)<br>
 *  - [16.12.2007] Added simplified broker interface (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public interface ITradeSystem3 {
	
	/**
	 * Simplified broker interface which is injected by the context.
	 */
	interface IOrderBroker {
		void placeOrder(Order order);
		void updateOrder(Order order);
		void cancelOrder(Order order);
	}
	
	enum Mode { PREPARE, BACKTEST, LIVE };
	
	/**
	 * the current mode is managed by the container and affects the actual
	 * behavior (e.g. training, live) of the trade-system implementation 
	 * (respectively the concrete handler methods onCandle(...) etc.)
	 * @param mode
	 */
	void setMode(Mode mode);
	
	/**
	 * returns the currently used mode of this trade-system.
	 * @return
	 */
	Mode getMode();
	

	/**
	 * TS pushes order commands into this queue. 
	 * It is set from the context.
	 *  
	 * @param queue
	 */
	void setOrderBroker(IOrderBroker broker);
	
	/**
	 * market parameters are routed into trade-systems through this method.
	 *   
	 * @param param
	 * @return Orders are returned. These orders are immediately executed by the context. 
	 * @throws Exception
	 */
	void onMarketParameter(MarketParameter param) throws Exception;
	
	/**
	 * candles are routed into trade-systems through this method.
	 *   
	 * @param candle
	 * @return Orders are returned. These orders are immediately executed by the context. 
	 * @throws Exception
	 */
	void onCandle(Candle candle) throws Exception;
	
	/**
	 * trade indications are routed into trade-systems through this method. 
	 * @param trade
	 * @return Orders are returned. These orders are immediately executed by the context. 
	 * @throws Exception
	 */
	void onTradeIndication(TradeIndication trade) throws Exception;
	
	/**
	 * quotes are routed into trade-systems through this method. 
	 * @param quote
	 * @return Orders are returned. These orders are immediately executed by the context
	 * @throws Exception
	 */
	void onQuote(Quote quote) throws Exception;
	
	/**
	 * whenever an order is executed, the execution object is routed through this method into the trade-system. 
	 * @param execution
	 * @return
	 * @throws Exception
	 */
	void onExecution(Execution execution) throws Exception;
	
	
	/**
	 * called whenever the state of an order has changed. 
	 * @param order
	 * @throws Exception
	 */
	void onOrderEvent(OrderEvent orderEvent) throws Exception; 
	
	/**
	 * returns the name of this trade-system.
	 * @return the name of this system 
	 */
	String getName();
    
	/**
	 * method that returns a description of the trade-system.
	 * @return a description
	 */
	String getDescription();
    
	/**
	 * returns the account object associated with this trade-system.
	 * @return
	 */
	Account getAccount();
	
	/**
	 * method to set the account object. 
	 * @param account
	 */
	void setAccount(Account account);
}