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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.tradesystem.domainmodel.Report;
import org.activequant.tradesystem.report.IReportRendererService;
import org.activequant.util.charting.CandlestickChart;
import org.activequant.util.charting.EquityChart;
import org.activequant.util.tools.DirUtils;
import org.activequant.util.tools.StackTraceParser;
import org.apache.log4j.Logger;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.HTMLRenderContext;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderContext;

/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [02.06.2007] Created (Erik Nijkamp)<br>
 *  - [05.07.2007] Making report engine private static and init upon startup. (Ulrich Staudinger) <br>
 *  - [05.07.2007] Adding renderer thread to decouple and speed up process. (Ulrich Staudinger) <br>
 *  - [03.08.2007] Removed asynchronousMode from interface, some cleanup (Erik Nijkamp) <br>
 *  - [18.11.2007] Output directory fix (Erik Nijkamp)<br>
 *  - [05.12.2007] Making chart dimensions configurable (Ulrich Staudinger)<br>
 *
 *  @author Erik Nijkamp
 *  @author Ulrich Staudinger
 */
public class BirtReportRendererImpl implements IReportRendererService {
	
	protected final static Logger log = Logger.getLogger(BirtReportRendererImpl.class);
	
	private int equityChartWidth = 600;
	private int equityChartHeight = 400;
	
	private int underlyingChartWidth = 600;
	private int underlyingChartHeight = 400;
	
	
	
	private final class RunAndRenderer extends Thread {
		private boolean isFree = true;
		public void run(){
			isFree = false;
			while(reportTasks.size()>0){
				try{
					Report report = reportTasks.get(0);
					reportTasks.remove(0);
					runTask(report);
					log.info("[RunAndRenderer] report rendered, reports in render queue left: "+reportTasks.size());
				}
				catch(Exception x){
					log.warn("[RunAndRenderer] "+StackTraceParser.getStackTrace(x));
				}
			}
			isFree = true; 
		}
	}
	
	public enum DocumentType { PDF, HTML };
	private static final String FILENAME = "report";
	
	private IReportEngine birtReportEngine = null;
	private IReportRunnable design = null;
	private RunAndRenderer renderThread = new RunAndRenderer();		
	private String filepath = "";
	private String reportDesign;
	private DocumentType documentType = DocumentType.HTML;
	private Map<String, Object> objects = new HashMap<String, Object>();
	private List<Report> reportTasks = Collections.synchronizedList(new LinkedList<Report>());
	private boolean asynchronousMode = false;
	
	/**
	 * default constructor
	 *
	 */
	public BirtReportRendererImpl() throws Exception {
		init();
	}
	
	public BirtReportRendererImpl(String reportDesign) throws Exception {
		init();
		setReportDesign(reportDesign);
	}
	
	public BirtReportRendererImpl(String reportDesign, String filePath) throws Exception {
		this.filepath = filePath;
		init();
		setReportDesign(reportDesign);
	}	
	
	public BirtReportRendererImpl(String reportDesign, String filePath, String birtLibPath) throws Exception {
		BirtEngine.setBirtLibs(birtLibPath);
		this.filepath = filePath;
		init();
		setReportDesign(reportDesign);
	}	
	
	private void init() throws Exception {
		if(filepath.length() > 0) {
			filepath = DirUtils.appendSlash(filepath);
		}

		// check dirs
		DirUtils.check(filepath);
		// and launch the engine
		birtReportEngine = BirtEngine.start();
	}
	
	/**
	 * @return Returns the documentType.
	 */
	public DocumentType getDocumentType() {
		return documentType;
	}

