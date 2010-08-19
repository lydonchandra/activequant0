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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Market;
import org.activequant.core.domainmodel.Sample;
import org.activequant.core.types.TimeFrame;
import org.activequant.core.types.TimeStamp;
import org.activequant.data.retrieval.ICandleSubscriptionSource;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.integration.SubscriptionSourceBase;
import org.activequant.tradesystem.market.IDataService;
import org.activequant.tradesystem.market.IPreparationService;
import org.activequant.util.pattern.events.Event;
import org.activequant.util.pattern.events.IEventListener;
import org.apache.log4j.Logger;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [04.06.2007] Created (Erik Nijkamp)<br>
 *  - [07.06.2007] Finished simple implementation (Erik Nijkamp)<br>
 *  - [14.06.2007] Added support for multiple data services (Erik Nijkamp)<br>
 *  - [14.07.2007] Event system upgraded (Erik Nijkamp)<br>
 *  - [28.09.2007] moved to new domain model (Erik Nijkamp)<br>
 *  - [17.10.2007] moved to new domain model, improved logging (Ulrich Staudinger)<br>
 *
 *  @author Erik Nijkamp
 *  @author Ulrich Staudinger
 *  
 */
public class HistoricalMonitorService extends MarketMonitorServiceBase implements ICandleSubscriptionSource {
	
	private final static Logger log = Logger.getLogger(HistoricalMonitorService.class);
	
	// services
	private IDataService[] dataServices;
	private IPreparationService preparationService;
	private Sample sample;
	
	private final Map<InstrumentSpecification,Event<Candle>> subscribedEvents = new ConcurrentHashMap<InstrumentSpecification,Event<Candle>>();
	private final SubscriptionSourceBase<Candle> subscriptionSource = new SubscriptionSourceBase<Candle>("HISTORICAL") {
		
	class CandleSubscription extends Subscription {
			private final InstrumentSpecification spec;
			
			public CandleSubscription(InstrumentSpecification spec) {
				this.spec = spec;
			}

			@Override
			protected void handleActivate() throws Exception {
				final Event<Candle> candleEvent = new Event<Candle>();
				candleEvent.addEventListener(new IEventListener<Candle>() {
					public void eventFired(Candle event) throws Exception {
						fireEvent(event);
					}
				});
				
				subscribedEvents.put(spec,candleEvent);
			}

			@Override
			protected void handleCancel() throws Exception {
				subscribedEvents.remove(spec);
			}
		}
		
		@Override
		protected CandleSubscription createSubscription(
				InstrumentSpecification spec, TimeFrame timeFrame) {
			return new CandleSubscription(spec);
		}
	};
	
	public HistoricalMonitorService() {
	}

	/**
	 * this constructor will use the complete data set for backtesting. 
	 * @param dataService
	 */
	public HistoricalMonitorService(IDataService... dataService) {
		this.dataServices = dataService;
	}

	public HistoricalMonitorService(Sample sample, IDataService... dataService) {
		this(dataService);
		this.sample = sample;
	}

	public HistoricalMonitorService(IPreparationService preparationService, Sample sample,
			IDataService... dataServices) {
		this(sample, dataServices);
		this.preparationService = preparationService;
	}

	/**
	 * @return the dataService
	 */
	public IDataService[] getDataServices() {
		return dataServices;
	}

	/**
	 * @param dataService the dataService to set
	 */
	public void setDataServices(IDataService... dataService) {
		this.dataServices = dataService;
	}
	
