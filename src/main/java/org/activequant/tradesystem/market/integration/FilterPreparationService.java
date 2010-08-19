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

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.Market;
import org.activequant.data.preparation.IChain;
import org.activequant.tradesystem.market.IPreparationService;
import org.activequant.util.pattern.events.Event;
import org.apache.log4j.Logger;



/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [09.06.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class FilterPreparationService implements IPreparationService {
	
	private final static Logger log = Logger.getLogger(FilterPreparationService.class);
	
	private Event<Market> onMarketPrepared = new Event<Market>();
	private IChain[] phases;
	
	public FilterPreparationService() {
		
	}
	
	public FilterPreparationService(IChain... phases) {
		this.phases = phases;
	}

	public IChain[] getFilterPhases() {
		return phases;
	}
	
	public void setFilterPhases(IChain[] phases) {
		this.phases = phases;
	}

	public CandleSeries[] prepareSeries(CandleSeries... series) throws Exception {
		// apply
		log.info("Preparing series data ");
		for(IChain phase: phases) {
			series = phase.process(series);
		}
		log.info("Prepared series data.");
		return series;
	}

	public Market prepareMarket(Market market) throws Exception {
		CandleSeries[] series = prepareSeries(market.getCandleSeries());
		market.setCandleSeries(series);
		onMarketPrepared.fire(market);	
		return market;
	}

	public Event<Market> getMarketPreparedEvent() {
		return onMarketPrepared;
	}
}
