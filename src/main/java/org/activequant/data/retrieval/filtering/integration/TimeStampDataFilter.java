package org.activequant.data.retrieval.filtering.integration;

import java.util.Calendar;
import java.util.TimeZone;

import org.activequant.core.domainmodel.MarketDataEntity;
import org.activequant.core.types.TimeStamp;
import org.activequant.data.retrieval.filtering.IDataFilter;
import org.apache.log4j.Logger;


/**
 * Filters market data entity based on its time stamp.
 * Matches against a pattern of months/days of the week/time.
 * <p>
 * Following reject patterns can be set:
 * <li>
 *    <ul> {@link #getMonthRejects() monthRejects} - specifies months that should be rejected (filtered out).
 *    <ul> {@link #getDayOfWeekRejects() dayOfWeekRejects} - specifies days of week that should be rejected.
 *    <ul> {@link #timeOfDayWeekRejects() timeOfDayRejects} - specifies time of day that should be rejected.
 * </li>
 * Reject patterns are specified as a comma-separated list of values or ranges,
 * for example:
 * <pre>
 *    filter.monthRejects="Jan,Jun-Aug";
 * </pre>
 * will reject items dated Jan, Jun, Jul, and Aug.
 * <p>
 * Valid names for the months are: Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec.
 * <p>
 * Valid names for the days-of-the-week: Sun, Mon, Tue, Wed, Thu, Fri, Sat.
 * <p>
 * Time format is: HH:mm, for example: 09:45, or 21:30. Time is always in 24-hour format.
 * <p>
 * Combining rules are: if reject matches at any level (months, days, time), the event
 * will be immediately rejected. In other words, reject patterns follow 'OR' logic: event is rejected if it 
 * matches month pattern OR day pattern OR time pattern.
 * <p>
 * Unset reject patterns (and empty patterns) mean "accept everything".
 * <p>
 * Some examples:
 * <pre>
 *    filter.dayOfWeekRejects="Sat,Sun";
 *    filter.timeRejects="00:00-09:29,16:01-23:59";
 * </pre>
 * <br>
 * <b>History:</b><br>
 *  - [04.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class TimeStampDataFilter<T extends MarketDataEntity> implements IDataFilter<T> {
	
	private final Logger log = Logger.getLogger(getClass());
	
	private interface CalendarValidator {
		public boolean validate(Calendar calendar);
	}
	
	private static final CalendarValidator ACCEPTOR = new CalendarValidator() {
		public boolean validate(Calendar calendar) { return true; }
	};
	
	private TimeZone zone = TimeZone.getTimeZone("UTC");
	private String monthRejects;
	private String dayOfWeekRejects;
	private String timeOfDayRejects;
	
	public TimeZone getZone() {
		return zone;
	}

	public void setZone(TimeZone zone) {
		this.zone = zone;
	}

	public String getMonthRejects() {
		return monthRejects;
	}

	public void setMonthRejects(String monthRejects) {
		this.monthRejects = monthRejects;
		validator = buildValidator();
	}

	public String getDayOfWeekRejects() {
		return dayOfWeekRejects;
	}

	public void setDayOfWeekRejects(String dayOfWeekRejects) {
		this.dayOfWeekRejects = dayOfWeekRejects;
		validator = buildValidator();
	}

	public String getTimeOfDayRejects() {
		return timeOfDayRejects;
	}

	public void setTimeOfDayRejects(String timeOfDayRejects) {
		this.timeOfDayRejects = timeOfDayRejects;
		validator = buildValidator();
	}

	private CalendarValidator validator = ACCEPTOR;
	
	private static String [] MONTHS = { 
		"Jan", "Feb", "Mar", "Apr", "May", "Jun",
		"Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
	};

	private static int parseMonthName(String name) {
		for(int i = 0; i < MONTHS.length; i++) {
			if(name.equalsIgnoreCase(MONTHS[i])) return i;
		}
		throw new IllegalArgumentException("bad month value: '" + name + "', expected one of 'jan', 'feb', etc.");
	}

	private CalendarValidator parseMonthsPattern(String pattern, final CalendarValidator child) {

		final boolean [] map = new boolean[MONTHS.length];
		
		for(String elt : pattern.trim().split(",")) {
			elt = elt.trim();
			if(elt.length() == 0) continue;

			int index = elt.indexOf('-');
			if(index >= 0) {
				// range
				int from = parseMonthName(elt.substring(0, index).trim());
				int to = parseMonthName(elt.substring(index + 1).trim());
				log.info("range of months: " + from + "-" + to);
				if(to < from) {
					to += MONTHS.length;
				}
				for(int i = from; i <= to; i++) {
					map[i % MONTHS.length] = true; // reject
				}
			} else {
				// single
				int val = parseMonthName(elt.trim());
				log.info("single month: " + val);
				map[val] = true; // reject
			}
		}
		
		return new CalendarValidator() {

			public boolean validate(Calendar calendar) {
				int month = calendar.get(Calendar.MONTH);
				if(map[month]) {
					return false;
				}
				return child.validate(calendar);
			}
			
		};
	}

	private static String [] DAYS_OF_WEEK = { 
		"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
	};

	private static int parseDayOfWeekName(String name) {
		for(int i = 0; i < DAYS_OF_WEEK.length; i++) {
			if(name.equalsIgnoreCase(DAYS_OF_WEEK[i])) return i;
		}
		throw new IllegalArgumentException("bad month value: '" + name + "', expected one of 'jan', 'feb', etc.");
	}
	
	private CalendarValidator parseDayOfWeekPattern(String pattern, final CalendarValidator child) {

		final boolean [] map = new boolean[DAYS_OF_WEEK.length];
		
		for(String elt : pattern.trim().split(",")) {
			elt = elt.trim();
			if(elt.length() == 0) continue;

			int index = elt.indexOf('-');
			if(index >= 0) {
				// range
				int from = parseDayOfWeekName(elt.substring(0, index).trim());
				int to   = parseDayOfWeekName(elt.substring(index + 1).trim());
				log.info("range of days: " + from + "-" + to);
				if(to < from) {
					to += DAYS_OF_WEEK.length;
				}
				for(int i = from; i <= to; i++) {
					map[i % DAYS_OF_WEEK.length] = true; // reject
				}
			} else {
				// single
				int val = parseDayOfWeekName(elt.trim());
				log.info("single day: " + val);
				map[val] = true; // reject
			}
		}
		
		return new CalendarValidator() {

			public boolean validate(Calendar calendar) {
				int d = calendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
				if(map[d]) {
					return false;
				}
				return child.validate(calendar); // chain to child validator
			}
			
		};
	}

	private static int parseTime(String val) {
		int index = val.indexOf(':');
		if(index < 0) {
			throw new IllegalArgumentException("bad hour:minute value: '" + val + "', expected something like 17:03");
		}
		int hh = Integer.parseInt(val.substring(0,index));
		if(hh < 0 || hh >= 24) {
			throw new IllegalArgumentException("hour value is out of range: " + hh + ", should be between 00 and 23");
		}
		int mm = Integer.parseInt(val.substring(index + 1));
		if(mm < 0 || mm >= 60) {
			throw new IllegalArgumentException("minute value is out of range: " + mm + ", should be between 00 and 59");
		}
		
		return 60 * hh + mm;
	}

	private CalendarValidator parseTimePattern(String pattern, final CalendarValidator child) {
		final boolean [] map = new boolean[24 * 60];
		for(String elt : pattern.trim().split(",")) {
			elt = elt.trim();
			if(elt.length() == 0) continue;
			
			int index = elt.indexOf('-');
			if(index >= 0) {
				// range
				int from = parseTime(elt.substring(0, index).trim());
				int to = parseTime(elt.substring(index + 1).trim());
				log.info("range of minutes: " + from + "-" + to);
				if(to < from) {
					to += map.length;
				}
				for(int i = from; i <= to; i++) {
					map[i] = true;
				}
			} else {
				// single
				int val = parseTime(elt.trim());
				log.info("single minute: " + val);
				map[val] = true;
			}
		}
		
		return new CalendarValidator() {

			public boolean validate(Calendar calendar) {
				int hh = calendar.get(Calendar.HOUR_OF_DAY);
				int mm = calendar.get(Calendar.MINUTE);
				
				int val = hh * 60 + mm;
				
				if(map[val]) {
					return false;
				}
				return child.validate(calendar);
			}
		};
	}

	// builds chain of validators
	private CalendarValidator buildValidator() {
		CalendarValidator validator = ACCEPTOR;

		if(timeOfDayRejects != null) {
			validator = parseTimePattern(timeOfDayRejects, validator);
		}

		if(dayOfWeekRejects != null) {
			validator = parseDayOfWeekPattern(dayOfWeekRejects, validator);
		}

		if(monthRejects != null) {
			validator = parseMonthsPattern(monthRejects, validator);
		}
		
		return validator;
	}

	public boolean evaluate(T data) {
		TimeStamp stamp = data.getTimeStamp();
		
		Calendar calendar = Calendar.getInstance(zone);
		calendar.setTime(stamp.getDate());
		return validator.validate(calendar);
	}

}
