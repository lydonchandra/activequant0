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
package org.activequant.tradesystem.report.integration.birt;

import java.util.logging.Level;

import org.activequant.util.tools.DirUtils;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;

/**
 * This is the birt engine wrapper. <br>
 * <br>
 * <b>History:</b><br>
 *  - [02.06.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class BirtEngine {
	
	private final static String BIRT_LIBS = "libs/birt";
	private final static String BIRT_LOGS = "output";
	
	private static String birtLibs = BIRT_LIBS;
	private static String birtLogs = BIRT_LOGS;
	
	/**
	 * @return Returns the birtLibs.
	 */
	public static String getBirtLibs() {
		return birtLibs;
	}

	/**
	 * @param birtLibs The birtLibs to set.
	 */
	public static void setBirtLibs(String birtLibs) {
		BirtEngine.birtLibs = birtLibs;
	}

	/**
	 * @return Returns the birtLogs.
	 */
	public static String getBirtLogs() {
		return birtLogs;
	}

	/**
	 * @param birtLogs The birtLogs to set.
	 */
	public static void setBirtLogs(String birtLogs) {
		BirtEngine.birtLogs = birtLogs;
	}
	
	public static IReportEngine start() throws Exception {
		// Check output dir
		DirUtils.check(birtLogs);
		
		// Configure the Engine and start the Platform
		EngineConfig config = new EngineConfig();
		config.setEngineHome(birtLibs);
		config.setLogConfig(birtLogs, Level.ALL);
		
		// Start up the OSGi framework
		Platform.startup(config);
		IReportEngineFactory factory = (IReportEngineFactory) Platform
				.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
		IReportEngine engine = factory.createReportEngine(config);
		engine.changeLogLevel(Level.ALL);
		return engine;
	}

	public static void stop(IReportEngine engine) {
		engine.shutdown();
		Platform.shutdown();
	}
}
