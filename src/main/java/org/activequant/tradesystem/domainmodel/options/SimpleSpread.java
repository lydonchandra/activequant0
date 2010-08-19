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
 * Simple spread class, can be used to model bull call, bull put, bear call or bear put spread. <br>
 * <br>
 * <b>History:</b><br>
 *  - [21.09.2007] Created (Ulrich Staudinger)<br>
 *  - [22.09.2007] Switched to Map<..> and IllegalArgumentException (Erik Nijkamp)<br>
 *  - [27.09.2007] Fixed exceptions ... (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 */
public class SimpleSpread implements IOptionCombination {

	private InstrumentSpecification underlying, shortOption, longOption;
	private int shortQuantity = 1, longQuantity = 1; 
	
	public SimpleSpread(InstrumentSpecification underlying, InstrumentSpecification shortOption, 
			InstrumentSpecification longOption) {
		this.underlying = underlying;
		this.shortOption = shortOption;
		this.longOption = longOption; 
	}

	public SimpleSpread(InstrumentSpecification underlying, InstrumentSpecification shortOption, InstrumentSpecification longOption, int shortQuantity, int longQuantity) {
		super();
		this.underlying = underlying;
		this.shortOption = shortOption;
		this.longOption = longOption;
		this.longQuantity = longQuantity;
		this.shortQuantity = shortQuantity;
	}

	public double cashFlowOpenPosition(
			Map<InstrumentSpecification, Quote> rateSheet) {

		Quote shortQuote = rateSheet.get(shortOption);
		Quote longQuote = rateSheet.get(longOption);

		if (shortQuote == null || longQuote == null)
			throw new IllegalArgumentException("Missing option quotation");

		if (shortQuote.getBidPrice() == Quote.NOT_SET
				|| longQuote.getAskPrice() == Quote.NOT_SET)
			throw new IllegalArgumentException("Missing option price");

		double flow = (shortQuantity * shortQuote.getBidPrice())
				- (longQuantity * longQuote.getAskPrice());

		return flow;
	}

	public double cashFlowClosePosition(
			Map<InstrumentSpecification, Quote> rateSheet) {

		Quote shortQuote = rateSheet.get(shortOption);
		Quote longQuote = rateSheet.get(longOption);

		if (shortQuote == null || longQuote == null)
			throw new IllegalArgumentException("Missing option quotation");

		if (shortQuote.getAskPrice() == Quote.NOT_SET
				|| longQuote.getBidPrice() == Quote.NOT_SET)
			throw new IllegalArgumentException("Missing option price");

		double flow = -(shortQuantity * shortQuote.getAskPrice())
				+ (longQuantity * longQuote.getBidPrice());

		return flow;

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

		double flow1 = 0.0, flow2 = 0.0;
		if (shortOption.getContractRight().equals("P")) {
			double flowFromShortOption = shortQuantity
					* (averagedPriceUnderlying - shortOption.getStrike());
			if (flowFromShortOption > 0)
				flow1 = 0;
			else
				flow1 = flowFromShortOption;
		} else if (shortOption.getContractRight().equals("C")) {
			double flowFromShortOption = shortQuantity
					* (shortOption.getStrike() - averagedPriceUnderlying);
			if (flowFromShortOption > 0)
				flow1 = 0;
			else
				flow1 = flowFromShortOption;
		}

		if (longOption.getContractRight().equals("C")) {
			double flowFromLongOption = longQuantity
					* (averagedPriceUnderlying - longOption.getStrike());
			if (flowFromLongOption < 0)
				flow2 = 0;
			else
				flow2 = flowFromLongOption;
		} else if (longOption.getContractRight().equals("P")) {
			double flowFromShortOption = longQuantity
					* (longOption.getStrike() - averagedPriceUnderlying);
			if (flowFromShortOption < 0)
				flow2 = 0;
			else
				flow2 = flowFromShortOption;
		}

		double flow = flow1 + flow2;

		return flow;
	}

	public InstrumentSpecification getLongOption() {
		return longOption;
	}

	public void setLongOption(InstrumentSpecification longOption) {
		this.longOption = longOption;
	}

	public InstrumentSpecification getShortOption() {
		return shortOption;
	}

	public void setShortOption(InstrumentSpecification shortOption) {
		this.shortOption = shortOption;
	}

	public InstrumentSpecification getUnderlying() {
		return underlying;
	}

	public void setUnderlying(InstrumentSpecification underlying) {
		this.underlying = underlying;
	}
}
