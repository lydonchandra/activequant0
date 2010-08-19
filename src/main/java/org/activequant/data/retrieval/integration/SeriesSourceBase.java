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
package org.activequant.data.retrieval.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.domainmodel.TimeSeries;
import org.activequant.data.retrieval.ISeriesDataSource;
import org.activequant.data.util.SymbolMap;
import org.activequant.util.exceptions.NotImplementedException;
import org.activequant.util.tools.Arrays;
import org.activequant.util.tools.CheckDateOrder;

/**
 * @TODO<br>
 * <br>  
 * <b>History:</b><br>
 *  - [17.10.2007] Created (Erik Nijkamp)<br>
 *  
 *  @author Erik Nijkamp
 *
 */
public abstract class SeriesSourceBase<T extends TimeSeries<?>> implements ISeriesDataSource<T> {
	
	protected abstract Class<T> getSeriesClass();
	
	protected SymbolMap getSymbolMap() {
		throw new NotImplementedException();
	}	
	
	protected void alignDateOrder(T timeSeries) {
		// need to check if we need to reverse the order due to date stuff.
		if (timeSeries.size() > 1 && !CheckDateOrder.isOrderValid(timeSeries)) {
			Collections.reverse(timeSeries);
		}
		assert(CheckDateOrder.isOrderValid(timeSeries));
	}
	
	public T[] fetch(SeriesSpecification... specs) throws Exception {
		List<T> ret = new ArrayList<T>();
		for(SeriesSpecification spec : specs){
			ret.add(fetch(spec));
		}
		return Arrays.asArray(ret, getSeriesClass());
	}

}
