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
package org.activequant.regression.util;

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.types.TimeStamp;
import org.activequant.data.types.Ohlc;
import org.activequant.regression.math.Series;
import org.activequant.util.tools.Arrays;



/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [05.05.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class SeriesUtil {

	public static Series[] convertToSeries(CandleSeries[] candleSeries, Ohlc ohlc) {
		Series[] series = new Series[candleSeries.length];
		for(int i = 0; i < series.length; i++) {
			series[i] = convertToSeries(candleSeries[i], ohlc);
		}
		return series;
	}
	
	public static Series convertToSeries(CandleSeries candleSeries, Ohlc ohlc) {
		double[][] allValues = candleSeries.getDoubles();
		double[] values = allValues[ohlc.ordinal()];
		TimeStamp [] dates = candleSeries.getTimeStamps();
		// reverse
		if(dates[0].isAfter(dates[1])) {
			Arrays.reverse(dates);
			Arrays.reverse(values);
		}
		return new Series(candleSeries.getSeriesSpecification().toString(), dates, values);
	}

}
