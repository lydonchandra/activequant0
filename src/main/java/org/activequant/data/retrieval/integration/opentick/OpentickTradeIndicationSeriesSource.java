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


import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.core.domainmodel.TradeIndicationSeries;
import org.activequant.data.retrieval.ITradeIndicationSeriesSource;
import org.activequant.data.util.UniqueDateGenerator;

import org.otfeed.*;
import org.otfeed.event.*;
import org.otfeed.command.*;

/**
 * Retrieves historical TradeIndication events from OpenTick Corp. (http://www.opentick.com)<br>
 * THIS API IS NOT SUPPORTED BY OpenTick.<br>
 * <br>
 * <b>History:</b><br>
 *  - [14.09.2007] initial code write-up<br>
 *
 *  @author Mike Kroutikov
 */
public class OpentickTradeIndicationSeriesSource extends OpentickSeriesSourceBase<TradeIndicationSeries>  implements ITradeIndicationSeriesSource {

	private boolean compoundVolume = false;

	/**
	 * Controls whether volume reflects the volume on this exchange only,
	 * or is compounded across all exchanges that trade this symbol.
	 * Default is <code>false</code>.
	 * 
	 * @return compound volume flag.
	 */
	public boolean isCompoundVolume() {
		return compoundVolume;
	}
	
	/**
	 * Sets compound volume flag.
	 * 
	 * @param val compound volume flag.
	 */
	public void setCompoundVolume(boolean val) {
		compoundVolume = val;
	}

	/**
	 * Default constructor.
	 */
	public OpentickTradeIndicationSeriesSource() { 
	}
	
	/**
	 * Creates new object and sets {@link #setConnectionFactory} property.
	 * 
	 * @param factory
	 */
	public OpentickTradeIndicationSeriesSource(IConnectionFactory factory) {
		super.setConnectionFactory(factory);
	}

	@Override
	OTRequest<TradeIndicationSeries> submitRequest(final SeriesSpecification query) throws Exception {

		final InstrumentSpecification spec = query.getInstrumentSpecification();

		final TradeIndicationSeries timeSeries = new TradeIndicationSeries(
				new SeriesSpecification(spec));
		
		final UniqueDateGenerator generator = new UniqueDateGenerator();

    	log.info("creating command: " + query);
		HistTicksCommand command = new HistTicksCommand();
		command.setExchangeCode(spec.getExchange());
		command.setSymbolCode(spec.getSymbol().toString());
		command.setStartDate(query.getStartTimeStamp().getDate()); 
		command.setEndDate(query.getEndTimeStamp().getDate());
		if(compoundVolume) {
			command.setVolumeStyle(VolumeStyleEnum.COMPOUND);
		} else {
			command.setVolumeStyle(VolumeStyleEnum.INDIVIDUAL);
		}
		
		command.setTradeDelegate(new IDataDelegate<OTTrade> () {
			public void onData(OTTrade data) {
				TradeIndication tradeIndication = new TradeIndication();
				tradeIndication.setTimeStamp(generator.generate(data.getTimestamp()));
				tradeIndication.setInstrumentSpecification(spec);

				tradeIndication.setPrice(data.getPrice());
				tradeIndication.setQuantity(data.getSize());
				
				timeSeries.add(0, tradeIndication);
			}
		});

		IConnection c = connect();
		IRequest r = c.prepareRequest(command);

		log.info("submitting request");
		r.submit();
		
		return new OTRequest<TradeIndicationSeries>(r, timeSeries);
	}

	@Override
	TradeIndicationSeries[] createArray(int length) {
		return new TradeIndicationSeries[length];
	}
}
