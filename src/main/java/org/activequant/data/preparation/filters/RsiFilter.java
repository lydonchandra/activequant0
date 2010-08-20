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
import org.activequant.util.algorithms.FinancialLibrary;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [30.04.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class RsiFilter extends FilterBase {
	
	public enum AdjustPolicy { NEURAL, STANDARD };
	
	private AdjustPolicy adjustPolicy = AdjustPolicy.NEURAL;
	
	private int days;
	
	public RsiFilter(int days) {
		super();
		this.days = days;
	}
	
	public RsiFilter(int days, AdjustPolicy adjustPolicy) {
		super();
		this.days = days;
		this.adjustPolicy = adjustPolicy;
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
		// calculate
		newSeries.setDoubles(rsi(days, series));
		// done
		return newSeries;
	}
	
	private double[][] rsi(int days, CandleSeries series) {
		double[][] ohlc = series.getDoubles();
		double[][] result = new double[4][series.size()];
		for(int i = 0; i < result.length; i++) {
			// iterate through opens, highs ... and move the timeframe
			for(int j = 0; j < series.size()-days; j++) {
				if (adjustPolicy == AdjustPolicy.STANDARD) {
					result[i][j + days] = FinancialLibrary
							.RSI(days, ohlc[i], j);
				} else {
					result[i][j + days] = (FinancialLibrary.RSI(days, ohlc[i],
							j) / 100.0) * 2 - 1.0;
				}
			}
		}
		return result;		
	}
}