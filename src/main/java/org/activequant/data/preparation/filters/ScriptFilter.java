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
package org.activequant.data.preparation.filters;

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.data.preparation.FilterBase;
import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFManager;





/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [30.04.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class ScriptFilter extends FilterBase {
	
	public static final String PARAMETER_NAME = "series";
	
	private BSFManager manager;
	private BSFEngine engine;
	private String script;
	private String languageName;
	
	public ScriptFilter(String languageName, String languageInitials) throws Exception {
		// members
		this.languageName = languageName;
		// init
		manager = new BSFManager();
		engine = manager.loadScriptingEngine(languageName);
	}

	public ScriptFilter(String languageName, String languageInitials,
			String script) throws Exception {
		this(languageName, languageInitials);
		// members
		this.script = script;
	}

	/**
	 * @return the script
	 */
	public String getScript() {
		return script;
	}

	/**
	 * @param script the script to set
	 */
	public void setScript(String script) {
		this.script = script;
	}

	public CandleSeries[] process(CandleSeries... series) throws Exception {
		// new series
		CandleSeries[] newSeries = cloneSeries(series);
		// publish bean
		manager.declareBean(PARAMETER_NAME, newSeries, CandleSeries[].class);
		// call script
		Object result = engine.eval(languageName, 0, 0, script);
		// convert
		return convertResult(result);
	}
	
	private CandleSeries[] convertResult(Object result) throws Exception {
		if(result.getClass() == CandleSeries[].class) {
			return (CandleSeries[]) result;
		}
		throw new Exception("Cannot convert result of type '"
				+ result.getClass().getSimpleName() + "'.");
	}
}