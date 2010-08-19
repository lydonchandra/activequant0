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
import org.activequant.core.domainmodel.Quote;
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.Execution;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.system.TradeSystemBase;
import org.activequant.tradesystem.system.annotation.NumberVariable;
import org.activequant.tradesystem.types.OrderSide;
import org.activequant.tradesystem.types.OrderState;
import org.activequant.tradesystem.types.OrderTif;
import org.activequant.tradesystem.types.OrderType;
import org.activequant.util.algorithms.FinancialLibrary;
import org.activequant.util.messaging.IMessenger;
import org.apache.log4j.Logger;

/**
 * 
 * Slightly different weightede ma slope system.<br>
 * <br>
 * <b>History:</b><br>
 *  - [15.11.2007] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class WeightedMASlopeSystem3 extends TradeSystemBase {	
	
	protected final static Logger log = Logger.getLogger(WeightedMASlopeSystem3.class); 
	
	@NumberVariable(min=1, max=10, step=1)
	public double period1; 
	@NumberVariable(min=1, max=10, step=1)
	public double period2;
	@NumberVariable(min=0.0, max=1.0,step=0.01)
	public double slopeThreshold;
	@NumberVariable(min=1, max=10, step=1)
	public double lookbackPeriod ;
	@NumberVariable(min=1, max=10, step=1)
	public double takeProfit = 2.0; 
	@NumberVariable(min=1, max=1000, step=10)
	public double stopLoss = 100.0;
	
	private IMessenger msg = null;
	
	public WeightedMASlopeSystem3() throws Exception {
		super();
		period1 = 2;
		period2 = 4;
		lookbackPeriod = 1;
		slopeThreshold = 0.02;
	}
	
	Boolean shortMode = false;
	Boolean flat = true;

	public Order[] onMarket(Account account, Market market) throws Exception {
		log.info("Evaluating market situation ...");
		// list of orders
		List<Order> orders = new ArrayList<Order>();
		
		// iterate
		for(CandleSeries series : market.getCandleSeries()) {
			if(series.size()>period2 && series.size() > (period1 + lookbackPeriod)){
				
				//
				double val0_0 = FinancialLibrary.WMA((int)period1, series.getCloses(), 0);
				double val0_LookBack = FinancialLibrary.WMA((int)period1, series.getCloses(), (int)lookbackPeriod);
				
				//
				double val1_0 = FinancialLibrary.WMA((int)period2, series.getCloses(), 0);
				double val1_1 = FinancialLibrary.WMA((int)period2, series.getCloses(), 1);
				double val1_LookBack = FinancialLibrary.WMA((int)period1, series.getCloses(), (int)lookbackPeriod);
				
				double[] val1 = new double[]{val1_0, val1_1};
				double slope = FinancialLibrary.priceSlope(1, val1, 0);
				
				log.info("Val0_0: "+val0_0+ " / Val 0_1: "+val0_LookBack + " / Val 1_0: "+val1_0 + " / val 1_1: "+val1_LookBack  + " // Slope of second average : "+slope);
				
				// order
				Order order = new Order();
				order.setType(OrderType.MARKET);
				order.setTimeInForce(OrderTif.GTC);
				order.setOrderTimeStamp(market.getMarketTimeStamp());
				order.setInstrumentSpecification(series.getSeriesSpecification().getInstrumentSpecification());
								
				order.setQuantity(1);
				
				// long/shorte 
				if(val0_0 > val0_LookBack && val0_0 > val1_0 && (shortMode||flat) && val1_0 > val1_LookBack && slope > slopeThreshold) {
					if(msg != null) msg.sendMessage("uls@jabber.org", null, "LONG : " +series.get(0).toString());
					
					
					Order[] closeOrders = super.closeAllPositions(account.getPortfolio().getPositions(), series.getInstrumentSpecification(), market);
					for(Order o : closeOrders){
						orders.add(o);
						flat = true; 
					}
					
					// cancel all existing orders. 
					for(Order openOrder : account.getOrderBook().getOpenOrders()){
						openOrder.setState(OrderState.CANCELED);
						// verydirtyharry.
						orders.add(openOrder);
					}
					
					if(flat){

						// long order.
						order.setSide(OrderSide.BUY);
						order.setMessage("OPEN");
						orders.add(order);
							
						
					}
					
					shortMode = false;
					flat = false; 
					
				} else if(val0_0 < val0_LookBack && val0_0 < val1_0 && (!shortMode||flat) && val1_0 < val1_LookBack && slope < -slopeThreshold) {
					
					  
					Order[] closeOrders = super.closeAllPositions(account.getPortfolio().getPositions(), market);
					for(Order o : closeOrders){
						orders.add(o);
						flat = true; 
					}

					for(Order openOrder : account.getOrderBook().getOpenOrders()){
						openOrder.setState(OrderState.CANCELED);
						// verydirtyharry.
						orders.add(openOrder);
					}
					if(flat){
						if(msg != null) msg.sendMessage("uls@jabber.org", null, "SHORT " +series.get(0).toString());
						
						// short order.					
						order.setSide(OrderSide.SELL);
						order.setMessage("OPEN");
						orders.add(order);
						
						// cancel all existing orders.  
					
						
					
						
					}
					
					shortMode=true;
					flat = false; 
				}			
				
				
			}
		}
		
		// return
		return orders.toArray(new Order[] {});
	}
	

	/**
	 * stub implementation, override in your trade system.
	 */
	@Override
	public Order[] onTradeIndication(Account account, TradeIndication priceIndication) throws Exception{
		return null;		
	}
	
	/**
	 * stub implementation, override in your trade system.
	 */
	@Override
	public Order[] onQuote(Account account, Quote quote) throws Exception {
		return null;		
	}
	
	/**
	 * stub implementation, override in your trade system.
	 */
	@Override
	public Order[] onExecution(Account account, Execution execution,  Order changedOrder) throws Exception {
		if(msg != null) msg.sendMessage("uls@jabber.org", null, "Execution " +execution.toString()+" \n Order status change :"+changedOrder.toString()+"\ncurrent position count "+account.getPortfolio().getPositions().length);
		List<Order> orders = new ArrayList<Order>();
		if(changedOrder.getType()==OrderType.LIMIT || changedOrder.getType()==OrderType.STOP || changedOrder.getType()==OrderType.TRAILING_STOP 
				|| changedOrder.getType()==OrderType.STOP_LIMIT){
			// cancel all other orders. 
			for(Order openOrder : account.getOrderBook().getOpenOrders()){
				openOrder.setState(OrderState.CANCELED);
				// verydirtyharry.
				orders.add(openOrder);
			}
			flat = true; 
		}
		else{
			
			if(changedOrder.getMessage()!=null && changedOrder.getMessage().equals("OPEN")){
				
				if(changedOrder.getSide() == OrderSide.BUY){

					// check if it was a simple buy order ... 
					// add a target profit order. 
					Order targetProfitOrder = changedOrder.clone();
					targetProfitOrder.setSide(OrderSide.SELL);
					targetProfitOrder.setType(OrderType.LIMIT);
					targetProfitOrder.setLimitPrice(changedOrder.getAveragePrice()+5);
					
					// add a stop loss order
					Order stopLossOrder = changedOrder.clone();
					stopLossOrder.setSide(OrderSide.SELL);
					stopLossOrder.setType(OrderType.STOP);
					stopLossOrder.setStopPrice(changedOrder.getAveragePrice()-15);
					
					// add a trailing order. 
					Order trailingOrder = changedOrder.clone();
					trailingOrder.setSide(OrderSide.SELL);
					trailingOrder.setType(OrderType.TRAILING_STOP);
					trailingOrder.setLimitPrice(changedOrder.getAveragePrice()+1.5);
					trailingOrder.setTrailingDistance(2);
					
					//orders.add(targetProfitOrder);
					
					orders.add(stopLossOrder);
					orders.add(trailingOrder);
		
				}
				else{
					// add a target profit order. 
					Order targetProfitOrder = changedOrder.clone();
					targetProfitOrder.setSide(OrderSide.BUY);
					targetProfitOrder.setType(OrderType.LIMIT);
					targetProfitOrder.setLimitPrice(changedOrder.getAveragePrice()-5);
					
					// add a stop loss order
					Order stopLossOrder = changedOrder.clone();
					stopLossOrder.setSide(OrderSide.BUY);
					stopLossOrder.setType(OrderType.STOP);
					stopLossOrder.setStopPrice(changedOrder.getAveragePrice()+15);
					
					// add a trailing order. 
					Order trailingOrder = changedOrder.clone();
					trailingOrder.setSide(OrderSide.BUY);
					trailingOrder.setType(OrderType.TRAILING_STOP);
					trailingOrder.setLimitPrice(changedOrder.getAveragePrice()-1.5);
					trailingOrder.setTrailingDistance(2);
				

					//orders.add(targetProfitOrder);
					orders.add(stopLossOrder);
					orders.add(trailingOrder);	
				}
				
			}
		}
		
		if(account.getPortfolio().getPositions().length == 0){
			flat = true; 
		}
		
		return orders.toArray(new Order[]{});
	}
	
	public void setMessenger(IMessenger val) {
		msg = val;
	}
	
	public IMessenger getMessenger() {
		return msg;
	}
	
	
	public String getName() {
		return "WMAS";
	}

	public String getDescription() {
		return "Ulrich's pleasure. ";
	}

}
