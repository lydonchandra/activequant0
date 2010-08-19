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
 *  - [Jun 18, 2007] Created (Ulrich Staudinger)<br>
 *  - [23.06.2007] Moved to new domain model (Erik Nijkamp)<br>
 *  - [05.08.2007] Fixed clone() (Erik Nijkamp)<br>
 *  - [28.09.2007] removed baseclass, removed instrumentspec (Erik Nijkamp)<br>
 *  - [28.09.2007] moved to new domain model (Erik Nijkamp)<br>
 *  - [23.10.2007] Added getTradeIndicationPrices and getTradeIndicationQuantities methods (Ulrich Staudinger)<br>
 *  - [06.11.2007] moved generic functionality to TimeSeries<T> (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public class TradeIndicationSeries extends TimeSeries<TradeIndication> {

	private static final long serialVersionUID = -6504260467121352769L;
    
    /**
     * constructor.
     */
    public TradeIndicationSeries() {
    	
    }

    /**
     * constructor.
     * @param list
     */
    public TradeIndicationSeries(List<TradeIndication> tradeIndications) {
    	super(tradeIndications);
    }
    
    public TradeIndicationSeries(SeriesSpecification spec, List<TradeIndication> tradeIndications) {
    	this(tradeIndications);
    	setSeriesSpecification(spec);
    }
    
    /**
     * constructor.
     * @param candles
     */
    public TradeIndicationSeries(SeriesSpecification spec, TradeIndication... ticks) {
    	this(Arrays.asList(ticks));
    	setSeriesSpecification(spec);
    }
	
    public void setTradeIndications(TradeIndication... ticks) {
    	setTradeIndications(Arrays.asList(ticks));	
    }
    
    public void setTradeIndications(List<TradeIndication> tradeIndications) {
    	clear();
    	addAll(tradeIndications);
    }
    
    public TradeIndication[] getTradeIndications() {
    	return Arrays.asArray(this, TradeIndication.class);
    }
    
    /**
     * returns the prices of all trade indications as a double array. 
     * @return
     */
    public double[] getTradeIndicationPrices(){
    	double[] ret = new double[this.size()];
    	for(int i=0;i<this.size();i++){
    		ret[i] = this.get(i).getPrice();
    	}
    	return ret; 
    }
    
    /**
     * returns the underlying quantities as an array of doubles. 
     * @return
     */
    public double[] getTradeIndicationQuantities(){
    	double[] ret = new double[this.size()];
    	for(int i=0;i<this.size();i++){
    		ret[i] = this.get(i).getQuantity();
    	}
    	return ret; 
    }
	
    /**
     * clone this instance.
     * @return cloned object
     */
	public TradeIndicationSeries clone() {
		TradeIndication[] clonedTicks = new TradeIndication[size()];
		TradeIndication[] ticks = toArray(new TradeIndication[] {});
		for(int i = 0; i < ticks.length; i++) {
			clonedTicks[i] = ticks[i].clone();
		}
		TradeIndicationSeries clone = new TradeIndicationSeries(seriesSpecification, clonedTicks);
		return clone;
	}
	
    public TradeIndicationSeries getTimeFrame(TimeStamp start, TimeStamp end) {
    	return (TradeIndicationSeries) super.getTimeFrame(start, end);
    }
	
	public TradeIndicationSeries subList(int start, int end) throws ValueNotFoundException {
		return (TradeIndicationSeries) super.subList(start, end);
	}
	
	public TradeIndicationSeries subList(TimeStamp start, TimeStamp end) throws ValueNotFoundException {
		return (TradeIndicationSeries) super.subList(start, end);
	}
	
	public TradeIndicationSeries subList(TimeStamp start, TimeStamp end, RangePolicy policy) throws ValueNotFoundException {
		return (TradeIndicationSeries) super.subList(start, end, policy);
	}
}