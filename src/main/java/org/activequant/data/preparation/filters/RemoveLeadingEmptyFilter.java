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
package org.activequant.data.preparation.filters;

import java.util.HashSet;
import java.util.Set;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.types.TimeStamp;
import org.activequant.data.preparation.FilterBase;
import org.activequant.data.types.Ohlc;





/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [10.11.2006] Created (Erik Nijkamp)<br>
 *
 *
 *  @author Erik Nijkamp
 */
public class RemoveLeadingEmptyFilter extends FilterBase {
	
	private Ohlc ohlc;
	
	public RemoveLeadingEmptyFilter() {
		super();
	}
	
	public RemoveLeadingEmptyFilter(Ohlc ohlc) {
		super();
		setOhlc(ohlc);
	}
	
	public void setOhlc(Ohlc ohlc) {
		this.ohlc = ohlc;
	}

	public CandleSeries[] process(CandleSeries... series) throws Exception {
		CandleSeries[] newSeries = cloneSeries(series);
		
		Set<TimeStamp> toRemove = new HashSet<TimeStamp>();
		for(int i = 0; i < newSeries.length; i++) {
			for (int j = 0; j < newSeries[i].size(); j++) {
				if(hasEmptyValue(newSeries[i].get(j))) {
					toRemove.add(newSeries[0].getTimeStamps()[j]);
				} else {
					break;
				}
			}
		}
		
		for(TimeStamp date: toRemove) {
			for(CandleSeries currentSeries: newSeries)
				currentSeries.removeByTimeStamp(date);
		}
		
		return newSeries;
	}
	
	private boolean hasEmptyValue(Candle candle) {
		if (ohlc == null) {
			double[] values = candle.getDoubles();
			for (double value : values) {
				if (value == 0.0)
					return true;
			}
		} else {
			double value = candle.getDoubles()[ohlc.ordinal()];
			if (value == 0.0)
				return true;
		}
		return false;
	}

}
