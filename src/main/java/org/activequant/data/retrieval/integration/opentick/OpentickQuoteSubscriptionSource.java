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
import org.activequant.core.types.TimeFrame;
import org.activequant.data.retrieval.IQuoteSubscriptionSource;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.integration.SubscriptionSourceBase;
import org.activequant.data.util.UniqueDateGenerator;
import org.activequant.util.exceptions.SubscriptionException;
import org.otfeed.IConnection;
import org.otfeed.IConnectionFactory;
import org.otfeed.IRequest;
import org.otfeed.command.TickStreamCommand;
import org.otfeed.command.VolumeStyleEnum;
import org.otfeed.event.IConnectionStateListener;
import org.otfeed.event.IDataDelegate;
import org.otfeed.event.OTBBO;
import org.otfeed.event.OTError;
import org.otfeed.event.OTHost;
import org.otfeed.event.OTMMQuote;
import org.otfeed.event.OTQuote;

/**
 * Provides feed of the real-time Quote events from Opentick Corp.
 * Note that Trade events are ignored (because these are not quotes).
 * Opentick's Quotes are reported as activequant Quotes. Optionally, this service 
 * can be configured to also include Opentick's MMQuotes (market-maker quotes), and
 * Opentick's BBO (best bid offer) events. See below {@link #isReportMmQuote() reportMmQuote} 
 * and {@link #isReportBbo() reportBbo}.
 * <br>
 * <b>History:</b><br> 
 * - [10.09.2007] Created (Ulrich Staudinger)<br>
 * - [28.09.2007] Initial implementation (Mike Kroutikov)<br>
 * - [07.10.2007] Many changes, split Trade quotes off (Mike Kroutikov)<br>
 * - [04.11.2007] Conversion to data2 retrieval model (Mike Kroutikov)<br> 
 * 
 * @author Ulrich Staudinger
 * @author Mike Kroutikov
 */
public class OpentickQuoteSubscriptionSource extends SubscriptionSourceBase<Quote> implements IQuoteSubscriptionSource {
	
	private IConnectionFactory factory;
	private boolean reportBbo = false;
	private boolean reportMmQuote = false;
	
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
	public OpentickQuoteSubscriptionSource() { 
		super("OPENTICK");
	}


	/**
	 * Creates object setting 
	 * {@link #setConnectionFactory(IConnectionFactory) connectionFactory} value.
	 * 
	 * @param val reference to connection factory.
	 */
	public OpentickQuoteSubscriptionSource(IConnectionFactory val) {
		this();
		factory = val;
	}
	
	/**
	 * Sets the connection factory. <br>
	 * <em>IMPORTANT!</em>: Always use OTPooledConnectionFactory here (not raw OTConnectionFactory).
	 * This is needed to support multiple subscriptions. Every subscription will
	 * open its own connection. OTPooledConnectionFactory uses a single underlying 
	 * connection, conserving the resources and making the quote source more robust.
	 * 
	 * @param val connection factory.
	 */
	public void setConnectionFactory(IConnectionFactory val) {
		factory = val;
	}
	
	/**
	 * Returns connection factory.
	 * 
	 * @return factory.
	 */
	public IConnectionFactory getConnectionFactory() {
		return factory;
	}

	/**
	 * Controls whether BBO (best buy offer) events are reported
	 * as quote. Default is <code>false</code>. Setting it to <code>true</code>
	 * will make the driver accept BBO events.
	 * <p>
	 * BBO is "half" of a quote (only bid half or ask half).
	 * It is converted to Quote by leaving the other side unset.
	 * 
	 * @param val reportBBO value.
	 */
	public void setReportBbo(boolean val) {
		reportBbo = val;
	}
	
	public boolean isReportBbo() {
		return reportBbo;
	}

	/**
	 * Controls whether MmQuote (market-maker quote) events are reported
	 * as quote. Default is <code>false</code>. Setting it to <code>true</code>
	 * will make the driver accept Opentick's MmQuote events.
	 *
	 * @param val reportMmQuote value.
	 */
	public void setReportMmQuote(boolean val) {
		reportMmQuote = val;
	}
	
	public boolean isReportMmQuote() {
		return reportMmQuote;
	}

	private class QuoteSubscription extends Subscription {
		
		private final IConnection connection; 
		private final IRequest request;
		private final UniqueDateGenerator generator = new UniqueDateGenerator();

		public QuoteSubscription(final InstrumentSpecification spec, 
				final IConnectionFactory factory) {

			connection = factory.connect(new IConnectionStateListener() {

				public void onConnected() {
					log.info("connected");
				}

				public void onConnecting(OTHost host) {
					log.info("connecting to: " + host);
				}

				public void onError(OTError error) {
					log.info("error: " + error);
				}

				public void onLogin() {
					log.info("logged in");
				}

				public void onRedirect(OTHost host) {
					log.info("redirected to: " + host);
				}
			});

			TickStreamCommand command = new TickStreamCommand();
			command.setExchangeCode(spec.getExchange());
			command.setSymbolCode(spec.getSymbol().toString());
			if(compoundVolume) {
				command.setVolumeStyle(VolumeStyleEnum.COMPOUND);
			} else {
				command.setVolumeStyle(VolumeStyleEnum.INDIVIDUAL);
			}
			
			command.setQuoteDelegate(new IDataDelegate<OTQuote>() {
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
					
					fireEvent(quote);
				}
			});

			if(reportMmQuote) {
				command.setMmQuoteDelegate(new IDataDelegate<OTMMQuote>() {
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

						fireEvent(quote);
					}
				});
			}
			
			if(reportBbo) {
				log.info("registering bbo delegate");
				command.setBboDelegate(new IDataDelegate<OTBBO>() {
					public void onData(OTBBO data) {
						Quote quote = new Quote();
						quote.setTimeStamp(generator.generate(data.getTimestamp()));
						quote.setInstrumentSpecification(spec);
						
						switch(data.getSide()) {
						case BUYER:
							if(data.getPrice()!=0.0)
								quote.setBidPrice(data.getPrice());
							else 
								quote.setBidPrice(Quote.NOT_SET);
							quote.setBidQuantity(data.getSize());
							break;
						case SELLER:
							if(data.getPrice()!=0.0)
								quote.setAskPrice(data.getPrice());
							else 
								quote.setAskPrice(Quote.NOT_SET);
							quote.setAskQuantity(data.getSize());
							break;
						}

						fireEvent(quote);
					}
				});

			}
			
			request = connection.prepareRequest(command);
		}
		
		@Override
		protected void handleActivate() {
			request.submit();
		}

		@Override
		protected void handleCancel() {
			request.cancel();
			connection.shutdown();
			connection.waitForCompletion();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void close() throws SubscriptionException {
		super.close();
	}

	@Override
	protected QuoteSubscription createSubscription(
			InstrumentSpecification spec, TimeFrame timeFrame) {
		if(factory == null) {
			throw new IllegalStateException("factory not set");
		}
		return new QuoteSubscription(spec, factory);
	}

	public ISubscription<Quote> subscribe(InstrumentSpecification spec)
			throws Exception {
		return super.subscribe(spec, TimeFrame.TIMEFRAME_1_TICK);
	}
}
