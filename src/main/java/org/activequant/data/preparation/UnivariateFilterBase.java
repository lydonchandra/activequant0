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

import org.activequant.core.domainmodel.CandleSeries;

/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [30.04.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public abstract class UnivariateFilterBase extends FilterBase {
	
	private int selectedSeries = 0;
	
	public UnivariateFilterBase() {
		super();
	}
	
	public UnivariateFilterBase(int selectedSeries) {
		this.selectedSeries = selectedSeries;
	}

	/**
	 * @return the selectedSeries
	 */
	public int getSelectedSeries() {
		return selectedSeries;
	}

	/**
	 * @param selectedSeries the selectedSeries to set
	 */
	public void setSelectedSeries(int selectedSeries) {
		this.selectedSeries = selectedSeries;
	}

	protected abstract CandleSeries process(CandleSeries series) throws Exception;
	
	public final CandleSeries[] process(CandleSeries... series) throws Exception {
		return new CandleSeries[] {process(series[selectedSeries])};
	}
}
