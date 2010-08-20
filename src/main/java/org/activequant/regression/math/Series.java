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
package org.activequant.regression.math;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import org.activequant.core.types.TimeStamp;
import org.activequant.util.exceptions.ValueNotFoundException;
import org.activequant.util.tools.Arrays;



/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [31.05.2006] Created (Erik Nijkamp)<br>
 *
 *
 *
 *  @author Erik Nijkamp
 */
public class Series {
	
	private double[] values = null;
	private TimeStamp[] dates = null;
	private TreeMap<TimeStamp, Double> cache = new TreeMap<TimeStamp, Double>();
	private String name = null;
	
	public Series(TimeStamp[] dates, double[] values) {
		setData(dates, values);
	}
	
	public Series(String name, TimeStamp[] dates, double[] values) {
		this(dates, values);
		this.name = name;
	}
	
	public Series(Vector<TimeStamp> dates, Vector<Double> values) {
		setData(dates, values);
	}
	
	private void rebuildCache() {
		cache.clear();
		for(int i = 0; i < values.length; i++) {
			cache.put(dates[i], values[i]);
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean hasName() {
		return name != null;
	}
	
	public int getLength() {
		return values.length;
	}
	
	public double[] getValues() {
		double[] newValues = new double[values.length];
		System.arraycopy(values, 0, newValues, 0, values.length);
		return newValues;
	}

	public double getValue(int i) {
		assert(i >= 0 && i < getLength());
		return values[i];
	}
	
	public void setData(TimeStamp[] dates, double[] values) {
		assert(dates != null && values != null) : "dates == null || values == null";
		assert(dates.length == values.length) : "dates.length != values.length";
		assert(dates[1].isAfter(dates[0])) : "wrong date order";
		this.values = values;
		this.dates = dates;
		rebuildCache();
	}
	
	public void setData(Vector<TimeStamp> dates, Vector<Double> values) {
		assert(dates != null && values != null) : "dates == null || values == null";
		assert(dates.size() == values.size()) : "dates.size() != values.size()";
		assert(dates.get(1).isAfter(dates.get(0))) : "wrong date order";
		this.dates = dates.toArray(new TimeStamp[dates.size()]);
		this.values = new double[values.size()];
		for(int i = 0; i < values.size(); i++) {
			this.values[i] = values.get(i);
		}
		rebuildCache();
		
	}
	
	public void setValue(int i, double value) {
		assert(i >= 0 && i < getLength());
		
		values[i] = value;
		rebuildCache();
	}

	public TimeStamp[] getDates() {
		TimeStamp[] newDates = new TimeStamp[dates.length];
		System.arraycopy(dates, 0, newDates, 0, dates.length);
		return newDates;
	}
	
	public void setDates(TimeStamp[] dates) {
		assert(dates != null) : "dates == null";
		this.dates = dates;
		rebuildCache();
	}
	
	public void setValues(double[] values) {
		assert(values != null) : "values == null";
		this.values = values;
	}
	
	public TimeStamp getDate(int i) {
		assert(i >= 0 && i < getLength());
		return dates[i];
	}
	
	public boolean containsDate(TimeStamp date) {
		return cache.containsKey(date);
	}
	
	public double getValueByDate(TimeStamp date) throws ValueNotFoundException {
		return cache.get(date);
	}
	
	public int getDatePosition(TimeStamp date) throws ValueNotFoundException {
		List<TimeStamp> dates = new LinkedList<TimeStamp>();
		dates.addAll(Arrays.asList(this.dates));
		int pos = Collections.binarySearch(dates, date);
		if (pos < 0)
			throw new ValueNotFoundException("Cannot find value for date '"
					+ date + "'.");
		return pos;
	}
	
	public TimeStamp getLastDate() {
		return dates[dates.length-1];
	}
	
	public Series getSubset(int start, int end) {
		assert(start >= 0 && end < getLength()) : "start < 0 || end > getLength()";
		Series newSeries = clone();
		int len = end - start + 1;
		TimeStamp[] newDates = new TimeStamp[len];
		System.arraycopy(dates, start, newDates, 0, len);
		double[] newValues = new double[len];
		System.arraycopy(values, start, newValues, 0, len);
		newSeries.setData(newDates, newValues);
		return newSeries;
	}
	
	public Series clone() {
		TimeStamp[] newDates = new TimeStamp[dates.length];
		System.arraycopy(dates, 0, newDates, 0, dates.length);
		double[] newValues = new double[values.length];
		System.arraycopy(values, 0, newValues, 0, values.length);
		return new Series(name, newDates, newValues);
	}
	
	public void remove(int pos) {
		assert(pos < getLength()) : "pos > getLength()";
		// dates
		Vector<TimeStamp> dates = new Vector<TimeStamp>();
		dates.addAll(Arrays.asList(this.dates));
		dates.remove(pos);		
		this.dates = dates.toArray(new TimeStamp[dates.size()]);
		// values
		Vector<Double> values = new Vector<Double>();
		for(int i = 0; i < this.values.length; i++) {
			values.add(this.values[i]);
		}
		values.remove(pos);		
		this.values = new double[values.size()];
		for(int i = 0; i < values.size(); i++) {
			this.values[i] = values.get(i);
		}
		// cache
		rebuildCache();
	}
	
	public void remove(TimeStamp date) throws ValueNotFoundException {
		remove(getDatePosition(date));
	}
}