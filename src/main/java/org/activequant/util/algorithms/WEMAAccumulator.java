package org.activequant.util.algorithms;

/**
 * Weighted Exponential Moving Average accumulator.
 * 
 * Assumes that signal comes at a particular point in time (measurement) and stays
 * constant until next point in time (next measurement). Distance between measurements
 * does not have to stay the same. Therefore, signal is a piecewise-constant function 
 * of time. The length of the interval between measurements is important, because
 * values that stay longer (have longer interval) contribute more than the values
 * that have shorter interval.
 * <p>
 * Note that classical EMA is defined for the case of equal time intervals.
 * <p> 
 * This generalization is important for the algorithms that want to assign different
 * weights to different measurements. If you do not need to weight the measurements,
 * its more efficient to use the classical EMA accumulator (its faster).
 * <p>
 * <b>History:</b><br>
 *  - [14.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 *  
 *  @see EMAAccumulator
 */
public class WEMAAccumulator {
	private double period;
	private double value;
	private double totalLength;

	/**
	 * Averaging period (in "time" units). Measurements that are older than
	 * this does not contribute much to the output value.
	 * 
	 * @return averaging period.
	 */
	public double getPeriod() { 
		return period; 
	}
	/**
	 * Sets the averaging period.
	 * 
	 * @param val averaging period.
	 */
	public void setPeriod(double val) { 
		period = val; 
	}

	/**
	 * Computed average. This value gets re-computed after every measurement.
	 * 
	 * @return computed value.
	 */
	public double getValue() {
		return value; 
	}
	/**
	 * Sets the average. Use this setter to assign initial value for the
	 * signal average.  
	 * 
	 * @param val initial value for the average.
	 */
	public void setValue(double val) { 
		value = val; 
	}

	/**
	 * Total time this accumulator was working. This value is recomputed
	 * after every measurement. One can check this and
	 * compare with the {@link #getPeriod() period} to get an idea if
	 * the accumulator has enough data to produce meaningful average.
	 * 
	 * @return total length.
	 */
	public double getTotalLength() { return totalLength; }
	
	/**
	 * Sets the total time accumulator was working. Useful for resets.
	 * 
	 * @param val value.
	 */
	public void setTotalLength(double val) { totalLength = val; }

	/**
	 * Call this to pass next measurement and its associated interval
	 * length (weight). After this function returns, following
	 * properties will be recomputed: {@link #getTotalLength() totalLength},
	 * {@link #getValue() value}.
	 * <p>
	 * For convenience, this function returns newly recomputed value of the
	 * average (same that can be obtained from {@link #getValue() value} property.
	 * 
	 * @param input value of the signal.
	 * @param length interval length this signal is active (weight).
	 * @return average.
	 */
	public double accumulate(double input, double length) {
		value += (input - value) * ( 1. - Math.exp( - length / period ) );
		
		totalLength += length;
		
		return value;
	}
}
