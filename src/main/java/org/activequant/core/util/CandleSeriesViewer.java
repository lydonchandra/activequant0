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
package org.activequant.core.util;

import java.text.SimpleDateFormat;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.activequant.core.domainmodel.CandleSeries;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [08.05.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class CandleSeriesViewer {
	
	public CandleSeriesViewer(CandleSeries[] series) {
		this("Viewer", series);
	}
	
	public CandleSeriesViewer(String title, CandleSeries[] series) {
		String[] names = new String[series.length];
		for(int i = 0; i < names.length; i++) {
			names[i] = series[i].getSeriesSpecification().toString() + " "+i;
		}
		showSeries(title, series, names);
	}
	
	public CandleSeriesViewer(String title, CandleSeries[] series, String[] names) {
		showSeries(title, series, names);
	}
	
	private void showSeries(String title, CandleSeries[] series, String[] seriesNames) {
		// show
		String[][] rowData = new String[getMaxRows(series)][series.length*2];
		String[] columnNames = new String[series.length*2];
		// copy
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy");
		for(int i = 0; i < series.length; i++) {
			int column = i * 2;
			// dates
			columnNames[column] = "Dates";
			columnNames[column+1] = seriesNames[i];
			for(int j = 0; j < series[i].size(); j++) {
				rowData[j][column] = format.format(series[i].getTimeStamps()[j]);
				rowData[j][column+1] = Double.toString(series[i].getCloses()[j]);
			}
		}
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTable table = new JTable(rowData, columnNames);
		frame.add(new JScrollPane(table));
		frame.pack();
		frame.setVisible(true);
	}
	
	private int getMaxRows(CandleSeries[] series) {
		int max = 0;
		for(CandleSeries s: series) {
			max = (max > s.size() ? max : s.size());
		}
		return max;
	}
}
