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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.activequant.util.scheduling.ScheduleIterator;



/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [11.05.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp (based on code by Tom White)
 */
public class CompositeIterator implements ScheduleIterator {

	private List<Date> orderedTimes = new ArrayList<Date> ();
	private List<ScheduleIterator>  orderedIterators = new ArrayList<ScheduleIterator>();

	public CompositeIterator(ScheduleIterator[] scheduleIterators) {
		for (ScheduleIterator iter: scheduleIterators) {
			insert(iter);
		}
	}

	private void insert(ScheduleIterator scheduleIterator) {
		Date time = scheduleIterator.next();
		if (time == null) {
			return;
		}
		int index = Collections.binarySearch(orderedTimes, time);
		if (index < 0) {
			index = -index - 1;
		}
		orderedTimes.add(index, time);
		orderedIterators.add(index, scheduleIterator);
	}

	public synchronized Date next() {
		Date next = null;
		while (!orderedTimes.isEmpty() &&
				(next == null || next.equals((Date) orderedTimes.get(0)))) {
			next = (Date) orderedTimes.remove(0);
			insert((ScheduleIterator) orderedIterators.remove(0));
		}
		return next;
	}


}
