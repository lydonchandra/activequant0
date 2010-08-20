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
package org.activequant.util.charting;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.jfree.chart.axis.Timeline;

/**
 * 
 * Taken from the web.<br>
 * <br>
 * <b>History:</b><br>
 *  - [14.08.2007] Created (?)<br>
 *  - [29.09.2007] Removed warnings, fixed time zone (Erik Nijkamp)<br>
 *  - [31.10.2007] Minor cleanup of old code (Ulrich Staudinger)<br>
 *
 *  @author N/A
 */
public class IntradayMarketTimeline implements Timeline {
    private long sundayStart = 0;
    private long sundayEnd = 0;
    private long mondayStart = 0;
    private long mondayEnd = 0;
    private long tuesdayStart = 0;
    private long tuesdayEnd = 0;
    private long wednesdayStart = 0;
    private long wednesdayEnd = 0;
    private long thursdayStart = 0;
    private long thursdayEnd = 0;
    private long fridayStart = 0;
    private long fridayEnd = 0;
    private long saturdayStart = 0;
    private long saturdayEnd = 0;
    private long sundayActive = 0;
    private long mondayActive = 0;
    private long tuesdayActive = 0;
    private long wednesdayActive = 0;
    private long thursdayActive = 0;
    private long fridayActive = 0;
    private long saturdayActive = 0;
    
