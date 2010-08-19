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

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Market;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.domainmodel.Symbol;
import org.activequant.data.retrieval.ICandleSeriesSource;
import org.activequant.tradesystem.market.IDataService;
import org.activequant.util.pattern.events.Event;
import org.apache.log4j.Logger;



/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [08.06.2007] Created (Erik Nijkamp)<br>
 *  - [11.07.2007] Switched to SeriesSpecification in constructor (Ulrich Staudinger)<br>
 *  - [04.08.2007] Added events, logging (Erik Nijkamp)<br>
 *  - [25.09.2007] Switch to InstrumentQuery etc. (Erik Nijkamp) <br>
 *  - [28.09.2007] moved to new domain model (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 *  @author Ulrich Staudinger
 */
public class CandleDataService implements IDataService {
	
	private final static Logger log = Logger.getLogger(CandleDataService.class);
	
	private Event<Market> marketLoadedEvent = new Event<Market>();
    private SeriesSpecification[] specifications;
    private ICandleSeriesSource candleSeriesSource;
    
	public CandleDataService() {
		
	}
	
	public CandleDataService(ICandleSeriesSource candleSeriesSource) {
		this.candleSeriesSource = candleSeriesSource;
	}
	
	public CandleDataService(ICandleSeriesSource candleSeriesSource, SeriesSpecification... specs) {
		this(candleSeriesSource);
		this.specifications = specs;
	}

	public CandleDataService(ICandleSeriesSource candleSeriesSource, Symbol... symbols) {
		this(candleSeriesSource);
		this.setSymbols(symbols);
	}
	
	public Event<Market> getMarketLoadedEvent() {
		return marketLoadedEvent;
	}

	/**
	 * @return the candleSeriesSource
	 */
	public ICandleSeriesSource getCandleSeriesSource() {
		return candleSeriesSource;
	}

	/**
	 * @param candleSeriesSource the candleSeriesSource to set
	 */
	public void setCandleSeriesSource(ICandleSeriesSource candleSeriesSource) {
		this.candleSeriesSource = candleSeriesSource;
	}

    /**
     * fetch several symbols.
     */
	public Market loadMarket() throws Exception {
    	// read market
		Market market = new Market(loadSeries());
		marketLoadedEvent.fire(market);
        return market; 
	}

	private CandleSeries[] loadSeries() throws Exception {
		// precheck
		assert(specifications != null && candleSeriesSource != null);
		// load
		log.info("Loading series data ...");
		List<CandleSeries> series = new ArrayList<CandleSeries>();
		for (SeriesSpecification spec : specifications) {
			CandleSeries candles = candleSeriesSource.fetch(spec);
			candles.setSeriesSpecification(spec);
			series.add(candles);
		}
		log.info("Series data loaded");
		return series.toArray(new CandleSeries[] {});
	}

	public SeriesSpecification[] getSpecifications() {
		return specifications;
	}

	public void setSpecifications(SeriesSpecification... specifications) {
		this.specifications = specifications;
	}
	
	public void setSymbols(Symbol... symbols) {
		SeriesSpecification[] specs = new SeriesSpecification[symbols.length];
		for(int i = 0; i < symbols.length; i++) {
			SeriesSpecification spec = new SeriesSpecification(new InstrumentSpecification(symbols[i]));
			specs[i] = spec;
		}
		this.specifications = specs;
	}
}
