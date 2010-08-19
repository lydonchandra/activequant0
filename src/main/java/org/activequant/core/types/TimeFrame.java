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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.activequant.util.tools.IdentityUtils;

/**
 * Time frame - describes Candle duration.
 * <p>
 * The duration is a combination of <em>units</em> and <em>length</em>,
 * with the <em>length</em> being measured in this units. For example,
 * time frame "1 day" has units value of {@link TimeFrame.Unit.DAY}, and length of 1.
 * <p>
 * There are special values of time frame units, that are not time-related:
 * <ul>
 *    <li> ticks - means that "duration" is measured in the number of trades.
 *    <li> contracts - means that "duration" is measured in the number of traded contracts
 *    		(volume-based time scale).
 * </ul>
 * 
 * <br>
 * <b>History:</b><br>
 *  - [03.05.2006] Created (Ulrich Staudinger)<br>
 *  - [03.05.2007] Refactoring (Erik Nijkamp)<br>
 *  - [25.06.2007] Simplified parseFromSeconds(...) (Erik Nijkamp)<br>
 *  - [13.09.2007] Added fix by Mike (Ulrich Staudinger)<br>
 *  - [22.11.2007] Introduced generalized time frame (Mike Kroutikov)<br>
 *  - [27.11.2007] Renamed ticks to trades, renamed class to DataFrameSize (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 *  @author Mike Kroutikov
 */
//TODO Implement -> RC3
public final class TimeFrame implements Comparable<TimeFrame> {

	private static final int CATEGORY_TICKS     = 0;
	private static final int CATEGORY_CONTRACTS = 1;
	private static final int CATEGORY_TIME      = 2;

	/**
	 * Units to measure durations.
	 * There are three orthogonal (non-comparable) categories of
	 * units: Tick-based category, Contract-based category,
	 * and Time-based category.
	 */
	public enum Unit {

		/**
		 * Duration measured in the units of trades.
		 */
		TRADE(Integer.MAX_VALUE, "t", CATEGORY_TICKS),

		/**
		 * Duration measured in the units of contracts traded.
		 */
		CONTRACT(Integer.MAX_VALUE, "c", CATEGORY_CONTRACTS),
		
		/**
		 * Duration measured in the units of seconds.
		 */
		SECOND(59, "s", CATEGORY_TIME),

		/**
		 * Duration measured in the units of minutes.
		 */
		MINUTE(59, "m", CATEGORY_TIME),
		
		/**
		 * Duration measured in the units of hours.
		 */
		HOUR(23, "h", CATEGORY_TIME),

		/**
		 * Duration measured in the units of days.
		 */
		DAY(1, "d", CATEGORY_TIME), // more than one day is too confusing
									// day-of-the week vs day-of-the-month

		/**
		 * Duration measured in the units of weeks.
		 */
		WEEK(3, "w", CATEGORY_TIME),  // Note: Feb may be *exactly* 4 weeks long
									// but we want *any* duration in weeks be always
									// less than *any* duration in months

		/**
		 * Duration measured in the units of months.
		 */
		MONTH(11, "M", CATEGORY_TIME),

		/**
		 * Duration measured in the units of years.
		 */
		YEAR(Integer.MAX_VALUE, "Y", CATEGORY_TIME);

		private final int max;
		public final String acronym;
		private final int category;
		
		private Unit(int m, String a, int c) {
			max = m;
			acronym = a;
			category = c;
		}
		
		private void validateLength(int length) {
			if(length < 1 || length > max) {
				throw new IllegalArgumentException("length for unit " + this + " must be between 1 and " + max);
			}
		}

		private final static Map<String,Unit> decodeMap = new HashMap<String,Unit>();
		static {
			for(Unit u : Unit.values()) {
				decodeMap.put(u.acronym, u);
			}
		}
	}
	
	public final Unit unit;
	public final int  length;

	private TimeFrame(Unit u, int l) {
		u.validateLength(l);
		unit = u;
		length = l;
	}
	
	public static TimeFrame trade() {
		return trades(1);
	}

	public static TimeFrame trades(int length) {
		return new TimeFrame(Unit.TRADE, length);
	}

