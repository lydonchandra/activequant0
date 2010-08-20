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

import java.util.Properties;


/**
 * @TODO desc<br>
 * @TODO: is this class still requried ? 
 * <br>
 * <b>History:</b><br>
 *  - [28.10.2006] Created (Erik Nijkamp)<br>
 *
 *
 *  @author Erik Nijkamp
 */
public class LooseFacade {
	
	protected <Integration> Integration createIntegration(String classname,
			Properties config) throws Exception {
		return new DynamicProducer<Integration>().create(classname, config);
	}

	protected <Integration, T1> Integration createIntegration(String classname, T1 t1,
			Properties config) throws Exception {
		return new DynamicProducer<Integration>().create(classname, t1, config);
	}

	protected <Integration, T1, T2> Integration createIntegration(String classname, T1 t1,
			T2 t2, Properties config) throws Exception {
		return new DynamicProducer<Integration>().create(classname, t1, t2, config);
	}

	protected <Integration, T1, T2, T3> Integration createIntegration(String classname,
			T1 t1, T2 t2, T3 t3, Properties config)
			throws Exception {
		return new DynamicProducer<Integration>()
				.create(classname, t1, t2, t3, config);
	}

	protected <Integration, T1, T2, T3, T4> Integration createIntegration(String classname,
			T1 t1, T2 t2, T3 t3, T4 t4, Properties config)
			throws Exception {
		return new DynamicProducer<Integration>().create(classname, t1, t2, t3, t4,
				config);
	}
	
	protected <Integration> Integration createIntegration(String classname,
			String[][] config) throws Exception {
		return new DynamicProducer<Integration>().create(classname, config);
	}

	protected <Integration, T1> Integration createIntegration(String classname, T1 t1,
			String[][] config) throws Exception {
		return new DynamicProducer<Integration>().create(classname, t1, config);
	}

	protected <Integration, T1, T2> Integration createIntegration(String classname, T1 t1,
			T2 t2, String[][] config) throws Exception {
		return new DynamicProducer<Integration>().create(classname, t1, t2, config);
	}

	protected <Integration, T1, T2, T3> Integration createIntegration(String classname,
			T1 t1, T2 t2, T3 t3, String[][] config)
			throws Exception {
		return new DynamicProducer<Integration>()
				.create(classname, t1, t2, t3, config);
	}

	protected <Integration, T1, T2, T3, T4> Integration createIntegration(String classname,
			T1 t1, T2 t2, T3 t3, T4 t4, String[][] config)
			throws Exception {
		return new DynamicProducer<Integration>().create(classname, t1, t2, t3, t4,
				config);
	}

}
