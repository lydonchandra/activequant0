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
package org.activequant.util.charting;

import org.activequant.tradesystem.domainmodel.BalanceBook;
import org.activequant.tradesystem.domainmodel.BalanceEntry;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;



/**
 * 
 * creates a line chart from a balance book. Merely a helper class. <br>
 * <br>
 * <b>History:</b><br>
 *  - [15.07.2007] Created (Ulrich Staudinger)<br>
 *  - [08.12.2007] Fixing rendering of charts <br>  
 *
 *  @author Ulrich Staudinger
 */
public class EquityChart extends Chart {

	/**
	 * calling super constructor. 
	 *
	 */
	public EquityChart(){
		super();
	}
	
	/**
	 * sets the equity chart on this object, but does not render it. Call renderToXYZ to actually render it. 
	 * @param balanceBook
	 */
	public void createEquityChart(BalanceBook balanceBook){
		
		final TimeSeries ts = new TimeSeries("Equity curve", Millisecond.class);
		double value = 0.0; 
		for(BalanceEntry be : balanceBook.getBalanceEntries()){
			value += be.getValue();
			ts.addOrUpdate(new Millisecond(be.getTimeStamp().getDate()), value);
		}
		final TimeSeriesCollection dataset = new TimeSeriesCollection(ts);
		
		final XYPlot plot1=chart.getXYPlot();
		plot1.setDataset(0, dataset);
		
	}
	
}
