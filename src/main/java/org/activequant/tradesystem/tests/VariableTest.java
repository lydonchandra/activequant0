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

import java.lang.reflect.Field;

import org.activequant.tradesystem.system.annotation.NumberVariable;
import org.junit.Test;



/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [07.06.2007] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class VariableTest {

	class A {
		@NumberVariable(min=1, max=10, step=1)
		int a = 0; 
	}
	
	@Test
	public void testVariable() {
		A a = new A();
		Field[] fields = a.getClass().getDeclaredFields();
		for (Field f : fields) {
			System.out.print("Field " + f + " : ");
			if (f.isAnnotationPresent(NumberVariable.class)) {
				System.out.print("Annotated! ");
				NumberVariable var = f.getAnnotation(NumberVariable.class);
				System.out.print(" Min : " + var.min());
				System.out.print(" Max : " + var.max());
				System.out.println(" Step : " + var.step());

			} else {
				System.out.println("Not annotated");
			}
		}
	}	
}
