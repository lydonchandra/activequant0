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
package org.activequant.tradesystem.domainmodel;

import org.activequant.core.types.TimeStamp;

/**
 * 
 * Resembles a transaction for the transaction book. <br>
 * <br>
 * <b>History:</b><br>
 *  - [13.06.2007] Created (Ulrich Staudinger)<br>
 *  - [14.07.2007] Added persistence (Erik Nijkamp)<br>
 *  - [14.07.2007] Added constructor etc. (Erik Nijkamp)<br>
 *  - [29.09.2007] Removed annotations (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 */
public class Transaction {

	private Long id;
	private TimeStamp stamp; 
	private double sum; 
	
	public Transaction() {

	}
	
	public Transaction(TimeStamp date, double sum) {
		this.stamp = date;
		this.sum = sum;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public boolean hasId() {
		return id != null;
	}


	public TimeStamp getTimeStamp() {
		return stamp;
	}


	public void setTimeStamp(TimeStamp date) {
		this.stamp = date;
	}

	public double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}
}