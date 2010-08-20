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
package org.activequant.regression.math;

import java.util.Vector;

/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [31.05.2006] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class Group {
	
	private Vector<Series> series = new Vector<Series>();
	
	public Group(Series series, Group group) {
		addSeries(series);
		addSeries(group.getSeries());
	}
	
	public Group(Series ... series) {
		addSeries(series);
	}
	
	public void addSeries(Series series) {
		this.series.add(series);
	}
	
	public void addSeries(Series ... series) {
		for(Series s: series) {
			this.series.add(s);
		}
	}
	
	public Series[] getSeries() {
		Series[] array = new Series[series.size()];
		series.toArray(array);
		return array;
	}
	
	public Series[] getSubset(int first, int last) {
		int count = last-first;
		Series[] subset = new Series[count];
		for(int i = 0; i < count; i++) {
			subset[first+i] = series.elementAt(i);
		}
		return subset;
	}
	
	// TODO cleanup (en)
	
	/*public void setSample(Sample sample) throws ValueNotFoundException {
		for(Series s: series) {
			s.setSample(sample);
		}		
	}
	
	public void setLength(int length) {
		for(Series s: series) {
			s.setLength(length);
		}
	}
	
	public void setLag(int lag) {
		for(Series s: series) {
			s.setLag(lag);
		}
	}
	
	public void setMinimalLength() {
		setLength(getMinimalLength());
	}
	
	public int getMinimalLength() {
		int minimalLength = series.get(0).getLength();
		for(Series s: series) {
			minimalLength = (minimalLength > s.getValues().length ? s.getValues().length : minimalLength);
		}
		return minimalLength;		
	}
	
	public Sample getMinimalSample() {
		int maxLag = 0;
		int minStart = 0;
		for(Series s: series) {
			maxLag = (maxLag > s.getLag() ? s.getLag() : maxLag);
			minStart = (minStart > s.getStart() ? s.getStart() : minStart);
		}
		int start = Math.abs(minStart + maxLag);
		return new Sample(start, getMinimalLength());
	}*/

}
