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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.types.TimeStamp;
import org.activequant.util.exceptions.ValueNotFoundException;

/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [03.05.2007] Created (Erik Nijkamp)<br>
 *  
 *  @author Erik Nijkamp
 */
public class CandleSeriesHash extends CandleSeries {

	private static final long serialVersionUID = 5254689954795874715L;
	
	private TreeMap<TimeStamp, Candle> cache = new TreeMap<TimeStamp, Candle>();
	
	public CandleSeriesHash(CandleSeries timeSeries) {
		super(timeSeries);
		rebuildCache();
	}
	
	private void rebuildCache() {
		cache.clear();
		for(Candle candle: this) {
			cache.put(candle.getTimeStamp(), candle);
		}
	}
	
	public TimeStamp getTimeStamp(int i) {
		return get(i).getTimeStamp();
	}
	
    public CandleSeries getTimeFrame(TimeStamp start, TimeStamp end) {        
        SortedMap<TimeStamp, Candle> subMap = cache.subMap(start, end);
        CandleSeries timeSeries = new CandleSeries();
        timeSeries.addAll(subMap.values());        
        return timeSeries;
    }
	
	public boolean containsTimeStamp(TimeStamp date) {
		return cache.containsKey(date);
	}
	
	public Candle getCandleByTimeStamp(TimeStamp date) {
		return cache.get(date);
	}
	
	public int getTimeStampPosition(TimeStamp date) throws ValueNotFoundException {
		List<TimeStamp> dates = new LinkedList<TimeStamp>();
		dates.addAll(cache.keySet());
		int pos = Collections.binarySearch(dates, date);
		if (pos < 0)
			throw new ValueNotFoundException("Cannot find value for date '"
					+ date + "'.");
		return pos;
	}
}