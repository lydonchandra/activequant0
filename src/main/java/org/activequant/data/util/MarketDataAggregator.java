package org.activequant.data.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.MarketDataEntity;
import org.activequant.core.domainmodel.Quote;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.core.types.TimeStamp;
import org.activequant.data.retrieval.ISeriesDataIteratorSource;
import org.activequant.util.algorithms.MergeSortIterator;
import org.activequant.util.pattern.events.IEventListener;
import org.activequant.util.pattern.events.IEventSource;
import org.apache.log4j.Logger;

/**
 * Aligns series of different market entities, and emits them as a single stream of
 * mixed entities: Quotes for different instruments, mixed with TradeIndications,
 * and Candles. The output sequence presents the order in which events occured
 * (i.e. different data sources are correctly aligned).
 * <p>
 * In other words, the output is the timeline-based output. It can be used with matching 
 * class {@link MarketDataDispatcher} to build a simulation of real-time event flow
 * from historical data.
 * 
 * <br>
 * <b>History:</b><br>
 *  - [26.11.2007] Created (Ulrich Staudinger)<br>
 *  - [27.11.2007] Generalized to support any market events (Mike Kroutikov)<br>
 *
 *  @author Ulrich Staudinger
 *  @author Mike Kroutikov
 */
public class MarketDataAggregator implements Iterable<MarketDataEntity>{
	
	private final Logger log = Logger.getLogger(getClass());
	
	private final Comparator<MarketDataEntity> comparator = new Comparator<MarketDataEntity>() {
		public int compare(MarketDataEntity o1, MarketDataEntity o2) {
//			log.info("comparing: " + o1 + " with " + o2);
			return o1.getTimeStamp().compareTo(o2.getTimeStamp());
		}
		
	};
	private final MergeSortIterator<MarketDataEntity> mergeSort = new MergeSortIterator<MarketDataEntity>(comparator);
	private final AtomicReference<TimeStamp> currentTimeStamp = new AtomicReference<TimeStamp>();

	private TimeStamp startTimeStamp;
	
	/**
	 * Start of the historical interval.
	 * 
	 * @return start.
	 */
	public TimeStamp getStartTimeStamp() {
		return startTimeStamp;
	}
	public void setStartTimeStamp(TimeStamp val) {
		startTimeStamp = val;
		currentTimeStamp.set(startTimeStamp);
	}
	
	private TimeStamp endTimeStamp;
	
	/**
	 * End of the historical interval.
	 * 
	 * @return end.
	 */
	public TimeStamp getEndTimeStamp() {
		return endTimeStamp;
	}
	public void setEndTimeStamp(TimeStamp val) {
		endTimeStamp = val;
	}
	
	private ISeriesDataIteratorSource<Quote> quoteSource;
	
	/**
	 * Setter used to receive SeriesSpecification events for Quotes issued by
	 * {@link MarketDataDispatcher}.
	 * 
	 * @param val source of quote specifications.
	 */
	public void setQuoteSeriesSpecificationEventSource(IEventSource<SeriesSpecification> val) {
		val.addEventListener(new IEventListener<SeriesSpecification>() {
			public void eventFired(SeriesSpecification ss) {
				fetchQuoteSeries(ss);
			}
		});
	}
	/**
	 * Historic data source for quotes.
	 * 
	 * @return data source.
	 */
	public ISeriesDataIteratorSource<Quote> getQuoteSeriesDataSource() {
		return quoteSource;
	}
	/**
	 * Sets historic data source for quotes.
	 * 
	 * @param val data source.
	 */
	public void setQuoteSeriesDataSource(ISeriesDataIteratorSource<Quote> val) {
		quoteSource = val;
	}
	
	@SuppressWarnings("unchecked")
	private void fetchQuoteSeries(SeriesSpecification ss) {
		try {
			SeriesSpecification specs = new SeriesSpecification(ss.getInstrumentSpecification(), ss.getTimeFrame());
			specs.setStartTimeStamp(currentTimeStamp.get());
			specs.setEndTimeStamp(endTimeStamp);
			log.info("fetching Quotes for: " + specs);
			Iterable<?> iterable = quoteSource.fetch(specs);
			mergeSort.addIterator((Iterator<MarketDataEntity>) iterable.iterator());
		} catch(Exception ex) {
			ex.printStackTrace();
			log.error(ex);
		}
	}
	
