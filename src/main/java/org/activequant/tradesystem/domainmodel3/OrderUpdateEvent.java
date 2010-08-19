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
 * Event that confirms order update.
 * It holds the original (before-update) image of the order.
 * The most current updated order can be found in {@link OrderTicket#getOrder()}.
 * <br>
 * <b>History:</b><br>
 *  - [11.12.2007] Created (Mike Kroutikov)<br>
 *  - [16.12.2007] Replaced OrderTicket with Order + added afterImage (Erik Nijkamp)<br>
 *
 *  @author Mike Kroutikov
 */
public class OrderUpdateEvent extends OrderEvent {
	private Order beforeImage;
	private Order afterImage;

	/**
	 * Creates empty event.
	 */
	public OrderUpdateEvent() { }
	
	public OrderUpdateEvent(Order beforeImage, Order afterImage, TimeStamp stamp, String message, Order image) {
		super(afterImage, stamp, message);
		this.beforeImage = beforeImage;
		this.afterImage = afterImage;		
	}
	
	/**
	 * Image of the order before the update has been applied.
	 * 
	 * @return order image.
	 */
	public Order getBeforeImage() {
		return beforeImage;
	}
	
	/**
	 * Sets the before-update order image.
	 * 
	 * @param val order image.
	 */
	public void setBeforeImage(Order val) {
		beforeImage = val;
	}
	
	public String toString() {
		return "OrderUpdateEvent: order=" + getOrder()
		+ ", eventTimeStamp=" + getEventTimeStamp() + ", beforeImage=" + beforeImage
		+ ", afterImage=" + afterImage;
	}

	public Order getAfterImage() {
		return afterImage;
	}

	public void setAfterImage(Order afterImage) {
		this.afterImage = afterImage;
	}
}