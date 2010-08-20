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
import org.activequant.data.util.Statistics;
import org.apache.log4j.Logger;



/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [24.02.2007] Created (Erik Nijkamp)<br>
 *  - [10.05.2007] Reviewing (Ulrich Staudinger)<br>
 *
 *  @author Erik Nijkamp
 */
public class SimpleSmoothFilter extends FilterBase {
	
    protected final static Logger log = Logger.getLogger(SimpleSmoothFilter.class);
		
	private double margin;
	private double smoothing;
	private double deviationFactor = 2.0;
	private boolean automaticMargin = true;

	public SimpleSmoothFilter() {
		super();
	}
	
	public SimpleSmoothFilter(double deviationFactor) {
		this.deviationFactor = deviationFactor;
	}

	public SimpleSmoothFilter(double margin, double smoothing) {
		this.margin = margin;
		this.smoothing = smoothing;
		this.automaticMargin = false;
	}
	
	public CandleSeries[] process(CandleSeries... series) throws Exception {
		// clone
		CandleSeries[] newSeries = cloneSeries(series);
		// iterate
		for(CandleSeries timeSeries: series) {
			smoothTimeSeries(timeSeries);
		}

		return newSeries;
	}
	
	public boolean isUsingAutomaticMargin() {
		return automaticMargin;
	}
	
	private void smoothTimeSeries(CandleSeries timeSeries) {
		double[] opens = smooth(timeSeries.getOpens());
		double[] highs = smooth(timeSeries.getHighs());
		double[] lows = smooth(timeSeries.getLows());
		double[] closes = smooth(timeSeries.getCloses());
		timeSeries.setOpens(opens);
		timeSeries.setHighs(highs);
		timeSeries.setLows(lows);
		timeSeries.setCloses(closes);
	}
	
	private double[] smooth(double[] values) {
		
		// results
		double[] newValues = new double[values.length];
		newValues[0] = values[0];

		// automatic margin erstimation
		if (isUsingAutomaticMargin()) {
			// phase 1: remove extreme outliers
			{
				// gradient
				double gradient = Statistics.standardGradient(values);
				// fix
				for (int j = 0; j < values.length - 1; j++) {
					// value
					double currentValue = values[j];
					double nextValue = values[j + 1];
					// difference %
					double difference = (nextValue / currentValue) - 1.0;
					// outlier?
					if (Math.abs(difference) > gradient) {
						log.debug("Fixing extreme outlier at position " + j);
						values[j + 1] = values[j];
					}
				}
			}
			// phase 2: calculate limits
			{
				// gradient
				double gradient = Statistics.standardGradient(values);
				// normalized gradients
				double deviation = Statistics.standardGradientDeviation(values);
				// margin is calculated using the standard gradient + mean deviation
				margin = gradient + deviation * deviationFactor;
				// smooth outliers using the standard gradient
				smoothing = gradient;
			}
		}

		// scan and fix
		for (int j = 0; j < values.length - 1; j++) {
			// value
			double currentValue = values[j];
			double nextValue = values[j + 1];
			// difference %
			double difference = (nextValue / currentValue) - 1.0;
			// outlier
			if (Math.abs(difference) > margin) {
				// fix j+1
				log.debug("Fixing soft outlier at position " + j);
				nextValue = (difference > 0 ? currentValue * (1.0 + smoothing)
						: currentValue * (1.0 - smoothing));
				newValues[j + 1] = nextValue;
			}
		}
		
		return newValues;
	}
}
