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

import java.util.ArrayList;
import java.util.List;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Market;
import org.activequant.core.domainmodel.Quote;
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.Execution;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.domainmodel.Position;
import org.activequant.tradesystem.types.OrderSide;
import org.activequant.tradesystem.types.OrderTif;
import org.activequant.tradesystem.types.OrderType;
import org.activequant.util.exceptions.NotImplementedException;
import org.activequant.util.tools.DecorationsMap;


/**
 * Base class for trade systems, all trade systems should extend this class. <br>
 * <br>
 * <b>History:</b><br> 
 * - [06.05.2007] Created (Erik Nijkamp)<br> 
 * - [10.05.2007] added javadoc and requiresPreparation bool method (Ulrich Staudinger)<br> 
 * - [10.08.2007] Adding convenience method to generate close orders for all open positions (Ulrich Staudinger) <br>
 * - [02.11.2007] Added executions to onOrderStatusChange (Ulrich Staudinger)<br>
 * - [07.12.2007] Adding convenience method to close only a single position.  (Ulrich Staudinger) <br>
 * 
 * @author Erik Nijkamp
 * @author Ulrich Staudinger
 */
public abstract class TradeSystemBase implements ITradeSystem {

	private DecorationsMap decorations = new DecorationsMap();

	public DecorationsMap getDecorationMap() {
		return decorations;
	}

	/**
	 * To be overridden if a trade system requires a preparation phase like
	 * neural networks or regression
	 * 
	 */
	public void prepare(Market marketSituation) throws Exception {
		throw new NotImplementedException();
	}

	/**
	 * To be overriden, see above.
	 */
	public boolean requiresPreparationDataSet() {
		return false;
	}

	/**
	 * convenience method to close all positions. 
	 * 
	 * @param positions
	 * @param market
	 * @param orders
	 */
	protected Order[] closeAllPositions(Position[] positions, Market market) {
		List<Order> orders = new ArrayList<Order>();
		// close any existing position.
		for (Position pos : positions) {
			Order[] os = closeAllPositions(positions, pos.getInstrumentSpecification(), market);
			for(Order o : os){
				orders.add(o);
			}
		}
		return orders.toArray(new Order[] {});
	}
	
	
	/**
	 * convenience method to close all positions of a specific instrument. 
	 * 
	 * @param positions
	 * @param market
	 * @param orders
	 */
	protected Order[] closeAllPositions(Position[] positions, InstrumentSpecification specification, Market market) {
		List<Order> orders = new ArrayList<Order>();
		// close any existing position.
		for (Position pos : positions) {
			if(pos.getInstrumentSpecification().equals(specification)){
				// add the close order.
				orders.add(generateCloseOrder(pos, market));
			}
		}
		return orders.toArray(new Order[] {});
	}
	
	/**
	 * generates a close order for a given position.
	 * @param pos
	 * @param market
	 * @return
	 */
	protected Order generateCloseOrder(Position pos, Market market){
		// add the close order.
		Order order = new Order();
		order.setOrderTimeStamp(market.getMarketTimeStamp());
		order.setSide(pos.getQuantity() > 0.0 ? OrderSide.SELL
				: OrderSide.BUY);
		order.setTimeInForce(OrderTif.GTC);
		order.setType(OrderType.MARKET);
		order.setQuantity(Math.abs(pos.getQuantity()));
		order.setInstrumentSpecification(pos.getInstrumentSpecification());
		return order;
	}


	/**
	 * stub implementation, override in your trade system.
	 */
	public Order[] onTradeIndication(Account account, TradeIndication priceIndication) throws Exception{
		return null;		
	}
	/**
	 * stub implementation, override in your trade system.
	 */
	
	public Order[] onQuote(Account account, Quote quote) throws Exception {
		return null;		
	}
	/**
	 * stub implementation, override in your trade system.
	 */
	
	public Order[] onExecution(Account account, Execution execution, Order changedOrder) throws Exception {
		return null;
	}
	
	
	
}