	/**
	 * @param documentType The documentType to set.
	 */
	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}
	
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
	
	/**
	 * @return Returns the reportDesign.
	 */
	public String getReportDesign() {
		return reportDesign;
	}

	/**
	 * 
	 * @param reportDesign The reportDesign to set - 
	 * a report design is the design definition file. 
	 * 
	 */
	public void setReportDesign(String reportDesign) throws Exception {
		// Set
		this.reportDesign = reportDesign;
		// Open report design
		design = birtReportEngine.openReportDesign(reportDesign);
	}
	
	/**
	 * renderReport implementation. 
	 */
	public void renderReport(Report report) throws Exception {		
		log.info("Rendering reports.");	
		if (!asynchronousMode) {
			runTask(report);
		} else {
			addRenderTask(report);
		}
	}

	private synchronized void addRenderTask(Report report){
		this.reportTasks.add(report);
		if(renderThread.isFree){
			renderThread = new RunAndRenderer();
			renderThread.start();
		}
	}
	
	private void runTask(Report report) throws Exception {
		// render the charts. 
		{
			log.info("# Rendering charts.");
			if (report.getMarket() != null) {
				for (CandleSeries cs : report.getMarket().getCandleSeries()) {
					CandlestickChart chart = new CandlestickChart();
					chart.setCandleSeries(cs);
					chart.drawExecutions(cs,
							report.getAccount().getOrderBook(), report
									.getAccount().getExecutionBook());

					String filename = filepath 
						+ DirUtils.asFileName(cs.getInstrumentSpecification()) + ".png";
					log.info("\trendering executions: " + filename);
					chart.renderToPng(filename, underlyingChartWidth, underlyingChartHeight);
				}
			}
		}

		log.info("# Rendering equity curve.");
		{
			if (report.getAccount() != null) {
				if (report.getAccount().getBalanceBook() != null) {
					EquityChart chart = new EquityChart();
					chart.createEquityChart(report.getAccount()
							.getBalanceBook());
					String filename = filepath + "equity.png";
					log.info("\trendering equity chart: " + filename);
					chart.renderToPng(filename, equityChartWidth, equityChartHeight);
				}
			}
		}

		log.info("# Rendering report.");
		{
			// create task to run and render report
			IRunAndRenderTask task = birtReportEngine.createRunAndRenderTask(design);
			task.setAppContext(getAppContext());
			task.setRenderOption(getRenderOptions());
			task.addScriptableJavaObject("report", report);
			for (Entry<String, Object> object : objects.entrySet()) {
				task.addScriptableJavaObject(object.getKey(), object.getValue());
			}
			// run report
			task.run();
			task.close();
		}
	}
	
	private HashMap<String, Object> getAppContext() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		if(documentType == DocumentType.HTML) {
			HTMLRenderContext renderContext = new HTMLRenderContext();
			map.put(EngineConstants.APPCONTEXT_HTML_RENDER_CONTEXT, renderContext);	
		} else {
			PDFRenderContext renderContext = new PDFRenderContext();
			map.put(EngineConstants.APPCONTEXT_PDF_RENDER_CONTEXT, renderContext);				
		}
		return map;
	}
	
	private IRenderOption getRenderOptions() {
		if(documentType == DocumentType.HTML) {
			// set output options
			IRenderOption options = new HTMLRenderOption();
			options.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_HTML);
			options.setOutputFileName(filepath + FILENAME + ".html");
			return options;
		} else {
			// set output options
			HTMLRenderOption options = new HTMLRenderOption();
			options.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_PDF);
			options.setOutputFileName(filepath + FILENAME + ".pdf");
			return options;			
		}		
	}

	/**
	 * @return the objects
	 */
	public Map<String, Object> getObjects() {
		return objects;
	}

	/**
	 * @param objects the objects to set
	 */
	public void setObjects(Map<String, Object> objects) {
		this.objects = objects;
	}

	/**
	 * @return the asynchronousMode
	 */
	public boolean isAsynchronousMode() {
		return asynchronousMode;
	}

	/**
	 * @param asynchronousMode the asynchronousMode to set
	 */
	public void setAsynchronousMode(boolean asynchronousMode) {
		this.asynchronousMode = asynchronousMode;
	}


	/**
	 * the width of the rendered equity chart. 
	 * @return
	 */

	public int getEquityChartWidth() {
		return equityChartWidth;
	}

	
	public void setEquityChartWidth(int equityChartWidth) {
		this.equityChartWidth = equityChartWidth;
	}

	/**
	 * the height of the rendered equity chart. 
	 * @return
	 */
	public int getEquityChartHeight() {
		return equityChartHeight;
	}

	public void setEquityChartHeight(int equityChartHeight) {
		this.equityChartHeight = equityChartHeight;
	}

	/**
	 * the width of a rendered underlying chart. 
	 * @return
	 */
	public int getUnderlyingChartWidth() {
		return underlyingChartWidth;
	}

	public void setUnderlyingChartWidth(int underlyingChartWidth) {
		this.underlyingChartWidth = underlyingChartWidth;
	}
	
	/**
	 * the height of a rendered underlying chart. 
	 * @return
	 */
	public int getUnderlyingChartHeight() {
		return underlyingChartHeight;
	}

	public void setUnderlyingChartHeight(int underlyingChartHeight) {
		this.underlyingChartHeight = underlyingChartHeight;
	}
}