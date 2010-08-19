package org.activequant.data.util;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.MarketDataEntity;
import org.activequant.core.domainmodel.Quote;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.core.types.TimeFrame;
import org.activequant.data.retrieval.ICandleSubscriptionSource;
import org.activequant.data.retrieval.IQuoteSubscriptionSource;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.ITradeIndicationSubscriptionSource;
import org.activequant.util.pattern.events.IEventSink;
import org.activequant.util.pattern.events.IEventSource;
import org.apache.log4j.Logger;

/**
 * Dispatched incoming mixed stream of quotes, trade indications, and candles
 * to their subscribers. This class is meant to be used together with
 * {@link MarketDataAggregator} to simulate live trading using historic
 * data.
 * <p>
 * For event pumping, this class exposes {@link IEventSink<MarketDataEntiy>}
 * interface. For the consumer side, it exposes three subscription sources: for quote
 * subscriptions, trade subscriptions, and candle subscriptions.
 * <p>
 * <b>History:</b><br>
 *  - [26.11.2007] Created (Ulrich Staudinger)<br>
 *  - [27.11.2007] Generalized to support any market events (Mike Kroutikov)<br>
 *
 *  @author Ulrich Staudinger
 *  @author Mike Kroutikov
 */
public class MarketDataDispatcher implements IEventSink<MarketDataEntity> {
	
	private final Logger log = Logger.getLogger(getClass());
	
	private static final String VENDOR_NAME = "DISPATCHER";

	private final DispatchingSubscriptionSourceBase<Quote> quoteDispatcher = new DispatchingSubscriptionSourceBase<Quote>(VENDOR_NAME) {
		@Override
		protected SeriesSpecification inferSeriesSpecification(Quote entity) {
			return new SeriesSpecification(entity.getInstrumentSpecification(), TimeFrame.TIMEFRAME_1_TICK);
		}
	}; 

	private final DispatchingSubscriptionSourceBase<TradeIndication> tradeDispatcher = new DispatchingSubscriptionSourceBase<TradeIndication>(VENDOR_NAME) {
		@Override
		protected SeriesSpecification inferSeriesSpecification(TradeIndication entity) {
			return new SeriesSpecification(entity.getInstrumentSpecification(), TimeFrame.TIMEFRAME_1_TICK);
		}
	}; 
	
	private final DispatchingSubscriptionSourceBase<Candle> candleDispatcher = new DispatchingSubscriptionSourceBase<Candle>(VENDOR_NAME) {
		@Override
		protected SeriesSpecification inferSeriesSpecification(Candle entity) {
			return new SeriesSpecification(entity.getInstrumentSpecification(), entity.getTimeFrame());
		}
	}; 

	public IQuoteSubscriptionSource getQuoteSubscriptionSource() {
		return new IQuoteSubscriptionSource() {

			public ISubscription<Quote>[] getSubscriptions() {
				return quoteDispatcher.getSubscriptions();
			}

			public String getVendorName() {
				return quoteDispatcher.getVendorName();
			}

			public ISubscription<Quote> subscribe(InstrumentSpecification spec)
					throws Exception {
				return quoteDispatcher.subscribe(spec, TimeFrame.TIMEFRAME_1_TICK);
			}
		};
	}

	public ITradeIndicationSubscriptionSource getTradeIndicationSubscriptionSource() {
		return new ITradeIndicationSubscriptionSource() {

			public ISubscription<TradeIndication>[] getSubscriptions() {
				return tradeDispatcher.getSubscriptions();
			}

			public String getVendorName() {
				return tradeDispatcher.getVendorName();
			}

			public ISubscription<TradeIndication> subscribe(InstrumentSpecification spec)
					throws Exception {
				return tradeDispatcher.subscribe(spec, TimeFrame.TIMEFRAME_1_TICK);
			}
		};
	}

	public ICandleSubscriptionSource getCandleSubscriptionSource() {
		return new ICandleSubscriptionSource() {

			public ISubscription<Candle>[] getSubscriptions() {
				return candleDispatcher.getSubscriptions();
			}

			public String getVendorName() {
				return candleDispatcher.getVendorName();
			}

			public ISubscription<Candle> subscribe(InstrumentSpecification spec, TimeFrame timeFrame)
					throws Exception {
				return candleDispatcher.subscribe(spec, timeFrame);
			}
		};
	}

	public void fire(MarketDataEntity e) throws Exception {
		log.debug("[dispatcher] fire: " + e);
		
		if(e.getClass().equals(Quote.class)) {
			quoteDispatcher.fire((Quote) e);
		} else if(e.getClass().equals(TradeIndication.class)) {
			tradeDispatcher.fire((TradeIndication) e);
		} else if(e.getClass().equals(Candle.class)) {
			candleDispatcher.fire((Candle) e);
		}
	}
	
	/**
	 * Allows interested parties to track subscription requests for Quotes.
	 * 
	 * @return event source.
	 */
	public IEventSource<SeriesSpecification> getQuoteSubscribeEventSource() {
		return quoteDispatcher.getSubscribeEventSource();
	}
	/**
	 * Allows interested parties to track unsubscribe actions on Quote subscription.
	 * 
	 * @return event source.
	 */
	public IEventSource<SeriesSpecification> getQuoteUnsubscribeEventSource() {
		return quoteDispatcher.getUnsubscribeEventSource();
	}

	/**
	 * Allows interested parties to track subscription requests for TradeIndications.
	 * 
	 * @return event source.
	 */
	public IEventSource<SeriesSpecification> getTradeIndicationSubscribeEventSource() {
		return tradeDispatcher.getSubscribeEventSource();
	}
	/**
	 * Allows interested parties to track unsubscribe actions on TradeIndication subscription.
	 * 
	 * @return event source.
	 */
	public IEventSource<SeriesSpecification> getTradeIndicationUnsubscribeEventSource() {
		return tradeDispatcher.getUnsubscribeEventSource();
	}

	/**
	 * Allows interested parties to track subscription requests for Candles.
	 * 
	 * @return event source.
	 */
	public IEventSource<SeriesSpecification> getCandleSubscribeEventSource() {
		return candleDispatcher.getSubscribeEventSource();
	}
	/**
	 * Allows interested parties to track unsubscribe actions on Candle subscription.
	 * 
	 * @return event source.
	 */
	public IEventSource<SeriesSpecification> getCandleUnsubscribeEventSource() {
		return candleDispatcher.getUnsubscribeEventSource();
	}
}
