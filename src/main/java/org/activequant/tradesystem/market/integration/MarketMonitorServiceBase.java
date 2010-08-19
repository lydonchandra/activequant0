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
package org.activequant.tradesystem.market.integration;

import org.activequant.core.domainmodel.Market;
import org.activequant.tradesystem.market.IMarketMonitorService;
import org.activequant.util.pattern.events.Event;
import org.activequant.util.pattern.events.IEventSource;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [04.06.2007] Created (Erik Nijkamp)<br>
 *  - [24.06.2007] Renamed (Ulrich Staudinger)<br>
 *
 *  @author Erik Nijkamp
 */
public abstract class MarketMonitorServiceBase implements IMarketMonitorService {
	
	protected Event<Market> onNewMarketEvent = new Event<Market>();
	
	public IEventSource<Market> getNewMarketEvent() {
		return onNewMarketEvent;
	}
		
}
