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
package org.activequant.core.domainmodel;

import static org.activequant.util.tools.IdentityUtils.safeCompare;
import static org.activequant.util.tools.IdentityUtils.safeHashCode;
import static org.activequant.util.tools.IdentityUtils.equalsTo;

import org.activequant.core.types.TimeStamp;

/**
 * Sampling interval (start date plus end date).<br>
 * <br>
 * <b>History:</b><br>
 *  - [31.05.2006] Created (Erik Nijkamp)<br>
 *  - [08.10.2007] Added identity (just in case we need it) (Mike Kroutikov)<br>
 *
 *  @author Erik Nijkamp
 */
public class Sample implements Comparable<Sample> {
	
	private TimeStamp start;
	private TimeStamp end;
	
	public Sample() {
	}
	
	public Sample(TimeStamp start, TimeStamp end) {
		this.start = start;
		this.end = end;
	}
	
	public TimeStamp getStartTimeStamp() {
		return start;		
	}
	
	public TimeStamp getEndTimeStamp() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEndTimeStamp(TimeStamp end) {
		this.end = end;
	}

	/**
	 * @param start the start to set
	 */
	public void setStartTimeStamp(TimeStamp start) {
		this.start = start;
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		// ATTENTION: keep in sync with compareTo();
		return safeHashCode(start)
			+ safeHashCode(end);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int compareTo(Sample o) {
		// ATTENTION: keep in sync with hashCode();
		int rc;
		
		if((rc = safeCompare(this.start, o.start)) != 0) {
			return rc;
		}
		if((rc = safeCompare(this.end, o.end)) != 0) {
			return rc;
		}
		
		return 0;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object other) {
		return equalsTo(this, other);
	}
}
