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
import org.activequant.core.types.TimeFrame;
import org.activequant.util.pattern.events.Event;
import org.activequant.util.scheduling.Scheduler;
import org.activequant.util.scheduling.SchedulerTask;
import org.activequant.util.scheduling.iterators.TimeFrameIterator;
import org.apache.log4j.Logger;



/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [04.06.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class MockMonitorService extends MarketMonitorServiceBase {
	
	private final static Logger log = Logger.getLogger(MockMonitorService.class);
	
	private Scheduler scheduler = new Scheduler();
	private TimeFrame timeFrame = TimeFrame.TIMEFRAME_5_SECONDS;
	private Event<Market> onNewMarketEvent = new Event<Market>();
	
	public MockMonitorService() {
	}
	
	private void onScheduledEvent() throws Exception {
		onNewMarketEvent.fire(new Market());
	}
	
	public Event<Market> getNewMarketEvent() {
		return onNewMarketEvent;
	}

	public boolean isRunning() {
		return false;
	}

	public void start() throws Exception {
		scheduler.schedule(new SchedulerTask() {
			@Override
			public void run() {
				try {
					onScheduledEvent();
				} catch (Throwable ex) {
					log.error(ex);
				}
			}
		}, new TimeFrameIterator(timeFrame));
	}

	public void stop() throws Exception {
		scheduler.cancel();
	}
}
