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
package org.activequant.tradesystem.types;

/**
 * @TODO <br>
 * <br>
 * <b>History:</b><br>
 *  - [13.11.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public enum OrderState {
	/**
	 * Newly-created object, never yet submitted to broker.
	 * This is the only state of the Order that does not have 
	 * <code>brokerAssignedId</code> set.
	 */
	NEW,	
    
    /**
     * Has been sent to the broker, but there's no a native id yet.
     */
    PENDING_NEW,
	
	/**
	 * Has been sent to the broker, and broker received it, but not yet 
	 * evaluated the order.
	 */
    PLACED,

    /**
     * Order is partially filled.
     */
    PARTIAL,
    
    /**
     * Order is fully filled. This is the terminal state of an order, no
     * further changes to the Order state or values are allowed.
     */
    FILLED,
    
    /**
     * Order has been rejected by the broker. No executions were done. This is a terminal 
     * state on an Order.
     */
    REJECTED, 
    
    /**
     * Broker confirmed that Order was successfully canceled (at user's request).
     * Order may still be partially filled.
     */
    CANCELED,
    
    /**
     * User requested to cancel this Order. Broker received the request, but is 
     * still evaluating the request.
     */
    PENDING_CANCEL,
    
    /**
     * Order has expired. Order still may be partially filled.
     */
    EXPIRED;
}

