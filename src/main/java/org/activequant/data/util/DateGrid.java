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
package org.activequant.data.util;

import java.util.Calendar;
import java.util.Vector;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.types.TimeStamp;
import org.activequant.core.util.CandleSeriesUtil;
import org.apache.log4j.Logger;



/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [08.11.2006] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class DateGrid {
	
	protected final static Logger log = Logger.getLogger(DateGrid.class);
	
	private Calendar calendar = Calendar.getInstance();
	private TimeStamp [] dates;
	private CandleSeries[] alignedSeries;
	
	public DateGrid(CandleSeries... series) throws Exception{	
		// Get start date
		TimeStamp earliestDate = series[0].lastElement().getTimeStamp();
		// Get end date
		TimeStamp latestDate = series[0].firstElement().getTimeStamp();
		for(int i = 1; i < series.length; i++) {
			TimeStamp date = series[i].lastElement().getTimeStamp();
			if(date.isBefore(earliestDate))
				earliestDate = date;
			if(date.isAfter(latestDate))
				latestDate = date;
		}
		
		// log
		log.debug("earliest date = " + earliestDate + " latest date = " + latestDate);
		assert(earliestDate.isBefore(latestDate));
		
		// Build date list
		buildDays(earliestDate, latestDate);
		
		// Algin series
		alignSeries(series);		
	}
	
	private void buildDays(TimeStamp start, TimeStamp end) {
		Vector<TimeStamp> datesVector = new Vector<TimeStamp>();
		calendar.setTime(start.getDate());
		while(!calendar.getTime().after(end.getDate())) {
			datesVector.add(0, new TimeStamp(calendar.getTime()));
			calendar.add(Calendar.DATE, 1);
		}
		dates = datesVector.toArray(new TimeStamp[datesVector.size()]);
	}
	
	private void alignSeries(CandleSeries... series) throws Exception {
		// New array
		alignedSeries = CandleSeriesUtil.cloneSeries(series);
		// New data holding all dates
		Candle[][] data = new Candle[series.length][dates.length];
		// Fill
		for(int i = 0; i < alignedSeries.length; i++) {
			for(int j = 0; j < dates.length; j++) {
				if(series[i].containsDate(dates[j]))
					data[i][j] = series[i].getByTimeStamp(dates[j]);
				else
					data[i][j] = new Candle(dates[j]);
			}			
		}
		// Apply
		for(int i = 0; i < alignedSeries.length; i++) {
			alignedSeries[i].setCandles(data[i]);
		}
	}
	
	public CandleSeries[] getAlignedSeries() {
		return alignedSeries;
	}
}
