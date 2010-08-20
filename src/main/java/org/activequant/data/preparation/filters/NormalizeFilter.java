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

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.data.preparation.FilterBase;
import org.activequant.util.algorithms.FinancialLibrary;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [23.02.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class NormalizeFilter extends FilterBase {
	
	public CandleSeries[] process(CandleSeries... series) throws Exception {		
		// new series
		CandleSeries[] newSeries = cloneSeries(series);
		// clear series
		clearSeries(newSeries);
		// normalize
		for(int i = 0; i < series.length; i++) {
			CandleSeries timeSeries = series[i];
			double[][] ohlc = timeSeries.getDoubles();
			double[][] ranges = new double[4][2];
			for(int j = 0; j < ranges.length; j++) {
				double[] values = ohlc[j];
				// min
				ranges[j][0] = FinancialLibrary.min(values);
				// max
				ranges[j][1] = FinancialLibrary.max(values);
			}
			for(Candle candle: timeSeries) {
				newSeries[i].add(normalize(candle.clone(), ranges));
			}
		}
		return newSeries;
	}
	
	private Candle normalize(Candle candle, double[][] ranges) {
		double[] values = candle.getDoubles();
		// iterate ohlc
		for(int i = 0; i < values.length; i++) {
			double value = values[i];
			double min = ranges[i][0];
			double max = ranges[i][1];
			// set base 0 (not min) and normalize
			values[i] = (value - min) / (max - min);
		}
		// set
		candle.setDoubles(values);
		return candle;
		
	}

}
