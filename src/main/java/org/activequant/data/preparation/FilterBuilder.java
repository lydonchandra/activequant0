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
package org.activequant.data.preparation;

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.Sample;
import org.activequant.core.types.TimeStamp;
import org.activequant.data.preparation.chains.Chain;
import org.activequant.data.preparation.filters.LagFilter;
import org.activequant.data.preparation.filters.NearbySampleFilter;
import org.activequant.data.preparation.filters.NormalizeFilter;
import org.activequant.data.preparation.filters.SampleFilter;
import org.activequant.data.preparation.filters.WeekdaysFilter;

/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [18.10.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class FilterBuilder {
	
	private IChain chain;
	
	public FilterBuilder() {
		this.chain = new Chain();
	}
	
	public FilterBuilder(IChain chain) {
		this.chain = chain;
	}
	
	public CandleSeries run(CandleSeries series) throws Exception {
		return chain.process(series);
	}
	
	public FilterBuilder lag(int time) {
		chain.setNext(new LagFilter(time));
		return this;
	}
	
	public FilterBuilder weekdays() {
		chain.setNext(new WeekdaysFilter());
		return this;
	}
	
	public FilterBuilder sample(TimeStamp start, TimeStamp end) {
		chain.setNext(new SampleFilter(start, end));
		return this;
	}
	
	public FilterBuilder sample(Sample sample) {
		chain.setNext(new SampleFilter(sample));
		return this;
	}
	
	public FilterBuilder nearby(TimeStamp start, TimeStamp end) {
		chain.setNext(new NearbySampleFilter(start, end));
		return this;
	}
	
	public FilterBuilder nearby(Sample sample) {
		chain.setNext(new NearbySampleFilter(sample));
		return this;
	}
	
	public FilterBuilder normalize() {
		chain.setNext(new NormalizeFilter());
		return this;
	}

}
