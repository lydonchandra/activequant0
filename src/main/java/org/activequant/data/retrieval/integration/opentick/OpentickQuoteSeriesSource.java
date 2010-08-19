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
import org.activequant.core.domainmodel.Quote;
import org.activequant.core.domainmodel.QuoteSeries;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.data.retrieval.IQuoteSeriesSource;
import org.activequant.data.util.UniqueDateGenerator;
import org.apache.log4j.Logger;

import org.otfeed.*;
import org.otfeed.event.*;
import org.otfeed.command.*;

/**
 * Retrieves historical Quote events from OpenTick Corp. (http://www.opentick.com)<br>
 * THIS API IS NOT SUPPORTED BY OpenTick.<br>
 * <br>
 * <b>History:</b><br>
 *  - [14.09.2007] initial code write-up<br>
 *
 *  @author Mike Kroutikov
 */
public class OpentickQuoteSeriesSource extends OpentickSeriesSourceBase<QuoteSeries> implements IQuoteSeriesSource {
	
	private final Logger log = Logger.getLogger(getClass());
	
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
	public OpentickQuoteSeriesSource() { 
	}
	
	/**
	 * Creates new object and sets {@link #setConnectionFactory} property.
	 * 
	 * @param factory
	 */
	public OpentickQuoteSeriesSource(IConnectionFactory factory) {
		super.setConnectionFactory(factory);
	}
	
	private boolean useMmQuotes = false;
	
	/**
	 * Determines whether Opentick's MMQuote (Market-maker quote) events
	 * are used for the feed. Default is <code>false</code>.
	 * 
	 * @param val useMmQuote value.
	 */
	public void setUseMmQuotes(boolean val) {
		useMmQuotes = val;
	}
	public boolean isUseMmQuotes() {
		return useMmQuotes;
	}

	private boolean useBbos = false;

	/**
	 * Determines whether Opentick's BBO (Best bid offer) events
	 * are used for the feed. Default is <code>false</code>.
	 * If used, only half of the activequat's Quote will be initialized
	 * (because BBO is "half" of the quote: only ask or only bid size).
	 * Therefore, when this feature is enabled, make sure that
	 * consumers of the feed are prepared to deal with partially
	 * initialized quotes.
	 * 
	 * @param val useMmQuote value.
	 */
	public void setUseBbos(boolean val) {
		useBbos = val;
	}
	public boolean isUseBbos() {
		return useBbos;
	}

	@Override
	OTRequest<QuoteSeries> submitRequest(final SeriesSpecification query) throws Exception {

		log.debug("Submitting request to opentick.");
		
		final InstrumentSpecification spec = query.getInstrumentSpecification();

		final QuoteSeries timeSeries = new QuoteSeries();
		timeSeries.setSeriesSpecification(query);

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
		
		command.setQuoteDelegate(new IDataDelegate<OTQuote> () {
			public void onData(OTQuote data) {
				Quote quote = new Quote();
				quote.setTimeStamp(generator.generate(data.getTimestamp()));
				quote.setInstrumentSpecification(spec);

				if(data.getAskPrice()!=0.0)
					quote.setAskPrice(data.getAskPrice());
				else 
					quote.setAskPrice(Quote.NOT_SET);
				quote.setAskQuantity(data.getAskSize());
				
				if(data.getBidPrice()!=0.0)
					quote.setBidPrice(data.getBidPrice());
				else 
					quote.setBidPrice(Quote.NOT_SET);
				quote.setBidQuantity(data.getBidSize());
				
				timeSeries.add(0, quote);
			}
		});
		
		if(useMmQuotes) {
			command.setMmQuoteDelegate(new IDataDelegate<OTMMQuote> () {
				public void onData(OTMMQuote data) {
					Quote quote = new Quote();
					quote.setTimeStamp(generator.generate(data.getTimestamp()));
					quote.setInstrumentSpecification(spec);
					
					if(data.getAskPrice()!=0.0)
						quote.setAskPrice(data.getAskPrice());
					else 
						quote.setAskPrice(Quote.NOT_SET);
					quote.setAskQuantity(data.getAskSize());
					
					if(data.getBidPrice()!=0.0)
						quote.setBidPrice(data.getBidPrice());
					else 
						quote.setBidPrice(Quote.NOT_SET);
					quote.setBidQuantity(data.getBidSize());
					
					timeSeries.add(0, quote);
				}
			});
			
		}

		if(useBbos) {
			command.setBboDelegate(new IDataDelegate<OTBBO> () {
				public void onData(OTBBO data) {
					Quote quote = new Quote();
					quote.setTimeStamp(generator.generate(data.getTimestamp()));
					quote.setInstrumentSpecification(spec);

					if(data.getSide().equals(TradeSideEnum.SELLER)) {
						if(data.getPrice()!=0.0)
							quote.setAskPrice(data.getPrice());
						else 
							quote.setAskPrice(Quote.NOT_SET);
						quote.setAskQuantity(data.getSize());
					} else if(data.getSide().equals(TradeSideEnum.BUYER)) {
						if(data.getPrice()!=0.0)
							quote.setBidPrice(data.getPrice());
						else 
							quote.setBidPrice(Quote.NOT_SET);
						quote.setBidQuantity(data.getSize());
					} else {
						return; // ignore garbage
					}
					
					timeSeries.add(0, quote);
				}
			});
		}

		IConnection c = connect();
		IRequest r = c.prepareRequest(command);

		log.info("submitting request");
		r.submit();
		
		return new OTRequest<QuoteSeries>(r, timeSeries);
	}

	@Override
	QuoteSeries[] createArray(int length) {
		return new QuoteSeries[length];
	}
}
