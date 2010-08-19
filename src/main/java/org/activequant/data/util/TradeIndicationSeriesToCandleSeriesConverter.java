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
import java.util.TimeZone;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.core.domainmodel.TradeIndicationSeries;
import org.activequant.core.types.TimeFrame;
import org.activequant.core.types.TimeStamp;
import org.activequant.data.retrieval.ICandleSeriesSource;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.ITradeIndicationSeriesSource;
import org.activequant.data.retrieval.ITradeIndicationSubscriptionSource;
import org.activequant.util.exceptions.SubscriptionException;
import org.activequant.util.pattern.events.Event;
import org.activequant.util.pattern.events.IEventListener;
import org.apache.log4j.Logger;

/**
 * Converts TradeIndicationSeries to CandleSeries. Request to fetch Candles
 * is converted to one or more requests to fetch TradeIndications. Candles
 * are built from these TradeIndications and returned to the caller.
 * <p>
 * Implementation uses {@link TradeIndicationToCandleConverter} class to do the actual conversion.
 * <br>
 * <b>History:</b><br>
 *  - [22.11.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class TradeIndicationSeriesToCandleSeriesConverter implements ICandleSeriesSource {
	
	private final Logger log = Logger.getLogger(getClass());

	private final ITradeIndicationSeriesSource tradeIndicationSeriesSource;
	private TimeFrame limitTimeFrame = TimeFrame.TIMEFRAME_1_DAY;
	
	public TradeIndicationSeriesToCandleSeriesConverter(ITradeIndicationSeriesSource source) {
		this.tradeIndicationSeriesSource = source;
	}
	
	private final Event<TradeIndication> tradeIndicationEvent = new Event<TradeIndication>();
	private final TradeIndicationToCandleConverter converter = new TradeIndicationToCandleConverter(new ITradeIndicationSubscriptionSource() {

		public ISubscription<TradeIndication>[] getSubscriptions() {
			return null;
		}

		public String getVendorName() {
			return null;
		}

		public ISubscription<TradeIndication> subscribe(
				final InstrumentSpecification spec) throws Exception {
			return new ISubscription<TradeIndication>() {

				public void activate() throws SubscriptionException {
					// TODO Auto-generated method stub
					
				}

				public void addEventListener(
						IEventListener<TradeIndication> listener) {
					tradeIndicationEvent.addEventListener(listener);
				}

				public void cancel() throws SubscriptionException {
				}

				public InstrumentSpecification getInstrumentSpecification() {
					return spec;
				}

				public TimeFrame getTimeFrame() {
					return TimeFrame.TIMEFRAME_1_TICK;
				}

				public String getVendorName() {
					return null;
				}

				public boolean isActive() {
					return false;
				}

				public void removeEventListener(
						IEventListener<TradeIndication> listener) {
					tradeIndicationEvent.removeEventListener(listener);
				}
			};
		}
	});
	
	public CandleSeries fetch(SeriesSpecification seriesSpecification)
			throws Exception {
		TimeFrame timeFrame = seriesSpecification.getTimeFrame();
		if(timeFrame == null) {
			throw new IllegalArgumentException("series specification must have time frame set");
		}

		final CandleSeries cs = new CandleSeries(seriesSpecification);

		ISubscription<Candle> candleSubscription = converter.subscribe(seriesSpecification.getInstrumentSpecification(), seriesSpecification.getTimeFrame());
		candleSubscription.addEventListener(new IEventListener<Candle>() {
			public void eventFired(Candle candle) throws Exception {
				log.info("incoming candle: " + candle);
				cs.add(0, candle);
			}
		});
		candleSubscription.activate();

		// to avoid fetching too much in one request,
		// split one Candle fetch into many TradeIndication fetches.
		TimeStamp startDate = seriesSpecification.getStartTimeStamp();
		TimeStamp endDate   = seriesSpecification.getEndTimeStamp();
		
		log.info("request to fetch candles from: " + startDate + " to " + endDate + " with limitTimeFrame " + limitTimeFrame);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		calendar.setTime(startDate.getDate());
		limitTimeFrame.alignCalendar(calendar);
		
		while(calendar.getTime().before(endDate.getDate())) {
			TimeStamp start = new TimeStamp(calendar.getTime());
			if(start.isBefore(startDate)) start = startDate;
			
			limitTimeFrame.addToCalendar(calendar);
			calendar.add(Calendar.SECOND, -1);
			TimeStamp end = new TimeStamp(calendar.getTime());
			if(end.isAfter(endDate)) end = endDate;
			
			calendar.add(Calendar.SECOND, 1);
			
			SeriesSpecification tsSpec = new SeriesSpecification(seriesSpecification.getInstrumentSpecification());
			tsSpec.setStartTimeStamp(start);
			tsSpec.setEndTimeStamp(end);
			
			log.info("fetching trade indications from: " + tsSpec.getStartTimeStamp() + " to " + tsSpec.getEndTimeStamp());
			
			TradeIndicationSeries tsSeries = tradeIndicationSeriesSource.fetch(tsSpec);
			
			log.info("fetched " + tsSeries.size() + " trade indications");
			if(tsSeries.size() == 0) {
				continue;
			}
			
			for(TradeIndication ti : tsSeries) {
				tradeIndicationEvent.fire(ti);
			}
		}
		
		candleSubscription.cancel();
		
		log.info("returning candle series: size=" + cs.size());
		for(Candle c : cs) {
			System.out.println("spec=" + c.getInstrumentSpecification());
			System.out.println(c);
		}
		
		return cs;
	}
	
	public CandleSeries[] fetch(SeriesSpecification... seriesSpecification)
			throws Exception {
		CandleSeries [] out = new CandleSeries[seriesSpecification.length];
		for(int i = 0; i < out.length; i++) {
			out[i] = fetch(seriesSpecification[i]);
		}
		
		return out;
	}

	public String getVendorName() {
		return tradeIndicationSeriesSource.getVendorName();
	}
	
	/**
	 * Determines the longest fetch interval to request from tradeIndicationSeries source.
	 * If requested Candle interval is greater than limit, we split it into several
	 * requests to the tradeIndicationSeries source, each one no longer than the limit.
	 * This is important, because otherwise large candle intervals may cause memory
	 * overflow problems.
	 * 
	 * @return limit.
	 */
	public TimeFrame getLimitTimeFrame() { 
		return limitTimeFrame;
	}

	/**
	 * Sets the fetching limit.
	 * 
	 * @param val limit.
	 */
	public void setLimitTimeFrame(TimeFrame val) { 
		limitTimeFrame = val;
	}
}