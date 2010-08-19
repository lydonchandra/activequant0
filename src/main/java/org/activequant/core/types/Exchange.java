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


/**
 *  Symbols.<br>
 * <br>
 * <b>History:</b><br>
 *  - [13.07.2006] Created (Erik Nijkamp)<br>
 *
 *
 *  @author Erik Nijkamp
 */
public enum Exchange {
    
    // Standard generic names
	CBOT("Chicago Board of Trade"),
	CME("Chicago Mercantile Exchange"),
	COMEX("New York Commodities Exchange"),
	NYMEX("New York Mercantile Exchange"),
	LIFFE("London International Financial Futures Exchange"),
	EUREX("European Exchange"),
	@Deprecated
	DTB("Deutsche Termin-Boerse");
	
	private String desc;
	
	Exchange(String desc) {
		this.desc = desc;
	}
	
	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	public String toString() {
		return name();
	}
	
	public static Exchange toClass(String value) {
		for(Exchange symbol: values()) {
			if(symbol.desc.equals(value)) {
				return symbol;
			}
		}
		throw new IllegalArgumentException("Value '"+value+"' not defined as constant.");
	}
}