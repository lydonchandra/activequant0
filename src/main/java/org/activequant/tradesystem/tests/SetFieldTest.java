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
package org.activequant.tradesystem.tests;

import org.activequant.tradesystem.system.integration.DualMovingAverageSystem;

/**
 * A test to test reflection and annotations ... <br>
 * <br>
 * <b>History:</b><br>
 *  - [07.06.2007] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class SetFieldTest {
	
	@SuppressWarnings("unchecked")
	public static void main(java.lang.String[] s)
			throws java.lang.ClassNotFoundException, // 0 
			java.lang.NoSuchFieldException, // 1 
			java.lang.Exception // 2 
	{
		final java.lang.Class example = java.lang.Class.forName(DualMovingAverageSystem.class.getName()); // 0 
		Object o = example.newInstance();
		final java.lang.reflect.Field field = o.getClass().getField("field"); // 1 
		field.setAccessible(true);
		field.setInt(o, 2423); // 2 
		System.out.println(field);
	}
	
}