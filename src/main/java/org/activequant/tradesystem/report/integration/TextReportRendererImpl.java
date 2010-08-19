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
package org.activequant.tradesystem.report.integration;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;

import org.activequant.regression.math.Series;
import org.activequant.tradesystem.domainmodel.Report;
import org.activequant.tradesystem.report.IReportRendererService;
import org.activequant.util.tools.StringUtils;
import org.apache.log4j.Logger;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [01.03.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 *  @author Ulrich Staudinger
 */
public class TextReportRendererImpl implements IReportRendererService {
	
	private final static Logger log = Logger.getLogger(TextReportRendererImpl.class);
		
	private String filepath;
	
	/**
	 * @return the filepath
	 */
	public String getFilepath() {
		return filepath;
	}

	/**
	 * @param filepath the filepath to set
	 */
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	
	public void renderReport(Report report) throws Exception {
		
		log.info("Rendering reports.");
		// folder
		checkFolder();
		// open
		PrintWriter out = new PrintWriter(filepath + report.getAccount().getHolder()+ ".txt");
        out.println("Report from "+new Date()+".\n");
            
            // cool method call :-)
			final String line = StringUtils.repeat('=', 90);
			// header
			{
				out.println(line);
				out.println("## Model" + report.getAccount().getHolder() + " ##");
				out.println(line);
                
                Iterator<String> keysIt = report.getReportValues().keySet().iterator();
                while(keysIt.hasNext()){
                    String key = keysIt.next();
                    Object value = report.getReportValues().get(key);
                    out.print(key);
                    out.print(": ");
                    out.println(value.toString());
                }
                
			}				
			out.println();
		// close
        out.close();				
	}
	

	public void renderReport(boolean asynchronousMode, Report report) throws Exception {
		renderReport(report);				
	}

	
	private void checkFolder() {
		File folder = new File(filepath);
		if(!folder.exists()) folder.mkdir();
	}
	
	public String getSeriesNames(Series[] series) {
		// filter names
		String names = "";
		for(int i = 0; i < series.length; i++) {
			names += getFileName(series[i].getName()) + (i == series.length-1 ? "" : ",");
		}
		return names;
	}
	
	public String getFileName(String path) {
		// remove dirs
		if(path.contains("/")) path = path.substring(path.lastIndexOf("/")+1);
		// remove dot
		if(path.contains(".")) path = path.substring(0, path.lastIndexOf("."));
		// done
		return path;
	}


}
