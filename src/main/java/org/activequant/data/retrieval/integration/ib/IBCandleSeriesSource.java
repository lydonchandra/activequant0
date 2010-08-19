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
package org.activequant.data.retrieval.integration.ib;

import java.util.Calendar;

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.types.Expiry;
import org.activequant.core.types.Symbols;
import org.activequant.core.types.TimeFrame;
import org.activequant.core.types.TimeStamp;
import org.activequant.core.util.TimeStampFormat;
import org.activequant.data.retrieval.integration.CandleSeriesSourceBase;
import org.apache.log4j.Logger;

/**
 * 
 * abstracted Interactive Broker Candle series source, requires a properly configured
 * monitor. <br>
 * <br>
 * <b>History:</b><br>
 *  - [24.06.2007] Created (Ulrich Staudinger)<br>
 *  - [11.08.2007] Added array functions (Erik Nijkamp)<br>
 *  - [14.08.2007] removing symbol code, extending from CandleSeriesSourceBase. (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public class IBCandleSeriesSource extends CandleSeriesSourceBase {

	private IBTwsConnection monitor;

	protected final static Logger log = Logger.getLogger(IBCandleSeriesSource.class);

	public IBCandleSeriesSource(IBTwsConnection monitor) {
		this.monitor = monitor;
	}

	public CandleSeries fetch(SeriesSpecification query) throws Exception {
		
		if(query.getTimeFrame() == null) {
			throw new NullPointerException("timeFrame");
		}

		TimeStamp startDate = query.getStartTimeStamp();
		TimeStamp endDate = query.getEndTimeStamp();
		
		if(endDate == null) {
			log.warn("end date not specified: assuming now");
			endDate = new TimeStamp();
		}
		
		if(startDate == null) {
			log.warn("start date not specified: assuming 1 day before end");
			Calendar cal = Calendar.getInstance();
			cal.setTime(endDate.getDate());
			cal.add(Calendar.DAY_OF_YEAR, -1);
			startDate = new TimeStamp(cal.getTime());
		}
		
		String type = "TRADES";
		if(query.getInstrumentSpecification().containsDecoration("type")){
			type = query.getInstrumentSpecification().getDecoration("type");
		}
		
		String barSize = IBTwsConnection.timeFrameToBarSize(query.getTimeFrame());
		return monitor.fetch(startDate, endDate, barSize, type, query);
	}
	
	public String getVendorName() {
		return "IB";
	}
	
	
	public static void main(String ... av) throws Exception {
		IBTwsConnection t = new IBTwsConnection("localhost", 7496, 1108);
		t.setFetchGranularity("1 D");
		
		t.connect();

		Thread.sleep(5000);

		Thread t1 = new Thread(t);
		t1.start();
		
		IBCandleSeriesSource src = new IBCandleSeriesSource(t);
		
		final TimeStampFormat dateFormat = new TimeStampFormat("yyyyMMdd");
		final SeriesSpecification callSpec= new SeriesSpecification(
				TimeFrame.TIMEFRAME_60_MINUTES, 
				new InstrumentSpecification(
						Symbols.DAX, 
						"DTB", 
						"IB", 
						"EUR", 
						"FUT", 
						new Expiry("20071221") 
						)
				);
		callSpec.setStartTimeStamp(dateFormat.parse("20071020"));
		callSpec.setEndTimeStamp(dateFormat.parse("20071030"));
		CandleSeries series = src.fetch(callSpec);
		
		log.info("fetched: " + series.size() + " candles");
	}
}