	public static TimeFrame contract() {
		return contracts(1);
	}

	public static TimeFrame contracts(int length) {
		return new TimeFrame(Unit.CONTRACT, length);
	}

	public static TimeFrame second() {
		return seconds(1);
	}

	public static TimeFrame seconds(int length) {
		return new TimeFrame(Unit.SECOND, length);
	}

	public static TimeFrame minute() {
		return minutes(1);
	}

	public static TimeFrame minutes(int length) {
		return new TimeFrame(Unit.MINUTE, length);
	}

	public static TimeFrame hour() {
		return hours(1);
	}

	public static TimeFrame hours(int length) {
		return new TimeFrame(Unit.HOUR, length);
	}

	public static TimeFrame day() {
		return days(1);
	}

	public static TimeFrame days(int length) {
		return new TimeFrame(Unit.DAY, length);
	}

	public static TimeFrame week() {
		return weeks(1);
	}

	public static TimeFrame weeks(int length) {
		return new TimeFrame(Unit.WEEK, length);
	}

	public static TimeFrame month() {
		return months(1);
	}

	public static TimeFrame months(int length) {
		return new TimeFrame(Unit.MONTH, length);
	}
	
	public static TimeFrame year() {
		return years(1);
	}

	public static TimeFrame years(int length) {
		return new TimeFrame(Unit.YEAR, length);
	}

	public static TimeFrame TIMEFRAME_1_TICK       = TimeFrame.trade();
	public static TimeFrame TIMEFRAME_1_SECOND     = TimeFrame.second(); 
	public static TimeFrame TIMEFRAME_5_SECONDS    = TimeFrame.seconds(5);
	public static TimeFrame TIMEFRAME_15_SECONDS   = TimeFrame.seconds(15); 
	public static TimeFrame TIMEFRAME_30_SECONDS   = TimeFrame.seconds(30); 
	public static TimeFrame TIMEFRAME_1_MINUTE     = TimeFrame.minute(); 
	public static TimeFrame TIMEFRAME_2_MINUTES    = TimeFrame.minutes(2); 
	public static TimeFrame TIMEFRAME_5_MINUTES    = TimeFrame.minutes(5); 
	public static TimeFrame TIMEFRAME_10_MINUTES   = TimeFrame.minutes(10); 
	public static TimeFrame TIMEFRAME_15_MINUTES   = TimeFrame.minutes(15); 
	public static TimeFrame TIMEFRAME_30_MINUTES   = TimeFrame.minutes(30); 
	public static TimeFrame TIMEFRAME_60_MINUTES   = TimeFrame.hour(); 
	public static TimeFrame TIMEFRAME_1_DAY        = TimeFrame.day();
	public static TimeFrame TIMEFRAME_1_WEEK       = TimeFrame.week();
	public static TimeFrame TIMEFRAME_1_MONTH      = TimeFrame.month();
	public static TimeFrame TIMEFRAME_1_YEAR       = TimeFrame.year();
	
	public int hashCode() {
		return IdentityUtils.safeHashCode(this.unit) + IdentityUtils.safeHashCode(this.length);
	}
	
	public boolean equals(Object other) {
		if(other.getClass().equals(this.getClass())) {
			TimeFrame o = (TimeFrame) other;
			return this.unit.category == o.unit.category 
					&& IdentityUtils.equalsTo(this, o);
		}
		return false;
	}

	public int compareTo(TimeFrame other) {
		if(unit.category != other.unit.category) {
			throw new UnsupportedOperationException("attempt to compare apples and oranges: " + this + " vs " + other);
		}
		int rc;
		rc = IdentityUtils.safeCompare(this.unit, other.unit);
		if(rc != 0) return rc;
		rc = IdentityUtils.safeCompare(this.length, other.length);
		if(rc != 0) return rc;
		
		return 0;
	}
	
