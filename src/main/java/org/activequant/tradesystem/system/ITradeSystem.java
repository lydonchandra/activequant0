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

import org.activequant.core.domainmodel.Market;
import org.activequant.core.domainmodel.Quote;
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.Execution;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.util.tools.DecorationsMap;


/**
 * Generic interface for a trade system. <br>
 * <br>
 * <b>History:</b><br>
 *  - [01.03.2007] Created (Ulrich Staudinger)<br>
 *  - [06.05.2007] Cleanup (Erik Nijkamp)<br>
 *  - [12.11.2007] removing instrument specifications from trade system interface methods (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public interface ITradeSystem {
	
	/**
	 * TradeSystemContainer calls this method to evaluate a market situation
	 * once all candles have been aquired for this timeframe.
	 * 
	 * @param marketSituation
	 * @param account
	 *            this contains the account we are working on. Through the
	 *            account, the system can obtain open positions, cash, etc.
	 * @return a NEW list of orders (this could include cancel orders for the
	 *         open orders), this list must not include already open orders,
	 *         otherwise they'll be placed again.
	 * @throws Exception
	 */
	public Order[] onMarket(Account account, Market market) throws Exception;
	
	/**
	 * trade indications are routed into trade systems through this method. A Trade Indication contains reference to it's 
	 * originating instrument.
	 *   
	 * @param account
	 * @param instrument
	 * @param priceIndication
	 * @return Orders are returned. These orders are immediately executed by the context. 
	 * @throws Exception
	 */
	public Order[] onTradeIndication(Account account, TradeIndication priceIndication) throws Exception;
	
	/**
	 * see onTradeIndication
	 * @param account
	 * @param quote
	 * @return
	 * @throws Exception
	 */
	public Order[] onQuote(Account account,Quote quote) throws Exception;
	
	/**
	 * whenever an order is executed, the execution object is routed through this method into the trade system. 
	 * @param account
	 * @param execution
	 * @param changedOrder
	 * @return
	 * @throws Exception
	 */
	public Order[] onExecution(Account account, Execution execution, Order changedOrder) throws Exception; 
	
	
	/**
	 * returns the name of this trade system.
	 * @return the name of this trade system. 
	 */
	public String getName();
    
    /**
     * method that returns a description of the trade system
     * @return a description
     */
    public String getDescription();
    
    /**
     * specifies whther this trade system requires a preparation phase or not.
     * This can be required for neural networks or for regression systems i.e. during backtesting. 
     * 
     * @return
     */
    public boolean requiresPreparationDataSet(); 
    
    /**
     * method called for setting the market snapshots upon which this system should 
     * initialize itself with. 
     * 
     * @param preparationSituation
     * @return
     * @throws Exception
     */
    public void prepare(Market market) throws Exception;
    
    /**
     * a hashmap for decorations to this object. 
     * @return
     */
    public DecorationsMap getDecorationMap();
    
}

