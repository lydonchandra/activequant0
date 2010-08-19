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
package org.activequant.data.util;

import flanagan.analysis.Stat;

/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [24.02.2007] Created (Erik Nijkamp)<br>
 *  - [10.05.2007] fixed standard deviation. (Ulrich Staudinger)<br>
 *  
 *  @author Erik Nijkamp
 */
public class Statistics {
	
	public static double mean(double[] values) {
		double sum = 0.0;
		for(double d: values) sum += d;
		return sum / values.length;
	}
	
	public static double standardDeviation(double[] values) {
		return Stat.standardDeviation(values);
	}
	
	public static double standardGradient(double[] values) {
		double sum = 0.0;
		for(int i = 0; i < values.length-1; i++) {
			double difference = values[i+1] - values[i];
			sum += Math.abs(difference);
		}
		return sum / (values.length-1);	
	}
	
    /**
     * Reference: [1] http://en.wikipedia.org/wiki/Standard_deviation 
     * @param values
     * @return
     */
	public static double standardGradientDeviation(double[] values) {
		double standardGradient = standardGradient(values);
		double[] newValues = new double[values.length - 1];
		for(int i = 0; i < newValues.length; i++) {
			double difference = Math.abs(values[i+1] - values[i]);
            newValues[i] = Math.pow((difference - standardGradient), 2);
		}
		return Math.sqrt(mean(newValues));
	}

}
