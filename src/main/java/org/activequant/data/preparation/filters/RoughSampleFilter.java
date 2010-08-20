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
import org.activequant.core.domainmodel.CandleSeries.RangePolicy;
import org.activequant.core.types.TimeStamp;
import org.activequant.core.util.TimeStampFormat;
import org.activequant.data.preparation.FilterBase;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [07.10.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class RoughSampleFilter extends FilterBase {
	
	private final static int DAYS_RANGE = 10;

	private int daysRange = DAYS_RANGE;
	private Sample sample;
	
	public RoughSampleFilter(TimeStamp startDate, TimeStamp endDate) {
		this.sample = new Sample(startDate, endDate);
	}
	
	public RoughSampleFilter(String startDate, String endDate) {
		TimeStampFormat format = new TimeStampFormat("dd/MM/yyyy");
		this.sample = new Sample(format.parse(startDate), format.parse(endDate));
	}
	
	public RoughSampleFilter(Sample sample) {
		this.sample = sample;
	}

	public CandleSeries[] process(CandleSeries... series) throws Exception {
		CandleSeries[] newSeries = new CandleSeries[series.length];
		// subset
		for (int i = 0; i < series.length; i++) {
			newSeries[i] = roughSubList(series[i], sample.getStartTimeStamp(), sample.getEndTimeStamp());
		}
		return newSeries;
	}
	
	private CandleSeries roughSubList(CandleSeries series, TimeStamp start, TimeStamp end) throws Exception {
		// move start
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(start.getDate());
		calendar.add(Calendar.DAY_OF_YEAR, -DAYS_RANGE);
		start = new TimeStamp(calendar.getTime());
		
		// move end
		calendar.setTime(end.getDate());
		calendar.add(Calendar.DAY_OF_YEAR, DAYS_RANGE);
		end = new TimeStamp(calendar.getTime());
		
		// subset
		return series.subList(start, end, RangePolicy.FEASIBLE);
	}

	/**
	 * @return the daysRange
	 */
	public int getDaysRange() {
		return daysRange;
	}

	/**
	 * @param daysRange the daysRange to set
	 */
	public void setDaysRange(int daysRange) {
		this.daysRange = daysRange;
	}
}
