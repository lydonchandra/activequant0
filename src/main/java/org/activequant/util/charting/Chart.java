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
package org.activequant.util.charting;

import java.awt.Font;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.activequant.core.types.TimeStamp;
import org.activequant.data.util.Tuple;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;

/**
 * 
 * Chart base class, based on JFreeChart<br>
 * <br>
 * <b>History:</b><br> 
 * - [14.08.2007] Created (Ulrich Staudinger)<br> 
 * - [29.09.2007] Removed warnings (Erik Nijkamp)<br>
 * - [31.10.2007] Cleanup and adding methods from derived classes (Ulrich Staudinger)<br>
 * 
 * @author ustaudinger
 */
public class Chart {

	protected final static Logger log = Logger.getLogger(Chart.class);
	
	/**
	 * the underlying chart.
	 */
	protected JFreeChart chart;
	/**
	 * counter storing the amount of current data sets in use.
	 */
	protected List<TimeSeries> datasets = new ArrayList<TimeSeries>();

	/**
	 * plain constructor.
	 */
	public Chart() {
		chart = ChartFactory.createTimeSeriesChart(null, "", "", null, true, false, false);
	}
	
	/**
	 * overloaded constructor. 
	 * @param timeAxisLabel
	 * @param valueAxisLabel
	 */
	public Chart(String timeAxisLabel, String valueAxisLabel){
		chart = ChartFactory.createTimeSeriesChart(null, timeAxisLabel, valueAxisLabel, null, true, false, false);
	}

	/**
	 * method to add a line series chart to the current chart. 
	 * @param title
	 * @param dateAndValues
	 */
	public void addLineSeriesChart(String title, List<Tuple<TimeStamp, Double>> dateAndValues) {
		
		// creating a new jfree chart time series object. 
		final TimeSeries ts = new TimeSeries(title, Millisecond.class);
		
		// iterate over the incoming value tuples and add them.  
		for (Tuple<TimeStamp, Double> tuple : dateAndValues) {
			TimeSeriesDataItem item = new TimeSeriesDataItem(new Millisecond(tuple.getObject1().getDate()), tuple.getObject2());
			ts.addOrUpdate(item.getPeriod(), item.getValue());
		}

		datasets.add(ts);
		
		// 
		final TimeSeriesCollection dataset = new TimeSeriesCollection(ts);
		
		// add it to the chart plot. 
		final XYPlot plot1 = chart.getXYPlot();

		// disable all shape rendering. 
		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setDrawOutlines(false);
		renderer.setUseOutlinePaint(false);
		renderer.setShapesVisible(false);
		// finally add the data set to chart 
		plot1.setDataset(datasets.size(), dataset);
		plot1.setRenderer(datasets.size(), renderer);
	}
	
	/**
	 * Adds or updates a point in the dataset.
	 * 
	 * @param datasetIndex dataset index (zero-based).
	 * @param timeStamp time stamp of the event.
	 * @param value event value.
	 */
	public void addSeriesDataItem(int datasetIndex, TimeStamp timeStamp, double value) {
		if(datasetIndex >= datasets.size()) {
			throw new IllegalArgumentException("wrong dataset index: size=" + datasets.size() + ", index=" + datasetIndex);
		}
		TimeSeries ts = datasets.get(datasetIndex);
		ts.addOrUpdate(new Millisecond(timeStamp.getDate()), value);
	}
	
	/**
	 * Deletes data points that are older than given time stamp from <em>all datasets</em>.
	 * This allows to keep sliding window of the data with the given width.
	 * 
	 * @param datasetIndex index of the dataset to truncate.
	 * 
	 * @param timeStamp truncation threshold.
	 */
	public void deleteBefore(TimeStamp timeStamp) {
		for(TimeSeries ts : datasets) {
			int index = ts.getIndex(new Millisecond(timeStamp.getDate()));
			if(index >= 0) {
				ts.delete(0, index);
			}
		}
	}

	/**
	 * Deletes data points from a given dataset which are older than the time stamp.
	 * This allows to keep sliding window of the data with the given width.
	 * 
	 * @param datasetIndex index of the dataset to truncate.
	 * 
	 * @param timeStamp truncation threshold.
	 */
	public void deleteBefore(int datasetIndex, TimeStamp timeStamp) {
		if(datasetIndex >= datasets.size()) {
			throw new IllegalArgumentException("wrong dataset index: size=" + datasets.size() + ", index=" + datasetIndex);
		}
		TimeSeries ts = datasets.get(datasetIndex);
		int index = ts.getIndex(new Millisecond(timeStamp.getDate()));
		if(index >= 0) {
			ts.delete(0, index);
		}
	}

