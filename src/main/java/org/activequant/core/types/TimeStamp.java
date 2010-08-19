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
package org.activequant.core.types;

import java.util.Date;

import org.activequant.util.tools.IdentityUtils;

/**
 * Immutable UTC date/time with nanosecond resolution.
 * <p>
 *  - [26.11.2007] Created (Mike Kroutkov)<br>
 *  
 *  @author Mike Kroutikov
 */
// TODO Implement -> RC3
public final class TimeStamp implements Comparable<TimeStamp> {
	
	private final static long NANOS_IN_MILLIS = 1000000L;

	private final long value;
	
	/**
	 * Returns time in nanoseconds since Unix epoch (Jan 1, 1970 UTC).
	 * 
	 * @return
	 */
	public long getNanoseconds() {
		return value;
	}
	
	public Date getDate() {
		return new Date(value / NANOS_IN_MILLIS);
	}
	
	/**
	 * Creates new Timestamp object from nanosecond value.
	 * 
	 * @param value nanoseconds since Jan 1, 1970 UTC.
	 */
	public TimeStamp(long value) {
		this.value = value;
	}

	/**
	 * Creates new TimeStamp object from date's millisecond value,
	 * and nanosecond value.
	 * 
	 * @param dateValue date.
	 * @param nanos nanosecond part of the timestamp.
	 */
	public TimeStamp(Date dateValue, int nanos) {
		if(nanos < 0 || nanos >= NANOS_IN_MILLIS) {
			throw new IllegalArgumentException("nanosecond part must be between 0 and " + (NANOS_IN_MILLIS - 1));
		}
		this.value = dateValue.getTime() * NANOS_IN_MILLIS + nanos;
	}

	/**
	 * Creates new TimeStamp object from date's millisecond value,
	 * nanosecond part is set to zero.
	 * 
	 * @param dateValue date.
	 */
	public TimeStamp(Date dateValue) {
		this(dateValue, 0);
	}

	/**
	 * Creates new TimeStamp object that corresponds to the current time.
	 * Note that it is the same as calling
	 * <pre>
	 * new TimeStamp(new Date());
	 * </pre>
	 * and therefore provides only millisecond precision (nanosecond part is
	 * guaranteed to be zero).
	 */
	public TimeStamp() {
		this(new Date());
	}

	@Override
	public int hashCode() {
		return TimeStamp.class.hashCode() + (new Long(value)).hashCode();
	}
	
	public int compareTo(TimeStamp other) {
		return IdentityUtils.safeCompare(value, other.value);
	}
	
	@Override
	public boolean equals(Object other) {
		return IdentityUtils.equalsTo(this, other);
	}
	
	public boolean isBefore(TimeStamp other) {
		return compareTo(other) < 0;
	}
	
	public boolean isAfter(TimeStamp other) {
		return compareTo(other) > 0;
	}
	
	public boolean isEqual(TimeStamp other) {
		return compareTo(other) == 0;
	}
	
	public String toString() {
		return Long.toString(value);
	}
}
