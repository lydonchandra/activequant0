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

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.Sample;
import org.activequant.core.types.TimeStamp;
import org.activequant.core.util.TimeStampFormat;
import org.activequant.data.preparation.FilterBase;





/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [10.11.2006] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class SampleFilter extends FilterBase {
	
	private Sample sample;
	
	public SampleFilter(TimeStamp startDate, TimeStamp endDate) {
		this.sample = new Sample(startDate, endDate);
	}
	
	public SampleFilter(String startDate, String endDate) {
		TimeStampFormat format = new TimeStampFormat("dd/MM/yyyy");
		this.sample = new Sample(format.parse(startDate), format.parse(endDate));
	}
	
	public SampleFilter(Sample sample) {
		this.sample = sample;
	}

	public CandleSeries[] process(CandleSeries... series) throws Exception {
		assert(checkDatesAlignment(series)) : "Aligned dated series required.";
		// clone
		CandleSeries[] newSeries = cloneSeries(series);
		// select
		int start = series[0].getTimeStampPosition(sample.getStartTimeStamp());
		int end = series[0].getTimeStampPosition(sample.getEndTimeStamp());
		for (int i = 0; i < series.length; i++) {
			newSeries[i] = series[i].subList(end, start);
		}
		return newSeries;
	}
	
	private boolean checkDatesAlignment(CandleSeries... series) {
		TimeStamp [] dates = series[0].getTimeStamps();
		for(int i = 1; i < series.length; i++) {
			TimeStamp[] checkDates = series[i].getTimeStamps();
			for(int j = 0; j < dates.length; j++) {
				if(!dates[j].equals(checkDates[j]))
					return false;
			}
		}
		return true;
	}
}
