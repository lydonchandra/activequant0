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

/**
 * Describes closed order status. This information is generally
 * an aggregation of the more detailed information stored in
 * the list of order's events. 
 * <p>
 * This event is a simulated event that is always the last event issued
 * for an order. Interested parties can wait only for
 * this event (and ignore all events of different types), if they do not want to
 * analyze the detailed order-related events.
 * <p>
 * If {@link #getTotalQuantity() totalQuantity} property is positive, then
 * order has been fully or partially filled.
 * <p>
 * If {@link #getTerminalError() terminalError} property is not null, then
 * order has been terminated prematurely (rejected or canceled). It still may
 * be partially filled.
 * <p>
 * Fully filled orders must have null <code>terminalError</code>.
 * <p>
 * Orders with null <code>terminalError</code> must be completely filled.
 * <p>
 * If <code>terminalError</code> is not null, then one have to examine 
 * <code>totalQuantity</code> to determine whether there were partial 
 * executions or not. 
 * <br>
 * <b>History:</b><br>
 *  - [11.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public final class OrderCompletionEvent extends OrderEvent {

	private double averagePrice;
	private double totalQuantity;
	private double totalCommission;  // FIXME: do we need currency for commission?

	private OrderEvent terminalError;
	
	/**
	 * Average fill price. If order did not receive any fills, the value is zeron.
	 * 
	 * @return average fill price.
	 */
	public double getAveragePrice() {
		return averagePrice;
	}
	
	/**
	 * Sets average fill price.
	 * 
	 * @param val price.
	 */
	public void setAveragePrice(double val) {
		averagePrice = val;
	}

	/**
	 * Total processed quantity.
	 * 
	 * @return quantity.
	 */
	public double getTotalQuantity() {
		return totalQuantity;
	}
	
	/**
	 * Sets total quantity.
	 * 
	 * @param totalQuantity quantity.
	 */
	public void setTotalQuantity(double totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	/**
	 * Total commission applied to this transaction.
	 * 
	 * @return commission.
	 */
	public double getTotalCommission() {
		return totalCommission;
	}
	
	/**
	 * Sets total commission.
	 * 
	 * @param totalCommission commission value.
	 */
	public void setTotalCommission(double totalCommission) {
		this.totalCommission = totalCommission;
	}
	
	/**
	 * If order processing was terminated abnormally (rejected, canceled),
	 * this will contain the error event explaining it. If order has been
	 * fully filled, this property will be null.
	 * 
	 * @return order error event.
	 */
	public OrderEvent getTerminalError() {
		return terminalError;
	}
	
	/**
	 * Sets order error event.
	 * 
	 * @param terminalError event.
	 */
	public void setTerminalError(OrderEvent terminalError) {
		this.terminalError = terminalError;
	}

	public String toString() {
		return "OrderTicketCompletion: averagePrice=" + averagePrice
		+ ", totalQuantity=" + getTotalQuantity() 
		+ ", totalCommission=" + totalCommission
		+ ", terminalError=" + terminalError;
	}
}