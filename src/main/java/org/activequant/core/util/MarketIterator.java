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
import java.util.ListIterator;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.Market;
import org.activequant.core.types.TimeStamp;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [08.05.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class MarketIterator implements ListIterator<MarketEntry> {
	
	
	private Market market;
	private int currentPosition = 0;
	
	public MarketIterator(Market market) {
		this.market = market;
	}
	
	public MarketEntry next() {
		// get candles
		Candle[] candles = new Candle[market.getCandleSeries().length];
		for(int i = 0; i < candles.length; i++) {
			candles[i] = market.getCandleSeries()[i].get(currentPosition);
		}
		// get parameters
		TimeStamp date = candles[0].getTimeStamp();
		HashMap<String, Object> parameters = market.getMarketParameters(date);		
		// next one
		currentPosition++;
		return new MarketEntry(date, candles, parameters);
	}
	
	public MarketEntry previous() {
		// previous one
		currentPosition--;
		// get candles
		Candle[] candles = new Candle[market.getCandleSeries().length];
		for(int i = 0; i < candles.length; i++) {
			candles[i] = market.getCandleSeries()[i].get(currentPosition);
		}
		// get parameters
		TimeStamp date = candles[0].getTimeStamp();
		HashMap<String, Object> parameters = market.getMarketParameters(date);		
		// return
		return new MarketEntry(date, candles, parameters);
	}

	public boolean hasNext() {
		return currentPosition < market.size() - 1;
	}

	public boolean hasPrevious() {
		return currentPosition > 0;
	}

	public int nextIndex() {
		return currentPosition;
	}

	public int previousIndex() {
		return currentPosition - 1;
	}

	public void remove() {
		
	}

	public void add(MarketEntry o) {
		
	}

	public void set(MarketEntry o) {
		
	}
}
