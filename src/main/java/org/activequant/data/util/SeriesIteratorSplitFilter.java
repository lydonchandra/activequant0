package org.activequant.data.util;

import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;

import org.activequant.core.domainmodel.MarketDataEntity;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.types.TimeFrame;
import org.activequant.core.types.TimeStamp;
import org.activequant.data.retrieval.ISeriesDataIteratorSource;
import org.apache.log4j.Logger;

/**
 * Splits time interval into smaller chunks (as per {@link #getSplitTimeFrame() splitTimeFrame}
 * property, and fetches the data from the provider.
 * <br>
 * <b>History:</b><br>
 *  - [03.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class SeriesIteratorSplitFilter<T extends MarketDataEntity> implements ISeriesDataIteratorSource<T> {
	
	private final Logger log = Logger.getLogger(getClass());
	
	private TimeFrame splitTimeFrame = TimeFrame.TIMEFRAME_60_MINUTES;
	public TimeFrame getSplitTimeFrame() {
		return splitTimeFrame;
	}
	public void setSplitTimeFrame(TimeFrame val) {
		splitTimeFrame = val;
	}
	
	private ISeriesDataIteratorSource<T> seriesSource;
	public ISeriesDataIteratorSource<T> getSeriesSource() {
		return seriesSource;
	}
	public void setSeriesSource(ISeriesDataIteratorSource<T> val) {
		seriesSource = val;
	}
	
	public Iterable<T> fetch(final SeriesSpecification ss)
			throws Exception {
		
		log.info("fetching: " + ss);
		
		if(ss.getStartTimeStamp() == null || ss.getEndTimeStamp() == null) {
			throw new IllegalArgumentException("both 'start' date and 'end' date should be set in specification: " + ss);
		}
		
		final Iterator<T> iterator = new Iterator<T>() {

//			private final long timeFrameMillis = splitTimeFrame.getValueInMilliseconds();
			private final Calendar current = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			{
				current.setTime(ss.getStartTimeStamp().getDate());
			}

			private Iterator<T> innerIterator;

			public boolean hasNext() {
				if(innerIterator != null && innerIterator.hasNext()) return true;
				
				log.info("Comparing "+current.getTime()+ " with " + ss.getEndTimeStamp().getDate());
				
				while(current.getTime().before(ss.getEndTimeStamp().getDate())) {
					try {
						TimeStamp from = new TimeStamp(current.getTime());
						splitTimeFrame.addToCalendar(current);
						TimeStamp to   = new TimeStamp(current.getTime());
						// need "open" interval to avoid requesting repeated data
						to = new TimeStamp(to.getNanoseconds() - 1);
						log.info("about to fetch next interval: from " + from + " to " + to);
						Iterable<T> series = seriesSource.fetch(new SeriesSpecification(ss.getInstrumentSpecification(), from, to, ss.getTimeFrame()));
						innerIterator = series.iterator();
						if(innerIterator.hasNext()) {
							return true;
						}
					} catch(Exception e) {
						e.printStackTrace();
						log.error(e);
						return false;
					}
				}
				
				return false;
			}

			public T next() {
				if(hasNext()) return innerIterator.next();
				return null;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
		
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return iterator;
			}
		};
	}
	
	public String getVendorName() {
		return "SPLITTER";
	}
}
