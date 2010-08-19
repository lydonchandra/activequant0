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
package org.activequant.tradesystem.domainmodel3;

import org.activequant.core.types.TimeStamp;

/**
 * Non-terminal order event error: order stays open.
 * For example, this can be an event informing user that <code>update()</code>
 * of the order was not successful.
 * <p>
 * <b>History:</b><br>
 *  - [11.12.2007] Created (Mike Kroutikov)<br>
 *  - [16.12.2007] Replaced OrderTicket with Order (Erik Nijkamp)<br>
 *
 *  @author Mike Kroutikov
 */
public final class OrderErrorEvent extends OrderEvent { 

	/**
	 * Creates empty error event.
	 */
	public OrderErrorEvent() { }
	
	/**
	 * Creates populated error event.
	 * 
	 * @param stamp time stamp.
	 * @param message message.
	 */
	public OrderErrorEvent(Order order, TimeStamp stamp, String message) {
		super(order, stamp, message);
	}

	public String toString() {
		return "OrderErrorEvent: order=" + getOrder()
		+ ", eventTimeStamp=" + getEventTimeStamp()
		+ ", message=" + getMessage();
	}
}
