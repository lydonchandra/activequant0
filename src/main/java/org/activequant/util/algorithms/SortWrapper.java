/************************************************************
 * 
 * CCAPI. 2001ff, activestocks.de / Ulrich Staudinger
 * @license    GPL 2 (http://www.gnu.org/licenses/gpl.html) 
 * 
 ************************************************************/
package org.activequant.util.algorithms;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.activequant.core.domainmodel.MarketDataEntity;
import org.activequant.core.domainmodel.TimeSeries;



/**
 * SortWrapper implementation to sort a a list of objects. 
 * Objects which should are to be sorted, should implement the Interface sortable.
 * Sortable objects should return int numbers, lower and higher specify the relative position to each other. 
 * <br>
 * <b>History:</b><br>
 *  - [20.04.2005] Created (Ulrich S.)<br>
 *  - [22.04.2006] Added templates/generics etc. => TypeSafety (Erik N.)<br>
 *  - [01.10.2007] Simplifying (Ulrich Staudinger)<br>
 *  - [27.11.2007] Adding sortByDate and sortById (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public class SortWrapper {
    // Sorts entire array
    public static <T extends Comparable<T>> void sort(List<T> array) {
    	Collections.sort(array);
    }
    
    /**
     * sorts a time series by date. 
     * 
     * @param timeSeries
     */
    public static void sortByTimeStamp(TimeSeries<MarketDataEntity> timeSeries){
    	
    	Comparator<MarketDataEntity> c = new Comparator<MarketDataEntity>(){
    		public int compare(MarketDataEntity o1, MarketDataEntity o2) {
    			return - o1.getTimeStamp().compareTo(o2.getTimeStamp());
    		}
    	};
    	Collections.sort(timeSeries, c); 
    }
    
    /**
     * sort a time series by id. 
     * @param timeSeries
     */
    public static void sortById(TimeSeries<MarketDataEntity> timeSeries){
    	
    	Comparator<MarketDataEntity> c = new Comparator<MarketDataEntity>(){
    		public int compare(MarketDataEntity o1, MarketDataEntity o2){
    			return o1.getId() > o2.getId() ? -1 : 1;
    		}
    	};
    	Collections.sort(timeSeries, c); 
    }
    
}
