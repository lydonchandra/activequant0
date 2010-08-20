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
package org.activequant.data.preparation.filters;

import java.util.Calendar;

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.types.TimeStamp;
import org.activequant.data.preparation.FilterBase;





/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [10.11.2006] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class LagFilter extends FilterBase {
	
	public enum AdjustPolicy { EXTEND, RELOCATE };
	
	private int lag;
	private Calendar calendar = Calendar.getInstance();
	private AdjustPolicy policy = AdjustPolicy.EXTEND;
	
	public LagFilter(int lag) {
		this.lag = lag;
	}
	
	public LagFilter(int lag, AdjustPolicy policy) {
		this.lag = lag;
		this.policy = policy;
	}
	
	public CandleSeries[] process(CandleSeries... series) throws Exception {
		return (policy == AdjustPolicy.EXTEND ? extend(series) : relocate(series));
	}
	
	private CandleSeries[] extend(CandleSeries[] series) throws Exception {
		if (lag == 0) {
			return series;
		}
		CandleSeries[] newSeries = cloneSeries(series);
		for (int i = 0; i < series.length; i++) {
			// values
			newSeries[i] = (lag < 0 ? extendNegativeLag(newSeries[i])
					: extendPositiveLag(newSeries[i]));
		}
		return newSeries;
	}
	
	private CandleSeries extendNegativeLag(CandleSeries series) {		
		TimeStamp lastDate = series.lastElement().getTimeStamp();
		double[][] valuesArray = series.getDoubles();
		double[][] newValuesArray = new double[valuesArray.length][];

		// ohlc data
		for (int i = 0; i < valuesArray.length; i++) {
			double[] values = valuesArray[i];
			double[] newValues = new double[values.length - lag];
			System.arraycopy(values, 0, newValues, -lag, values.length);
			newValuesArray[i] = newValues;
		}

		// dates
		TimeStamp[] newDates = new TimeStamp[series.size() - lag];
		// fill lag days
		calendar.setTime(lastDate.getDate());
		for (int j = 0; j < -lag; j++) {
			calendar.add(Calendar.DATE, 1);
			newDates[series.size() + j] = new TimeStamp(calendar.getTime());
		}
		// copy old dates
		System.arraycopy(series.getTimeStamps(), 0, newDates, 0, series.size());
		
		// apply
		CandleSeries newSeries = series.clone();
		newSeries.setDoubles(newValuesArray);
		newSeries.setTimeStamps(newDates);		
		return newSeries;
	}
	
	private CandleSeries extendPositiveLag(CandleSeries series) {		
		TimeStamp lastDate = series.lastElement().getTimeStamp();
		double[][] valuesArray = series.getDoubles();
		double[][] newValuesArray = new double[valuesArray.length][];

		// ohlc data
		for (int i = 0; i < valuesArray.length; i++) {
			double[] values = valuesArray[i];
			double[] newValues = new double[values.length + lag];
			System.arraycopy(values, 0, newValues, 0, values.length);
			newValuesArray[i] = newValues;
		}

		// dates
		TimeStamp[] newDates = new TimeStamp[series.size() + lag];
		// fill lag days
		calendar.setTime(lastDate.getDate());
		for (int j = 0; j < lag; j++) {
			newDates[j] = new TimeStamp(calendar.getTime());
			calendar.add(Calendar.DATE, 1);
		}
		// copy old dates
		System.arraycopy(series.getTimeStamps(), 0, newDates, lag, series.size());
		
		// apply
		CandleSeries newSeries = series.clone();
		newSeries.setDoubles(newValuesArray);
		newSeries.setTimeStamps(newDates);		
		return newSeries;
	}
	
	private CandleSeries[] relocate(CandleSeries[] series) throws Exception {
		if(lag == 0) {
			return series;
		}
		CandleSeries[] newSeries = cloneSeries(series);
		for (int i = 0; i < series.length; i++) {
			// values
			newSeries[i] = (lag < 0 ? relocateNegativeLag(newSeries[i])
					: relocatePositiveLag(newSeries[i]));
		}
		return newSeries;
	}
	
	private CandleSeries relocateNegativeLag(CandleSeries series) {		
		double[][] valuesArray = series.getDoubles();
		double[][] newValuesArray = new double[valuesArray.length][];

		// ohlc data
		for (int i = 0; i < valuesArray.length; i++) {
			double[] values = valuesArray[i];
			double[] newValues = new double[values.length];
			System.arraycopy(values, 0, newValues, -lag, values.length+lag);
			newValuesArray[i] = newValues;
		}

		// dates
		TimeStamp[] newDates = new TimeStamp[series.size()];
		// copy old dates
		System.arraycopy(series.getTimeStamps(), 0, newDates, 0, series.size());
		
		// apply
		CandleSeries newSeries = series.clone();
		newSeries.setDoubles(newValuesArray);
		newSeries.setTimeStamps(newDates);		
		return newSeries;
	}
	
	private CandleSeries relocatePositiveLag(CandleSeries series) {		
		double[][] valuesArray = series.getDoubles();
		double[][] newValuesArray = new double[valuesArray.length][];

		// ohlc data
		for (int i = 0; i < valuesArray.length; i++) {
			double[] values = valuesArray[i];
			double[] newValues = new double[values.length];
			System.arraycopy(values, lag, newValues, 0, values.length-lag);
			newValuesArray[i] = newValues;
		}

		// dates
		TimeStamp[] newDates = new TimeStamp[series.size()];
		// copy old dates
		System.arraycopy(series.getTimeStamps(), 0, newDates, 0, series.size());
		
		// apply
		CandleSeries newSeries = series.clone();
		newSeries.setDoubles(newValuesArray);
		newSeries.setTimeStamps(newDates);		
		return newSeries;
	}
}
