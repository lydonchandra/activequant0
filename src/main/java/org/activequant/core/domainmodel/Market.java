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
import java.util.HashMap;
import java.util.List;

import org.activequant.core.types.TimeStamp;
import org.activequant.core.util.MarketIterator;
import org.activequant.core.util.MarketParameterMap;
import org.activequant.util.exceptions.ValueNotFoundException;
import org.activequant.util.tools.Arrays;



/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [01.03.2007] Created (Ulrich Staudinger)<br>
 *  - [05.05.2007] Cleanup (Erik Nijkamp)<br>
 *  - [07.05.2007] Changed parameters approach (Erik Nijkamp)<br>
 *  - [16.05.2007] Added market date (Ulrich Staudinger)<br>
 *  - [23.06.2007] Moved to new domain model (Erik Nijkamp)<br>
 *  - [25.06.2007] Added containtsX methods (Erik Nijkamp)<br>
 *  - [26.06.2007] fixed Array.asList method calls which produced unmodifiable lists (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public class Market implements Cloneable {
	
	//TODO Fix -> RC3
	/* TODO this class was/is used for backtesting (en)
	 it should contain:
	 - historical data
	 - a list of candles
	 - quotes
	 - tradeindications
	 - market parameters
	 - "current" backtesting date
	 - the backtesting "sample" timeframe => and data which extends this sample
	   (otherwise backtesting would not work for the wirst run with lag(-2)
	*/
	
    // sample contains the validity date of this market, i.e. if only a sample of this market contains valid information
    private Sample sample = null;
//    private List<TradeIndicationSeries> tradeSeries = new ArrayList<TradeIndicationSeries>();
//    private List<QuoteSeries> quoteSeries = new ArrayList<QuoteSeries>(); 
	private List<CandleSeries> candleSeries = new ArrayList<CandleSeries>();
	private MarketParameterMap marketParameters = new MarketParameterMap();

    private TimeStamp marketTimeStamp = null; 

	
	public Market() {

	}
	
	public Market(TimeStamp sampleStart, TimeStamp sampleEnd) {
		sample = new Sample(sampleStart, sampleEnd);
	}
	
	public Market(Sample snapshot) {
		this.sample = snapshot;
	}
	
	public Market(CandleSeries... series) {
		this.candleSeries = new ArrayList<CandleSeries>(Arrays.asList(series));
	}
	
	public Market(Sample snapshot, CandleSeries... series) {
		this(snapshot);
		this.candleSeries = new ArrayList<CandleSeries>(Arrays.asList(series));
	}
	
	public Market(TimeStamp sampleStart, TimeStamp sampleEnd, CandleSeries... series) {
		this(new Sample(sampleStart, sampleEnd), series);
	}
    
    /**
     * method to get the list of timeseries
     * @return
     */
	public CandleSeries[] getCandleSeries() {
		return candleSeries.toArray(new CandleSeries[] {});
	}
    
    public boolean containsCandleSeries(SeriesSpecification spec) {
        for(CandleSeries cl : candleSeries){
            if(cl.getSeriesSpecification().equals(spec))
            	return true; 
        }
        return false;
    }
    
    public CandleSeries getCandleSeries(SeriesSpecification spec) throws ValueNotFoundException {
        for(CandleSeries cl : candleSeries){
            if(cl.getSeriesSpecification().equals(spec))
            	return cl; 
        }
        throw new ValueNotFoundException("No such series : "+spec.toString());
    }

    /**
     * method to set the market snapshot in whole
     * @param marketSnapshot
     */
	public void setCandleSeries(CandleSeries... marketSnapshot) {
		this.candleSeries = Arrays.asList(marketSnapshot);
	}
	
	/**
	 * add a series to the current snapshot
	 * @param series
	 */
	public void addCandleSeries(CandleSeries... series) {
		candleSeries.addAll(Arrays.asList(series));
	}
	
	/**
	 * add a series to the current snapshot
	 * @param series
	 */
	public void addCandleSeries(int position, CandleSeries series) {
		candleSeries.add(position, series);
	}
	
	/**
	 * add a series to the current snapshot
	 * @param series
	 */
	public void setCandleSeries(int position, CandleSeries series) {
		candleSeries.set(position, series);
	}
	
    /**
     * cleaner. 
     *
     */
    public void removeAllCandleSeries(){
        candleSeries = new ArrayList<CandleSeries>();
    }
   
    /**
     * method to fetch a market parameter. 
     * @param key
     * @return
     * @throws NoSuchParameter
     */
    public Object getMarketParameter(TimeStamp date, String key)
			throws ValueNotFoundException {
		return marketParameters.get(date, key);
	}
    
    /**
     * method to set a market parameter. 
     * @param key
     * @param value
     */
    public void setMarketParameter(TimeStamp date, String key, double value){
        marketParameters.put(date, key, value);
    }
    
    public boolean containsMarketParameters(TimeStamp date) {
    	return marketParameters.containsKey(date);
    }

    public HashMap<String, Object> getMarketParameters(TimeStamp date) {
    	HashMap<String, Object> map = marketParameters.get(date);
        return (map != null ? map : new HashMap<String, Object>());
    }

    public void setMarketParameters(TimeStamp date, HashMap<String, Object> marketWideParameters) {
        this.marketParameters.put(date, marketWideParameters);
    }
    
    public MarketIterator iterator() {
    	return new MarketIterator(this);
    }
    
    public MarketIterator iterator(int startPosition) throws Exception {
    	MarketIterator iterator = new MarketIterator(this);
        for(int i=0;i<startPosition;i++){
            iterator.next();
        }
        return iterator;
    }
    
    /**
     * returns the submarket for a given position in time 
     * @param startPosition
     * @return
     * @throws Exception
     */
    public Market getSubMarket(int startPosition) throws Exception {
        Market ret = (Market)clone();
        ret.removeAllCandleSeries();
        
        for(CandleSeries cs : this.candleSeries){
            CandleSeries csNew = cs.subList(startPosition, cs.size()-1);
            ret.addCandleSeries(csNew);
        }
        ret.setMarketTimeStamp(ret.getCandleSeries()[0].get(0).getTimeStamp());
        return ret; 
    }
    
    
    /**
     * returns the amount of candles in a given timeseries object in this market
     * @return
     */
    public int size() {
    	return candleSeries.get(0).size();
    }

	/**
	 * @return the snapshot
	 */
	public Sample getSample() {
		return sample;
	}

	/**
	 * @param snapshot the snapshot to set
	 */
	public void setSample(Sample sample) {
		this.sample = sample;
	}

    public TimeStamp getMarketTimeStamp() {
        return marketTimeStamp;
    }

    public void setMarketTimeStamp(TimeStamp marketTimeStamp) {
        this.marketTimeStamp = marketTimeStamp;
    }
}
