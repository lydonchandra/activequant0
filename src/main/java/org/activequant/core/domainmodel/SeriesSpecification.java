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
package org.activequant.core.domainmodel;

import static org.activequant.util.tools.IdentityUtils.safeCompare;
import static org.activequant.util.tools.IdentityUtils.safeHashCode;

import org.activequant.core.types.Expiry;
import org.activequant.core.types.TimeFrame;
import org.activequant.core.types.TimeStamp;
import org.activequant.util.tools.IdentityUtils;


/**
 * @TODO. <br>
 * <br>
 * <b>History:</b><br>
 *  - [03.05.2006] Created (Ulrich Staudinger)<br>
 *  - [03.05.2007] Cleanup (Erik Nijkamp)<br>
 *  - [19.06.2007] added InstrumentSpecification (Ulrich Staudinger)<br>
 *  - [20.06.2007] Simplified (Erik Nijkamp)<br>
 *  - [27.10.2007] Added object identity code (Erik Nijkamp)<br>
 *  
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public class SeriesSpecification implements Comparable<SeriesSpecification> {

	private static final long serialVersionUID = -1L;

	private InstrumentSpecification instrumentSpecification;

	private TimeStamp startTimeStamp;

	private TimeStamp endTimeStamp;

	private TimeFrame timeFrame;

	public SeriesSpecification() {

	}

	public SeriesSpecification(Symbol symbol) {
		this.instrumentSpecification = new InstrumentSpecification(symbol);
	}

	public SeriesSpecification(Symbol symbol, TimeFrame timeFrame) {
		this.instrumentSpecification = new InstrumentSpecification(symbol);
		this.timeFrame = timeFrame;
	}

	public SeriesSpecification(InstrumentSpecification spec, TimeFrame timeFrame) {
		this.instrumentSpecification = spec;
		this.timeFrame = timeFrame;
	}

	public SeriesSpecification(Symbol symbol, TimeStamp startDate, TimeStamp endDate,
			TimeFrame timeFrame) {
		this.instrumentSpecification = new InstrumentSpecification(symbol);
		this.startTimeStamp = startDate;
		this.endTimeStamp = endDate;
		this.timeFrame = timeFrame;
	}

	public SeriesSpecification(InstrumentSpecification spec, TimeStamp startDate,
			TimeStamp endDate, TimeFrame timeFrame) {
		this.instrumentSpecification = spec;
		this.startTimeStamp = startDate;
		this.endTimeStamp = endDate;
		this.timeFrame = timeFrame;
	}

	public SeriesSpecification(InstrumentSpecification spec) {
		this.instrumentSpecification = spec;
	}

	public SeriesSpecification(SeriesSpecification spec) {
		this.instrumentSpecification = new InstrumentSpecification(spec
				.getInstrumentSpecification());
		this.timeFrame = spec.timeFrame;
		this.startTimeStamp = spec.getStartTimeStamp();
		this.endTimeStamp = spec.getEndTimeStamp();
	}

	public SeriesSpecification(TimeFrame timeFrame, InstrumentSpecification spec) {
		this(spec);
		this.timeFrame = timeFrame;
	}
	
	public SeriesSpecification(TimeFrame timeFrame, Symbol symbol,
			String exchange, String vendor, String currency,
			String securityType) {
		this.instrumentSpecification = new InstrumentSpecification(symbol, exchange,
				vendor, currency, securityType);
		this.timeFrame = timeFrame;
	}
	
	public SeriesSpecification(TimeFrame timeFrame, Symbol symbol,
			String exchange, String vendor, String currency,
			String securityType, Expiry expiry) {
		this.instrumentSpecification = new InstrumentSpecification(symbol, exchange,
				vendor, currency, securityType, expiry);
		this.timeFrame = timeFrame;
	}

	public SeriesSpecification(TimeFrame timeFrame, Symbol symbol) {
		this.instrumentSpecification = new InstrumentSpecification(symbol);
		this.timeFrame = timeFrame;
	}

	public TimeStamp getEndTimeStamp() {
		return endTimeStamp;
	}

	public void setEndTimeStamp(TimeStamp end) {
		this.endTimeStamp = end;
	}

	public TimeStamp getStartTimeStamp() {
		return startTimeStamp;
	}

	public void setStartTimeStamp(TimeStamp start) {
		this.startTimeStamp = start;
	}

	public TimeFrame getTimeFrame() {
		return timeFrame;
	}

	public void setTimeFrame(TimeFrame timeFrame) {
		this.timeFrame = timeFrame;
	}

	public String toString() {
		return instrumentSpecification.toString() + " \t " + startTimeStamp + " \t " + endTimeStamp;
	}

	public InstrumentSpecification getInstrumentSpecification() {
		return instrumentSpecification;
	}

	public void setInstrumentSpecification(
			InstrumentSpecification instrumentSpecification) {
		this.instrumentSpecification = instrumentSpecification;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		// ATTENTION: keep in sync with compareTo()
		return safeHashCode(this.instrumentSpecification) 
			+ safeHashCode(this.startTimeStamp)
			+ safeHashCode(this.endTimeStamp)
			+ safeHashCode(this.timeFrame);
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(SeriesSpecification other) {
		// ATTENTION: keep in sync with hashCode();
		int rc;
		
		rc = safeCompare(this.instrumentSpecification, other.instrumentSpecification);
		if(rc != 0) return rc;
		rc = safeCompare(this.startTimeStamp, other.startTimeStamp);
		if(rc != 0) return rc;
		rc = safeCompare(this.endTimeStamp, other.endTimeStamp);
		if(rc != 0) return rc;
		rc = safeCompare(this.timeFrame, other.timeFrame);
		
		return rc;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object other) {
		// NOTE: delegates to compareTo()
		return IdentityUtils.equalsTo(this, other);
	}

}