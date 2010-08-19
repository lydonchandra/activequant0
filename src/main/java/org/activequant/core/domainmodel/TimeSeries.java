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
import java.util.ListIterator;

import org.activequant.core.domainmodel.CandleSeries.PositionPolicy;
import org.activequant.core.domainmodel.CandleSeries.RangePolicy;
import org.activequant.core.types.TimeStamp;
import org.activequant.util.exceptions.ValueNotFoundException;

/**
 * 
 * Series base class which handles a set of elements and the according specification.<br>
 * Each element within the collection is unique, cannot be stored twice and belongs<br>
 * to this collection.<br>
 * <br>
 * <b>History:</b><br>
 *  - [16.06.2007] Created (Ulrich Staudinger)<br>
 *  - [16.06.2007] Added some generic functionality (Erik Nijkamp)<br>
 *  - [23.06.2007] moved to new domain model (Erik Nijkamp)<br>
 *  - [14.08.2007] refactored contractSpec to seriesSpecification (Ulrich Staudinger)<br>
 *  - [27.09.2007] @JoinColumn + removed array mess (Erik Nijkamp)<br>
 *  - [28.09.2007] added generic functionality (Erik Nijkamp)<br>
 *  - [19.11.2007] Adding apply series specification method (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public abstract class TimeSeries<T extends MarketDataEntity> extends ArrayList<T> {	
		
	protected Long id;
	protected SeriesSpecification seriesSpecification;
	
	protected TimeSeries() {
		
	}
	
	protected TimeSeries(SeriesSpecification seriesSpecification) {
		this.seriesSpecification = seriesSpecification;
	}
	
	protected TimeSeries(List<T> seriesData) {
		super(seriesData);
	}
	
	/**
	 * @return the id
	 */    
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getSymbolName() {
		return getInstrumentSpecification().getSymbol().toString();
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return seriesSpecification.toString();
	}
	
	public boolean hasName() {
		return seriesSpecification != null;
	}
	
	public void applyInstrumentSpecification(InstrumentSpecification instrumentSpecification) {
		assert(instrumentSpecification != null);
		seriesSpecification.setInstrumentSpecification(instrumentSpecification);
		for(T object : this) {
			object.setInstrumentSpecification(instrumentSpecification);
		}
	}

	public void applySeriesSpecification(SeriesSpecification seriesSpecification) {
		assert(seriesSpecification != null);
		this.seriesSpecification = seriesSpecification;
		for(T object : this) {
			object.setInstrumentSpecification(seriesSpecification.getInstrumentSpecification());
		}
	}
	
	public SeriesSpecification getSeriesSpecification() {
		return seriesSpecification;
	}

	public void setSeriesSpecification(SeriesSpecification seriesSpecification) {
		this.seriesSpecification = seriesSpecification;
	}
	
	public InstrumentSpecification getInstrumentSpecification() {
		assert(seriesSpecification != null);
		return seriesSpecification.getInstrumentSpecification();
	}
	
	public abstract TimeSeries<T> clone();
	
	@SuppressWarnings("unchecked")
	public TimeSeries<T> clonedSubList(int start, int end) {
		assert(start >= 0 && end <= size() && start < end) : "start < 0 || end > size() || start > end";
		TimeSeries<T> clone = (TimeSeries<T>) clone();
		java.util.List<T> subList = super.subList(start, end);
		clone.clear();
		clone.addAll(subList);
		return clone;
	}
	
	
	public T firstElement() {
		assert(!this.isEmpty());
		return get(0);
	}
	
	public T lastElement() {
		assert(!this.isEmpty());
		return this.get(size()-1);
	}
	
    
    /**
     * returns the position of the given date within the array
     * @param date
     * @return
     * @throws ValueNotFoundException
     */
	public int getTimeStampPosition(TimeStamp date) throws ValueNotFoundException {
		ListIterator<T> iter = this.listIterator();
		while (iter.hasNext()) {
			if (iter.next().getTimeStamp().equals(date))
				return iter.previousIndex();
		}
		throw new ValueNotFoundException("Cannot find value for date '" + date
				+ "'.");
	}
	
    /**
     * returns the position of the given date within the array
     * @param date
     * @return
     * @throws ValueNotFoundException
     */
	protected int getFeasibleTimeStampPosition(TimeStamp date) throws ValueNotFoundException {
        ListIterator<T> iter = this.listIterator();
		while (iter.hasNext()) {
			T next = iter.next();
			if (next.getTimeStamp().isEqual(date)
					|| next.getTimeStamp().isBefore(date) )
				return iter.previousIndex();
		}
		throw new ValueNotFoundException("Cannot find value for date '"
				+ date + "'.");
	}
	
    /**
     * returns the position of the given date within the array
     * @param date
     * @return
     * @throws ValueNotFoundException
     */
	public int getTimeStampPosition(TimeStamp date, PositionPolicy policy)
			throws ValueNotFoundException {
		if (policy == PositionPolicy.EXACT) {
			return getTimeStampPosition(date);
		} else {
			return getFeasibleTimeStampPosition(date);
		}
	}	

	protected int getLastFeasiblePositionTimeStamp(TimeStamp date)
			throws ValueNotFoundException {
		ListIterator<T> iter = listIterator();
		while (iter.hasNext()) {
			T t = iter.next();
			if (t.getTimeStamp().equals(date)) {
				return iter.previousIndex();
			} else if(t.getTimeStamp().isBefore(date)) {
				return iter.previousIndex()-1;
			}
		}
		throw new ValueNotFoundException("Cannot find value for date '" + date + "'.");
	}
    
	
	public boolean containsDate(TimeStamp date) {
		for(TimeStamp checkDate: getTimeStamps()) {
			if(checkDate.isEqual(date))
				return true;
		}
		return false;
	}
	
    
    public TimeStamp [] getTimeStamps() {
        TimeStamp [] dates = new TimeStamp[size()];
        int idx = 0;
        for (T t : this) {
        	dates[idx++] = t.getTimeStamp();
        }
        return dates;
    }
    
    public void setTimeStamps(TimeStamp[] dates) {
        int idx = 0;
        for (T t : this) {
        	t.setTimeStamp(dates[idx++]);
        }
    }
    
	public T getByTimeStamp(TimeStamp date) throws ValueNotFoundException {
		return get(getTimeStampPosition(date));
	}
	
	public T getByFeasibleTimeStamp(TimeStamp date) throws ValueNotFoundException {
		return get(getFeasibleTimeStampPosition(date));
	}
	
	public void removeByTimeStamp(TimeStamp date) throws ValueNotFoundException {
		remove(getByTimeStamp(date));
	}
	

    /**
     * method to fetch a subset / timeframe of this series.
     * 
     * @param start
     * @param end
     * @return
     */
    public List<T> getTimeFrame(TimeStamp start, TimeStamp end) {
    	if(!start.isBefore(end)) {
    		throw new IllegalArgumentException("start must be before end: start=" + start + ", end=" + end);
    	}
    	List<T> list = clone();
    	list.clear();
        for (T t : this) {
            if ((t.getTimeStamp().isAfter(start) && t.getTimeStamp().isBefore(end))
            	|| t.getTimeStamp().equals(start) || t.getTimeStamp().equals(end) ) {
            	list.add(t);
            }
        }
        return list;
    }
	
	public List<T> subList(int start, int end) {
		assert(start >= 0 && end <= size() && start < end) : "start < 0 || end > size() || start > end";
		List<T> list = clone();
		list.clear();
		List<T> subList = super.subList(start, end);
		list.addAll(subList);
		return list;
	}
	
	public List<T> subList(TimeStamp start, TimeStamp end) throws ValueNotFoundException {
		int startPos = getTimeStampPosition(start);
		int endPos = getTimeStampPosition(end);
		return subList(startPos, endPos);
	}
	
	public List<T> subList(TimeStamp start, TimeStamp end, RangePolicy policy)
			throws ValueNotFoundException {
		if (policy == RangePolicy.EXACT) {
			int startPos = getTimeStampPosition(start);
			int endPos = getTimeStampPosition(end);
			return subList(endPos, startPos);
		} else if (policy == RangePolicy.ALL) {
			int startPos = getTimeStampPosition(start) + 1;
			int endPos = getTimeStampPosition(end);
			return subList(endPos, startPos);
		} else {
			int startPos = getLastFeasiblePositionTimeStamp(start) + 1;
			int endPos = getFeasibleTimeStampPosition(end);
			return subList(endPos, startPos);
		}
	}
}