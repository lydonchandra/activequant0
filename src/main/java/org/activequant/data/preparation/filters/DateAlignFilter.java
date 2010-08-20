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

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.util.CandleSeriesUtil;
import org.activequant.data.preparation.FilterBase;
import org.activequant.data.util.DateGrid;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [10.11.2006] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class DateAlignFilter extends FilterBase {
	
	public enum AdjustPolicy { FILL, MINIMUM };
	
	private AdjustPolicy adjustPolicy = AdjustPolicy.FILL;
	
	public DateAlignFilter() {
		super();
	}
	
	public DateAlignFilter(AdjustPolicy adjustPolicy) {
		super();
		this.adjustPolicy = adjustPolicy;
	}

	public CandleSeries[] process(CandleSeries... series) throws Exception {
		CandleSeries[] newSeries = cloneSeries(series);
		return (adjustPolicy == AdjustPolicy.FILL ? fill(newSeries) : minimum(newSeries));
	}
	
	private CandleSeries[] fill(CandleSeries... series) throws Exception {
		DateGrid grid = new DateGrid(series);
		return grid.getAlignedSeries();
	}
	
	private CandleSeries[] minimum(CandleSeries... series) {
		CandleSeries ts1 = series[0];
		// need to align the timeseries in the market dto. 
		for (CandleSeries ts2 : series) {
			// aligning the timeseries against each other. 
			CandleSeriesUtil.alignTimeSeries(ts1, ts2);
		}
		// have to do it once again, as the first time series object is now fully aligned
		// with all other time series objects. 
		for (CandleSeries ts2 : series) {
			// aligning the timeseries against each other. 
			CandleSeriesUtil.alignTimeSeries(ts1, ts2);
		}
		return series;
	}
}
