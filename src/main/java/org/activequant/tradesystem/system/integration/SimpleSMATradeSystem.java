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
package org.activequant.tradesystem.system.integration;

import java.util.ArrayList;
import java.util.List;

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.Market;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.domainmodel.Position;
import org.activequant.tradesystem.system.TradeSystemBase;
import org.activequant.tradesystem.system.annotation.NumberVariable;
import org.activequant.tradesystem.types.OrderSide;
import org.activequant.tradesystem.types.OrderTif;
import org.activequant.tradesystem.types.OrderType;
import org.activequant.util.algorithms.FinancialLibrary;
import org.apache.log4j.Logger;

/**
 * 
 * Simple single average trade system. <br>
 * <br>
 * <b>History:</b><br>
 *  - [09.06.2007] Created (Ulrich Staudinger)<br>
 *  - [11.06.2007] Moved to order enums (Erik Nijkamp)<br>
 *  - [15.06.2007] Polished and finalized code (Erik Nijkamp)<br>
 *  - [21.06.2007] Moved to contract specification. (Ulrich Staudinger)<br>
 *  - [21.06.2007] Cleanup (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public class SimpleSMATradeSystem extends TradeSystemBase {	
	
	protected final static Logger log = Logger.getLogger(SimpleSMATradeSystem.class);
	
	public SimpleSMATradeSystem() {
		super();
	}

	@NumberVariable(min=1, max=30, step=1)
	public double period = 5; 
	
	public Order[] onMarket(Account account, Market market) throws Exception {
		// list of orders
		List<Order> orders = new ArrayList<Order>();
		
		// iterate
		for(CandleSeries series : market.getCandleSeries()) {
			if(series.size()>(period+1)){
				// values
				double close0 = series.get(0).getClosePrice();
				double close1 = series.get(1).getClosePrice();
				double sma0 = FinancialLibrary.SMA((int)period, series.getOpens(), 0);
				double sma1 = FinancialLibrary.SMA((int)period, series.getOpens(), 1);			
				log.info("[evaluate] "+series.getInstrumentSpecification().toString()+ ": "+close0+ " / "+sma0+ ", "+close1+" / "+sma1);
							
				// order
				Order order = new Order();
				order.setType(OrderType.MARKET);
				order.setTimeInForce(OrderTif.GTC);
				order.setOrderTimeStamp(market.getMarketTimeStamp());
				order.setInstrumentSpecification(series.getInstrumentSpecification());
				int totalSymbols = market.getCandleSeries().length;
				
				// money management. 
				double cashToUse = account.getBalanceBook().getCurrentBalance() * (0.6 / totalSymbols); 
				int quantity = (int)(cashToUse / series.get(0).getClosePrice());				
				order.setQuantity(quantity);
				
				
				
				// long/short
				if(close0 > sma0 && close1 < sma1) {					
					// long order.
					order.setSide(OrderSide.BUY);
					closeOrders(account.getPortfolio().getPositions(), series, market, orders);					
					orders.add(order);
				} else if(close0 < sma0 && close1 > sma1) {
					// short order.					
					order.setSide(OrderSide.SHORT_SELL);
					closeOrders(account.getPortfolio().getPositions(), series, market, orders);		
					orders.add(order);					
				}			
			}
		}
		
		// return
		return orders.toArray(new Order[] {});
	}
	
	private void closeOrders(Position[] positions, CandleSeries series,
			Market market, List<Order> orders) {
		// close any existing position.
		for (Position pos : positions) {
			if (pos.getInstrumentSpecification().equals(
					series.getSeriesSpecification().getInstrumentSpecification())) {
				// add the close order.
				Order order = new Order();
				order.setOrderTimeStamp(market.getMarketTimeStamp());
				order.setSide(pos.getQuantity() > 0.0 ? OrderSide.SELL : OrderSide.BUY);
				order.setTimeInForce(OrderTif.GTC);
				order.setType(OrderType.MARKET);
				order.setQuantity(Math.abs(pos.getQuantity()));
				order.setInstrumentSpecification(series.getSeriesSpecification().getInstrumentSpecification());
				orders.add(order);
			}
		}
	}

	public String getName() {
		return "SMA";
	}

	public String getDescription() {
		return "Simple SMA trade system.";
	}

}
