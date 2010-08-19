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


/**
 * 
 * Long Underlying, Short call and Long put. Expiry must be equal. <br>
 * <br>
 * <b>History:</b><br>
 *  - [22.09.2007] Created (Ulrich Staudinger)<br>
 *  - [22.09.2007] Switched to Map<..> and IllegalArgumentException (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 */
public class Conversion implements IOptionCombination {

	private InstrumentSpecification underlying, shortCall, longPut;

	private int quantity = 1;

	public Conversion(InstrumentSpecification underlying,
			InstrumentSpecification shortCall, InstrumentSpecification longPut,
			int quantity) {
		super();
		this.underlying = underlying;
		this.shortCall = shortCall;
		this.longPut = longPut;
		this.quantity = quantity;
	}

	public Conversion(InstrumentSpecification underlying,
			InstrumentSpecification shortCall, InstrumentSpecification longPut) {
		super();
		this.underlying = underlying;
		this.shortCall = shortCall;
		this.longPut = longPut;
	}

	public InstrumentSpecification getLongPut() {
		return longPut;
	}

	public void setLongPut(InstrumentSpecification longPut) {
		this.longPut = longPut;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public InstrumentSpecification getShortCall() {
		return shortCall;
	}

	public void setShortCall(InstrumentSpecification shortCall) {
		this.shortCall = shortCall;
	}

	public InstrumentSpecification getUnderlying() {
		return underlying;
	}

	public void setUnderlying(InstrumentSpecification underlying) {
		this.underlying = underlying;
	}

	public double cashFlowOpenPosition(
			Map<InstrumentSpecification, Quote> rateSheet) {

		Quote q1 = rateSheet.get(underlying);
		Quote q2 = rateSheet.get(shortCall);
		Quote q3 = rateSheet.get(longPut);

		if (q1 == null || q2 == null || q3 == null)
			throw new IllegalArgumentException("Quote missing.");
		if (q1.getAskPrice() == Quote.NOT_SET
				|| q2.getBidPrice() == Quote.NOT_SET
				|| q3.getAskPrice() == Quote.NOT_SET)
			throw new IllegalArgumentException("Bid or ask missing.");

		double flow = -(quantity * q1.getAskPrice())
				+ (quantity * q2.getBidPrice()) - (quantity * q3.getAskPrice());

		return flow;

	}

	public double cashFlowClosePosition(
			Map<InstrumentSpecification, Quote> rateSheet) {

		Quote q1 = rateSheet.get(underlying);
		Quote q2 = rateSheet.get(shortCall);
		Quote q3 = rateSheet.get(longPut);

		if (q1 == null || q2 == null || q3 == null)
			throw new IllegalArgumentException("Quote missing.");
		if (q1.getBidPrice() == Quote.NOT_SET
				|| q2.getAskPrice() == Quote.NOT_SET
				|| q3.getBidPrice() == Quote.NOT_SET)
			throw new IllegalArgumentException("Bid or ask missing.");

		double flow = (quantity * q1.getBidPrice())
				- (quantity * q2.getAskPrice()) + (quantity * q3.getBidPrice());

		return flow;

	}

	/**
	 * cash flow at expiry in case of a conversion is always an outgoing strike
	 * value (multiplied by quantity)
	 */
	public double cashFlowAtExpiry(
			Map<InstrumentSpecification, Quote> rateSheet) {
		double flow = -(quantity * shortCall.getStrike());
		return flow;
	}

}
