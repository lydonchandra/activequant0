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

****/package org.activequant.tradesystem.system.integration;

import java.util.ArrayList;
import java.util.List;

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.Market;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.domainmodel.Position;
import org.activequant.tradesystem.system.TradeSystemBase;
import org.activequant.tradesystem.types.OrderSide;
import org.activequant.tradesystem.types.OrderTif;
import org.activequant.tradesystem.types.OrderType;
import org.apache.log4j.Logger;

/**
 * plain test trade system that creates random buy and sell orders<br>
 * <br>
 * <b>History:</b><br>
 *  - [11.05.2007] Created (Erik Nijkamp)<br>
 *  - [09.06.2007] moved to new account model (Ulrich Staudinger)<br>
 *  - [03.07.2007] Adding more complex example code, ie. random buy and sell, tif etc. (Ulrich Staudinger)<br>
 *
 *  @author Erik Nijkamp
 *  @author Ulrich Staudinger
 */
public class MockTradeSystem extends TradeSystemBase {
	
	protected final static Logger log = Logger.getLogger(MockTradeSystem.class);

	public synchronized Order[] onMarket(Account account, Market market) throws Exception {
		log.info("[evaluate] using account '" + account.getId() + "'");
		log.info("[evaluate] candle series in market: "+market.getCandleSeries().length);
		
		List<Order> orders = new ArrayList<Order>();
		
		//iterate over the candle series in the market object.
		for(CandleSeries cs : market.getCandleSeries()){
			log.info("[evaluate] market contains: "+cs.getInstrumentSpecification().toString());
			if(!cs.isEmpty()){
				log.info("[evaluate] data size of this contract: "+cs.size() +"  -> "+cs.get(0).toString());
			}
			
			// close existing orders
			closeOrders(account.getPortfolio().getPositions(), cs, market, orders);	
			
			// new order
			Order order = new Order();
			order.setSide(Math.random() > 0.5 ? OrderSide.BUY : OrderSide.SELL);
			order.setTimeInForce(OrderTif.GTC);
			order.setType(OrderType.MARKET);
			order.setQuantity(1);
			order.setInstrumentSpecification(cs.getInstrumentSpecification());
			order.setOrderTimeStamp(market.getMarketTimeStamp());
			orders.add(order);			
		}		

		return orders.toArray(new Order[]{});
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


	public String getDescription() {
		return "Simple test system";
	}

	public String getName() {
		return "TestSystem";
	}
}
