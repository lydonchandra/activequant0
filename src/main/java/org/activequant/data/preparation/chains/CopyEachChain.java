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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.data.preparation.ChainBase;
import org.activequant.data.preparation.IFilter;





/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [08.05.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class CopyEachChain extends ChainBase {
	
	public CopyEachChain() {
		super();
	}

	public CopyEachChain(IFilter...transforms) {
		super(transforms);
	}
	
	public CandleSeries[] process(CandleSeries... sourceSeries) throws Exception {
		// merged result
		List<CandleSeries> newSeries = new ArrayList<CandleSeries>();
		// for each filter copy the series
		for(IFilter transform: transforms) {
			CandleSeries[] processedSeries = transform.process(cloneSeries(sourceSeries));
			newSeries.addAll(Arrays.asList(processedSeries));
		}
		// return
		return newSeries.toArray(new CandleSeries[] {});
	}
}
