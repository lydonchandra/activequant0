package org.activequant.data.retrieval;

import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.domainmodel.TimeSeries;

/**
 * Generic data retrieval interfaces for TimeSeries specializations.<br>
 * <br>  
 * <b>History:</b><br>
 *  - [17.10.2007] Created (Erik Nijkamp)<br>
 *  
 *  @author Erik Nijkamp
 *
 */
public interface ISeriesDataSource<T extends TimeSeries<?>> {

	/**
	 * returns a series according to the specification
	 * @param seriesSpecification
	 * @return
	 * @throws Exception
	 */
	T fetch(SeriesSpecification seriesSpecification) throws Exception;
	
	/**
	 * returns an array of series objects according to n specifications
	 * @param seriesSpecification
	 * @return
	 * @throws Exception
	 */
	T[] fetch(SeriesSpecification... seriesSpecification) throws Exception;
	    
    /**
     * returns vendor name
     * @return
     */
    String getVendorName();
	
}
