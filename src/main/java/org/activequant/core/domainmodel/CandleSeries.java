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
import org.activequant.data.util.Tuple;
import org.activequant.util.exceptions.ValueNotFoundException;
import org.activequant.util.tools.Arrays;
import org.activequant.util.tools.CheckDateOrder;



/**
 * TimeSeries class. Based on TimeSeries class of ccapi2<br>
 * <br>
 * <b>History:</b><br>
 *  - [03.05.2007] File created (Erik Nijkamp)<br>
 *  - [23.06.2007] Moved to new domain model (Erik Nijkamp)<br>
 *  - [12.07.2007] Finalized clone method (Ulrich Staudinger)<br>
 *  - [05.08.2007] fixed clone() (Erik Nijkamp)<br>
 *  - [31.08.2007] added getClosesAsTuples (Ulrich Staudinger)<br>
 *  - [31.08.2007] added Tuple constructor (Ulrich Staudinger)<br>
 *  - [28.09.2007] removed baseclass, removed instrumentspec (Erik Nijkamp)<br>
 *  - [28.09.2007] moved to new domain model (Erik Nijkamp)<br>
 *  - [06.11.2007] moved generic functionality to TimeSeries<T> (Erik Nijkamp)<br>
 *  
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public class CandleSeries extends TimeSeries<Candle> {

	private static final long serialVersionUID = 7075375398315155768L;
	
	public enum RangePolicy { EXACT, ALL, FEASIBLE };
	public enum PositionPolicy { EXACT, FEASIBLE };
	
	/**
	 * plain constructor.
	 *
	 */
	public CandleSeries() {
		super();
	}
	
	/**
	 * constructor.
	 * @param specs
	 * @param candles
	 *
	 */
	public CandleSeries(Candle... candles) {
		setCandles(candles);
	}
	
	public CandleSeries(SeriesSpecification spec, Candle... candles) {
		this(candles);
		setSeriesSpecification(spec);
	}
	
    /**
     * constructor for building candle list from tuples. 
     * @param list
     */
    public CandleSeries(InstrumentSpecification spec, List<Tuple<TimeStamp, Double>> tuples) {
    	for(Tuple<TimeStamp, Double> t : tuples){
    		Candle c = new Candle();
    		c.setInstrumentSpecification(spec);
    		c.setTimeStamp(t.getObject1());
    		c.setClosePrice(t.getObject2());
    		this.add(c);
    	}
    	assert(CheckDateOrder.isOrderValid(this));
    }

    /**
     * constructor.
     * @param list
     */
    public CandleSeries(List<Candle> candles) {
    	super(candles);
    	assert(CheckDateOrder.isOrderValid(this));
    }
        
    public CandleSeries(TimeStamp[] dates, 
    		double[] opens, 
    		double[] highs,
			double[] lows, 
			double[] closes) {
		// members
    	for(int i = 0; i < dates.length; i++) {
    		add(new Candle(dates[i], opens[i], highs[i], lows[i], closes[i]));
    	}
    	assert(CheckDateOrder.isOrderValid(this));
    }
    
    public CandleSeries(SeriesSpecification spec,
    		TimeStamp[] dates, 
    		double[] opens, 
    		double[] highs,
			double[] lows, 
			double[] closes) {
    	super(spec);
		// members
    	for(int i = 0; i < dates.length; i++) {
    		add(new Candle(getInstrumentSpecification(), dates[i], opens[i], highs[i], lows[i], closes[i]));
    	}
    	assert(CheckDateOrder.isOrderValid(this));
    }
    
    @Override
	public void applySeriesSpecification(SeriesSpecification seriesSpecification) {
		assert(seriesSpecification!=null);
		this.seriesSpecification = seriesSpecification;
		for(Candle object : this) {
			object.setInstrumentSpecification(seriesSpecification.getInstrumentSpecification());
			object.setTimeFrame(seriesSpecification.getTimeFrame());
		}
	}
    
    public void setCandles(Candle... candles) {
    	setCandles(Arrays.asList(candles));
    }
    
    public void setCandles(List<Candle> candles) {
    	assert(CheckDateOrder.isOrderValid(this));
    	clear();
    	addAll(candles);
    }
    
    public Candle[] getCandles() {
    	return Arrays.asArray(this, Candle.class);
    }

    /**
	 * 
	 * @return the low candle of this series.
	 */
    public Candle getLow() {
        Candle min = get(0);
        for (Candle c : this) {
            if (c.getLowPrice() < min.getLowPrice()) {
            	min = c;
            }
        }
        return min;
    }

    /**
     * 
     * @return the high candle of this series.
     */
    public Candle getHigh() {
    	Candle max = get(0);
        for (Candle c : this) {
            if (c.getHighPrice() > max.getHighPrice()) {
            	max = c;
            }
        }
        return max;
    }
    
    public void setOpens(double[] doubles) {
    	assert(doubles.length == size()) : "length invalid";
    	for(int i = 0; i < size(); i++) {
    		get(i).setOpenPrice(doubles[i]);
    	}
    }
    
    public void setHighs(double[] doubles) {
    	assert(doubles.length == size()) : "length invalid";
    	for(int i = 0; i < size(); i++) {
    		get(i).setHighPrice(doubles[i]);
    	}
    }
    
    public void setLows(double[] doubles) {
    	assert(doubles.length == size()) : "length invalid";
    	for(int i = 0; i < size(); i++) {
    		get(i).setLowPrice(doubles[i]);
    	}
    }
    
    public void setCloses(double[] doubles) {
    	assert(doubles.length == size()) : "length invalid";
    	for(int i = 0; i < size(); i++) {
    		get(i).setClosePrice(doubles[i]);
    	}
    }
    
    /**
     * returns all close values in a double array. As a candleseries gets longer, 
     * this method takes more time, as it does not implement any sort of caching.
     * It simply generates a double array, copies all close values into it and
     * returns the array. (Reminder: could be improved.)
     * @return
     */
    public double[] getCloses() {
        double[] doubles = new double[size()];
        int idx = 0;
        for (Candle c : this) {
        	doubles[idx++] = c.getClosePrice();
        }
        return doubles;
    }

    public List<Tuple<TimeStamp, Double>> getClosesAsTuples(){
    	List<Tuple<TimeStamp, Double>> ret = new ArrayList<Tuple<TimeStamp, Double>>();
    	for(Candle c : this) {
    		Tuple<TimeStamp, Double> t = new Tuple<TimeStamp, Double>();
    		t.setObject1(c.getTimeStamp());
    		t.setObject2(c.getClosePrice());
    		ret.add(t);
    	}
    	return ret;
    }
    
    public double[] getOpens() {
        double[] doubles = new double[size()];
        int idx = 0;
        for (Candle c : this) {
        	doubles[idx++] = c.getOpenPrice();
        }
        return doubles;
    }

    public double[] getHighs() {
        double[] doubles = new double[size()];
        int idx = 0;
        for (Candle c : this) {
        	doubles[idx++] = c.getHighPrice();
        }
        return doubles;
    }
    
    public double[] getLows() {
        double[] doubles = new double[size()];
        int idx = 0;
        for (Candle c : this) {
        	doubles[idx++] = c.getLowPrice();
        }
        return doubles;
    }
    
    public double[][] getDoubles() {
    	return new double[][] { getOpens(), getHighs(), getLows(), getCloses() };
    }
    
    public void setDoubles(double[][] doubles) {
    	assert(doubles.length == 4) : "array length != 4";
    	setOpens(doubles[0]);
    	setHighs(doubles[1]);
    	setLows(doubles[2]);
    	setCloses(doubles[3]);
    }
    
	public CandleSeries clone() {
		Candle[] clonedCandles = new Candle[size()];
		Candle[] candles = toArray(new Candle[]{});
		for(int i = 0; i < candles.length; i++) {
			clonedCandles[i] = candles[i].clone();
		}
		CandleSeries clone = new CandleSeries(seriesSpecification, clonedCandles);
		return clone; 
	}
	
    public CandleSeries getTimeFrame(TimeStamp start, TimeStamp end) {
    	return (CandleSeries) super.getTimeFrame(start, end);
    }
	
	public CandleSeries subList(int start, int end) throws ValueNotFoundException {
		return (CandleSeries) super.subList(start, end);
	}
	
	public CandleSeries subList(TimeStamp start, TimeStamp end) throws ValueNotFoundException {
		return (CandleSeries) super.subList(start, end);
	}
	
	public CandleSeries subList(TimeStamp start, TimeStamp end, RangePolicy policy) throws ValueNotFoundException {
		return (CandleSeries) super.subList(start, end, policy);
	}
}