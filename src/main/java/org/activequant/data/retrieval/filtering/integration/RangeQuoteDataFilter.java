package org.activequant.data.retrieval.filtering.integration;

import org.activequant.core.domainmodel.Quote;
import org.activequant.data.retrieval.filtering.IDataFilter;

/**
 * Filters quote basing on its price and size values.
 * 
 * Makes sure that both <code>price</code> and <code>quantity</code> are within
 * specified range.
 * Ranges are controlled by the properties:
 * <li>
 *    <ul> {@link #getMinPrice() minPrice} - minimal allowed price, default <code>0</code>.
 *    <ul> {@link #getMaxPrice() maxPrice} - maximal allowed price, default <code>Double.MAX_VALUE</code>.
 *    <ul> {@link #getMinQuantity() minQuantity} - minimal allowed quantity, default <code>0</code>.
 *    <ul> {@link #getMaxQuantity() maxQuantity} - maximal allowed quantity, default <code>Double.MAX_VALUE</code>.
 *    <ul> {@link #getMinSpread() minSpread} - minimal allowed spread, default <code>0</code>.
 *    <ul> {@link #getMaxSpread() maxSpread} - maximum allowed spread, default <code>Double.MAX_VALUE</code>.
 *    <ul> {@link #isAllowNotSetValues() allowNotSetValues} - whether to allow events with NOT_SET values, default <code>true</code>.
 * </li>
 * As one can see, default values practically do not filter anything (except, possibly, 
 * unset values).
 * <br>
 * <b>History:</b><br>
 *  - [04.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class RangeQuoteDataFilter implements IDataFilter<Quote> {
	
	private double minPrice = 0.;
	private double maxPrice = Double.MAX_VALUE;
	
	private double minQuantity = 0.;
	private double maxQuantity = Double.MAX_VALUE;
	
	private double minSpread = 0.;
	private double maxSpread = Double.MAX_VALUE;
	
	private boolean allowNotSetValues = true;

	/**
	 * Minimal allowed price. To filter out zero-priced quotes, set this to
	 * a small value (for example, <code>0.00001</code>). The default value
	 * of <code>0.0</code> allows zero-priced quotes.
	 * 
	 * @return min price value.
	 */
	public double getMinPrice() {
		return minPrice;
	}
	
	/**
	 * Sets minimal allowed price.
	 * 
	 * @param val value.
	 */
	public void setMinPrice(double val) {
		minPrice = val;
	}
	
	/**
	 * Maximum allowed price.
	 * 
	 * @return max price value.
	 */
	public double getMaxPrice() {
		return maxPrice;
	}
	
	/**
	 * Sets maximum allowed price.
	 * 
	 * @param val value.
	 */
	public void setMaxPrice(double val) {
		maxPrice = val;
	}
	
	/**
	 * Minimal allowed quantity. To filter quotes with zero quantity, set it
	 * to some small number, e.g. <code>0.00001</code>. Default value is <code>0.0</code>,
	 * which allows zeroes to pass thru this filter.
	 * 
	 * @return min quantity.
	 */
	public double getMinQuantity() {
		return minQuantity;
	}
	
	/**
	 * Sets minimal allowed quantity.
	 * 
	 * @param val value.
	 */
	public void setMinQuantity(double val) {
		minQuantity = val;
	}
	
	/**
	 * Maximum allowed quantity. Default is <code>Double.MAX_VALUE</code>.
	 * 
	 * @return max quantity.
	 */
	public double getMaxQuantity() {
		return maxQuantity;
	}
	
	/**
	 * Sets maximum allowed quantity.
	 * 
	 * @param val value.
	 */
	public void setMaxQuantity(double val) {
		maxQuantity = val;
	}

	/**
	 * Minimal allowed spread. Default value is <code>0.0</code>. This means that
	 * quotes with negative spread are rejected.
	 * 
	 * @return min spread value.
	 */
	public double getMinSpread() {
		return minSpread;
	}
	
	/**
	 * Sets minimal allowed spread.
	 * 
	 * @param val value.
	 */
	public void setMinSpread(double val) {
		minSpread = val;
	}

	/**
	 * Maximum allowed spread. Default value is <code>Double.MAX_VALUE</code>.
	 * 
	 * @return max spread value.
	 */
	public double getMaxSpread() {
		return maxSpread;
	}
	
	/**
	 * Sets maximum allowed spread.
	 * 
	 * @param val value.
	 */
	public void setMaxSpread(double val) {
		maxSpread = val;
	}

	/**
	 * Controls whether partial quotes are allowed (quotes one half of which is
	 * not set). Default is <code>true</code>. Note that quotes that are completely
	 * unset (both bid and ask sides are unset) are rejected regardless of the value 
	 * of this flag.
	 * 
	 * @return value.
	 */
	public boolean isAllowNotSetValues() {
		return allowNotSetValues;
	}
	/**
	 * Sets whether partially set quotes are allowed.
	 * 
	 * @param val true/false.
	 */
	public void setAllowNotSetValues(boolean val) {
		allowNotSetValues = val;
	}
	
	public boolean evaluate(Quote data) {
		double askPrice = data.getAskPrice();
		double bidPrice = data.getAskPrice();
		double askQuantity = data.getAskQuantity();
		double bidQuantity = data.getAskQuantity();
		
		if(allowNotSetValues) {
			if(askPrice == Quote.NOT_SET && askQuantity == Quote.NOT_SET) {
				if(bidPrice < minPrice || bidPrice > maxPrice) {
					return false;
				} 
				if(bidQuantity < minQuantity || bidQuantity > maxQuantity) {
					return false;
				}
				
				return true; // spread test not applicable
			}
	
			if(bidPrice == Quote.NOT_SET && bidQuantity == Quote.NOT_SET) {
				if(askPrice < minPrice || askPrice > maxPrice) {
					return false;
				} 
				if(askQuantity < minQuantity || askQuantity > maxQuantity) {
					return false;
				}
				
				return true; // spread test not applicable
			}
		}

		if(askPrice < minPrice || askPrice > maxPrice) {
			return false;
		} 
		if(askQuantity < minQuantity || askQuantity > maxQuantity) {
			return false;
		}

		if(bidPrice < minPrice || bidPrice > maxPrice) {
			return false;
		} 
		if(bidQuantity < minQuantity || bidQuantity > maxQuantity) {
			return false;
		}

		if(askPrice - bidPrice < minSpread || askPrice - bidPrice > maxSpread) {
			return false;
		}

		return true;
	}
}
