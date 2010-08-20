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


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [30.04.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class PercentageFilter extends FilterBase {
	
	public PercentageFilter() {
		super();
	}
	
	public CandleSeries[] process(CandleSeries... series) throws Exception {
		CandleSeries[] newSeries = new CandleSeries[series.length];
		for(int i = 0; i < series.length; i++) {
			newSeries[i] = process(series[i]);
		}
		return newSeries;
	}

	public CandleSeries process(CandleSeries series) throws Exception {
		// clone original
		CandleSeries newSeries = series.clone();
		newSeries.clear();
		// fill gap
		newSeries.add(0, new Candle(series.get(0).getTimeStamp()));
		// calculate
		for(int i = 1; i < series.size(); i++) {
			Candle previousCandle = series.get(i-1);
			Candle currentCandle = series.get(i);
			Candle candle = roc(previousCandle, currentCandle);
			newSeries.add(candle);
		}
		return newSeries;
	}
	
	private Candle roc(Candle previousCandle, Candle currentCandle) {
		double[] previousDoubles = previousCandle.getDoubles();
		double[] currentDoubles = currentCandle.getDoubles();
		double[] rocDoubles = new double[4];
		for(int i = 0; i < currentDoubles.length; i++) {
			double previous = previousDoubles[i];
			double current = currentDoubles[i];
			double value = 0.0;
			if(previous != 0 && current != 0)				
				value = (current / previous) - 1.0;
			rocDoubles[i] = value;
		}
		return new Candle(currentCandle.getTimeStamp(), rocDoubles);
	}
}