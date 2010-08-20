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
import org.activequant.data.preparation.FilterBase;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [10.11.2006] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class SelectionFilter extends FilterBase {
	
	private int length = 0;
	private int start = 0;
	
	public SelectionFilter() {
		super();
	}
	
	public int getLength() {
		return length;
	}
	
	public int getStart() {
		return start;
	}
	
	public void setStart(int start) {
		this.start = start;
		this.length = length - start;
	}
	
	public void setLength(int length) {
		this.length = length;	
	}

	public CandleSeries[] process(CandleSeries... series) throws Exception {
		// clone
		CandleSeries[] newSeries = cloneSeries(series);
		// select
		for (int i = 0; i < series.length; i++) {
			newSeries[i] = series[i].subList(start, start + length);
		}
		return newSeries;
	}
}