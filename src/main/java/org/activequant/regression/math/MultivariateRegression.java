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

//import org.activequant.regression. flanagan.Regression;

import flanagan.analysis.*;

/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [31.05.2006] Created (Erik Nijkamp)<br>
 *  - [31.09.2007] Moved fixed class files to flanagan jar (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class MultivariateRegression implements IRegression {
	
	public Result regress(double[] endogenous, double[][] exogenous, String[] names) {
		assert(exogenous.length > 1) : "exogenous.length < 2";
    	// setup regression
        Regression reg = new Regression(exogenous, endogenous);
        // regress linear
        reg.linear();
        // export results
        Coefficient[] coefs = new Coefficient[reg.getCoeff().length];
        for(int i = 0; i < coefs.length; i++) {
        	coefs[i] = 
        		new Coefficient(
        				i == 0,
        				(i == 0 ? "c" : names[i]),
        				reg.getCoeff()[i], 
        				reg.getCoeffVar()[i], 
        				reg.getCoeffSd()[i]);
        }
        // return
        return new Result(
        		coefs, 
        		reg.getSampleR2(), 
        		reg.getMultipleF());
	}
   
    public Result regress(double[] endogenous, double[][] exogenous) {
    	assert(exogenous.length > 1) : "exogenous.length < 2";
    	// setup regression
        Regression reg = new Regression(exogenous, endogenous);
        // regress linear
        reg.linear();
        // export results
        Coefficient[] coefs = new Coefficient[reg.getCoeff().length];
        for(int i = 0; i < coefs.length; i++) {
        	coefs[i] = 
        		new Coefficient(
        				i == 0,
        				(i == 0 ? "c" : "x" + i),
        				reg.getCoeff()[i], 
        				reg.getCoeffVar()[i], 
        				reg.getCoeffSd()[i]);
        }
        // return
        return new Result(
        		coefs, 
        		reg.getSampleR2(), 
        		reg.getMultipleF());
    }
    
    public Result regress(Group group) {
		Series[] series = group.getSeries();
		// values
		double[] endogenous = series[0].getValues();
		double[][] exogenous = new double[series.length - 1][];
		for (int i = 0; i < exogenous.length; i++) {
			exogenous[i] = series[i + 1].getValues();
		}
		// names
		boolean hasNames = series[0].hasName();
		if (!hasNames) {
			return regress(endogenous, exogenous);
		} else {
			String[] names = new String[series.length];
			for (int i = 0; i < names.length; i++) {
				Series current = series[i];
				names[i] = (current.hasName() ? current.getName() : "<" + i
						+ ">");
			}
			return regress(endogenous, exogenous, names);
		}
	}
    
    public Result regress(Series ... vars) {
    	Group group = new Group();
    	for(Series s: vars) group.addSeries(s);
    	return regress(group);    	
    }
    
    public Result regress(Series y, Group x) {
    	Series[] series = new Series[x.getSeries().length + 1];
    	series[0] = y;
    	System.arraycopy(x.getSeries(), 0, series, 1, x.getSeries().length);
    	return regress(series);
    }

	public Result regress(Series y, Series[] x) {
		return regress(y, new Group(x));
	}

}