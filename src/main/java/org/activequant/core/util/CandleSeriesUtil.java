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
package org.activequant.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.util.algorithms.SortWrapper;
import org.activequant.util.tools.CheckDateOrder;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [03.05.2007] Created (Erik Nijkamp)<br>
 *  - [10.06.2007] added merge (Ulrich Staudinger)<br>
 *  - [15.06.2007] Polished merge() (Erik Nijkamp)<br>
 *  
 *  @author Erik Nijkamp
 *  @author Ulrich Staudinger
 */
public class CandleSeriesUtil {
    
	public static CandleSeries[] cloneSeries(CandleSeries... series) {
		CandleSeries[] newSeries = new CandleSeries[series.length];
		for (int i = 0; i < newSeries.length; i++) {
			newSeries[i] = series[i].clone();
		}
		return newSeries;
	}
	
	public static void clearSeries(CandleSeries... series) {
		for(CandleSeries timeSeries: series) {
			timeSeries.clear();
		}
	}
	
    /**
     * will align two timeseries. will remove days that are not present in one of the two series. 
     *  
     * 
     * @param ts1
     * @param ts2
     */
    public static void alignTimeSeries(CandleSeries ts1, CandleSeries ts2) {
        HashMap<String, Integer> entries = new HashMap<String, Integer>();

        for (Candle c : ts1) {
            entries.put(c.getTimeStamp().toString(), 1);
        }

        for (Candle c : ts2) {
            if (entries.containsKey(c.getTimeStamp().toString()))
                entries.put(c.getTimeStamp().toString(), 2);
            else
                entries.put(c.getTimeStamp().toString(), 1);
        }

        List<Candle> toBeDeleted = new ArrayList<Candle>();

        // walk over the ts1 and remove all the unknown candles.
        for (Candle c : ts1) {
            if (entries.get(c.getTimeStamp().toString()) != 2) {
                toBeDeleted.add(c);
            }
        }

        // walk over the ts2 and remove all the unknown candles.
        for (Candle c : ts2) {
            if (entries.get(c.getTimeStamp().toString()) != 2) {
                toBeDeleted.add(c);
            }
        }

        for (Candle c : toBeDeleted) {
            ts1.remove(c);
            ts2.remove(c);
        }
    }
    
    /**
     * method to merge two timeseries objects. the first object is modified! 
     * 
     * @param ts1
     * @param ts2
     * @return the first object. 
     */
    public static CandleSeries merge(CandleSeries ts1, CandleSeries ts2){
    	
    	TreeSet<Candle> set = new TreeSet<Candle>(new Comparator<Candle>() {

			public int compare(Candle c2, Candle c1) {
				// null-safe (just in case)
				if(c1.getTimeStamp() == null) {
					return c2.getTimeStamp() == null ? 0 : -1;
				} else if(c2.getTimeStamp() == null) {
					return 1;
				}
				
				return c1.getTimeStamp().compareTo(c2.getTimeStamp());
			}
    	});
    	
    	// start with second set so that first ts1 has chance to overwrite
    	// candles: if two candles have same date, ts1 must win!
    	if(ts2 != null) {
    		set.addAll(ts2);
    	}
    	set.addAll(ts1);

    	CandleSeries ret = new CandleSeries(ts1.getSeriesSpecification(), 
    			set.toArray(new Candle[0]));
    	
		assert(CheckDateOrder.isOrderValid(ret));
    	
    	return ret; 
    }
    
    /**
     * helper method to sort a candle series. 
     * @param ts1
     * @return
     */
    public static CandleSeries sort(CandleSeries ts1){
    	SortWrapper.sort(ts1);
    	if (ts1.size() > 1 && !CheckDateOrder.isOrderValid(ts1)) {
			Collections.reverse(ts1);
		}
    	assert(CheckDateOrder.isOrderValid(ts1));
    	return ts1;
    }
}
