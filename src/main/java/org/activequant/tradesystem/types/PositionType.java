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


/**
 * @TODO <br>
 * <br>
 * <b>History:</b><br>
 *  - [03.05.2006] Created (Ulrich Staudinger)<br>
 *  - [27.0562006] Polished (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 */
public enum PositionType {

	LONG(1), 
    SHORT(2);
	
	private int value; 
    
	PositionType(int i){
		this.value = i;
	}
	
	public int getValue(){
		return value; 
	}
	
	public static PositionType valueOf(int i) {
		for(PositionType type: values()) {
			if(type.value == i)
				return type;
		}
		throw new IllegalArgumentException("Unsupported position type");
	}
}

