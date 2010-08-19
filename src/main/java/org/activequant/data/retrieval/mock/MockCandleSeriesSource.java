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
package org.activequant.data.retrieval.mock;

import java.util.ArrayList;
import java.util.List;

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.data.retrieval.ICandleSeriesSource;
import org.activequant.util.tools.Arrays;

/**
 * Mock CandleSeries source for unit testing services that depend on
 * candle list source service.<br>
 * To use this class, instantiate it and add the desired response
 * by adding it to {@link #getResponseList() responseList}.
 * <br>
 * <b>History:</b><br>
 *  - [Oct 9, 2007] Created (Mike Kroutikov)<br>
 *  - [09.11.2007] Moved to new data interfaces (Erik Nijkamp)<br>
 *
 *  @author Mike Kroutikov
 */
public class MockCandleSeriesSource implements ICandleSeriesSource {
	
	private int index = 0;
	private final List<CandleSeries> responseList = new ArrayList<CandleSeries>();
	
	public MockCandleSeriesSource() {
		
	}
	
	public MockCandleSeriesSource(CandleSeries... series) {
		responseList.addAll(Arrays.asList(series));
	}

	public String getVendorName() {
		return "MOCK";
	}

	/**
	 * Call this method from the Unit test to specify
	 * what fetch will return.
	 * 
	 * @param list candle list to be returned for every
	 *        query.
	 */
	public List<CandleSeries> getResponseList() {
		return responseList;
	}
	
	private CandleSeries returnResponse() {
		if(responseList.size() > 0) {
			index %= responseList.size();
			return responseList.get(index ++);
		} else {
			return null;
		}
	}

	private CandleSeries[] returnResponseList() {
		return responseList.toArray(new CandleSeries[0]);
	}

	public CandleSeries fetch(SeriesSpecification contractQuery) throws Exception {
		return returnResponse();
	}

	public CandleSeries[] fetch(SeriesSpecification... contractsQueries)
			throws Exception {
		return returnResponseList();
	}
}
