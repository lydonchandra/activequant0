
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
import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.core.types.TimeFrame;
import org.activequant.core.types.TimeStamp;
import org.activequant.data.retrieval.ICandleSubscriptionSource;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.ITradeIndicationSubscriptionSource;
import org.activequant.data.retrieval.integration.SubscriptionSourceBase;
import org.activequant.util.pattern.events.Event;
import org.activequant.util.pattern.events.IEventListener;
import org.apache.log4j.Logger;

/**
 * Generates candles from trade events by accumulating and computing Open/High/Low/Close
 * values and volume. Trades also normally act as a source of synchronization:
 * their date information is used to determine when to fire
 * Candle event. This mechanism of generating candles is the default one,
 * it is called "internal synchronization" method.
 * Optionally this class can use external source of synchronization.
 * 
 * <h2>Synchronization logic explained</h2>
 * In this context, synchronization means determining the exact time when
 * to emit the Candle.
 *  
 * <h3>Internal synchronization</h3>
 * When trade events are used for this purpose,
 * class keeps track of incoming trade events' dates. It also records the anticipated
 * time of closing the current Candle (when fresh current Candle is created, its date
 * is incremented by the value of timeframe to arrive at the anticipated close time
 * for this Candle). At every incoming trade tick, this class checks whether timestamp
 * of the trade exceeds the anticipated candle close time. If it is, the 
 * synchronization strobe is generated that fires the candle (this happens <em>before</em>
 * the tick that caused this is considered for Open/High/Low/Close computation).
 * 
 * <h3>External synchronization and External-only synchronization</h3>
 * If the trade flow is very sparse, and there is a real chance that a gap in trade events 
 * can be as long as the target timeframe of the candle, external synchronization source
 * will help. By attaching an external synchronization source one makes sure that
 * candles are evaluated for closing often enough to avoid candle gaps. By the default,
 * external synchronization source is used by this class <em>in addition</em> to the
 * internal one. One can force this class to use only external synchronization by
 * setting property <code>useExternalSyncOnly</code> to <code>true</code>.
 * 
 * <h3>Forced external synchronization</h3>
 * This special mode uses external synchronization source to unconditionally
 * emit the candles (without any regard to the anticipated Candle close time).
 * Use this mode with caution, because it assumes that all subscriptions ask for
 * the same timeframe, and it assumes that the same timeframe is used as the base 
 * for the external synchronization source. To enable this mode, set both
 * {@link #setUseExternalSyncOnly(boolean) useExternalSyncOnly} and
 * {@link #setForceFrameOnExternalSync(boolean) forceFrameOnExternalSync} to
 * <code>true</code>.
 * 
 * <br>
 * <b>History:</b><br>
 *  - [03.06.2007] Created (Ulrich Staudinger)<br>
 *  - [03.06.2007] Polished (Erik Nijkamp)<br>
 *  - [23.06.2007] Moved to new domain model (Erik Nijkamp)<br>
 *  - [24.06.2007] Wrapper finished. (Ulrich Staudinger)<br>
 *  - [29.09.2007] Cleanup + moved to new domain model (Erik Nijkamp)<br>
 *  - [29.09.2007] Refactored push/pull approach (Erik Nijkamp)<br>
 *  - [07.10.2007] Added additional SeriesSpec constructor (Erik Nijkamp)<br>
 *  - [31.10.2007] Fixed bug in second constructor, updated code to seriesSpec.getIn() (Ulrich Staudinger)<br>
 *  - [04.11.2007] Converted to new data retrieval model, cleanup, no more pushing (Mike Kroutikov) <br>
 * 
 * @author Ulrich Staudinger
 * @author Mike Kroutikov
 */
public class TradeIndicationToCandleConverter extends SubscriptionSourceBase<Candle> implements ICandleSubscriptionSource {

	private static Logger log = Logger.getLogger(TradeIndicationToCandleConverter.class);
	
	public TradeIndicationToCandleConverter() {
		super("TRADE2CANDLE");
	}

	public TradeIndicationToCandleConverter(
			ITradeIndicationSubscriptionSource tradeSource) {
		this();
		setTradeIndicationSource(tradeSource);
	}

	private ITradeIndicationSubscriptionSource tradeIndicationSource = null;
	
	public void setTradeIndicationSource(ITradeIndicationSubscriptionSource val) {
		tradeIndicationSource = val;
	}
	
	public ITradeIndicationSubscriptionSource getTradeIndicationSource() {
		return tradeIndicationSource;
	}

	private final Event<TimeStamp> timeSyncEvent = new Event<TimeStamp>();
	
	private final IEventListener<TimeStamp> syncEventListener = new IEventListener<TimeStamp>() {
		public void eventFired(TimeStamp event) throws Exception {
			timeSyncEvent.fire(event);
		}
	};
	
	/**
	 * Read-only property that exports synchronization event listener.
	 * Plug this event listener into any appropriate event source
	 * to provide external synchronization source. By the default, external
	 * synchronization source will be used <em>in addition</em> to the
	 * synchronization from incoming flow of ticks. But if 
	 * {@link #setUseExternalSyncOnly(boolean) useExternalSyncOnly}
	 *  is set to <code>true</code>, then
	 * only external sync source will be used. See also
	 * {@link #setForceFrameOnExternalSync(boolean) forceFrameOnExternalSync}
	 * property.
	 * <p/>
	 * The synchronization source must provide correct dates, as they are used to
	 * set the Candle date.
	 * 
	 * @return sync event listener.
	 */
	public IEventListener<TimeStamp> getSyncEventListener() {
		return syncEventListener;
	}
	
