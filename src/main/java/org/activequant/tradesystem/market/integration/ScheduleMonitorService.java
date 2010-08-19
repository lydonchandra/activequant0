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
package org.activequant.tradesystem.market.integration;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.Market;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.types.TimeFrame;
import org.activequant.core.types.TimeStamp;
import org.activequant.data.retrieval.ICandleSeriesSource;
import org.activequant.data.retrieval.ICandleSubscriptionSource;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.util.pattern.events.Event;
import org.activequant.util.pattern.events.IEventListener;
import org.activequant.util.scheduling.Scheduler;
import org.activequant.util.scheduling.SchedulerTask;
import org.activequant.util.scheduling.iterators.TimeFrameIterator;
import org.activequant.util.tools.StackTraceParser;
import org.apache.log4j.Logger;

/**
 * Market monitor that is a source of synchronization events (that are assumed
 * to be hooked so that they generate next Candle on every synchronization strobe).
 * It collects the generated Candles, updates the market and distributes it to
 * the market listeners.
 * <p>
 * Optionally, this monitor can be configured to backfill the market. To activate
 * backfill mode, set {@link #setBackfillCandleSource(ICandleSeriesSource) backfillCandleSource}.
 * When backfill mode is active, series specifications 
 * (see {@link #setSeriesSpecifications(SeriesSpecification[]) seriesSPecifications} property).
 * must contain not-null <code>start</code> and <code>end</code> dates, that will
 * determine the backfill period.
 * 
 * <br>
 * <b>History:</b><br>
 *  - [04.06.2007] Created (Erik Nijkamp)<br>
 *  - [02.07.2007] Cleanup (Ulrich Staudinger)<br>
 *  - [01.08.2007] Added backfill mechanism (Ulrich Staudinger)<br>
 *  - [28.09.2007] moved to new domain model (Erik Nijkamp)<br>
 *  - [02.11.2007] Added second constructor, moved initialization code to init() method (Ulrich Staudinger)<br>
 *  - [04.11.2007] Converted to use subscription data feed sources (Mike Kroutikov)<br>
 *
 *  @author Erik Nijkamp
 *  @author Ulrich Staudinger
 *  @author Mike Kroutikov
 */
public class ScheduleMonitorService extends MarketMonitorServiceBase {
	
	private final static Logger log = Logger.getLogger(ScheduleMonitorService.class);
	
	private final Scheduler scheduler = new Scheduler();
	
	private TimeFrame timeFrame;
	
	private final ICandleSubscriptionSource candleSource;
	
	private SeriesSpecification [] seriesSpecifications;
	
	private IEventListener<TimeStamp> [] syncEventListeners;
	
	private ICandleSeriesSource backfillCandleSource = null;
	
	// source of market events
	private final Event<Market> onNewMarketEvent = new Event<Market>();
	
	// source of synchronization events (to force framing of the next Candle)
	private final Event<TimeStamp> syncEvent = new Event<TimeStamp>();
	
	private Market market;
	
	/**
	 * Creates a monitor service with the market evaluation frequency given by
	 * <code>timeFrame</code> and <em>without</em> backfill functionality.
	 * 
	 * @param timeFrame frequency of market evaluation.
	 * @param candleSource source of the candle subscriptions.
	 */
	public ScheduleMonitorService(TimeFrame timeFrame, ICandleSubscriptionSource candleSource) {
		this.timeFrame = timeFrame;
		this.candleSource = candleSource;
	}

	/**
	 * Creates a monitor service with the market evaluation frequency given by
	 * <code>timeFrame</code> and <em>with</em> backfill functionality.
	 * 
	 * @param timeFrame frequency of market evaluation.
	 * @param backfillCandleSource source of backfill data.
	 * @param candleSource source of the candle subscriptions.
	 */
	public ScheduleMonitorService(TimeFrame timeFrame, ICandleSeriesSource backfillCandleSource, ICandleSubscriptionSource candleSource) {
		this.timeFrame = timeFrame;
		this.candleSource = candleSource;
		this.backfillCandleSource = backfillCandleSource; 
	}
	
