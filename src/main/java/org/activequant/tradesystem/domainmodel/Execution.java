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
import org.activequant.core.types.TimeStamp;

/**
 * An order execution class. <br>
 * <br>
 * <b>History:</b><br>
 *  - [09.06.2007] Created (Ulrich Staudinger)<br>
 *  - [14.07.2007] Added persistence (Erik Nijkamp)<br>
 *  - [29.09.2007] Removed annotations (Erik Nijkamp)<br>
 *  - [02.11.2007] Added constructor (Erik Nijkamp)<br>
 *  - [13.11.2007] Replaced order id with reference (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 */
public class Execution {
	
	private Long id;
	
	private Order order = null;
	private double executionQuantity = 0.0; 
	private double executionPrice = 0.0; 
	private TimeStamp executionTimeStamp = null;
	
	public Execution() {
		
	}
	
	public Execution(Order order,
			double executionQuantity,
			double executionPrice,
			TimeStamp executionTimeStamp) {
		this.order = order;
		this.executionQuantity = executionQuantity;
		this.executionPrice = executionPrice;
		this.executionTimeStamp = executionTimeStamp;
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

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
	
	public InstrumentSpecification getInstrumentSpecification() {
		assert(order != null);
		return order.getInstrumentSpecification();
	}

	/**
	 * contains only positive numbers. 
	 * @return
	 */
	public double getExecutionQuantity() {
		return executionQuantity;
	}

	public void setExecutionQuantity(double val) {
		this.executionQuantity = val;
	}

	public double getExecutionPrice() {
		return executionPrice;
	}

	public void setExecutionPrice(double executionPrice) {
		this.executionPrice = executionPrice;
	}

	public TimeStamp getExecutionTimeStamp() {
		return executionTimeStamp;
	}

	public void setExecutionTimeStamp(TimeStamp executionTimeStamp) {
		this.executionTimeStamp = executionTimeStamp;
	}
    
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(" ExID: ").append(getId());
		sb.append(" / ExDate:").append(executionTimeStamp);
		sb.append(" / Price:").append(executionPrice);
		sb.append(" / Amnt:").append(executionQuantity);
		sb.append(" / OID:").append(order.getBrokerAssignedId());
		
		return sb.toString();
	}
    
}