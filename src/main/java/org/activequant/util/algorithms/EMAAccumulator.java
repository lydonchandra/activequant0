package org.activequant.util.algorithms;

/**
 * Exponential Moving Average accumulator.
 * Takes input as a series of measurements, and produces average signal by applying
 * EMA. 
 * <p>
 * <b>History:</b><br>
 *  - [14.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 *  
 *  @see WEMAAccumulator
 */
public class EMAAccumulator {
	private int period;
	private double value;
	private int totalLength;

	/**
	 * Averaging period (in "steps"). Measurements that are older than
	 * this does not contribute much to the output value.
	 * 
	 * @return averaging period.
	 */
	public int getPeriod() { 
		return period; 
	}
	/**
	 * Sets the averaging period.
	 * 
	 * @param val averaging period.
	 */
	public void setPeriod(int val) { 
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
	 * Total number of steps (or measuments) that this accumulator contain.
	 * This value is recomputed after every measurement. One can check this and
	 * compare with the {@link #getPeriod() period} to get an idea if
	 * the accumulator has enough data to produce meaningful average.
	 * 
	 * @return total length.
	 */
	public int getTotalLength() { return totalLength; }
	
	/**
	 * Sets the total time accumulator was working. Useful for resets.
	 * 
	 * @param val value.
	 */
	public void setTotalLength(int val) { totalLength = val; }

	/**
	 * Call this to pass next measurement.
	 * After this function returns, following
	 * properties will be recomputed: {@link #getTotalLength() totalLength},
	 * {@link #getValue() value}.
	 * <p>
	 * For convenience, this function returns newly recomputed value of the
	 * average (same that can be obtained from {@link #getValue() value} property.
	 * 
	 * @param input value of the signal.
	 * @return average.
	 */
	public double accumulate(double input) {
		value += (input - value) / period;
		
		totalLength ++;
		
		return value;
	}
}
