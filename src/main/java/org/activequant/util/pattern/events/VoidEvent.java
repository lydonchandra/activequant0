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
package org.activequant.util.pattern.events;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Generic event<br>
 * <br>
 * <b>History:</b><br>
 *  - [24.10.2006] Created (Erik Nijkamp)<br>
 *  - [30.10.2006] Added isEmpty() and soft references (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class VoidEvent implements IVoidEventSource, IVoidEventSink {
	private final Queue<IVoidEventListener> listeners = new ConcurrentLinkedQueue<IVoidEventListener>();

	public void fire() throws Exception {
		for (IVoidEventListener listener : listeners) {
			listener.eventFired();
		}
	}
	
	public boolean isEmpty() {
		return listeners.isEmpty();
	}	

	public void addEventListener(IVoidEventListener listener) {
		listeners.add(listener);
	}

	public boolean removeEventListener(IVoidEventListener listener) {
		return listeners.remove(listener);
	}
	
	public void forward(final IVoidEventSink target) {
		listeners.add(new IVoidEventListener() {
			public void eventFired() throws Exception {
				target.fire();
			}			
		});
	}

	public void clear() {
		listeners.clear();
	}
}
