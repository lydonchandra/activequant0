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
package org.activequant.tradesystem.domainmodel;

import static org.activequant.util.tools.IdentityUtils.equalsTo;
import static org.activequant.util.tools.IdentityUtils.safeCompare;
import static org.activequant.util.tools.IdentityUtils.safeHashCode;

import org.activequant.core.types.TimeStamp;

/**
 * This is an entry for the balance class.  <br>
 * <br>
 * <b>History:</b><br>
 *  - [03.05.2007] Created (Ulrich Staudinger)<br>
 *  - [14.07.2007] Added persistence (Erik Nijkamp)<br>
 *  - [29.09.2007] Removed annotations (Erik Nijkamp)<br>
 *  - [02.11.2007] Added NOT_SET (Erik Nijkamp)<br>
 *  
 *  @author Ulrich Staudinger
 */
public class BalanceEntry implements Comparable<BalanceEntry> {
	
	public static final double NOT_SET = -1;
	
	private Long id;
    
	private TimeStamp stamp; 
    private double value = NOT_SET;
    
    public BalanceEntry() {
    }
    
	public BalanceEntry(double value, TimeStamp date) {
	    this.value = value; 
		this.stamp = date; 
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public boolean hasId() {
		return id != null;
	}
	
	public TimeStamp getTimeStamp() {
		return stamp;
	}
	
	public void setTimeStamp(TimeStamp date) {
		this.stamp = date;
	}	
    
    public String toString() {
        return "Transaction: " + stamp + " : " + value;
    }
    
    public double getValue() {
        return value; 
    }

	public void setValue(double value) {
		this.value = value;
	}
	
	public int hashCode() {
		return safeHashCode(stamp);
	}
	
	public boolean equals(Object o) {
		return equalsTo(this, o);
	}
	
	public int compareTo(BalanceEntry other) {
		return safeCompare(stamp, other.stamp);
	}
}
