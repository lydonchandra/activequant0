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
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.core.types.TimeFrame;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.ITradeIndicationSubscriptionSource;
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
import org.otfeed.event.OTError;
import org.otfeed.event.OTHost;
import org.otfeed.event.OTTrade;

/**
 * Provides real-time TradeIndication feed from Opentick Corp.
 * Only "Trade" events are reported as ticks. Quotes, Market-maker quotes, 
 * Best Bid Offers are not reported as ticks (because these are not trades).
 * See {@link OpentickQuoteSubscriptionSource} for real-time feed of the Quotes. 
 * <br>
 * <b>History:</b><br> 
 * - [10.09.2007] Created (Ulrich Staudinger)<br>
 * - [28.09.2007] Initial implementation (Mike Kroutikov)<br>
 * - [04.11.2007] Conversion to data2 retrieval model (Mike Kroutikov)<br> 
 * 
 * @author Ulrich Staudinger
 * @author Mike Kroutikov
 */
public class OpentickTradeIndicationSubscriptionSource extends SubscriptionSourceBase<TradeIndication> implements ITradeIndicationSubscriptionSource {
	
	private IConnectionFactory factory;
	
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
	public OpentickTradeIndicationSubscriptionSource() { 
		super("OPENTICK");
	}

	/**
	 * Creates object setting 
	 * {@link #setConnectionFactory(IConnectionFactory) connectionFactory} value.
	 * 
	 * @param val reference to connection factory.
	 */
	public OpentickTradeIndicationSubscriptionSource(IConnectionFactory val) {
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

	private class TradeSubscription extends Subscription {
		
		private final IConnection connection; 
		private final IRequest request;
		private final UniqueDateGenerator generator = new UniqueDateGenerator();

		public TradeSubscription(final InstrumentSpecification spec, 
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
			
			log.info("registering trade delegate");
			command.setTradeDelegate(new IDataDelegate<OTTrade>() {
				public void onData(OTTrade data) {
					TradeIndication tradeIndication = new TradeIndication();
					tradeIndication.setTimeStamp(generator.generate(data.getTimestamp()));
					tradeIndication.setInstrumentSpecification(spec);

					if(data.getPrice()!=0.0)
						tradeIndication.setPrice(data.getPrice());
					else
						tradeIndication.setPrice(TradeIndication.NOT_SET);
					tradeIndication.setQuantity(data.getSize());

					if(data.getPrice()>0.0)
					fireEvent(tradeIndication);
				}
			});
			
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
	protected TradeSubscription createSubscription(
			InstrumentSpecification spec, TimeFrame timeFrame) {
		if(factory == null) {
			throw new IllegalStateException("factory not set");
		}
		return new TradeSubscription(spec, factory);
	}

	public ISubscription<TradeIndication> subscribe(InstrumentSpecification spec)
			throws Exception {
		return super.subscribe(spec, TimeFrame.TIMEFRAME_1_TICK);
	}
}
