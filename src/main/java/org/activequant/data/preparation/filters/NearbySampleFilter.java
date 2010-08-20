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
import org.activequant.core.domainmodel.Sample;
import org.activequant.core.types.TimeStamp;
import org.activequant.core.util.TimeStampFormat;
import org.activequant.data.preparation.FilterBase;





/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [23.02.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class NearbySampleFilter extends FilterBase {
	
	public enum AdjustPolicy { INCREMENT, FEASIBLE };
	
	private AdjustPolicy adjustPolicy = AdjustPolicy.FEASIBLE;
	
	private Sample sample;
	
	public NearbySampleFilter(TimeStamp startDate, TimeStamp endDate) {
		this.sample = new Sample(startDate, endDate);
	}
	
	public NearbySampleFilter(String startDate, String endDate) {
		TimeStampFormat format = new TimeStampFormat("dd/MM/yyyy");
		this.sample = new Sample(format.parse(startDate), format.parse(endDate));
	}
	
	public NearbySampleFilter(Sample sample) {
		this.sample = sample;
	}

	public CandleSeries[] process(CandleSeries... series) throws Exception {
		if(!checkDatesAlignment(series)) {
			throw new IllegalArgumentException("Aligned dated series required.");
		}
		CandleSeries[] newSeries = new CandleSeries[series.length];
		if (adjustPolicy == AdjustPolicy.INCREMENT) {
			int start = findNearbyDate(series[0], sample.getStartTimeStamp());
			int end = findNearbyDate(series[0], sample.getEndTimeStamp());
			for (int i = 0; i < series.length; i++) {
				newSeries[i] = series[i].subList(start, end + 1);
			}
		} else {
			for (int i = 0; i < series.length; i++) {
				newSeries[i] = series[i].subList(sample.getStartTimeStamp(), sample
						.getEndTimeStamp(), CandleSeries.RangePolicy.FEASIBLE);
			}
		}
		return newSeries;
	}
	
	private boolean checkDatesAlignment(CandleSeries... series) {
		for(int i = 0; i < series[0].getTimeStamps().length; i++) {
			for(int j = 1; j < series.length; j++) {
				assert (series[0].size() == series[j].size()) : "series[0].size() != series[j].size()";
				if (!series[0].getTimeStamps()[i].equals(series[j].getTimeStamps()[i]))
					return false;
			}
		}
		return true;
	}
	
	private int findNearbyDate(CandleSeries series, TimeStamp date) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date.getDate());
		TimeStamp lastSeriesDate = series.firstElement().getTimeStamp();
		while (true) {
			// match?
			if (series.containsDate(date)) {
				return series.getTimeStampPosition(date);
			}
			// next date
			calendar.add(Calendar.DATE, 1);
			date = new TimeStamp(calendar.getTime());			
			// end?			
			if (!date.isBefore(lastSeriesDate)) {
				throw new Exception("Cannot find nearby date for '" + date
						+ "'.");
			}			
		}		
	}
}
