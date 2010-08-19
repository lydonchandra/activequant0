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

import javax.swing.JFrame;

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.Market;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.system.TradeSystemBase;
import org.activequant.util.charting.CandlestickChart;
import org.activequant.util.tools.DirUtils;
import org.apache.log4j.Logger;

/**
 * plain trade system that just charts upon evaluation.<br>
 * <br>
 * <b>History:</b><br>
 *  - [16.08.2007] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class ChartingTradeSystem extends TradeSystemBase {
	
	protected final static Logger log = Logger.getLogger(ChartingTradeSystem.class);
	private List<JFrame> charts = new ArrayList<JFrame>();
	
	
	public synchronized Order[] onMarket(Account account, Market market) throws Exception {
		log.info("[evaluate] using account '" + account.getId() + "'");
		log.info("[evaluate] candle series in market: "+market.getCandleSeries().length);
		
		List<Order> orders = new ArrayList<Order>();
		int window=0;
		for(JFrame cf : charts){
			cf.dispose();
		}
		charts.clear();
		//iterate over the candle series in the market object.
		for(CandleSeries cs : market.getCandleSeries()){
			if(!cs.isEmpty()){
				CandleSeries tempSeries = new CandleSeries(cs.getSeriesSpecification());
				// chart the latest 100 candles.
				int maxIndex = cs.size()>101?100:cs.size();
				for(int i=0;i<maxIndex-1;i++){
					tempSeries.add(cs.get(i));
				}
				
				// simply chart it. 
				CandlestickChart chart = new CandlestickChart();
				chart.setCandleSeries(tempSeries);
				JFrame cf = chart.getJFrame("" + DirUtils.asFileName(cs.getInstrumentSpecification()), false);
				cf.setBounds(window*400, 0, 400, 300);
				cf.setVisible(true);
				charts.add(cf);
				
			}
			window++;
		}		

		return orders.toArray(new Order[]{});
	}
	
	

	public String getDescription() {
		return "Simple real time charting trade system.";
	}

	public String getName() {
		return "ChartingTradeSystem";
	}
}
