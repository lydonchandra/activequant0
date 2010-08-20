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
package org.activequant.util.scheduling.iterators;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.activequant.core.types.TimeFrame;
import org.activequant.util.scheduling.ScheduleIterator;



/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [11.05.2007] Created (Erik Nijkamp)<br>
 *  - [26.06.2007] Fixing reschedule calculation (Ulrich Staudinger)<br>
 *  - [27.11.2007] Adding an offset parameter. (Ulrich Staudinger)<br>
 *  - [02.12.2007] Converted to new time frames, removed offset parameter (Mike Kroutikov)<br>
 *
 *  @author Erik Nijkamp
 *  @author Ulrich Staudinger
 *  @author Mike Kroutikov
 */
public class TimeFrameIterator implements ScheduleIterator {

	private final TimeFrame timeFrame;

	public TimeFrameIterator(TimeFrame timeFrame) {
		this.timeFrame = timeFrame;
	}

	/**
	 * method to calculate the next trigger date.
	 */
	public Date next() {
		// calculating the next trigger period.
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		calendar.setTime(new Date());
		
		timeFrame.alignCalendar(calendar);
		timeFrame.addToCalendar(calendar);
		
		return calendar.getTime();
	}
}