	private Calendar addToCalendar(Calendar calendar, int length) {

		if(unit.category != CATEGORY_TIME) {
			throw new IllegalStateException("can not translate to time interval non-discrete or tick-based timeframe");
		}

		switch(unit) {
		case SECOND:
			calendar.add(Calendar.SECOND, length);
			break;
		case MINUTE:
			calendar.add(Calendar.MINUTE, length);
			break;
		case HOUR:
			calendar.add(Calendar.HOUR_OF_DAY, length);
			break;
		case DAY:
			calendar.add(Calendar.DAY_OF_YEAR, length);
			break;
		case WEEK:
			calendar.add(Calendar.WEEK_OF_YEAR, length);
			break;
		case MONTH:
			calendar.add(Calendar.MONTH, length);
			break;
		case YEAR:
			calendar.add(Calendar.YEAR, length);
			break;
		default:
			throw new AssertionError("unexpected");
		}
		
		return calendar;
	}
	
	/**
	 * Method that aligns the Calendar on the boundary of this time frame.
	 * For example, if time frame is one hour, then aligned calendar will
	 * have zero minutes, zero seconds, and zero milliseconds.
	 * 
	 * @param calendar calendar to act upon.
	 * @return passed calendar for convenience (operation chaining).
	 */
	public Calendar alignCalendar(Calendar calendar) {

		if(unit.category != CATEGORY_TIME) {
			throw new IllegalStateException("can not translate to time interval non-discrete or tick-based timeframe");
		}

		switch(unit) {
		case SECOND:
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.add(Calendar.SECOND, - (calendar.get(Calendar.SECOND) % length));
			break;
		case MINUTE:
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.add(Calendar.MINUTE, - (calendar.get(Calendar.MINUTE) % length));
			break;
		case HOUR:
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.add(Calendar.HOUR_OF_DAY, - (calendar.get(Calendar.HOUR_OF_DAY) % length));
			break;
		case DAY:
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.add(Calendar.DAY_OF_WEEK, - (calendar.get(Calendar.DAY_OF_WEEK) % length));
			break;
		case WEEK:
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			if(dayOfWeek > Calendar.MONDAY) {
				calendar.add(Calendar.DAY_OF_YEAR, Calendar.MONDAY - dayOfWeek);
			} else {
				calendar.add(Calendar.DAY_OF_YEAR, 7 + dayOfWeek - Calendar.MONDAY);
			}
			break;
		case MONTH:
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.MONTH, - (calendar.get(Calendar.MONTH) % length));
			break;
		case YEAR:
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, 0);
			calendar.add(Calendar.YEAR, - (calendar.get(Calendar.YEAR) % length));
			break;
		default:
			throw new AssertionError("unexpected");
		}
		
		return calendar;
	}

	/**
	 * Method that adds this time frame to the calendar.
	 * Note that this is not a trivial operation, since, for example,
	 * length of month depends on the current date of the calendar.
	 * 
	 * @param calendar calendar to act upon.
	 * @return passed calendar for convenience (operation chaining).
	 */
	public Calendar addToCalendar(Calendar calendar) {
		return addToCalendar(calendar, length);
	}

	/**
	 * Method that subtracts this time frame from the calendar.
	 * Note that this is not a trivial operation, since, for example,
	 * length of month depends on the current date of the calendar.
	 * 
	 * @param calendar calendar to act upon.
	 * @return passed calendar for convenience (operation chaining).
	 */
	public Calendar subtractFromCalendar(Calendar calendar) {
		return addToCalendar(calendar, -length);
	}
	
	public String toString() {
		return "" + length + unit.acronym;
	}
	
	/**
	 * Parses string representation of time frame (reverse operation to
	 * {@link #toString()}).
	 * 
	 * @param str string to parse.
	 * @return parsed value.
	 * @throws IllegalArgumentException if can not parse.
	 */
	public static TimeFrame parse(String str) throws IllegalArgumentException {

		int index = 0;
		while(index < str.length()) {
			if(Character.isDigit(str.charAt(index))) {
				index++;
			} else {
				break;
			}
		}
		
		if(index == 0) {
			throw new IllegalArgumentException("unparsable timeframe string: '" + str + "' (bad numeric)");
		}
		
		Unit unit = Unit.decodeMap.get(str.substring(index));
		if(unit == null) {
			throw new IllegalArgumentException("unparsable timeframe string: '" + str + "' (bad suffix)");
		}
		int length = Integer.parseInt(str.substring(0, index));
		
		return new TimeFrame(unit, length);
	}
}