    private static final Calendar cal = new GregorianCalendar();	
    private static final int LOCALTIMEZONEOFFSET = (cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)) / (60*1000);

    private long activeTimePerWeek = 0;
    //private long closedTimePerWeek = 0;
   
    private static final long MILLIS_PER_WEEK = 7 * 24 * 60 * 60 * 1000;
    private static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;

    //specify the start and end times for the market
    //times are given in milliseconds in relation to 00:00 for that day.
    //so a start time of 01:00 would be 1 hour * 60 minutes * 60 seconds * 1000 milliseconds
    public IntradayMarketTimeline(long sundayStart, long sundayEnd,
			long mondayStart, long mondayEnd, long tuesdayStart,
			long tuesdayEnd, long wednesdayStart, long wednesdayEnd,
			long thursdayStart, long thursdayEnd, long fridayStart,
			long fridayEnd, long saturdayStart, long saturdayEnd) {
		this.sundayStart = sundayStart;
		this.sundayEnd = sundayEnd;
		this.mondayStart = mondayStart;
		this.mondayEnd = mondayEnd;
		this.tuesdayStart = tuesdayStart;
		this.tuesdayEnd = tuesdayEnd;
		this.wednesdayStart = wednesdayStart;
		this.wednesdayEnd = wednesdayEnd;
		this.thursdayStart = thursdayStart;
		this.thursdayEnd = thursdayEnd;
		this.fridayStart = fridayStart;
		this.fridayEnd = fridayEnd;
		this.saturdayStart = saturdayStart;
		this.saturdayEnd = saturdayEnd;

		//calculate the amount of time the market is open during each day
		this.sundayActive = this.sundayEnd - this.sundayStart;
		this.mondayActive = this.mondayEnd - this.mondayStart;
		this.tuesdayActive = this.tuesdayEnd - this.tuesdayStart;
		this.wednesdayActive = this.wednesdayEnd - this.wednesdayStart;
		this.thursdayActive = this.thursdayEnd - this.thursdayStart;
		this.fridayActive = this.fridayEnd - this.fridayStart;
		this.saturdayActive = this.saturdayEnd - this.saturdayStart;

		this.activeTimePerWeek = this.sundayActive + this.mondayActive
				+ this.tuesdayActive + this.wednesdayActive
				+ this.thursdayActive + this.fridayActive + this.saturdayActive;

		//calculate the amount of time the market is closed each week
		//this.closedTimePerWeek = this.MILLIS_PER_WEEK - this.activeTimePerWeek;
	}

	/**
	 * Translates a millisecond (as defined by java.util.Date) into an index
	 * along this timeline.
	 *
	 * @param target - the milliseconds of a date to convert
	 *
	 * @return A timeline value.
	 */
	public long toTimelineValue(long currentDayMillis) {

		//
		//find out the day of the week the current day is
		//SUNDAY = 1,...SATURDAY = 7
		Calendar currentDay = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		currentDay.setTimeInMillis(currentDayMillis);

		//a list of the days from the current date until last thursday 00:00
		//which includes thursday itself from (00:00,23:59)
		ArrayList<Integer> daysFromThursday = new ArrayList<Integer>();

		Calendar lastThursday = Calendar.getInstance(TimeZone
				.getTimeZone("GMT"));

		lastThursday.setTimeInMillis(currentDayMillis);

		//move backwards in time until you hit a wednesday
		while (lastThursday.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY) {
			//add this day to the list
			daysFromThursday.add(new Integer(lastThursday
					.get(Calendar.DAY_OF_WEEK)));
			//
			//move back one more day
			lastThursday.add(Calendar.DATE, -1);
		}
		//
		//add thursday to the list
		daysFromThursday.add(new Integer(Calendar.THURSDAY));

		//adjust Calendar to the beginning of last thursday
		lastThursday.set(Calendar.MILLISECOND, 0);
		lastThursday.set(Calendar.SECOND, 0);
		lastThursday.set(Calendar.MINUTE, 0);
		lastThursday.set(Calendar.HOUR_OF_DAY, 0);

		//get the milliseconds for the beginning of last Thursday
		long lastThursdayMillis = lastThursday.getTimeInMillis();

		//because Jan 1, 1970 was a Thursday, lastThursdayMillis
		//gives an even # of weeks from Jan 1, 1970 until lastThursdayMillis. 
		//so subtract the (down time per week * the
		//number of weeks) since Jan 1, 1970

		//the number of weeks since Jan 1, 1970
		int numberOfWeeks = (int) Math.round((new Long(lastThursdayMillis
				/ MILLIS_PER_WEEK)).doubleValue());
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

		TimeZone.setDefault(TimeZone.getDefault());

		//get the timeline value for the number of millis since
		//Jan 1, 1970 to last thursday, by multiplying the number of weeks
		//since Jan 1, 1970 by the amount of active milliseconds per week
		long timelineValue = numberOfWeeks * this.activeTimePerWeek;
		//          
		//add the amount of active millseconds for the current day
		//of the given time
		long millisOfCurrentDay = currentDay.get(Calendar.HOUR_OF_DAY)
				* 3600000 + currentDay.get(Calendar.MINUTE) * 60000
				+ currentDay.get(Calendar.SECOND) * 1000
				+ currentDay.get(Calendar.MILLISECOND);

		long startOfCurrentDay = this.getStartTime(currentDay
				.get(Calendar.DAY_OF_WEEK));
		long endOfCurrentDay = this.getEndTime(currentDay
				.get(Calendar.DAY_OF_WEEK));

		if (millisOfCurrentDay >= startOfCurrentDay
				&& millisOfCurrentDay <= endOfCurrentDay) {

			timelineValue += millisOfCurrentDay - startOfCurrentDay;
		}

		//get the size of the list
		int listSize = daysFromThursday.size();

		//add the active time since last thursday, skipping the first day since
		//we already took care of that
		for (int i = 1; i < listSize; i++) {
			int day = ((Integer) daysFromThursday.get(i)).intValue();

			timelineValue += this.getActiveTimePerDay(day);

		}

		return timelineValue;
	}

	/**
	 * Translates a date into a value on this timeline.
	 *
	 * @param date  the date.
	 *
	 * @return A timeline value
	 */
	public long toTimelineValue(Date date) {
		//normalize to GMT
		return this.toTimelineValue(LOCALTIMEZONEOFFSET + date.getTime());
	}

	/**
	 * Translates a value relative to this timeline into a domain value. The
	 * domain value obtained by this method is not always the same domain value
	 * that could have been supplied to
	 * translateDomainValueToTimelineValue(domainValue).
	 * This is because the original tranformation may not be complete
	 * reversable.
	 *
	 * @see org.jfree.chart.axis.SegmentedTimeline
	 *
	 * @param timelineValue  a timeline value.
	 *
	 * @return A domain value.
	 */
	public long toMillisecond(long timelineValue) {

		if (this.activeTimePerWeek == 0L)
			return 0;

		//starting from Jan 1, 1970 work backwards.
		//find out the number of whole weeks in the timelineValue
		Long l = new Long(timelineValue / this.activeTimePerWeek);
		int numWeeks = (int) Math.floor(l.doubleValue());

		//the amount of time left on the timeline from the last thursday
		long timeLeftSinceThursday = timelineValue
				- (numWeeks * this.activeTimePerWeek);

		int day = Calendar.THURSDAY;
		int numDays = 0;

		//from last friday until the current day
		//if the amount of time left is greater than
		//the active time for that day, increment the number of
		//days and subtract from the time left
		while (numDays < 7) {
			if (day == Calendar.SUNDAY) {
				if (timeLeftSinceThursday > this.sundayActive) {
					timeLeftSinceThursday -= this.sundayActive;
					numDays++;
				} else {
					break;
				}
			} else if (day == Calendar.MONDAY) {
				if (timeLeftSinceThursday > this.mondayActive) {
					timeLeftSinceThursday -= this.mondayActive;
					numDays++;
				} else {
					break;
				}
			} else if (day == Calendar.TUESDAY) {
				if (timeLeftSinceThursday > this.tuesdayActive) {
					timeLeftSinceThursday -= this.tuesdayActive;
					numDays++;
				} else {
					break;
				}
			} else if (day == Calendar.WEDNESDAY) {
				if (timeLeftSinceThursday > this.wednesdayActive) {
					timeLeftSinceThursday -= this.wednesdayActive;
					numDays++;
				} else {
					break;
				}
			} else if (day == Calendar.THURSDAY) {

				if (timeLeftSinceThursday > this.thursdayActive) {
					timeLeftSinceThursday -= this.thursdayActive;
					numDays++;

					//thursday numDays =  " + Integer.toString(numDays));
				} else {

					break;
				}
			} else if (day == Calendar.FRIDAY) {
				if (timeLeftSinceThursday > this.fridayActive) {
					timeLeftSinceThursday -= this.fridayActive;
					numDays++;
				} else {
					break;
				}
			} else if (day == Calendar.SATURDAY) {
				if (timeLeftSinceThursday > this.saturdayActive) {
					timeLeftSinceThursday -= this.saturdayActive;
					numDays++;
				} else {
					break;
				}
			}

			day = this.nextDay(day);
		}

		long millis = numWeeks * MILLIS_PER_WEEK + numDays
				* MILLIS_PER_DAY + this.getStartTime(day)
				+ timeLeftSinceThursday;

		return millis;
	}

	/**
	 * Returns <code>true</code> if a value is contained in the timeline values.
	 *
	 * @param millisecond  the millisecond.
	 *
	 * @return <code>true</code> if value is contained in the timeline and
	 *         <code>false</code> otherwise.
	 */
	public boolean containsDomainValue(long millisecond) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTimeInMillis(millisecond);

		return isActiveDate(cal);
	}

	/**
	 * Returns <code>true</code> if a date is contained in the timeline values.
	 *
	 * @param date  the date to verify.
	 *
	 * @return <code>true</code> if value is contained in the timeline and
	 *         <code>false</code>  otherwise.
	 */
	public boolean containsDomainValue(Date date) {
		//normalize to GMT
		return this.containsDomainValue(LOCALTIMEZONEOFFSET
				+ date.getTime());
	}

	/**
	 * Returns <code>true</code> if a range of values are contained in the
	 * timeline.
	 *
	 * @param fromMillisecond  the start of the range to verify.
	 * @param toMillisecond  the end of the range to verify.
	 *
	 * @return <code>true</code> if the range is contained in the timeline or
	 *         <code>false</code> otherwise
	 */
	public boolean containsDomainRange(long fromMillisecond, long toMillisecond) {
		Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

		cal1.setTimeInMillis(fromMillisecond);
		cal2.setTimeInMillis(toMillisecond);

		return this.isActiveDate(cal1, cal2);
	}

	/**
	 * Returns <code>true</code> if a range of dates are contained in the
	 * timeline.
	 *
	 * @param fromDate  the start of the range to verify.
	 * @param toDate  the end of the range to verify.
	 *
	 * @return <code>true</code> if the range is contained in the timeline or
	 *         <code>false</code> otherwise
	 */
	@SuppressWarnings("deprecation")
	public boolean containsDomainRange(Date fromDate, Date toDate) {
		//normalize to GMT
		return this.containsDomainRange(fromDate.getTimezoneOffset()
				+ fromDate.getTime(), toDate.getTimezoneOffset()
				+ toDate.getTime());
	}

	private long getActiveTimePerDay(int day) {
		long closedTime = 0;

		if (day == Calendar.SUNDAY) {
			closedTime = this.sundayActive;
		} else if (day == Calendar.MONDAY) {
			closedTime = this.mondayActive;
		} else if (day == Calendar.TUESDAY) {
			closedTime = this.tuesdayActive;
		} else if (day == Calendar.WEDNESDAY) {
			closedTime = this.wednesdayActive;
		} else if (day == Calendar.THURSDAY) {
			closedTime = this.thursdayActive;
		} else if (day == Calendar.FRIDAY) {
			closedTime = this.fridayActive;
		} else if (day == Calendar.SATURDAY) {
			closedTime = this.saturdayActive;
		}

		return closedTime;
	}

	private long getStartTime(int day) {
		long startTime = 0;

		if (day == Calendar.SUNDAY) {
			startTime = this.sundayStart;
		} else if (day == Calendar.MONDAY) {
			startTime = this.mondayStart;
		} else if (day == Calendar.TUESDAY) {
			startTime = this.tuesdayStart;
		} else if (day == Calendar.WEDNESDAY) {
			startTime = this.wednesdayStart;
		} else if (day == Calendar.THURSDAY) {
			startTime = this.thursdayStart;
		} else if (day == Calendar.FRIDAY) {
			startTime = this.fridayStart;
		} else if (day == Calendar.SATURDAY) {
			startTime = this.saturdayStart;
		}

		return startTime;
	}

	private long getEndTime(int day) {
		long endTime = 0;

		if (day == Calendar.SUNDAY) {
			endTime = this.sundayEnd;
		} else if (day == Calendar.MONDAY) {
			endTime = this.mondayEnd;
		} else if (day == Calendar.TUESDAY) {
			endTime = this.tuesdayEnd;
		} else if (day == Calendar.WEDNESDAY) {
			endTime = this.wednesdayEnd;
		} else if (day == Calendar.THURSDAY) {
			endTime = this.thursdayEnd;
		} else if (day == Calendar.FRIDAY) {
			endTime = this.fridayEnd;
		} else if (day == Calendar.SATURDAY) {
			endTime = this.saturdayEnd;
		}

		return endTime;
	}

	private boolean isActiveDate(Calendar cal) {
		int day = cal.get(Calendar.DAY_OF_WEEK);

		long timeSinceStart = cal.get(Calendar.HOUR_OF_DAY) * 3600000
				+ cal.get(Calendar.MINUTE) * 60000 + cal.get(Calendar.SECOND)
				* 1000;

		if (day == Calendar.SUNDAY) {
			return (timeSinceStart > this.sundayStart && timeSinceStart < this.sundayEnd);
		} else if (day == Calendar.MONDAY) {
			return (timeSinceStart > this.mondayStart && timeSinceStart < this.mondayEnd);
		} else if (day == Calendar.TUESDAY) {
			return (timeSinceStart > this.tuesdayStart && timeSinceStart < this.tuesdayEnd);
		} else if (day == Calendar.WEDNESDAY) {
			return (timeSinceStart > this.wednesdayStart && timeSinceStart < this.wednesdayEnd);
		} else if (day == Calendar.THURSDAY) {
			return (timeSinceStart > this.thursdayStart && timeSinceStart < this.thursdayEnd);
		} else if (day == Calendar.FRIDAY) {
			return (timeSinceStart > this.fridayStart && timeSinceStart < this.fridayEnd);
		} else if (day == Calendar.SATURDAY) {
			return (timeSinceStart > this.saturdayStart && timeSinceStart < this.saturdayEnd);
		} else {
			return false;
		}
	}

	private boolean isActiveDate(Calendar cal, Calendar cal2) {
		int day = cal.get(Calendar.DAY_OF_WEEK);

		long timeSinceStart = cal.get(Calendar.HOUR_OF_DAY) * 3600000
				+ cal.get(Calendar.MINUTE) * 60000 + cal.get(Calendar.SECOND)
				* 1000;

		boolean firstDate = false;

		if (day == Calendar.SUNDAY) {
			firstDate = (timeSinceStart > this.sundayStart && timeSinceStart < this.sundayEnd);
		} else if (day == Calendar.MONDAY) {
			firstDate = (timeSinceStart > this.mondayStart && timeSinceStart < this.mondayEnd);
		} else if (day == Calendar.TUESDAY) {
			firstDate = (timeSinceStart > this.tuesdayStart && timeSinceStart < this.tuesdayEnd);
		} else if (day == Calendar.WEDNESDAY) {
			firstDate = (timeSinceStart > this.wednesdayStart && timeSinceStart < this.wednesdayEnd);
		} else if (day == Calendar.THURSDAY) {
			firstDate = (timeSinceStart > this.thursdayStart && timeSinceStart < this.thursdayEnd);
		} else if (day == Calendar.FRIDAY) {
			firstDate = (timeSinceStart > this.fridayStart && timeSinceStart < this.fridayEnd);
		} else if (day == Calendar.SATURDAY) {
			firstDate = (timeSinceStart > this.saturdayStart && timeSinceStart < this.saturdayEnd);
		} else {
			firstDate = false;
		}

		int day2 = cal2.get(Calendar.DAY_OF_WEEK);

		timeSinceStart = cal2.get(Calendar.HOUR_OF_DAY) * 3600000
				+ cal2.get(Calendar.MINUTE) * 60000 + cal2.get(Calendar.SECOND)
				* 1000;

		boolean secondDate = false;

		if (day2 == Calendar.SUNDAY) {
			secondDate = (timeSinceStart > this.sundayStart && timeSinceStart < this.sundayEnd);
		} else if (day2 == Calendar.MONDAY) {
			secondDate = (timeSinceStart > this.mondayStart && timeSinceStart < this.mondayEnd);
		} else if (day2 == Calendar.TUESDAY) {
			secondDate = (timeSinceStart > this.tuesdayStart && timeSinceStart < this.tuesdayEnd);
		} else if (day2 == Calendar.WEDNESDAY) {
			secondDate = (timeSinceStart > this.wednesdayStart && timeSinceStart < this.wednesdayEnd);
		} else if (day2 == Calendar.THURSDAY) {
			secondDate = (timeSinceStart > this.thursdayStart && timeSinceStart < this.thursdayEnd);
		} else if (day2 == Calendar.FRIDAY) {
			secondDate = (timeSinceStart > this.fridayStart && timeSinceStart < this.fridayEnd);
		} else if (day2 == Calendar.SATURDAY) {
			secondDate = (timeSinceStart > this.saturdayStart && timeSinceStart < this.saturdayEnd);
		} else {
			secondDate = false;
		}

		return (firstDate && secondDate);
	}

	private int nextDay(int day) {
		day++;

		day = day % 7;

		return day;
	}

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		IntradayMarketTimeline testedObject = new IntradayMarketTimeline(0L,
				0L, 34200000L, 57600000L, 34200000L, 57600000L, 34200000L,
				57600000L, 34200000L, 57600000L, 34200000L, 57600000L, 0L, 0L);

		//one week + 1 day  +  10:48am
		//604800000
		//86400000
		System.out.println("millisecond -> timeline"
				+ Long.toString(IntradayMarketTimeline.MILLIS_PER_DAY
						+ IntradayMarketTimeline.MILLIS_PER_WEEK + +38880000L));

		//leads to 121680000 milliseconds in timeline time
		System.out
				.println(Long.toString(testedObject
						.toTimelineValue(IntradayMarketTimeline.MILLIS_PER_DAY
								+ IntradayMarketTimeline.MILLIS_PER_WEEK
								+ +38880000L)));

		System.out.println("timeline -> millisecond "
				+ Long.toString(testedObject.toMillisecond(145080000L)));
	}
}