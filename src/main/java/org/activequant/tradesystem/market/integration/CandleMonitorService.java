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

import java.util.LinkedList;
import java.util.List;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.Market;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.data.retrieval.ICandleSubscriptionSource;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.util.pattern.events.IEventListener;
import org.apache.log4j.Logger;



/**
 * Monitor that activates subscription for a set of instruments and
 * generates new market event on every candle update.
 * <br>
 * <b>History:</b><br>
 *  - [04.06.2007] Created (Erik Nijkamp)<br>
 *  - [24.06.2007] Minor safety scans (Ulrich Staudinger)<br>
 *  - [25.06.2007] Added thread safety (Erik Nijkamp)<br> 
 *  - [28.09.2007] moved to new domain model (Erik Nijkamp)<br>
 *  - [10.11.2007] refactored to subscription data model (Mike Kroutikov)<br>
 *
 *  @author Erik Nijkamp
 *  @author Ulrich Staudinger
 *  @author Mike Kroutikov
 */
public class CandleMonitorService extends MarketMonitorServiceBase {
	
	private final static Logger log = Logger.getLogger(CandleMonitorService.class);
	
	private ICandleSubscriptionSource candleSource;
	private Market market;
	
	public CandleMonitorService(ICandleSubscriptionSource candleSource) {
		this.candleSource = candleSource;
	}
	
	private SeriesSpecification [] seriesSpecs;

	public SeriesSpecification[] getSeriesSpecifications() {
		return seriesSpecs;
	}

	public void setSeriesSpecifications(SeriesSpecification [] val) {
		seriesSpecs = val;
	}
	
	private synchronized void onNewCandleEvent(SeriesSpecification specification, Candle candle) throws Exception {
		log.info("[onNewCandleEvent] Incoming candle.");
		if(!market.containsCandleSeries(specification)){
			// no time series exist. 
			log.warn("[onNewCandleEvent] timeseries does not yet exist. ");
			// create a new time series for it. 
			CandleSeries cs = new CandleSeries();
			market.addCandleSeries(0, cs);
		} 
		market.getCandleSeries(specification).add(0, candle);
		onNewMarketEvent.fire(market);
	}

	public boolean isRunning() {
		return false;
	}

	private final List<ISubscription<Candle>> subscriptions = new LinkedList<ISubscription<Candle>>();
	
	public void start() throws Exception {
		for (final SeriesSpecification ss : seriesSpecs) {
			ISubscription<Candle> subscription = candleSource.subscribe(ss.getInstrumentSpecification(), ss.getTimeFrame());
			subscriptions.add(subscription);
			
			subscription.addEventListener(new IEventListener<Candle>() {
				public void eventFired(Candle candle) throws Exception {
					onNewCandleEvent(ss, candle);
				}
			});
			subscription.activate();
		}
	}

	public void stop() throws Exception {
		for(ISubscription<Candle> s : subscriptions) {
			s.cancel();
		}
	}	
}