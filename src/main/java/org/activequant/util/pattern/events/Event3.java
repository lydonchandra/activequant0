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
 *  - [30.10.2006] Added isEmpty() (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class Event3<T1, T2, T3> {
	
	private final Queue<IEventListener3<T1, T2, T3>> listeners = new ConcurrentLinkedQueue<IEventListener3<T1, T2, T3>>();

	public void fire(T1 t1, T2 t2, T3 t3) throws Exception {
		for (IEventListener3<T1, T2, T3> listener : listeners) {
			listener.eventFired(t1, t2, t3);
		}
	}
	
	public int size() {
		return listeners.size();
	}
	
	public boolean isEmpty() {
		return listeners.isEmpty();
	}	

	public IEventListener3<T1, T2, T3> addEventListener(IEventListener3<T1, T2, T3> listener) {
		listeners.add(listener);
		return listener;
	}

	public void removeEventListener(IEventListener3<T1, T2, T3> listener) {
		listeners.remove(listener);
	}
	
	public void clear() {
		listeners.clear();
	}
	
	public void forward(final Event3<T1, T2, T3> target) {
		listeners.add(new IEventListener3<T1, T2, T3>() {
			public void eventFired(T1 t1, T2 t2, T3 t3) throws Exception {
				target.fire(t1, t2, t3);
			}			
		});
	}
}