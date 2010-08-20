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

import java.util.Vector;

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
 *  - [12.08.2007] Fix (Erik Nijkamp)<br>
 *
 *
 *  @author Erik Nijkamp
 */
public class RemoveEmptyFilter extends FilterBase {
	
	private Ohlc ohlc;
	
	public RemoveEmptyFilter() {
		super();
	}
	
	public RemoveEmptyFilter(Ohlc ohlc) {
		super();
		setOhlc(ohlc);
	}
	
	public void setOhlc(Ohlc ohlc) {
		this.ohlc = ohlc;
	}

	public CandleSeries[] process(CandleSeries... series) throws Exception {
		CandleSeries[] newSeries = cloneSeries(series);
		
		Vector<TimeStamp> toRemove = new Vector<TimeStamp>();
		for(int i = 0; i < newSeries[0].size(); i++) {
			for (int j = 0; j < newSeries.length; j++) {
				if(hasEmptyValue(newSeries[j].get(i))) {
					toRemove.add(newSeries[0].getTimeStamps()[i]);
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
				if (value == Candle.NOT_SET)
					return true;
			}
		} else {
			double value = candle.getDoubles()[ohlc.ordinal()];
			if (value == Candle.NOT_SET)
				return true;
		}
		return false;
	}

}
