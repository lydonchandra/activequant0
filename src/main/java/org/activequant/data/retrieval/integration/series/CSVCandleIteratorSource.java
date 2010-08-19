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
package org.activequant.data.retrieval.integration.series;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.types.TimeStamp;

/**
 * Reads data from a CSV file. Ignores <code>startTimeStamp</code>
 * and <code>endTimeStamp</code> information in the SeriesSpecification.
 * The InstrumentSpecification value in SeriesSpecification is assumed to
 * be the specification of the instrument in the CSV file. 
 * <p>
 * <b>History:</b><br>
 *  - [20.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class CSVCandleIteratorSource extends CSVSeriesIteratorSourceBase<Candle> {

	protected Candle parseEntity(SeriesSpecification spec, String ... properties) {
		if(properties.length != 8) {
			throw new IllegalArgumentException("wrong number of columns");
		}
		
		Candle out = new Candle();
		out.setInstrumentSpecification(spec.getInstrumentSpecification());
		out.setTimeFrame(spec.getTimeFrame());
		
		out.setTimeStamp(new TimeStamp(Long.parseLong(properties[0].trim())));
		out.setOpenPrice(Double.parseDouble(properties[1].trim()));
		out.setHighPrice(Double.parseDouble(properties[2].trim()));
		out.setLowPrice(Double.parseDouble(properties[3].trim()));
		out.setClosePrice(Double.parseDouble(properties[4].trim()));
		out.setVolume(Double.parseDouble(properties[5].trim()));
		out.setHighTimeStamp(new TimeStamp(Long.parseLong(properties[6].trim())));
		out.setLowTimeStamp(new TimeStamp(Long.parseLong(properties[7].trim())));
		
		return out;
	}
}