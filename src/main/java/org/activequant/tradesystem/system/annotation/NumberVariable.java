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
package org.activequant.tradesystem.system.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 
 * Use this annotation to tag variables inside a trade system. 
 * An optimizer could use this to iterate over the parameter and find 
 * the best solution.<br>
 * <br>
 * <b>History:</b><br>
 *  - [27.09.2007] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NumberVariable {
	/**
	 * @return the minimum value of this variable, i.e. 21
	 */
	double min();
	/**
	 * @return the maximum value of this variable, ie. 100
	 */
	double max();
	/**
	 * @return the step size of this variable. ie. 0.1
	 */
	double step();
}
