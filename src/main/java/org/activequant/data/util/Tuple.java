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
package org.activequant.data.util;

/**
 * simple tuple data container. <br>
 * <br>
 * <b>History:</b><br>
 *  - [24.06.2007] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class Tuple<T,V> {

	private T object1 = null; 
	private V object2 = null; 
	
	public Tuple() {
	
	}

	public Tuple(T o1, V o2) {
		this.object1 = o1; 
		this.object2 = o2; 
	}

	public T getObject1() {
		return object1;
	}

	public void setObject1(T object1) {
		this.object1 = object1;
	}

	public V getObject2() {
		return object2;
	}

	public void setObject2(V object2) {
		this.object2 = object2;
	}	
}