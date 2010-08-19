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
package org.activequant.tradesystem.types;

import org.activequant.tradesystem.broker3.IBroker;
import org.activequant.util.tools.IdentityUtils;


/**
 * TODO<br>
 * <br>
 * <b>History:</b><br>
 *  - [29.05.2007] Created (Ulrich.)<br>
 *  - [11.07.2007] Added class constructor, equals, toString (Erik)<br>
 *  - [14.07.2007] Added persistence (Erik Nijkamp)<br>
 *  - [23.08.2007] Switching to strings (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public class BrokerId implements Comparable<BrokerId> {
	
	private final String identifier; 
	
	public BrokerId(String identifier) {
		this.identifier = identifier;
	}
	
	public BrokerId(Class<? extends IBroker> identifier) {
		this.identifier = identifier.getName();
	}
	
	public boolean equals(Object other) {
		return IdentityUtils.equalsTo(this, other);
	}
	
	public int hashCode() {
		return IdentityUtils.safeHashCode(identifier);
	}
	
	public String toString() {
		return identifier;
	}

	public int compareTo(BrokerId other) {
		return IdentityUtils.safeCompare(identifier, other.identifier);
	}
}
