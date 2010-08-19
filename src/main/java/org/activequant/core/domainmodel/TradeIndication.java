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
package org.activequant.core.domainmodel;

import static org.activequant.util.tools.IdentityUtils.safeCompare;
import static org.activequant.util.tools.IdentityUtils.safeHashCode;
import static org.activequant.util.tools.IdentityUtils.equalsTo;

import org.activequant.core.types.TimeStamp;

/**
 * Represents a single price move of the market.
 * <br>
 * <em>IMPORTANT</em>: This object has "value" identity. 
 * It means that its identity
 * (hashCode, equals, and comapreTo) is determined by subset of the fields,
 * declared "business keys". Other fields are considered a "payload".
 * This identity reflects the suggested unique key constraints in the
 * related sql table.
 * <em>IMPORTANT</em>: If this object is added to a collection that relies on
 * identity, changing business key will break the collection 
 * contract and will cause unexpected results.
 * 
 * <br>
 * Business keys for TradeIndication are:
 * <ul>
 * <li><code>{@link #getInstrumentSpecification() instrumentSpecification}</code>
 * <li><code>{@link #getTimeStamp() date}</code>
 * <li><code>{@link #getPrice() price}</code>
 * <li><code>{@link #getQuantity() quantity}</code>
 * </ul>
 * 
 * <br>
 * <b>History:</b><br>
 *  - [02.06.2007] Created (Ulrich Staudinger)<br>
 *  - [23.06.2007] moved to new domain model (Erik Nijkamp)<br>
 *  - [24.06.2007] Added functionality (Erik Nijkamp)<br>
 *  - [24.08.2007] Consistent value types (Erik Nijkamp)<br>
 *  - [30.09.2007] Added equals (Erik Nijkamp)<br>
 *  - [07.10.2007] Added proper object identity code (hashcode, equals) (Erik Nijkamp)<br>
 *  - [08.10.2007] Aligned identity with comparable (Mike Kroutikov)<br>
 *  
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public class TradeIndication extends MarketDataEntity implements Comparable<TradeIndication> {
	
	public static final int NOT_SET = -1;
	
	/**
     * contains the current quotation, the '*kurs*'  / paid price.
     */
    private double price = NOT_SET;
    
    /**
     * the quantity for this quotation 
     */
    private double quantity = 0;    

    /**
     * just a very flat constructor.
     *
     */
    public TradeIndication() {
    	
    }
    
    public TradeIndication(InstrumentSpecification spec) {
    	super(spec);
    }
    
    public TradeIndication(InstrumentSpecification spec, double tick) {
    	super(spec);
    	this.price = tick;  
    }
    

    public TradeIndication(InstrumentSpecification spec, double tick, long quantity) {
    	super(spec);
    	this.price = tick;  
    	this.quantity = quantity; 
    }
    
    
    /**
     * constructor.
     * @param priceIndication
     *
     */
    public TradeIndication(double tick) {
    	this.price = tick;    	
    }
    
    public TradeIndication(TimeStamp date, double tick) {
    	super(date);
    	this.price = tick;
    }
    
    /**
     * constructor.
     * @param date
     * @param priceIndication
     * @param quantity
     *
     */
    public TradeIndication(TimeStamp date, double tick, double volume) {
    	super(date);
    	this.price = tick;
    	this.quantity = volume;
    }
    
    public TradeIndication(InstrumentSpecification spec, TimeStamp date, double tick, double volume) {
    	super(spec, date);
    	this.price = tick;
    	this.quantity = volume;
    }
    
    /**
     * return object's state as string.
     * @return text
     */
    @Override
	public String toString(){
		return " Date: " + getTimeStamp() + " TradeIndication: " + price + " Volume: " + quantity;
	}

    /**
     * clone this instance.
     * @return cloned object
     */
    @Override
	public TradeIndication clone() {
		return new TradeIndication(getInstrumentSpecification(), getTimeStamp(), 
				price, quantity);
	}

    /**
     * {@inheritDoc}
     */
	public int compareTo(TradeIndication other) {
		// ATTENTION: keep in sync with hashCode();
		int rc;
		
		if((rc = safeCompare(this.getInstrumentSpecification(), 
				other.getInstrumentSpecification())) != 0) {
			return rc;
		}
		if((rc = safeCompare(this.getTimeStamp(), other.getTimeStamp())) != 0) {
			return rc;
		}
		if((rc = safeCompare(this.price, other.price)) != 0) {
			return rc;
		}
		if((rc = safeCompare(this.quantity, other.quantity)) != 0) {
			return rc;
		}

		return 0;
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
	public int hashCode() {
		// ATTENTION: keep in sync with compareTo();
        return safeHashCode(this.getInstrumentSpecification())
        	+ safeHashCode(this.getTimeStamp())
        	+ safeHashCode(this.price)
        	+ safeHashCode(this.quantity);
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
    	return equalsTo(this, other);
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double tick) {
		this.price = tick;
	}

    public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double volume) {
		this.quantity = volume;
	}
}