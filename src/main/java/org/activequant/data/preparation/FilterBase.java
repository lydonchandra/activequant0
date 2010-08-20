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
package org.activequant.data.preparation;

import java.util.List;

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.util.CandleSeriesUtil;

/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [25.02.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public abstract class FilterBase implements IFilter {
	
	public FilterBase() {
		
	}
	
	public CandleSeries[] process(List<CandleSeries> series) throws Exception {
		return process(series.toArray(new CandleSeries[]{}));
	}
		
	protected CandleSeries[] cloneSeries(CandleSeries[] series) {
		// clone
		return CandleSeriesUtil.cloneSeries(series);
	}
	
	protected void clearSeries(CandleSeries[] series) {
		// clear
		CandleSeriesUtil.clearSeries(series);
	}
}
