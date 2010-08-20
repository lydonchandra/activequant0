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

import java.util.List;

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.util.pattern.events.IEventSource;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [02.05.2007] Created (Erik Nijkamp)<br>
 *  - [04.08.2007] Added missing functions (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public interface IChain extends IFilter {
	
	public IEventSource<CandleSeries[]> getSeriesProcessedEvent();
	
	public IChain setNext(IFilter transform);
	
	public void setFilters(IFilter... filters);
	
	public IFilter[] getFilters();
	
	public CandleSeries process(CandleSeries sourceSeries) throws Exception;
	
	public CandleSeries[] process(CandleSeries... sourceSeries) throws Exception;
	
	public CandleSeries[] process(List<CandleSeries> sourceSeries) throws Exception;
}