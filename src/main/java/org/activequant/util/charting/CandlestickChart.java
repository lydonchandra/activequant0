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

****/package org.activequant.util.charting;

import java.util.ArrayList;
import java.util.List;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.CandleSeries.PositionPolicy;
import org.activequant.core.types.TimeStamp;
import org.activequant.data.util.Tuple;
import org.activequant.tradesystem.domainmodel.Execution;
import org.activequant.tradesystem.domainmodel.ExecutionBook;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.domainmodel.OrderBook;
import org.activequant.tradesystem.types.OrderSide;
import org.activequant.util.exceptions.ValueNotFoundException;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;


/**
 * 
 * creates a candle stick chart. <br>
 * <br>
 * <b>History:</b><br>
 *  - [01.06.2007] Created (Ulrich Staudinger)<br>
 *  - [29.09.2007] Removed warnings (Erik Nijkamp)<br>
 *  - [18.10.2007] Added facility to add a textual annotation (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class CandlestickChart extends Chart {

	/**
	 * calling super constructor. 
	 *
	 */
	public CandlestickChart(){
		super();
	}
	
	public CandlestickChart(CandleSeries ts){
		super();
		setCandleSeries(ts);
	}
	
	/**
	 * use this to specify the timeseries information for this chart. 
	 * 
	 * created on 07.05.2006
	 * @param ts
	 */
	public void setCandleSeries(CandleSeries ts) {
		setCandleSeries("underlying", ts);
	}
	
	/**
	 * use this to specify the timeseries information for this chart. 
	 * 
	 * created on 07.05.2006
	 * @param title
	 * @param ts
	 */
	public void setCandleSeries(String title, CandleSeries ts) {
		
		OHLCDataItem[] ohlcs = new OHLCDataItem[ts.size()];
		for (int i = 0; i < ohlcs.length; i++) {
			Candle c = ts.get(ohlcs.length - i - 1);
			if(c!=null){
				OHLCDataItem item = new OHLCDataItem(
						c.getTimeStamp().getDate(), c.getOpenPrice(), c.getHighPrice(), c.getLowPrice(),
						c.getClosePrice(), c.getVolume());
				ohlcs[i] = item;
			}
			else{
				log.warn("[setCandleSeries] Candle was null.");
			}
		}
		
		DefaultOHLCDataset setOHLC = new DefaultOHLCDataset(title, ohlcs);
		CandlestickRenderer c1=new CandlestickRenderer();
        

		c1.setAutoWidthFactor(0.0);
		c1.setAutoWidthGap(0.3);
		
		XYPlot plot1=chart.getXYPlot();
		
		plot1.setDataset(0, setOHLC);
		plot1.setRenderer(0, c1);
		
	}
	
	
	/**
	 * helper method to draw the executions into this chart. 
	 * @param referenceSeries
	 * @param orderBook
	 * @param execBook
	 */
	public void drawExecutions(CandleSeries referenceSeries, OrderBook orderBook, ExecutionBook execBook){
		Order[] orders = orderBook.getOrdersByContractSpecification(referenceSeries.getInstrumentSpecification());
		
		
		for(Order o : orders){
			Execution[] execs = execBook.getExecutionsByOrderId(o.getBrokerAssignedId());
			for(Execution exe : execs){
				try{
					// fetch the position time,
					int positionInSeries = referenceSeries.getTimeStampPosition(exe.getExecutionTimeStamp(),PositionPolicy.FEASIBLE);
					
					// make sure a candle is found. 
					if(positionInSeries>0){
						Candle c = referenceSeries.get(positionInSeries-1);
						// draw the execution. 
						if(o.getSide().equals(OrderSide.BUY)){
							// draw up arrow above price 
							chart.getXYPlot().addAnnotation(getUpArrow(c.getTimeStamp().getDate().getTime(), exe.getExecutionPrice()));
						}
						else if(o.getSide().equals(OrderSide.SELL)){
							// draw down arrow above price 
							chart.getXYPlot().addAnnotation(getDownArrow(c.getTimeStamp().getDate().getTime(), exe.getExecutionPrice()));
						}
						if(o.getSide().equals(OrderSide.SHORT_SELL)){
							// draw down arrow below price 
							chart.getXYPlot().addAnnotation(getDownArrow(c.getTimeStamp().getDate().getTime(), exe.getExecutionPrice()));
						}
					}
					
				}
				catch(ValueNotFoundException e){
					// ignore. could be this chart should only show a subset of dates.  
				}
			}
		}
		
		
		// iterate over the candles and build the position count.
		List<Tuple<TimeStamp,Double>> posLine = new ArrayList<Tuple<TimeStamp, Double>>();
		double positionCount = 0.0;
		for(int i=referenceSeries.size()-2;i>0;i--){
			Candle c = referenceSeries.get(i);
			// try to see if we have an order for that date ....
			for(Order o : orders){
				if(o.getOrderTimeStamp().equals(c.getTimeStamp())){
					positionCount += (o.getSide()==OrderSide.BUY)?o.getQuantity():-o.getQuantity();
				}
			}
			
			if(positionCount!=0.0){
				double change  = 0.001;
			
				double placement = c.getClosePrice() + (Math.signum(positionCount)*change);
				// 	calculate the value where to draw it. 
				Tuple<TimeStamp,Double> t = new Tuple<TimeStamp, Double>(c.getTimeStamp(),placement);
				posLine.add(t);
			}
			
		}
		addDotSeriesChart("Positions", posLine);
	}
	
}
