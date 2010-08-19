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
package org.activequant.data.retrieval.integration.opentick;


import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.types.TimeFrame;
import org.activequant.core.types.TimeStamp;
import org.activequant.data.retrieval.ICandleSeriesSource;

import org.otfeed.*;
import org.otfeed.event.*;
import org.otfeed.command.*;

/**
 * Retrieves historical Candle data from OpenTick Corp. (http://www.opentick.com)<br>
 * THIS API IS NOT SUPPORTED BY OpenTick.<br>
 * <br>
 * <b>History:</b><br>
 *  - [14.09.2007] initial code write-up<br>
 *
 *  @author Mike Kroutikov
 */
public class OpentickCandleSeriesSource extends OpentickSeriesSourceBase<CandleSeries> implements ICandleSeriesSource {

	/**
	 * Default constructor.
	 */
	public OpentickCandleSeriesSource() { 
	}
	
	/**
	 * Creates new object and sets {@link #setConnectionFactory} property.
	 * 
	 * @param factory
	 */
	public OpentickCandleSeriesSource(IConnectionFactory factory) {
		super.setConnectionFactory(factory);
	}

	private static AggregationSpan convertToAggregationSpan(TimeFrame timeFrame) {
		switch(timeFrame.unit) {
		case MINUTE:  
			return AggregationSpan.minutes(timeFrame.length);
		case HOUR:  
			return AggregationSpan.hours(timeFrame.length);
		case DAY:      
			return AggregationSpan.days(timeFrame.length);
		case WEEK:     
			return AggregationSpan.weeks(timeFrame.length);
		}
		throw new IllegalArgumentException("unsupported time frame value: " + timeFrame);
	}
    
	@Override
	OTRequest<CandleSeries> submitRequest(final SeriesSpecification query) throws Exception {

		AggregationSpan span = convertToAggregationSpan(query.getTimeFrame());
    	
		final InstrumentSpecification spec = query.getInstrumentSpecification();
		final CandleSeries timeSeries = new CandleSeries();
		timeSeries.setSeriesSpecification(query);

    	log.info("creating command: " + query + ", span=" + span);
		HistDataCommand command = new HistDataCommand();
		command.setExchangeCode(spec.getExchange());
		command.setSymbolCode(spec.getSymbol().toString());
		command.setStartDate(query.getStartTimeStamp().getDate());
		command.setEndDate(query.getEndTimeStamp().getDate());
		command.setAggregationSpan(span);
		
		command.setDataDelegate(new IDataDelegate<OTOHLC> () {
			public void onData(OTOHLC data) {
				Candle candle = new Candle();
				candle.setTimeStamp(new TimeStamp(data.getTimestamp()));
				candle.setOpenPrice(data.getOpenPrice());
				candle.setHighPrice(data.getHighPrice());
				candle.setLowPrice(data.getLowPrice());
				candle.setClosePrice(data.getClosePrice());
				candle.setVolume(data.getVolume());
				// hi date and low date should be set to date if they are not available from a source.
				candle.setHighTimeStamp(candle.getTimeStamp());
				candle.setLowTimeStamp(candle.getTimeStamp());
				candle.setInstrumentSpecification(query.getInstrumentSpecification());
				candle.setTimeFrame(query.getTimeFrame());
				timeSeries.add(0, candle);
			}
		});

		IConnection c = connect();
		IRequest r = c.prepareRequest(command);

		log.info("submitting request");
		r.submit();
		
		return new OTRequest<CandleSeries>(r, timeSeries);
	}

	@Override
	CandleSeries[] createArray(int length) {
		return new CandleSeries[length];
	}
}
