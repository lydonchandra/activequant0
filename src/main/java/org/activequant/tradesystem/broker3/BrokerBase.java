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
package org.activequant.tradesystem.broker3;

import java.util.IdentityHashMap;
import java.util.Map;

import org.activequant.tradesystem.domainmodel3.Order;
import org.activequant.tradesystem.domainmodel3.OrderEvent;
import org.activequant.tradesystem.types.BrokerId;
import org.activequant.util.pattern.events.Event;
import org.activequant.util.pattern.events.IEventListener;
import org.activequant.util.pattern.events.IEventSource;

/**
 * @TODO
 * <b>History:</b><br>
 *  - [17.11.2007] Created (Erik N.)<br>
 *
 *  @author Erik Nijkamp
 */
public abstract class BrokerBase implements IBroker {
    
	private class EventSource extends Event<OrderEvent> {		
		Order order;		
		public EventSource(Order order) {
			this.order = order;
		}		
	}	
	
    private final BrokerId brokerID = new BrokerId(getClass());
    
    private Map<Order, EventSource> events = new IdentityHashMap<Order, EventSource>();
    
    public BrokerBase() {

    }

    public BrokerId getBrokerID() {
    	return brokerID;
    }
    
    public void placeOrder(Order order, IEventListener<OrderEvent> listener) throws Exception {
    	getEventSource(order).addEventListener(listener);
    	placeOrder(order);
    }
    
    public IEventSource<OrderEvent> getEventSource(Order order) {
    	if(events.containsKey(order)) {
    		return events.get(order);
    	} else {
    		EventSource source = new EventSource(order);
    		events.put(order, source);
    		return source;
    	}
    }
}