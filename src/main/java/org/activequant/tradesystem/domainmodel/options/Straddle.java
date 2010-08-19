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
 * Straddle option combination. <br>
 * <br>
 * <b>History:</b><br>
 *  - [21.09.2007] Created (Ulrich Staudinger)<br>
 *  - [22.09.2007] Switched to Map<..> and IllegalArgumentException (Erik Nijkamp)<br>
 *  - [27.09.2007] Fixed exceptions ... (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 */
public class Straddle implements IOptionCombination {

	protected InstrumentSpecification underlying; 
	protected InstrumentSpecification call, put;
	protected double positionSizeCall = 1, positionSizePut = 1;
	protected OrderSide side = OrderSide.BUY;
	

	public Straddle(InstrumentSpecification underlying, InstrumentSpecification call, InstrumentSpecification put, OrderSide side) {
		super();
		this.underlying = underlying;
		this.call = call;
		this.put = put;
		this.side = side;
		checkInit();
	}

	public Straddle(InstrumentSpecification underlying, InstrumentSpecification call, InstrumentSpecification put) {
		super();
		this.underlying = underlying;
		this.call = call;
		this.put = put;
		checkInit();
	}
	
	protected void checkInit() {
		if(!call.getContractRight().equals("C")) throw new IllegalArgumentException("Call wrong."); 
		if(!put.getContractRight().equals("P")) throw new IllegalArgumentException("Put wrong.");
	}
	
	public InstrumentSpecification getCall() {
		return call;
	}

	public void setCall(InstrumentSpecification call) {
		this.call = call;
	}

	public InstrumentSpecification getPut() {
		return put;
	}

	public void setPut(InstrumentSpecification put) {
		this.put = put;
	}

	public InstrumentSpecification getUnderlying() {
		return underlying;
	}

	public void setUnderlying(InstrumentSpecification underlying) {
		this.underlying = underlying;
	}

	public double cashFlowOpenPosition(
			Map<InstrumentSpecification, Quote> rateSheet) {

		Quote callQuote = rateSheet.get(call);
		Quote putQuote = rateSheet.get(put);

		if (callQuote == null || putQuote == null)
			throw new IllegalArgumentException("Missing call or put quotation");

		if (side == OrderSide.BUY) {
			if (callQuote.getAskPrice() == Quote.NOT_SET
					|| putQuote.getAskPrice() == Quote.NOT_SET)
				throw new IllegalArgumentException(
						"Missing call or put ask price");
			double flow = (-positionSizeCall * callQuote.getAskPrice())
					+ (-positionSizePut * putQuote.getAskPrice());
			return flow;
		} else {
			if (callQuote.getBidPrice() == Quote.NOT_SET
					|| putQuote.getBidPrice() == Quote.NOT_SET)
				throw new IllegalArgumentException(
						"Missing call or put ask price");
			double flow = (positionSizeCall * callQuote.getBidPrice())
					+ (positionSizePut * putQuote.getBidPrice());
			return flow;
		}

	}

	public double cashFlowClosePosition(
			Map<InstrumentSpecification, Quote> rateSheet) {
		try {
			// inverse it.
			if (side == OrderSide.BUY)
				side = OrderSide.SELL;
			else
				side = OrderSide.BUY;

			return cashFlowOpenPosition(rateSheet);
		} finally {
			// re-inverse it.
			if (side == OrderSide.BUY)
				side = OrderSide.SELL;
			else
				side = OrderSide.BUY;
		}
	}

	public double cashFlowAtExpiry(Map<InstrumentSpecification, Quote> rateSheet) {

		Quote instrumentQuote = rateSheet.get(underlying);
		if (instrumentQuote == null)
			throw new IllegalArgumentException("Missing quote");

		if (instrumentQuote.getAskPrice() == Quote.NOT_SET
				|| instrumentQuote.getBidPrice() == Quote.NOT_SET)
			throw new IllegalArgumentException("Missing quote");

		double averagedPriceUnderlying = (instrumentQuote.getAskPrice() + instrumentQuote
				.getBidPrice()) / 2.0;

		if (side == OrderSide.BUY) {
			double flowCall = averagedPriceUnderlying - call.getStrike();
			double flowPut = put.getStrike() - averagedPriceUnderlying;
			if (flowCall < 0)
				flowCall = 0;
			if (flowPut < 0)
				flowPut = 0;
			return flowCall + flowPut;
		} else {
			double flowCall = call.getStrike() - averagedPriceUnderlying;
			double flowPut = averagedPriceUnderlying - put.getStrike();
			if (flowCall > 0)
				flowCall = 0;
			if (flowPut > 0)
				flowPut = 0;
			return flowCall - flowPut;
		}

	}

	public Straddle(InstrumentSpecification underlying,
			InstrumentSpecification call, InstrumentSpecification put,
			double positionSizeCall, double positionSizePut, OrderSide side) {
		super();
		this.underlying = underlying;
		this.call = call;
		this.put = put;
		this.positionSizeCall = positionSizeCall;
		this.positionSizePut = positionSizePut;
		this.side = side;
		checkInit();
	}

	public double getPositionSizeCall() {
		return positionSizeCall;
	}

	public void setPositionSizeCall(double positionSizeCall) {
		this.positionSizeCall = positionSizeCall;
	}

	public double getPositionSizePut() {
		return positionSizePut;
	}

	public void setPositionSizePut(double positionSizePut) {
		this.positionSizePut = positionSizePut;
	}

	public OrderSide getSide() {
		return side;
	}

	public void setSide(OrderSide side) {
		this.side = side;
	}

}
