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
 * Event sent by broker to confirm order execution (complete or partial).
 * If execution fills the order, it moves to the terminal state (closed).
 * <br>
 * <b>History:</b><br>
 *  - [Dec 11, 2007] Created (Mike)<br>
 *
 *  @author Mike
 */
public class OrderExecutionEvent extends OrderEvent {
	
	private double executionQuantity = 0.0; 
	private double executionPrice = 0.0;
	private double commission = 0.0; // FIXME: can commission be in different currency that the order?
	
	/**
	 * How many contracts were executed (bought/sold).
	 * 
	 * @return quantity.
	 */
	public double getExecutionQuantity() {
		return executionQuantity;
	}

	/**
	 * Sets execution quantity.
	 * 
	 * @param val quantity.
	 */
	public void setExecutionQuantity(double val) {
		this.executionQuantity = val;
	}

	/**
	 * What was the price of transaction.
	 * 
	 * @return price.
	 */
	public double getExecutionPrice() {
		return executionPrice;
	}
	
	/**
	 * Sets the price.
	 * 
	 * @param executionPrice price.
	 */

	public void setExecutionPrice(double executionPrice) {
		this.executionPrice = executionPrice;
	}

	/**
	 * Commission charged by the broker for this transaction.
	 * 
	 * @return commission.
	 */
	public double getCommission() {
		return commission;
	}

	/**
	 * Sets commission.
	 * 
	 * @param commission commission.
	 */
	public void setCommission(double commission) {
		this.commission = commission;
	}

	public String toString() {
		return "OrderExecutionEvent: eventTimeStamp=" + getEventTimeStamp()
		+ ", message=" + getMessage()
		+ ", quantity=" + getExecutionQuantity()
		+ ", price=" + getExecutionPrice()
		+ ", commission=" + getCommission();
	}
}