	private boolean useExternalSyncOnly = false;
	
	/**
	 * Determines whether external sync is used exclusively, or in conjunction
	 * with the internal sync mechanism.
	 * 
	 * @param val true or false.
	 */
	public void setUseExternalSyncOnly(boolean val) {
		useExternalSyncOnly = val;
	}
	public boolean getUseExternalSyncOnly() {
		return useExternalSyncOnly;
	}
	
	private boolean forceFrameOnExternalSync = false;
	
	/**
	 * Determines if external sync source causes Candle generation unconditionally 
	 * (i.e. without comparing event timestamp with anticipated candle close time).
	 * Default value is <code>false</code>. If this is set to <code>true</code>,
	 * {@link #setUseExternalSyncOnly(boolean) useExternalSyncOnly} property
	 * must be <code>true</code>, and source of external synchronization be attached
	 * via {@link #getSyncEventListener() syncEventListener}.
	 *  
	 * @param val
	 */
	public void setForceFrameOnExternalSync(boolean val) {
		forceFrameOnExternalSync = val;
	}
	
	public boolean getForceFrameOnExternalSync() {
		return forceFrameOnExternalSync;
	}

	private class CandleSubscription extends Subscription {
		
		private final InstrumentSpecification spec;
		private final TimeFrame timeFrame;
		private final ISubscription<TradeIndication> subscription;
		
		private Candle currentCandle = null;
		private final Calendar nextFrameCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		
		public CandleSubscription(InstrumentSpecification s, TimeFrame tf) throws Exception {
			spec = s;
			timeFrame = tf;
			subscription = tradeIndicationSource.subscribe(spec);
			subscription.addEventListener(new IEventListener<TradeIndication>() {
				public void eventFired(TradeIndication event) throws Exception {
					handleTrade(event);
				}
			});
		}

		private Candle initializeCandle() {
			Candle c = new Candle(spec);
			c.setTimeStamp(new TimeStamp(nextFrameCalendar.getTime()));
			c.setTimeFrame(timeFrame);
			return c;
		}

		private void handleTrade(TradeIndication trade) {
			log.debug("handling incoming trade: " + trade);
			
			Candle c = currentCandle;
			if(c == null) {
				// first time! initialize some data
				nextFrameCalendar.setTime(trade.getTimeStamp().getDate());
				timeFrame.alignCalendar(nextFrameCalendar);
				timeFrame.addToCalendar(nextFrameCalendar);

				c = currentCandle = initializeCandle();
				
			}
			
			if(!useExternalSyncOnly) {
				// every trade event also serves as synchronization source
				try {
					log.debug("using trade event as sync source");
					timeSyncEvent.fire(trade.getTimeStamp());
				} catch (Exception e) {
					log.warn("exception in event-dispatching code: " + e);
					e.printStackTrace();
				}
			}
			
			workoutTrade(c, trade);
		}

		private final IEventListener<TimeStamp> syncListener = new IEventListener<TimeStamp>() {
			public void eventFired(TimeStamp event) throws Exception {
				handleSync(event);
			}
		};
		
		private synchronized void handleSync(TimeStamp date) {
			
			log.debug("handling sync strobe: " + date);
			
			if(currentCandle == null) {
				log.info("no trades seen yet: not generating a candle");
				return;
			}
			
			if(!forceFrameOnExternalSync) {
				if(date.getDate().before(nextFrameCalendar.getTime())) {
					return; // not yet
				}
			}
			
			log.debug("preparing candle for shipping");
			
			Candle prev = currentCandle;
			currentCandle = null;
			
			fireEvent(prev);
		}

		@Override
		protected void handleActivate() throws Exception {
			timeSyncEvent.addEventListener(syncListener);
			subscription.activate();
		}

		@Override
		protected void handleCancel() throws Exception {
			timeSyncEvent.removeEventListener(syncListener);
			subscription.cancel();
		}
	}

	private static void workoutTrade(Candle c, TradeIndication trade) {

		double price = trade.getPrice(); 
		double size  = trade.getQuantity();
		if(c.getOpenPrice() == Candle.NOT_SET) {
			c.setOpenPrice(price);
			c.setHighPrice(price);
			c.setLowPrice(price);
			c.setClosePrice(price);
			c.setVolume(size);
			c.setHighTimeStamp(trade.getTimeStamp());
			c.setLowTimeStamp(trade.getTimeStamp());
		} else {
			if(c.getHighPrice() < price) {
				c.setHighPrice(price);
				c.setHighTimeStamp(trade.getTimeStamp());
			} else if(c.getLowPrice() > price) {
				c.setLowPrice(price);
				c.setLowTimeStamp(trade.getTimeStamp());
			}
			
			c.setClosePrice(price);
			c.setVolume(c.getVolume() + size);
		}
	}

	@Override
	protected CandleSubscription createSubscription(
			InstrumentSpecification spec, TimeFrame timeFrame) {
		
//		if(useExternalSyncOnly && timeSyncEvent.isEmpty()) {
//			throw new IllegalStateException("external sync event not supplied! either allow internal synchrnization or attach sync source to this class");
//		}
		
		if(forceFrameOnExternalSync) {
			if(!useExternalSyncOnly) {
				throw new IllegalArgumentException("forced mode can be used only with external sync source exclusively (set useExternalSyncOnly to \"true\")");
			}
		}
		
		try {
			return new CandleSubscription(spec, timeFrame);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