	/**
	 * method to add a line series drawing to this chart. 
	 * @param title
	 * @param dates
	 * @param values
	 */
	public void addLineSeriesChart(String title, TimeStamp[] dates, double[] values){
		assert(dates.length == values.length);
		final List<Tuple<TimeStamp,Double>> vals = new ArrayList<Tuple<TimeStamp,Double>>();
		for(int i=0;i<dates.length;i++){
			Tuple<TimeStamp, Double> tuple = new Tuple<TimeStamp, Double>(dates[i], values[i]);
			vals.add(tuple);
		}
		this.addLineSeriesChart(title, vals);
	}
	
	/**
	 * use this to add a text annotation to this chart. 
	 * @param date
	 * @param yValue the vertical position of the text
	 * @param text
	 */
	public void addTextAnnotation(TimeStamp date, double yValue, String text) {
		chart.getXYPlot().addAnnotation(
				new XYTextAnnotation(text, date.getDate().getTime(), yValue));
	}

	/**
	 * method to add a dot chart.
	 * @param title
	 * @param dateAndValues
	 */
	public void addDotSeriesChart(String title,
			List<Tuple<TimeStamp, Double>> dateAndValues) {

		if (chart != null) {
			//
			final TimeSeries ts = new TimeSeries(title, Millisecond.class);
			for (Tuple<TimeStamp, Double> tuple : dateAndValues) {
				//
				TimeSeriesDataItem item = new TimeSeriesDataItem(
						new Millisecond(tuple.getObject1().getDate()), tuple.getObject2());
				ts.addOrUpdate(item.getPeriod(), item.getValue());
			}

			datasets.add(ts);
			final TimeSeriesCollection dataset = new TimeSeriesCollection(ts);

			final XYPlot plot1 = chart.getXYPlot();

			final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
			renderer.setShapesVisible(true);
			renderer.setLinesVisible(false);
			plot1.setDataset(datasets.size(), dataset);
			plot1.setRenderer(datasets.size(), renderer);
		}
	}

	/**
	 * Persistencifies this chart to a png file. 
	 * 
	 * created on 07.05.2006
	 * 
	 * @param filename
	 * @param w
	 * @param h
	 * @throws Exception
	 */
	public void renderToPng(String filename, int w, int h) throws Exception {
		FileOutputStream f = new FileOutputStream(filename);
		ChartUtilities.writeChartAsPNG(f, chart, w, h);
	}
	
	/**
	 * Creates a JFrame that can be displayed on the screen. The returned JFrame
	 * is not yet visible. To actually show it, do the following:
	 * <ul>
	 *   <li><code>setBounds()</code> to set frame bounds.
	 *   <li><code>setVisible(true)</code> to show it.
	 * </ul> 
	 * 
	 * @param title frame title.
	 * @param scrollPane true is scroll pane is needed.
	 * 
	 * @return JFrame
	 */
	public JFrame getJFrame(String title, boolean scrollPane) {
		return new ChartFrame(title, this.getChart(), scrollPane);
	}

	/**
	 * Creates a JFrame that can be displayed on the screen. The returned JFrame
	 * is not yet visible. To actually show it, do the following:
	 * <ul>
	 *   <li><code>setBounds()</code> to set frame bounds.
	 *   <li><code>setVisible(true)</code> to show it.
	 * </ul> 
	 * 
	 * @param title frame title.
	 * 
	 * @return JFrame
	 */
	public JFrame getJFrame(String title) {
		return getJFrame(title, false);
	}

	/**
	 * helper method to indicate a monday to friday timeline. 
	 */
	public void setSuppressWeekends() {
		SegmentedTimeline timeline = SegmentedTimeline
				.newMondayThroughFridayTimeline();
		final DateAxis axis = (DateAxis) chart.getXYPlot().getDomainAxis();
		axis.setAutoRange(true);
		axis.setTimeline(timeline);
	}
	

	public XYTextAnnotation getUpArrow(double x, double y){
		XYTextAnnotation arrow = new XYTextAnnotation(">", x,y);
		arrow.setRotationAngle(Math.PI * 1.5);
		arrow.setFont(new Font("SansSerif", Font.BOLD, 9));
        return arrow;
	}

	public XYTextAnnotation getDownArrow(double x, double y){
		XYTextAnnotation arrow = new XYTextAnnotation(">", x,y);
		arrow.setRotationAngle(Math.PI * 0.5);
		arrow.setFont(new Font("SansSerif", Font.BOLD, 9));
        return arrow;
	}


	public JFreeChart getChart() {
		return chart;
	}

	public void setChart(JFreeChart chart) {
		this.chart = chart;
	}

}
