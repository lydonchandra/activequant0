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

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.apache.log4j.Logger;


/**
 * This describes an open position. <br>
 * <br>
 * <b>History:</b><br>
 *  - [27.04.2006] Created (Erik N.) <br>
 *  - [07.05.2006] Added lots of stuff, especially the stop code. (us)<br>
 *  - [10.06.2006] Moved stop code to order / broker management.
 *  - [14.07.2007] Added persistence (Erik Nijkamp)<br>
 *  - [29.09.2007] Removed annotations (Erik Nijkamp)<br>
 *
 *	@author Erik Nijkamp
 *  @author Ulrich Staudinger
 */
public class Position {

	protected final static Logger log = Logger.getLogger(Position.class);

	private Long id;
	
	/**
	 * the underlying of this position, what do you hold in this position
	 */
	private InstrumentSpecification instrumentSpecification = null; 


	/**
	 * specifies the amount of contracts that you hold in a given instrument.
	 * 
	 * negative amount means you hold a short position. 
	 * positive amount means you hold a long position. 
	 */
	private double quantity = 0;

	/**
	 * this value describes the average price of this position. 
	 */
	private double averagePrice = 0.0;
	
	public Position() {

	}
	
	public Position(InstrumentSpecification spec, 
			double avgPrice,
			double amount) {
		this.instrumentSpecification = spec;
		this.quantity = amount;
		this.averagePrice = avgPrice;
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
	
	public void setInstrumentSpecification(InstrumentSpecification spec) {
		this.instrumentSpecification = spec;
	}

	public InstrumentSpecification getInstrumentSpecification() {
		return instrumentSpecification;
	}

	public double getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(double price) {
		this.averagePrice = price;
	}
	
	/**
	 * negative numbers for short positions, positive numbers for long positions. 
	 * @return
	 */
	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double amount) {
		this.quantity = amount;
	}

	/**
	 * returns the difference between handed over price and open price of this
	 * position. Takes into consideration the direction of this position.
	 * This does not take into consideration the amount! 
	 * 
	 * @param price
	 * @return
	 */
	public double getPriceDifference(double price) {
		
		if (this.quantity > 0 ) {
			return price - this.averagePrice;
		} else {
			return this.averagePrice - price;
		}
	}

	@Override
	public String toString() {
		String ret = "";
		ret += "ID: " + id + " ";
		ret += "Instrument: " + instrumentSpecification + " ";
		ret += "Quantity: " + quantity + " ";
		ret += "AveragePrice: " + averagePrice + " ";
		return ret;
	}

}
