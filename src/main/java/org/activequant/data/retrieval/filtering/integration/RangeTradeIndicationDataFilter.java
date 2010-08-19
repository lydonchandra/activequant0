package org.activequant.data.retrieval.filtering.integration;

import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.data.retrieval.filtering.IDataFilter;

/**
 * Filters trade indication basing on its price and size values.
 * 
 * Makes sure that both <code>price</code> and <code>quantity</code> are within
 * specified range.
 * Ranges are controlled by the properties:
 * <li>
 *    <ul> {@link #getMinPrice() minPrice} - minimal allowed price, default <code>0</code>.
 *    <ul> {@link #getMaxPrice() maxPrice} - maximal allowed price, default <code>Double.MAX_VALUE</code>.
 *    <ul> {@link #getMinQuantity() minQuantity} - minimal allowed quantity, default <code>0</code>.
 *    <ul> {@link #getMaxQuantity() maxQuantity} - maximal allowed quantity, default <code>Double.MAX_VALUE</code>.
 * </li>
 * As one can see, default values practically do not filter anything (except, possibly, 
 * unset values).
 * <br>
 * <b>History:</b><br>
 *  - [04.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class RangeTradeIndicationDataFilter implements IDataFilter<TradeIndication> {
	
	private double minPrice = 0.;
	private double maxPrice = Double.MAX_VALUE;
	
	private double minQuantity = 0.;
	private double maxQuantity = Double.MAX_VALUE;
	
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

	public boolean evaluate(TradeIndication data) {

		if(data.getPrice() < minPrice || data.getPrice() > maxPrice) {
			return false;
		}
		
		if(data.getQuantity() < minQuantity || data.getQuantity() > maxQuantity) {
			return false;
		}

		return true;
	}

}
