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
package org.activequant.util.tools;

import java.util.List;

import org.activequant.core.domainmodel.MarketDataEntity;
import org.activequant.core.domainmodel.TimeSeries;
import org.activequant.util.exceptions.InvalidDateOrderException;

/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [18.05.2007] Created (Erik Nijkamp)<br>
 *  - [09.11.2007] Switched to MarketDataEntity (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class CheckDateOrder {
	
	/**
	 * candles[0] newest, candles[n] oldest
	 * @param candles
	 * @return
	 */
	public static boolean isOrderValid(MarketDataEntity... entities) {
		for(int i = 1; i < entities.length; i++) {
			if(entities[i-1].getTimeStamp().isBefore(entities[i].getTimeStamp())) {
				// invalid candle order found. 
				return false;
			}
		}
		return true;
	}

	public static <T extends TimeSeries<?>> boolean isOrderValid(T entities) {
		return isOrderValid(entities.toArray(new MarketDataEntity[] {}));
	}
	
	public static void checkOrder(MarketDataEntity... entities) throws InvalidDateOrderException {
		if(!isOrderValid(entities)) throw new InvalidDateOrderException();
	}
	
	public static void checkOrder(List<MarketDataEntity> entities) throws InvalidDateOrderException {
		if(!isOrderValid(entities.toArray(new MarketDataEntity[] {}))) throw new InvalidDateOrderException();
	}

}
