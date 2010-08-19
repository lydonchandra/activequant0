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
package org.activequant.util.tools;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [06.05.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class DecorationsMap extends HashMap<String, Object> {

	private static final long serialVersionUID = -3809303664491393186L;
	
	public DecorationsMap() {
		super();
	}
	
	public DecorationsMap(String[] keys, Object[] values) {
		super();
		assert(keys.length == values.length);
		for(int i = 0; i < keys.length; i++) {
			put(keys[i], values[i]);
		}
	}
	
	public DecorationsMap(Map<String,Object> map) {
		for(String key : map.keySet()) {
			put(key, map.get(key));
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getObjectByType(Class<T> type) {
		for(Object object: values()) {
			if(object.getClass().equals(type)) {
				return (T) object;
			}
		}
		throw new IllegalArgumentException();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T[] getObjectsByType(Class<T> type) {
		List<T> result = new ArrayList<T>();
		for(Object object: values()) {
			if(object.getClass().equals(type)) {
				result.add((T) object);
			}
		}		
		T[] array = (T[]) Array.newInstance(type, result.size());
		return result.toArray(array);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String name) {
		return (T) super.get(name);
	}
	
	public void addDecorations(String decos) {
		String[] split = decos.split(";");
		for(String pair : split) {
			String[] terms = pair.split("=");
			addDecorations(terms);
		}
	}
	
	public void addDecorations(String[] decos) {
		assert(decos.length % 2 == 0);
		for(int i = 0; i < decos.length; i+=2) {
			put(decos[i], decos[i+1]);
		}
	}
}