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
package org.activequant.data.preparation.chains;

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.data.preparation.ChainBase;
import org.activequant.data.preparation.IFilter;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [02.05.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class CopyRangeChain extends ChainBase {
	
	private int selectedStart, selectedEnd;
	
	public void setSelectedRangeStart(int selectedStart) {
		this.selectedStart = selectedStart;
	}
	
	public void setSelectedRangeEnd(int selectedEnd) {
		this.selectedEnd = selectedEnd;
	}
	
	public CopyRangeChain(IFilter... transforms) {
		super(transforms);
	}
	
	public CopyRangeChain(int selectedStart, int selectedEnd,
			IFilter... transforms) {
		super(transforms);
		this.selectedStart = selectedStart;
		this.selectedEnd = selectedEnd;
	}
	
	public CandleSeries[] process(CandleSeries... sourceSeries) throws Exception {
		CandleSeries[] clonedSeries = new CandleSeries[selectedEnd - selectedStart + 1];
		for(int i = selectedStart, j = 0; i < selectedEnd + 1; i++, j++) {
			clonedSeries[j] = sourceSeries[i].clone();
		}
		// process chain
		CandleSeries[] processed = super.process(clonedSeries);
		// merge results
		CandleSeries[] newSeries = new CandleSeries[sourceSeries.length + processed.length];
		System.arraycopy(sourceSeries, 0, newSeries, 0, sourceSeries.length);
		System.arraycopy(processed, 0, newSeries, sourceSeries.length, processed.length);
		// return
		return newSeries;
	}
}