	/**
	 * @return the preparationService
	 */
	public IPreparationService getPreparationService() {
		return preparationService;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean hasPreparationService() {
		return preparationService != null;
	}

	/**
	 * @param preparationService the preparationService to set
	 */
	public void setPreparationService(IPreparationService preparationService) {
		this.preparationService = preparationService;
	}
	
	/**
	 * @return the sample
	 */
	public Sample getSample() {
		return sample;
	}

	/**
	 * @param sample the sample to set
	 */
	public void setSample(Sample sample) {
		this.sample = sample;
	}

	public boolean isRunning() {
		return false;
	}

	public void start() throws Exception {
		// result
		Market market = new Market();
		
        // get some market data      
        log.info("# Reading data. #");
        for(IDataService service: getDataServices()) {
            Market loadedMarket = service.loadMarket();
            market.addCandleSeries(loadedMarket.getCandleSeries());
        }

        log.info("Loaded "+market.getCandleSeries()[0].size()+" candles (first symbol).");        
       
        // polish data
        if (hasPreparationService()) {
			log.info("# Preparing data. #");
			market = getPreparationService().prepareMarket(market);
			log.info("Prepared " + market.getCandleSeries()[0].size()
					+ " candles (first symbol).");
		}
        
        // building markets
        log.info(" # Build markets. #");
        Market[] markets = fragmentMarkets(market);
        
        log.info(" # markets built (" + markets.length + ") entries" );
        
        log.info(" # Evaluate markets. #");
        int index = 0; 
        int maxIndex = markets.length;
        for(Market current: markets) {
        	index++;
			if(index % 100 == 0){
				log.info("Evaluated "+(index)+"/"+(maxIndex));
			}
        
			// distribute the latest candles for order execution.
			distributeLatestCandles(current);
			// evaluate ... 
        	onNewMarketEvent.fire(current);
		}
	}
	
	private void distributeLatestCandles(Market market) throws Exception {
		for(CandleSeries cs : market.getCandleSeries()) {
			Event<Candle> candleEvent = subscribedEvents.get(cs.getInstrumentSpecification());
			if(candleEvent != null) {
				candleEvent.fire(cs.get(0));
			}
		}
	}
	
	private Sample getRefSample(CandleSeries refSeries){ 
		// create a new sample. 
		Sample refSample = new Sample(refSeries.get(refSeries.size()-1).getTimeStamp(), refSeries.get(0).getTimeStamp());
		return refSample; 
	}
	
	private Market[] fragmentMarkets(Market market) throws Exception {
		assert(market.getCandleSeries().length > 0);
		
		// reference timeseries
		CandleSeries refTs = market.getCandleSeries()[0];

		// get dates
		TimeStamp base = refTs.lastElement().getTimeStamp();
		
		// checking if sample is null. 
		if(sample == null){
			sample = getRefSample(market.getCandleSeries()[0]);
		}
		
		TimeStamp oldest = sample.getStartTimeStamp();
		TimeStamp newest = sample.getEndTimeStamp();
		TimeStamp oldestTemp = oldest;
		oldest = newest;
		newest = oldestTemp;
		
		log.info("Fragmenting the market: newestDate=" + newest + ", oldestDate=" + oldest);
		
		CandleSeries cs = new CandleSeries();
		int csSize = refTs.size();int x = 5;
		List<Candle> csList = new ArrayList<Candle>(csSize);
		
		if(refTs.get(0).getTimeStamp().compareTo(refTs.get(refTs.size() - 1).getTimeStamp()) < 0) {
			ListIterator<Candle> iter = refTs.listIterator();
			//int idx = csSize-1;
			
			while( iter.hasNext() ) { iter.next(); }
			
			while( iter.hasPrevious() ) {
				
				Candle candle = iter.previous();				
				csList.add(candle);				
			}
			
			cs.addAll(csList);
			refTs = cs;
			//throw new AssertionError("sort order is wrong");
		}
		
		// positions
		int newestPos = refTs.getTimeStampPosition(newest,
				CandleSeries.PositionPolicy.FEASIBLE);
		int oldestPos = refTs.getTimeStampPosition(oldest,
				CandleSeries.PositionPolicy.FEASIBLE);
		
		log.info("Fragmenting the market: newestPos=" + newestPos + ", oldestPos=" + oldestPos);

		// markets
		List<Market> markets = new ArrayList<Market>();

		// iterate through timespans
		for (int i = oldestPos; i >= newestPos; i--) {
			TimeStamp currentNewest = refTs.get(i).getTimeStamp();
			List<CandleSeries> partialSeriesList = new ArrayList<CandleSeries>();
			for (CandleSeries series : market.getCandleSeries()) {
				// partial series
				CandleSeries partialSeries = series.subList(base, currentNewest,
						CandleSeries.RangePolicy.ALL);
				partialSeries.setSeriesSpecification(series.getSeriesSpecification());						
				// add
				partialSeriesList.add(partialSeries);
			}
			Market partialMarket = new Market(oldest, currentNewest,
					partialSeriesList.toArray(new CandleSeries[] {}));

			partialMarket.setMarketTimeStamp(currentNewest);
			markets.add(partialMarket);
		}

		return markets.toArray(new Market[] {});
	}

	public void stop() throws Exception {
	}
	
	public ISubscription<Candle>[] getSubscriptions() {
		return subscriptionSource.getSubscriptions();
	}

	public String getVendorName() {
		return subscriptionSource.getVendorName();
	}

	public ISubscription<Candle> subscribe(InstrumentSpecification spec,
			TimeFrame timeFrame) throws Exception {
		return subscriptionSource.subscribe(spec, timeFrame);
	}	
	
}