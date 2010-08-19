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

import org.activequant.core.util.TimeStampFormat;
import org.activequant.util.tools.IdentityUtils;


/**
 *  Expiry type : encapsulates year and month of option expiry.<br>
 * <br>
 * <b>History:</b><br>
 *  - [19.10.2007] Created (Mike Krotikov)<br>
 *  - [23.10.2007] Adding days to simple date format (Ulrich Staudinger)<br>
 *  - [07.11.2007] Fixed days/month parsing (Erik Nijkamp)<br> 
 *
 *  @author Mike Kroutikov
 */
public final class Expiry implements Comparable<Expiry> {
	
	private static final TimeStampFormat FMT_LONG = new TimeStampFormat("yyyyMMdd");
	
	private final TimeStamp stamp;
	
	public Expiry() {
		stamp = new TimeStamp();
	}
	
	public Expiry(Expiry other) {
		stamp = other.stamp;
	}
	
	public Expiry(TimeStamp timeStamp) {
		// this normalizes the date (strips off hour/minute/etc details)
		this(FMT_LONG.format(timeStamp));
	}
	
	public Expiry(String text) {
		stamp = parseExpiryDate(text);
	}
	
	public TimeStamp getTimeStamp() { return stamp; }

	public String toString() {
		return FMT_LONG.format(this.stamp);
	}
	
	private static TimeStamp parseExpiryDate(String text) {
		return FMT_LONG.parse(text);
	}

	public int compareTo(Expiry other) {
		return this.stamp.compareTo(other.stamp);
	}
	
	public int hashCode() {
		return this.stamp.hashCode();
	}
	
	public boolean equals(Object other) {
		return IdentityUtils.equalsTo(this, other);
	}
}
