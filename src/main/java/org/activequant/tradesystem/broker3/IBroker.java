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

import org.activequant.tradesystem.domainmodel3.Order;
import org.activequant.tradesystem.domainmodel3.OrderEvent;
import org.activequant.tradesystem.domainmodel3.OrderTicket;
import org.activequant.tradesystem.types.BrokerId;
import org.activequant.util.pattern.events.IEventListener;
import org.activequant.util.pattern.events.IEventSource;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [26.04.2006] Created (Erik N.)<br>
 *  - [05.08.2006] Created (Ulrich S.)<br>
 *  - [10.06.2007] Removed getOpenPosition method (Ulrich Staudinger)<br>
 *  - [11.07.2007] Kicked implementation specific methods (Erik N.)<br>
 *  - [05.08.2007] Added events (Erik N.)<br>
 *  - [06.11.2007] Adding order cancel methods (Ulrich Staudinger)<br>
 *  - [16.12.1007] Moving tracker functionality to Broker (Erik Nijkamp)<br>
 * <br>
 *
 *  @author Erik Nijkamp
 *  @author Ulrich Staudinger
 */
//TODO WIP -> RC3
public interface IBroker {
	
    /**
     * returns the specific broker id of this implementation. 
     * @return
     */
    BrokerId getBrokerID();
   
    /**
     * Submits the order. Before this method is called, order ticket
     * is not available (brokerAssignedId et al). Events can start flowing 
     * only after <code>submit</code> is called. Therefore, attach all your
     * event listeners before submitting the order.
     * 
     * @param order
     * @throws Exception
     */
    void placeOrder(Order order) throws Exception;
    
    /**
     * Submits the order. Before this method is called, order ticket
     * is not available (brokerAssignedId et al). 
     * 
     * @param order
     * @param listener
     * @throws Exception
     */
    void placeOrder(Order order, IEventListener<OrderEvent> listener) throws Exception;
    
    /**
     * Updates pending order (optional operation).
     * 
     * @param order the order to replace the original one.
     * @throws Exception
     */
    void updateOrder(Order order) throws Exception;
    
    /**
     * Cancels pending order.
     * 
     * @param order
     * @throws Exception
     */
    void cancelOrder(Order order) throws Exception;
    
    /**
     * Return array of managed orders.
     * 
     * @return
     */
    Order[] getOrders();
    
    /**
     * Returns order ticket (containing the order state and all order-related events).
     * 
     * @param order
     * @return order ticket
     */
    OrderTicket getTicket(Order order);

    /**
     * Returns event source for the detailed order-related events.
     * These events are more detailed that the completion event
     * (e.g. show individual executions).
     * 
     * @param order.
     * @return order event source.
     */
    IEventSource<OrderEvent> getEventSource(Order order);
}