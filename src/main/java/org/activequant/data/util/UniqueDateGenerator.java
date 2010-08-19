package org.activequant.data.util;

import java.util.Date;

import org.activequant.core.types.TimeStamp;

/**
 * Utility class to generate unique date for market events.
 * The problem is that some feed sources do not supply milliseconds (Opentick's)
 * in the date field. Since AQ code depends on date to be unique within the
 * type and feed source, we need to fake millisecond part.
 * This object generates unique date by catching duplicates and 
 * assigning milliseconds sequentially.
 * <br>
 * <b>History:</b><br>
 *  - [27.11.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class UniqueDateGenerator {
	
	private static long NANOS_IN_MILLIS = 1000000;
	
	private long lastMillis = 0L;
	private long nanos      = 0;

	public TimeStamp generate(Date input) {
		
		long time = input.getTime();
		if(time > lastMillis) {
			lastMillis = time;
			nanos = 0;
		} else if(time == lastMillis) {
			nanos++;
			if(nanos >= NANOS_IN_MILLIS) {
				// can come here only if events are generated at rate one per nanosecond
				// practically impossible
				throw new AssertionError("failed to disambiguate");
			}
		} else {
			throw new AssertionError("input dates are out-of-order");
		}

		return new TimeStamp(lastMillis * NANOS_IN_MILLIS + nanos); // disambiguate
	}
}
