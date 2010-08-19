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

import static org.activequant.util.tools.IdentityUtils.equalsTo;
import static org.activequant.util.tools.IdentityUtils.safeCompare;
import static org.activequant.util.tools.IdentityUtils.safeHashCode;

import org.activequant.core.types.TimeFrame;
import org.activequant.core.types.TimeStamp;
import org.activequant.util.tools.Arrays;


/**
 * Represents an ohlcv candle. This candle contains inner series and quotes, too.<br>
 * 
 * <em>IMPORTANT</em>: This object has "value" identity. 
 * It means that its identity
 * (hashCode, equals, and comapreTo) is determined by subset of the fields,
 * declared "business keys". Other fields are considered a "payload".
 * This identity reflects the suggested unique key constraints in the
 * related sql table.<br>
 * <em>IMPORTANT</em>: If this object is added to a collection that relies on
 * identity, changing business key will break the collection 
 * contract and will cause unexpected results.<br>
 * <em>IMPORTANT</em> {@link #getTimeStamp() timeStamp} property corresponds to the
 * Candle's <em>close</em> time.
 * 
 * <br>
 * Business keys for Candle are:
 * <ul>
 * <li><code>{@link #getInstrumentSpecification() instrumentSpecification}</code>
 * <li><code>{@link #getTimeStamp() timeStamp}</code>
 * <li><code>{@link #getTimeFrame() timeFrame}</code>
 * <li><code>{@link #getOpenPrice() openPrice}</code>
 * <li><code>{@link #getHighPrice() highPrice}</code>
 * <li><code>{@link #getLowPrice() lowPrice}</code>
 * <li><code>{@link #getVolume() volume}</code>
 * <li><code>{@link #getHighTimeStamp() highTimeStamp}</code>
 * <li><code>{@link #getLowTimeStamp() lowTimeStamp}</code>
 * </ul>
 * 
 * <br>
 * <b>History:</b><br>
 *  - [04.05.2007] Created (ustaudinger)<br>
 *  - [29.05.2007] Adding inner time series (ustaudinger)<br>
 *  - [03.06.2007] Initializing the OHLCV values with -1 (ustaudinger)<br>
 *  - [23.06.2007] Moved to new domain model (Erik Nijkamp)<br>
 *  - [30.09.2007] Added equals (Erik Nijkamp)<br>
 *  - [07.10.2007] Added proper object identity code (hashcode, equals) (Erik Nijkamp)<br>
 *  - [08.10.2007] Aligned identity with comparable (Mike Kroutikov)<br>
 *  - [05.11.2007] Fixed cloning (Erik Nijkamp)<br>
 *  
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public class Candle extends MarketDataEntity implements Comparable<Candle> {
	
	public static final int NOT_SET = -1;

    private double openPrice = NOT_SET;
	private double highPrice = NOT_SET;
    private double lowPrice = NOT_SET;
    private double closePrice = NOT_SET;
    private double volume = 0.0;
    private TimeFrame timeFrame = null;
    
    
    /**
     * initialized with date. Contains the time of the high. 
     */
    private TimeStamp highStamp = null;
    
    /**
     * initialized with date. Contains the time of the low. 
     */
    private TimeStamp lowStamp = null;
    
    /**
     * price indications out of which this candle was built. 
     */
    private TradeIndicationSeries innerTicks;
    
    /**
     * inner candle series
     */
    private TimeSeries<Candle> innerCandles;
    
    public Candle() {
    	
    }
    
    public Candle(InstrumentSpecification spec) {
    	setInstrumentSpecification(spec);
    }
    
    public Candle(TimeStamp date, 
    		double openPrice, 
    		double highPrice, 
    		double lowPrice, 
    		double closePrice) {
		super(date);
        this.openPrice = openPrice;
		this.highPrice = highPrice;
    	this.lowPrice = lowPrice;
    	this.closePrice = closePrice;         
    }
    
    public Candle(TimeStamp date, 
    		double openPrice, 
    		double highPrice, 
    		double lowPrice, 
    		double closePrice, 
    		double volume) {
    	this(date, openPrice, highPrice, lowPrice, closePrice);
    	this.volume = volume;
    }
    
    public Candle(InstrumentSpecification spec, 
    		TimeStamp date, 
    		double open, 
    		double high, 
    		double low, 
    		double close, 
    		long volume) {
    	this(date, open, high, low, close);
    	this.volume = volume;
    	setInstrumentSpecification(spec);
    }
    
    public Candle(InstrumentSpecification spec, 
    		TimeStamp date, 
    		double openPrice, 
    		double highPrice, 
    		double lowPrice, 
    		double closePrice, 
    		double volume,
    		TimeFrame timeFrame) {
    	this(date, openPrice, highPrice, lowPrice, closePrice);
    	this.volume = volume;
    	this.timeFrame = timeFrame;
    	this.setInstrumentSpecification(spec);
    }
    
    public Candle(TimeStamp date) {
    	super(date);
    }
    
    public Candle(InstrumentSpecification spec, TimeStamp date) {
    	super(spec, date);
    }
    
    public Candle(InstrumentSpecification spec, TimeStamp date, double... ohcl) {
    	super(spec, date);
		this.openPrice = ohcl[0];
		this.highPrice = ohcl[1];
    	this.lowPrice = ohcl[2];
    	this.closePrice = ohcl[3];
    }
    
    public Candle(TimeStamp date, double... ohcl) {
    	super(date);
		this.openPrice = ohcl[0];
		this.highPrice = ohcl[1];
    	this.lowPrice = ohcl[2];
    	this.closePrice = ohcl[3];
    }
    
    /**
     * clone this instance.
     * @return cloned object
     */
    @Override
	public Candle clone() {
		return new Candle(getInstrumentSpecification(), 
				getTimeStamp(), 
				openPrice, 
				highPrice, 
				lowPrice, 
				closePrice, 
				volume,
				timeFrame);
	}
	
    /**
     * return object's state as string.
     * @return text
     */
    @Override
	public String toString() {
		return " D : " + getTimeStamp() + " O : " + openPrice + " H : " + highPrice + " L : " + lowPrice
				+ " C : " + closePrice + " V : " + volume;
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
	public int hashCode() {
		// ATTENTION: keep in sync with compareTo();
		return safeHashCode(this.getInstrumentSpecification())
			+ safeHashCode(this.getTimeStamp())
			+ safeHashCode(this.timeFrame)
			+ safeHashCode(this.openPrice)
			+ safeHashCode(this.highPrice)
			+ safeHashCode(this.lowPrice)
			+ safeHashCode(this.closePrice)
			+ safeHashCode(this.volume)
			+ safeHashCode(this.highStamp)
			+ safeHashCode(this.lowStamp);
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
    	// NOTE: delegates to compareTo()
    	return equalsTo(this, other);
    }

    /**
     * {@inheritDoc}
     */
	public int compareTo(Candle other) {
		// ATTENTION: keep in sync with hashCode();
		int rc;

		rc = safeCompare(this.getInstrumentSpecification(), other.getInstrumentSpecification());
		if(rc != 0) return rc;
		rc = safeCompare(this.getTimeStamp(), other.getTimeStamp());
		if(rc != 0) return rc;
		rc = safeCompare(this.timeFrame, other.timeFrame);
		if(rc != 0) return rc;
		rc = safeCompare(this.openPrice, other.openPrice);
		if(rc != 0) return rc;
		rc = safeCompare(this.highPrice, other.highPrice);
		if(rc != 0) return rc;
		rc = safeCompare(this.lowPrice, other.lowPrice);
		if(rc != 0) return rc;
		rc = safeCompare(this.closePrice, other.closePrice);
		if(rc != 0) return rc;
		rc = safeCompare(this.volume, other.volume);
		if(rc != 0) return rc;
		rc = safeCompare(this.highStamp, other.highStamp);
		if(rc != 0) return rc;
		rc = safeCompare(this.lowStamp, other.lowStamp);
		if(rc != 0) return rc;
		
		return rc;
	}
    
	/**
	 * OHLC data unset?
	 * @return
	 */
    public boolean isEmpty() {
    	return openPrice == NOT_SET && highPrice == NOT_SET && lowPrice == NOT_SET && closePrice == NOT_SET;
    }
    
	/**
	 * clear ohlc
	 * @return
	 */
    public void reset() {
    	openPrice = highPrice = lowPrice = closePrice = NOT_SET;
    }
    
	/**
	 * @return the close
	 */
	public double getClosePrice() {
		return closePrice;
	}
	
	/**
	 * @param close the close to set
	 */
	public void setClosePrice(double close) {
		this.closePrice = close;
	}
	
	/**
	 * @return the high
	 */
	public double getHighPrice() {
		return highPrice;
	}
	
	/**
	 * @param hi the high to set
	 */
	public void setHighPrice(double high) {
		this.highPrice = high;
	}
	
	/**
	 * @return the low
	 */
	public double getLowPrice() {
		return lowPrice;
	}
	
	/**
	 * @param low the low to set
	 */
	public void setLowPrice(double low) {
		this.lowPrice = low;
	}
	
	/**
	 * @return the open
	 */
	public double getOpenPrice() {
		return openPrice;
	}
	
	/**
	 * @param open the open to set
	 */
	public void setOpenPrice(double open) {
		this.openPrice = open;
	}
	
	/**
	 * @return the volume
	 */
	public double getVolume() {
		return volume;
	}
	
	/**
	 * @param volume the volume to set
	 */
	public void setVolume(double volume) {
		this.volume = volume;
	}
	
	public double[] getDoubles() {
		return new double[] { openPrice, highPrice, lowPrice, closePrice };
	}
	
	public void setDoubles(double[] values) {
		assert(values.length == 4) : "values.length != 4)";
		setOpenPrice(values[0]);
		setHighPrice(values[1]);
		setLowPrice(values[2]);
		setClosePrice(values[3]);
	}
	
	/**
	 * returns true if this candle is rising, means open < close. 
	 * otherwise it returns false
	 **/
    public boolean isRising(){
        return openPrice < closePrice;
    }	

    public TimeStamp getHighTimeStamp() {
    	return highStamp;
    }

    public void setHighTimeStamp(TimeStamp highDate) {
        this.highStamp = highDate;
    }
    
    public TimeStamp getLowTimeStamp() {
        return lowStamp;
    }

    public void setLowTimeStamp(TimeStamp lowDate) {
        this.lowStamp = lowDate;
    }
        
    public TradeIndicationSeries getInnerTicks() {
        return innerTicks;
    }

    public void setInnerTicks(TradeIndicationSeries innerTicks) {
        this.innerTicks = innerTicks;
    }

	/**
	 * @return the innerSeries
	 */
	public Candle[] getInnerCandles() {
		return Arrays.asArray(innerCandles, Candle.class);
	}

	/**
	 * @param innerSeries the innerSeries to set
	 */
	public void setInnerCandles(TimeFrame timeFrame, Candle[] innerSeries) {
		SeriesSpecification spec = new SeriesSpecification(this.getInstrumentSpecification(), timeFrame);
		this.innerCandles = new CandleSeries(spec, innerSeries);
	}
	
	public boolean hasInnerCandles() {
		return innerCandles != null;
	}

	public TimeFrame getTimeFrame() {
		return timeFrame;
	}

	public void setTimeFrame(TimeFrame val) {
		timeFrame = val;
	}

}