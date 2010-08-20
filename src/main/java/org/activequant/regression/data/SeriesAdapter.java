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
package org.activequant.regression.data;

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.data.retrieval.ICandleSeriesSource;
import org.activequant.data.types.Ohlc;
import org.activequant.regression.math.Group;
import org.activequant.regression.math.Series;
import org.activequant.regression.util.SeriesUtil;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [11.08.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class SeriesAdapter {
	
	private ICandleSeriesSource seriesSource;
	private Ohlc ohlc;
	
	public SeriesAdapter(ICandleSeriesSource seriesSource, Ohlc ohlc) {
		this.seriesSource = seriesSource;
		this.ohlc = ohlc;
	}
	
	public Series read(SeriesSpecification spec) throws Exception {
		CandleSeries candles = seriesSource.fetch(spec);
		Series series = SeriesUtil.convertToSeries(candles, ohlc);
		return series;
	}
	
	public Group read(SeriesSpecification ... spec) throws Exception {
		CandleSeries[] candles = seriesSource.fetch(spec);
		Series[] series = SeriesUtil.convertToSeries(candles, ohlc);
		return new Group(series);
	}
}
