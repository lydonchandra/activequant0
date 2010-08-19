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
package org.activequant.core.util;

import java.util.HashMap;

import org.activequant.core.types.TimeStamp;
import org.activequant.util.exceptions.ValueNotFoundException;

/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [07.05.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class MarketParameterMap extends HashMap<TimeStamp, HashMap<String, Object>> {

	private static final long serialVersionUID = 5463468632722866072L;
	
	public void put(TimeStamp date, String key, double value) {
		if(containsKey(date)) {
			get(date).put(key, value);
		} else {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(key, value);
			put(date, map);
		}
	}

	/**
     * method to fetch a market parameter. 
     * @param key
     * @return
     * @throws NoSuchParameter
     */
    public Object get(TimeStamp date, String key)
			throws ValueNotFoundException {
    	HashMap<String, Object> map = get(date);
		if (map == null) {
			throw new ValueNotFoundException("No such date : " + date);
		}
		Object parameter = map.get(key);
		if (parameter == null) {
			throw new ValueNotFoundException("No such parameter : " + key);
		}
		return parameter;
	}
}
