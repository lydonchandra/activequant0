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

import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.domainmodel.TradeIndication;
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
public class CSVTradeIndicationIteratorSource extends CSVSeriesIteratorSourceBase<TradeIndication> {

	protected TradeIndication parseEntity(SeriesSpecification spec, String ... properties) {
		if(properties.length != 3) {
			throw new IllegalArgumentException("wrong number of columns");
		}
		
		TradeIndication out = new TradeIndication();
		out.setInstrumentSpecification(spec.getInstrumentSpecification());

		out.setTimeStamp(new TimeStamp(Long.parseLong(properties[0].trim())));
		out.setPrice(Double.parseDouble(properties[1].trim()));
		out.setQuantity(Double.parseDouble(properties[2].trim()));
		
		return out;
	}
}