	/**
	 * Initializes the class. Configure this as bean's "init-method" if
	 * instantiating this via Spring. If not, do not forget to call this 
	 * after class is completely configured.
	 * 
	 * @throws Exception if something goes wrong.
	 */
	public void init() throws Exception {
		// prepare market (and backfill if necessary) 
		prepareMarket();

		// activate the sources. 
		subscribeSources();
	}
	
	/**
	 * Iterates over all instruments and sets-up the subscriptions.
	 * 
	 * @throws Exception
	 */
	private void subscribeSources() throws Exception {

		log.info("Attaching sync listeners: number of listeners=" + syncEventListeners.length);
		
		// attach sync event listeners to our synchronization source
		if(syncEventListeners != null) {
			for(IEventListener<TimeStamp> s : syncEventListeners) {
				syncEvent.addEventListener(s);
			}
		}
		
		log.info("Subscribing for series, length=" + seriesSpecifications.length);
		
		// subscribe to all series
		for(final SeriesSpecification spec : seriesSpecifications){
			
			if(spec.getTimeFrame() != null && !spec.getTimeFrame().equals(timeFrame)) {
				throw new IllegalStateException("inconsistent timeFrame in: " + spec + ", expected: " + timeFrame);
			}
			
			ISubscription<Candle> s = candleSource.subscribe(spec.getInstrumentSpecification(), timeFrame);
			
			s.addEventListener(new IEventListener<Candle> () {
				public void eventFired(Candle candle) throws Exception {
					log.info("updating market with new candle: " + candle);
					market.getCandleSeries(spec).add(0, candle);
				}
			});
			
			s.activate();
		}
	}
	
	private void prepareMarket() throws Exception {
		log.info("Preparing market ...");
		// new market
		CandleSeries[] timeSeries = new CandleSeries[seriesSpecifications.length];
		for(int i = 0; i < timeSeries.length; i++) {
			SeriesSpecification spec = seriesSpecifications[i];
			timeSeries[i] = new CandleSeries(spec);
			if(backfillCandleSource != null) {
				log.info("Backfilling data for " + spec.toString()+ " from candle source (" + backfillCandleSource.getVendorName()+") ... ");
				spec.setEndTimeStamp(new TimeStamp());
				CandleSeries backfillSeries = backfillCandleSource.fetch(spec);
				timeSeries[i] = backfillSeries;
			}
		}
		market = new Market(timeSeries);
		
		log.info("Market preparation complete");
	}
	
	public void setTimeFrame(TimeFrame val) {
		timeFrame = val;
	}
	
	public TimeFrame getTimeFrame() {
		return timeFrame;
	}
	
	public Event<Market> getNewMarketEvent() {
		return onNewMarketEvent;
	}

	public void start() throws Exception {
		
		log.info("starting scheduler");
		
		// scheduler
		scheduler.schedule(new SchedulerTask() {
			@Override
			public void run() {
				try {
					onScheduledEvent();
				} catch (Throwable ex) {
					log.error(StackTraceParser.getStackTrace(ex));
				}
			}
		}, new TimeFrameIterator(timeFrame));
	}
	
	private void onScheduledEvent() throws Exception {
		log.info("Incoming scheduled event.");
		
		// this forces candleSource subscriptions to emit candles
		// and put them into market (see subscribeSources() above).
		syncEvent.fire(new TimeStamp());
		
		// distribute fresh market
		onNewMarketEvent.fire(market);
	}

	public void stop() throws Exception {
		scheduler.cancel();
	}

	public ICandleSeriesSource getBackfillCandleSource() {
		return backfillCandleSource;
	}

	public void setBackfillCandleSource(ICandleSeriesSource backfillCandleSource) {
		this.backfillCandleSource = backfillCandleSource;
	}
	
	public SeriesSpecification [] getSeriesSpecifications() {
		return seriesSpecifications;
	}
	
	public void setSeriesSpecifications(SeriesSpecification [] val) {
		seriesSpecifications = val;
	}
	
	public void setSyncEventListeners(IEventListener<TimeStamp> [] val) {
		syncEventListeners = val;
	}
	
	public IEventListener<TimeStamp> [] getSyncEventListeners() {
		return syncEventListeners;
	}

	public boolean isRunning() {
		throw new UnsupportedOperationException();
	}
}
