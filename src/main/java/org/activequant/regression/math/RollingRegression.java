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
 *
 *  @author Erik Nijkamp
 */
public class RollingRegression {
	
	private IRegression regression = null;
	private int timeFrame = 0;
	
	public RollingRegression(
			IRegression reg,
			int timeFrame) {
		this.regression = reg;
		this.timeFrame = timeFrame;
	}
	
	public Result[] regress(double[] endogenous, double[][] exogenous) {
		Vector<Result> results = new Vector<Result>();
    	for(int current = 0; (current+timeFrame) < endogenous.length; current++) {
    		double[] currentEndogenous = new double[timeFrame];
    		System.arraycopy(endogenous, current, currentEndogenous, 0, timeFrame);
    		double[][] currentExogenous = new double[exogenous.length][timeFrame];
    		for(int i = 0; i < exogenous.length; i++) {
    			System.arraycopy(exogenous[i], current, currentExogenous[i], 0, timeFrame);
    		}
    		Result result = regression.regress(currentEndogenous, currentExogenous);
    		results.add(result);
    	}
    	Result[] array = new Result[results.size()];
    	results.toArray(array);
    	return array;
	}
	
	public Result[] regress(double[] endogenous, double[][] exogenous, String[] names) {
		return null;
	}
	
	public Result[] regress(Group group) {
		Series[] series = group.getSeries();
		double[] endogenous = series[0].getValues();
		double[][] exogenous = new double[series.length-1][];
		for(int i = 0; i < exogenous.length; i++) {
			exogenous[i] = series[i+1].getValues();
		}
		return regress(endogenous, exogenous);
	}
	
	public Result[] regress(Series ... vars) {
		Group group = new Group();
		for(Series s: vars) group.addSeries(s);
		return regress(group); 
	}
	
	public Result[] regress(Series y, Group x) {
		Series[] series = new Series[x.getSeries().length + 1];
		series[0] = y;
		System.arraycopy(x.getSeries(), 0, series, 1, x.getSeries().length);
		return regress(series);
	}

}