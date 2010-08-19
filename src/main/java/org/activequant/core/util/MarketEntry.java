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

import java.util.HashMap;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.types.TimeStamp;


/**
 * 
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [16.06.2007] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class MarketEntry {
	private TimeStamp timeStamp;
	private Candle[] candles;
	private HashMap<String, Object> parameters;
	
	public MarketEntry(TimeStamp date, Candle[] candles, HashMap<String, Object> parameters) {
		this.timeStamp = date;
		this.candles = candles;
		this.parameters = parameters;
	}
	
	/**
	 * @return the candles
	 */
	public Candle[] getCandles() {
		return candles;
	}
	
	/**
	 * @return the date
	 */
	public TimeStamp getTimeStamp() {
		return timeStamp;
	}
	
	/**
	 * @return the parameters
	 */
	public HashMap<String, Object> getParameters() {
		return parameters;
	}
}
