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

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.util.CandleSeriesUtil;
import org.activequant.util.pattern.events.Event;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [02.05.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public abstract class ChainBase implements IChain {
	
	protected Event<CandleSeries[]> seriesProcessedEvent = new Event<CandleSeries[]>();
	protected Vector<IFilter> transforms = new Vector<IFilter>();
	
	public ChainBase(IFilter...transforms) {
		this.transforms.addAll(Arrays.asList(transforms));
	}	
	public IChain setNext(IFilter transform) {
		transforms.add(transform);
		return this;
	}
	
	public void setFilters(IFilter... filters) {
		transforms.clear();
		for(IFilter filter: filters) transforms.add(filter);
	}
	
	public IFilter[] getFilters() {
		return transforms.toArray(new IFilter[] {});
	}
	
	public CandleSeries[] process(List<CandleSeries> series) throws Exception {
		return process(series.toArray(new CandleSeries[]{}));
	}
	
	public CandleSeries process(CandleSeries sourceSeries) throws Exception {
		CandleSeries[] array = { sourceSeries };
		array = process(array);
		return array[0];
	}
	
	public CandleSeries[] process(CandleSeries... sourceSeries) throws Exception {
		for(IFilter transform: transforms) {
			sourceSeries = transform.process(sourceSeries);
		}
		seriesProcessedEvent.fire(sourceSeries);
		return sourceSeries;
	}
	
	protected CandleSeries[] cloneSeries(CandleSeries[] series) {
		// clone
		return CandleSeriesUtil.cloneSeries(series);
	}
	
	protected void clearSeries(CandleSeries[] series) {
		// clear
		CandleSeriesUtil.clearSeries(series);
	}
	
	public Event<CandleSeries[]> getSeriesProcessedEvent() {
		return seriesProcessedEvent;
	}
	
}
