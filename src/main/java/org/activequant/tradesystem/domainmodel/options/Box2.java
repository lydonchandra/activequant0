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
package org.activequant.tradesystem.domainmodel.options;

import java.util.Map;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Quote;
import org.activequant.tradesystem.types.OrderSide;



/**
 * 
 * Option box.<br>
 * <br>
 * <b>History:</b><br>
 *  - [20.09.2007] Created (Ulrich Staudinger)<br>
 *  - [22.09.2007] Switched to Map<..> and IllegalArgumentException (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 */
public class Box2 implements IOptionCombination {

	private InstrumentSpecification lowerCall, lowerPut, upperCall, upperPut; 
	private OrderSide longOrShortBox = OrderSide.BUY;
	
	
	public double cashFlowOpenPosition(
			Map<InstrumentSpecification, Quote> rateSheet) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double cashFlowClosePosition(
			Map<InstrumentSpecification, Quote> rateSheet) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double cashFlowAtExpiry(
			Map<InstrumentSpecification, Quote> rateSheet) {
		// TODO Auto-generated method stub
		return 0;
	}

	public InstrumentSpecification getLowerCall() {
		return lowerCall;
	}

	public void setLowerCall(InstrumentSpecification lowerCall) {
		this.lowerCall = lowerCall;
	}

	public InstrumentSpecification getLowerPut() {
		return lowerPut;
	}

	public void setLowerPut(InstrumentSpecification lowerPut) {
		this.lowerPut = lowerPut;
	}

	public InstrumentSpecification getUpperCall() {
		return upperCall;
	}

	public void setUpperCall(InstrumentSpecification upperCall) {
		this.upperCall = upperCall;
	}

	public InstrumentSpecification getUpperPut() {
		return upperPut;
	}

	public void setUpperPut(InstrumentSpecification upperPut) {
		this.upperPut = upperPut;
	}

	public OrderSide getLongOrShortBox() {
		return longOrShortBox;
	}

	public void setLongOrShortBox(OrderSide longOrShortBox) {
		this.longOrShortBox = longOrShortBox;
	}

}
