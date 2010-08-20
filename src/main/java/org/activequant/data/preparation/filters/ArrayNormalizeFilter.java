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
import org.activequant.data.types.Ohlc;
import org.activequant.util.algorithms.FinancialLibrary;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [23.02.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class ArrayNormalizeFilter extends FilterBase {
	
	public CandleSeries[] process(CandleSeries... series) throws Exception {		
		// new series
		CandleSeries[] newSeries = cloneSeries(series);
		// normalize
		for(int i = 0; i < series[0].size(); i++) {
			// get candles
			Candle[] candles = new Candle[series.length];
			for(int j = 0; j < series.length; j++) {
				candles[j] = series[j].get(i);
			}
			// get arrays
			double[] opens = normalize(extractOhlc(Ohlc.OPEN, candles));
			double[] high = normalize(extractOhlc(Ohlc.HIGH, candles));
			double[] low = normalize(extractOhlc(Ohlc.LOW, candles));
			double[] close = normalize(extractOhlc(Ohlc.CLOSE, candles));
			// set candles
			for(int j = 0; j < series.length; j++) {
				// set ohlc
				candles[j].setOpenPrice(opens[j]);
				candles[j].setHighPrice(high[j]);
				candles[j].setLowPrice(low[j]);
				candles[j].setClosePrice(close[j]);
				// set candle
				newSeries[j].set(i, candles[j]);
			}			
		}
		return newSeries;
	}
	
    private double[] extractOhlc(Ohlc ohlc, Candle[] candles) {
    	double[] array = new double[candles.length];
    	for(int i = 0; i < array.length; i++) {
    		array[i] = candles[i].getDoubles()[ohlc.ordinal()];
    	}
    	return array;
    }
	
    private double[] normalize(double[] values) {
    	return FinancialLibrary.normalizeArray(values);
    }
}
