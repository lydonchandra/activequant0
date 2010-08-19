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
package org.activequant.tradesystem.domainmodel2;

import org.activequant.tradesystem.domainmodel2.event.OrderCompletionEvent;
import org.activequant.tradesystem.domainmodel2.event.OrderEvent;
import org.activequant.util.pattern.events.IEventListener;

/**
 * Utilities for working with IBroker and IOrderTracker.
 * <br>
 * <b>History:</b><br> 
 * - [13.12.2007] Created (Mike Kroutikov) <br>
 * 
 * @author Mike Kroutikov
 */
public final class BrokerSupport {
	
	private BrokerSupport() { }

	/**
	 * Suspends current thread until ticket is completed (filled, or canceled, or rejected),
	 * or timeout occurs.
	 *  
	 * @param tracker order ticket tracker.
	 * @param millis timeout value in milliseconds.
	 * @return null if not completed before the timeout, completion info otherwise.
	 */
	public static OrderCompletionEvent waitForTicketCompletion(
			IOrderTracker tracker, long millis) {
		long target = System.currentTimeMillis() + millis;
		
		final Object mutex = new Object();
		final IEventListener<OrderEvent> listener = new IEventListener<OrderEvent>() {
			public void eventFired(OrderEvent event) throws Exception {
				if(event instanceof OrderCompletionEvent) {
					synchronized(mutex) { 
						mutex.notifyAll();
					}
				}
			}
		};
			
		tracker.getOrderEventSource().addEventListener(listener);
		
		try {
			while(true) {
				OrderCompletionEvent completion = tracker.getOrderCompletion();
				if(completion != null) {
					return completion;
				}
				
				long timeout = target - System.currentTimeMillis();
				if(timeout <= 0) return null;
				
				synchronized(mutex) {
					try {
						mutex.wait(timeout);
					} catch (InterruptedException e) {
						return null;
					}
				}
			}
		} finally {
			tracker.getOrderEventSource().removeEventListener(listener);
		}
	}

	/**
	 * Suspends current thread until ticket is completed (filled, or canceled, or rejected).
	 *  
	 * @param tracker order ticket tracker.
	 * @return completion info.
	 */
	public static OrderCompletionEvent waitForTicketCompletion(IOrderTracker tracker) {
		return waitForTicketCompletion(tracker, Long.MAX_VALUE);
	}
}
