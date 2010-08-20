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


/**
 * @TODO desc<br>
 * @TODO: is this class still requried ? 
 * <br>
 * <b>History:</b><br>
 *  - [27.10.2006] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class Producer<Product> {

	public Producer() {
	}

	@SuppressWarnings("unchecked")
	public synchronized Product create(String className) throws Exception {
		return create(className, new Object[] {});
	}

	public <T1> Product create(String className, T1 t1) throws Exception {
		return create(className, new Object[] { t1 });
	}

	public <T1, T2> Product create(String className, T1 t1, T2 t2)
			throws Exception {
		return create(className, new Object[] { t1, t2 });
	}

	public <T1, T2, T3> Product create(String className, T1 t1, T2 t2, T3 t3)
			throws Exception {
		return create(className, new Object[] { t1, t2, t3 });
	}

	@SuppressWarnings("unchecked")
	public synchronized Product create(String className, Object[] args)
			throws Exception {
		// Get reflected class
		Class<? extends Product> clazz = (Class<? extends Product>) Class
				.forName(className);
		if (clazz == null)
			throw new Exception("Cannot find implementation '" + className
					+ "'.");

		// Get constructor by signature
		Class<?>[] types = new Class<?>[args.length];
		for (int i = 0; i < types.length; i++)
			types[i] = args[i].getClass();

		Constructor<? extends Product> constructor = clazz
				.getConstructor(types);

		// Create instance
		Product instance = constructor.newInstance(args);
		return instance;
	}
}