	private ISeriesDataIteratorSource<TradeIndication> tradeSource;
	
	/**
	 * Setter used to receive SeriesSpecification events for TradeIndications issued by
	 * {@link MarketDataDispatcher}.
	 * 
	 * @param val source of trade specifications.
	 */
	public void setTradeIndicationSeriesSpecificationEventSource(IEventSource<SeriesSpecification> val) {
		val.addEventListener(new IEventListener<SeriesSpecification>() {
			public void eventFired(SeriesSpecification event) {
				fetchTradeSeries(event);
			}
		});
	}
	/**
	 * Historic data source for trade indications.
	 * 
	 * @return data source.
	 */
	public ISeriesDataIteratorSource<TradeIndication> getTradeIndicationSeriesDataSource() {
		return tradeSource;
	}
	/**
	 * Sets historic data source for trade indications.
	 * 
	 * @param val data source.
	 */
	public void setTradeIndicationSeriesDataSource(ISeriesDataIteratorSource<TradeIndication> val) {
		tradeSource = val;
	}

	@SuppressWarnings("unchecked")
	private void fetchTradeSeries(SeriesSpecification ss) {
		try {
			SeriesSpecification specs = new SeriesSpecification(ss.getInstrumentSpecification(), ss.getTimeFrame());
			specs.setStartTimeStamp(currentTimeStamp.get());
			specs.setEndTimeStamp(endTimeStamp);
			log.info("fetching TradeIndications for: " + specs);
			Iterable<?> iterable = tradeSource.fetch(specs);
			mergeSort.addIterator((Iterator<MarketDataEntity>) iterable.iterator());
		} catch(Exception ex) {
			ex.printStackTrace();
			log.error(ex);
		}
	}

	private ISeriesDataIteratorSource<Candle> candleSource;
	
	/**
	 * Setter used to receive SeriesSpecification events for Candles issued by
	 * {@link MarketDataDispatcher}.
	 * 
	 * @param val source of candle specifications.
	 */
	public void setCandleSeriesSpecificationEventSource(IEventSource<SeriesSpecification> val) {
		val.addEventListener(new IEventListener<SeriesSpecification>() {
			public void eventFired(SeriesSpecification event) {
				fetchCandleSeries(event);
			}
		});
	}
	/**
	 * Historic data source for candles.
	 * 
	 * @return data source.
	 */
	public ISeriesDataIteratorSource<Candle> getCandleSeriesDataSource() {
		return candleSource;
	}
	/**
	 * Sets historic data source for candles.
	 * 
	 * @param val data source.
	 */
	public void setCandleSeriesDataSource(ISeriesDataIteratorSource<Candle> val) {
		candleSource = val;
	}

	@SuppressWarnings("unchecked")
	private void fetchCandleSeries(SeriesSpecification ss) {
		try {
			SeriesSpecification specs = new SeriesSpecification(ss.getInstrumentSpecification(), ss.getTimeFrame());
			specs.setStartTimeStamp(currentTimeStamp.get());
			specs.setEndTimeStamp(endTimeStamp);
			log.info("fetching Candles for: " + specs);
			Iterable<?> iterable = candleSource.fetch(specs);
			mergeSort.addIterator((Iterator<MarketDataEntity>) iterable.iterator());
		} catch(Exception ex) {
			ex.printStackTrace();
			log.error(ex);
		}
	}

	public Iterator<MarketDataEntity> iterator() {
		return new Iterator<MarketDataEntity>() {

			public boolean hasNext() {
				return mergeSort.hasNext();
			}

			public MarketDataEntity next() {
				MarketDataEntity entity = mergeSort.next();
				currentTimeStamp.set(entity.getTimeStamp());
				return entity;
			}

			public void remove() {
				mergeSort.remove();
			}
		};
	}
}
