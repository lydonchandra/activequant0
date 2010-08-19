package org.activequant.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.activequant.core.types.TimeStamp;

/**
 * Similar to SimpleDarteFormat, but:
 * <ul>
 *    <li> uses UTC time zone by the default.
 *    <li> allows nanosecond part to be printed (pattern <em>nnnnnn</em>)
 * </ul>
 * Example:
 * <pre>
 * YYYY/MM/dd HH:mm:ss SSSnnnnnn
 * </pre>
 * may output 
 * <pre>
 * 2007/12/02 19:03:16.670123456
 * </pre>
 * Nanosecond (like oth3er date-related parts) is optional. Therefore, if pattern
 * does not contain nanoseconds, this class works like UTC-based SimpleDateFormat.
 * <br>
 * <b>History:</b><br>
 *  - [2.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class TimeStampFormat {
	private static final TimeZone UTC_ZONE = TimeZone.getTimeZone("UTC");
	private static final String NANO_PATTERN="nnnnnn";
	
	private final String format;
	private final int    index;
	private final SimpleDateFormat dateFormat;
	
	public TimeStampFormat(String format) {
		this.format = format;
		
		int idx = format.indexOf(NANO_PATTERN);
		if(idx < 0) {
			// no nanos needed - works like SimpleDateFormat
			dateFormat = new SimpleDateFormat(format);
			index = -1;
		} else {
			// remove nanos pattern, and pass the rest to the date formatter
			dateFormat = new SimpleDateFormat(format.replace(NANO_PATTERN, ""));
			
			// adjust index for the quotes 
			int quotes = 0;
			for(int i = 0; i < idx; i++) {
				if(format.charAt(i) == '\'') quotes++;
			}
			if((quotes % 2) != 0) {
				// NANO_PATTERN within a string literal - not yet supported!
				throw new IllegalArgumentException("pattern 'nnnnnn' can not be used withing quoted format string - check single quotes: " + format);
			}
			idx -= quotes;
			index = idx;
		}
		
		dateFormat.setTimeZone(UTC_ZONE);
	}
	
	public void setTimeZone(TimeZone tz) {
		dateFormat.setTimeZone(tz);
	}

	public String format(TimeStamp timeStamp) {
		if(index < 0) {
			return dateFormat.format(timeStamp.getDate());
		} else {
			StringBuilder bld = new StringBuilder(dateFormat.format(timeStamp.getDate()));
			bld.insert(index, formatNanos(timeStamp));
			return bld.toString();
		}
	}
	
	private static String formatNanos(TimeStamp ts) {
		int nanos = (int) (ts.getNanoseconds() % 1000000L);
		String out = Integer.toString(nanos);
		switch(out.length()) {
		case 1: return "00000" + out;
		case 2: return "0000" + out;
		case 3: return "000" + out;
		case 4: return "00" + out;
		case 5: return "0" + out;
		default: return out;
		}
	}
	
	public TimeStamp parse(String value) {
		try {
			if(index < 0) {
				return new TimeStamp(dateFormat.parse(value));
			} else {
				String nanoString = value.substring(index, index + NANO_PATTERN.length());
				int nanos = Integer.parseInt(nanoString);
				Date date = dateFormat.parse(
						value.substring(0, index) 
						+ value.substring(index + NANO_PATTERN.length()));
				return new TimeStamp(date, nanos);
			}
		} catch(Exception ex) {
			throw new IllegalArgumentException("bad format '" + value + "', expected pattern: '" + format + "'", ex);
		}
	}
	
	public String toString() {
		return format;
	}
}
