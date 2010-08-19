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
package org.activequant.tradesystem.domainmodel2.event;

import org.activequant.core.types.TimeStamp;
import org.activequant.tradesystem.domainmodel2.Order;
import org.activequant.tradesystem.domainmodel2.OrderTicket;

/**
 * Event that confirms order update.
 * It holds the original (before-update) image of the order.
 * The most current updated order can be found in {@link OrderTicket#getOrder()}.
 * <br>
 * <b>History:</b><br>
 *  - [11.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class OrderUpdateEvent extends OrderEvent {
	private Order updated;

	/**
	 * Creates empty event.
	 */
	public OrderUpdateEvent() { }
	
	public OrderUpdateEvent(TimeStamp stamp, String message, Order updated) {
		super(stamp, message);
		this.updated = updated;
	}
	
	/**
	 * Image of the order after the update has been applied.
	 * 
	 * @return order image.
	 */
	public Order getUpdatedOrder() {
		return updated;
	}
	
	/**
	 * Sets the updated order.
	 * 
	 * @param val order image.
	 */
	public void setUpdatedOrder(Order val) {
		updated = val;
	}
	
	public String toString() {
		return "OrderUpdateEvent: eventTimeStamp=" + getEventTimeStamp() + ", updatedOrder=" + updated;
	}
}