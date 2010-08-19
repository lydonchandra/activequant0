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


/**
 * Represents instrument symbol.
 * This class is immutable.
 * <br>
 * <b>History:</b><br>
 *  - [30.09.2007] Created based on Mike Kroutikovs code (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 *  @author Mike Kroutikov
 */
public final class Symbol implements Comparable<Symbol> {
	   
    private final String name;
    
    public Symbol(String val) {
    	name = val;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
   
    public int compareTo(Symbol other) {
        return name.compareTo(other.name);
    }
   
    @Override
    public boolean equals(Object other) {
        if(other instanceof Symbol) {
            return compareTo((Symbol) other) == 0;
        }
        return false;
    }
   
    @Override
    public String toString() {
        return name;
    }
}

