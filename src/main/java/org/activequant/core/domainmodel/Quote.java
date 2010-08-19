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

import java.util.ArrayList;
import java.util.List;

import org.activequant.core.types.TimeStamp;
import org.activequant.util.tools.Arrays;

import static org.activequant.util.tools.IdentityUtils.safeCompare;
import static org.activequant.util.tools.IdentityUtils.safeHashCode;
import static org.activequant.util.tools.IdentityUtils.equalsTo;


/**
 * Price quote (bid/ask price and size pair).
 * <br>
 * <em>IMPORTANT</em>: This object has "value" identity. 
 * It means that its identity
 * (hashCode, equals, and comapreTo) is determined by subset of the fields,
 * declared "business keys". Other fields are considered a "payload".
 * This identity reflects the suggested unique key constraints in the
 * related sql table.
 * <em>IMPORTANT</em>: If this object is added to a collection that relies on
 * identity, changing business key will break the collection 
 * contract and willcause unexpected results.
 * 
 * <br>
 * Business keys for Quote are:
 * <ul>
 * <li><code>{@link #getInstrumentSpecification() instrumentSpecification}</code>
 * <li><code>{@link #getTimeStamp() date}</code>
 * <li><code>{@link #getBidPrice() bidPrice}</code>
 * <li><code>{@link #getBidQuantity() bidSize}</code>
 * <li><code>{@link #getAskPrice() askPrice}</code>
 * <li><code>{@link #getAskQuantity() askSize}</code>
 * </ul>
 * 
 * <br>
 * <b>History:</b><br>
 *  - [02.06.2007] Created (Ulrich Staudinger)<br>
 *  - [14.06.2007] Changed prices + sizes to Double (us)<br>
 *  - [23.06.2007] Moved to new domain model (Erik Nijkamp)<br>
 *  - [24.06.2007] Added functionality (Erik Nijkamp)<br>
 *  - [24.08.2007] Consistent value types (Erik Nijkamp)<br>
 *  - [25.08.2007] Domain model cleanup (Erik Nijkamp)<br>
 *  - [07.10.2007] Added proper object identity code (hashcode, equals) (Erik Nijkamp)<br>
 *  - [08.10.2007] Aligned identity with comparable (Mike Kroutikov)<br>
 *  - [02.11.2007] Fixed constructors + removed duplicate date member (Erik Nijkamp)<br>
 *  - [02.11.2007] Removed Double functions, added askPrice setter (Erik Nijkamp)<br>
 *  
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public class Quote extends MarketDataEntity implements Comparable<Quote> {
	
	public static final int NOT_SET = -1;

    private double bidPrice = NOT_SET;
    private double bidQuantity = NOT_SET;
    private double askPrice = NOT_SET;
    private double askQuantity = NOT_SET;

    private List<PriceValue> bidPriceValues = new ArrayList<PriceValue>();
    private List<PriceValue> askPriceValues = new ArrayList<PriceValue>();
    
    public Quote() {
        
    }
    
    public Quote(InstrumentSpecification spec) {
        super(spec);
    }
    
    public Quote(TimeStamp date) {
    	super(date);
    }
    
    public Quote(TimeStamp date, 
    		double bidPrice, 
    		double bidQuantity,
			double askPrice, 
			double askQuantity) {
		super(date);
		this.bidPrice = bidPrice;
		this.bidQuantity = bidQuantity;
		this.askPrice = askPrice;
		this.askQuantity = askQuantity;
	}
    
    public Quote(InstrumentSpecification spec,
    		TimeStamp date, 
    		double bidPrice, 
    		double bidQuantity,
			double askPrice, 
			double askQuantity) {
    	this(date, bidPrice, bidQuantity, askPrice, askQuantity);
    	setInstrumentSpecification(spec);
	}
    
    /**
     * return object's state as string.
     * @return text
     */
    @Override
	public String toString() {
		return " Date: " + getTimeStamp() + " BidSize : " + bidQuantity + " BidPrice: "
				+ bidPrice + " AskSize : " + askQuantity + " AskPrice: "
				+ askPrice;
	}

    /**
     * clone this instance.
     * @return cloned object
     */
    @Override
	public Quote clone() {
		return new Quote(getInstrumentSpecification(), getTimeStamp(), 
				bidPrice, bidQuantity,
				askPrice, askQuantity);
	}

    /**
     * {@inheritDoc}
     */
	public int compareTo(Quote other) {
		// ATTENTION: keep in sync with hashCode();
		int rc;
		
		if((rc = safeCompare(getInstrumentSpecification(), other.getInstrumentSpecification())) != 0) {
			return rc;
		}
		if((rc = safeCompare(this.getTimeStamp(), other.getTimeStamp())) != 0) {
			return rc;
		}
		if((rc = safeCompare(this.bidPrice, other.bidPrice)) != 0) {
			return rc;
		}
		if((rc = safeCompare(this.bidQuantity, other.bidQuantity)) != 0) {
			return rc;
		}
		if((rc = safeCompare(this.askPrice, other.askPrice)) != 0) {
			return rc;
		}
		if((rc = safeCompare(this.askQuantity, other.askQuantity)) != 0) {
			return rc;
		}

		return 0;
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
	public int hashCode() {
		// ATTENTION: keep in sync with comapreTo();
        return safeHashCode(getInstrumentSpecification())
        	+ safeHashCode(this.getTimeStamp())
        	+ safeHashCode(this.bidPrice)
        	+ safeHashCode(this.bidQuantity)
        	+ safeHashCode(this.askPrice)
        	+ safeHashCode(this.askQuantity);
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
    	// NOTE: delegates to compareTo()
    	return equalsTo(this, other);
	}
    
    public double getAskPrice() {
        return askPrice;
    }    

	public void setAskPrice(double askPrice) {
		this.askPrice = askPrice;
	}

    public double getAskQuantity() {
        return askQuantity;
    }
    
    public void setAskQuantity(double askSize) {
        this.askQuantity = askSize;
    }
    
    public double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public double getBidQuantity() {
        return bidQuantity;
    }

    public void setBidQuantity(double bidSize) {
        this.bidQuantity = bidSize;
    }

	public static class PriceValue {
        private double price;
        private double quantity;

        public void setPrice(double price) {
            this.price = price;
        }

        public void setQuantity(double value) {
            this.quantity = value;
        }

        public double getPrice() {
            return this.price;
        }

        public double getQuantity() {
            return this.quantity;
        }
    }

	public PriceValue[] getBidPriceValues() {
		return bidPriceValues.toArray(new PriceValue[] {});
	}

	public void setBidPriceValues(PriceValue... bidPriceValues) {
		this.bidPriceValues = Arrays.asList(bidPriceValues);
	}
	
	public void setBidPriceValues(List<PriceValue> bidPriceValues) {
		this.bidPriceValues = bidPriceValues;
	}

	public PriceValue[] getAskPriceValues() {
		return askPriceValues.toArray(new PriceValue[] {});
	}

	public void setAskPriceValues(PriceValue... askPriceValues) {
		this.askPriceValues = Arrays.asList(askPriceValues);
	}
	
	public void setAskPriceValues(List<PriceValue> askPriceValues) {
		this.askPriceValues = askPriceValues;
	}
}
