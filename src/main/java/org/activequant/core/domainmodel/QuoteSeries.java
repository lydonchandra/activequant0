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

import java.util.List;

import org.activequant.core.domainmodel.CandleSeries.RangePolicy;
import org.activequant.core.types.TimeStamp;
import org.activequant.util.exceptions.ValueNotFoundException;
import org.activequant.util.tools.Arrays;



/**
 * 
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [08.07.2007] Created (Ulrich Staudinger)<br>
 *  - [23.06.2007] moved to new domain model (Erik Nijkamp)<br>
 *  - [05.08.2007] Fixed clone() (Erik Nijkamp)<br>
 *  - [28.09.2007] removed baseclass, removed instrumentspec (Erik Nijkamp)<br>
 *  - [28.09.2007] moved to new domain model (Erik Nijkamp)<br>
 *  - [06.11.2007] moved generic functionality to TimeSeries<T> (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public class QuoteSeries extends TimeSeries<Quote> {

	private static final long serialVersionUID = -3722093387498618030L;
	
    /**
     * constructor.
     */
    public QuoteSeries() {
    	
    }

    /**
     * constructor.
     * @param list
     */
    public QuoteSeries(List<Quote> quotes) {
    	super(quotes);
    }
    
    public QuoteSeries(SeriesSpecification spec, List<Quote> quotes) {
    	super(quotes);
    	setSeriesSpecification(spec);
    }
    
    
    /**
     * constructor.
     * @param candles
     */
    public QuoteSeries(Quote... quotes) {
    	this(Arrays.asList(quotes));
    }
    
    public QuoteSeries(SeriesSpecification spec, Quote... quotes) {
    	this(quotes);
    	setSeriesSpecification(spec);
    }
    
    
    public void setQuotes(Quote... quotes) {
    	setQuotes(Arrays.asList(quotes));
    }
    
    public void setQuotes(List<Quote> quotes) {
    	clear();
    	addAll(quotes);
    }
    
    public Quote[] getQuotes() {
    	return Arrays.asArray(this, Quote.class);
    }
	
    /**
     * clone this instance.
     * @return cloned object
     */
	public QuoteSeries clone() {
		Quote[] quotes = new Quote[size()];
		Quote[] clonedQuotes = toArray(new Quote[]{});
		for(int i = 0; i < quotes.length; i++) {
			clonedQuotes[i] = quotes[i].clone();
		}
		QuoteSeries clone = new QuoteSeries(seriesSpecification, clonedQuotes);
		return clone;
	}
	
    public QuoteSeries getTimeFrame(TimeStamp start, TimeStamp end) {
    	return (QuoteSeries) super.getTimeFrame(start, end);
    }
	
	public QuoteSeries subList(int start, int end) throws ValueNotFoundException {
		return (QuoteSeries) super.subList(start, end);
	}
	
	public QuoteSeries subList(TimeStamp start, TimeStamp end) throws ValueNotFoundException {
		return (QuoteSeries) super.subList(start, end);
	}
	
	public QuoteSeries subList(TimeStamp start, TimeStamp end, RangePolicy policy) throws ValueNotFoundException {
		return (QuoteSeries) super.subList(start, end, policy);
	}
}