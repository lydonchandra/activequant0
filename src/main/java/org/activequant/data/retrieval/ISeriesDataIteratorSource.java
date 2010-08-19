package org.activequant.data.retrieval;

import org.activequant.core.domainmodel.MarketDataEntity;
import org.activequant.core.domainmodel.SeriesSpecification;

/**
 * Generic interface for historical data retrieval.
 * <br>  
 * <b>History:</b><br>
 *  - [17.10.2007] Created (Erik Nijkamp)<br>
 *  - [28.11.2007] Now returns itterator (Mike Kroutikov)<br>
 *  
 *  @author Mike Kroutikov
 *  @author Erik Nijkamp
 *
 */
public interface ISeriesDataIteratorSource<T extends MarketDataEntity> {

	/**
	 * Returns iterator over the series of the market events.
	 * Iterator is always in time order (from oldest, to more recent).
	 * 
	 * @param seriesSpecification describes instrument, and (optionally)
	 * 			start/end dates and time frame (if applicable).
	 * 
	 * @return iterable object.
	 * 
	 * @throws Exception
	 */
	Iterable<T> fetch(SeriesSpecification seriesSpecification) throws Exception;
	    
    /**
     * Returns vendor name
     * 
     * @return string.
     */
    String getVendorName();
}
