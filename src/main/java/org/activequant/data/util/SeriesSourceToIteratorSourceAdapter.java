package org.activequant.data.util;

import java.util.Iterator;

import org.activequant.core.domainmodel.MarketDataEntity;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.domainmodel.TimeSeries;
import org.activequant.data.retrieval.ISeriesDataSource;
import org.activequant.data.retrieval.ISeriesDataIteratorSource;
import org.apache.log4j.Logger;

/**
 * Converts array-values series source to iterator-values series source.
 * <br>
 * <b>History:</b><br>
 *  - [29.11.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class SeriesSourceToIteratorSourceAdapter<T extends MarketDataEntity> implements ISeriesDataIteratorSource<T> {
	
	private final Logger log = Logger.getLogger(getClass());
	
	private ISeriesDataSource<? extends TimeSeries<T>> arrayDataSource;
	public ISeriesDataSource<? extends TimeSeries<T>> getArryDataSource() {
		return arrayDataSource;
	}
	public void setArrayDataSource(ISeriesDataSource<TimeSeries<T>> val) {
		arrayDataSource = val;
	}
	
	private Iterator<T> fetchArrayAndBuildIterator(SeriesSpecification ss) throws Exception {
		log.info("about to fetch interval: from " + ss.getStartTimeStamp() + " to " + ss.getEndTimeStamp());
		TimeSeries<T> series = arrayDataSource.fetch(ss);
		log.info("fetched: " + series.size() + " entities");
		return new ReverseListIterator<T>(series);
	}
	
	public Iterable<T> fetch(final SeriesSpecification ss)
			throws Exception {
		
		log.info("fetching: " + ss);
		
		if(ss.getStartTimeStamp() == null || ss.getEndTimeStamp() == null) {
			throw new IllegalArgumentException("both 'start' date and 'end' date should be set in specification: " + ss);
		}
		
		final Iterator<T> iterator = new Iterator<T>() {

			private Iterator<T> arrayIterator;

			public boolean hasNext() {
				if(arrayIterator == null) {
					try {
						arrayIterator = fetchArrayAndBuildIterator(ss);
					} catch(Exception e) {
						e.printStackTrace();
						log.error(e);
						return false;
					}
				}

				return arrayIterator.hasNext();
			}

			public T next() {
				if(hasNext()) return arrayIterator.next();
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
		return "ARRAYADAPTER";
	}
}
