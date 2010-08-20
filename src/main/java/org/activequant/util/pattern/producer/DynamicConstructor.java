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
package org.activequant.util.pattern.producer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @TODO desc<br>
 * @TODO: is this class still requried ? 
 * <br>
 * <b>History:</b><br>
 *  - [28.10.2006] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class DynamicConstructor<Interface> {
	
	public static class Parameter {
		private Class<?> type;
		private String name;
		private char arrayDelimiter;
		
		public Parameter(Class<?> type, String name, char arrayDelimiter) {
			this(type, name);
			this.arrayDelimiter = arrayDelimiter;
		}
	
		public Parameter(Class<?> type, String name) {
			this.type = type;
			this.name = name;
		}
		
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * @return the type
		 */
		public Class<?> getType() {
			return type;
		}

		/**
		 * @return the delimiter
		 */
		public char getArrayDelimiter() {
			return arrayDelimiter;
		}
	}
	
	private Parameter[] params;

	private Constructor<? extends Interface> constructor;

	public DynamicConstructor(Class<? extends Interface> parent, Parameter... params)
			throws SecurityException, NoSuchMethodException {
		Class<?>[] types = new Class<?>[params.length];
		for (int i = 0; i < types.length; i++)
			types[i] = params[i].getType();
		constructor = parent.getConstructor(types);
		this.params = params;
	}

	public Interface newInstance(Object... args) throws IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		return constructor.newInstance(args);
	}

	/**
	 * @return the params
	 */
	public Parameter[] getParams() {
		return params;
	}
}
