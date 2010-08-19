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

import org.activequant.core.types.TimeStamp;


/**
 * 
 * Market entity, base class for candle, quote and trade indication<br>
 * <br>
 * <b>History:</b><br>
 *  - [12.10.2007] Created (Ulrich Staudinger)<br>
 *  - [01.11.2007] Added constructor, abstract class (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 */
public abstract class MarketDataEntity {

	/**
	 * id of this market entity.
	 */
	private Long id = null;

	
	/**
	 * the specification for this market entity.
	 */
	private InstrumentSpecification instrumentSpecification;
	
	/**
	 * the date of this market entity. 
	 */
	private TimeStamp timeStamp = new TimeStamp();
	
	public MarketDataEntity() {
	}
	
	public MarketDataEntity(TimeStamp date) {
		this.timeStamp = date;
	}
	
	public MarketDataEntity(InstrumentSpecification instrumentSpecification) {
		this.instrumentSpecification = instrumentSpecification;
	}
	
	public MarketDataEntity(InstrumentSpecification instrumentSpecification, TimeStamp date) {
		this.instrumentSpecification = instrumentSpecification;
		this.timeStamp = date;
	}
	
	public InstrumentSpecification getInstrumentSpecification() {
		return instrumentSpecification;
	}

	public void setInstrumentSpecification(
			InstrumentSpecification instrumentSpecification) {
		this.instrumentSpecification = instrumentSpecification;
	}

	/**
	 * TimeStamp when this event hits the market.
	 * <em>IMPORTANT</em> for Candles it corresponds to the <em>close</em>
	 * time, so that Candle accumulates events up to the time stamp.
	 * 
	 * @return event time stamp.
	 */
	public TimeStamp getTimeStamp() {
		return timeStamp;
	}

	/**
	 * Sets event time stamp.
	 * 
	 * @param stamp time stamp.
	 */
	public void setTimeStamp(TimeStamp stamp) {
		this.timeStamp = stamp;
	} 

	public boolean hasId() {
		return id != null;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}	
}
