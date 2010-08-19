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

import static org.activequant.core.domainmodel.Quote.NOT_SET;

import java.util.Map;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Quote;


/**
 * 
 * Butterfly.<br>
 * <br>
 * <b>History:</b><br>
 *  - [20.09.2007] Created (Ulrich Staudinger)<br>
 *  - [22.09.2007] Switched to Map<..> and IllegalArgumentException (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 */
public class Butterfly implements IOptionCombination {
	
	private boolean longButterfly = true; 
	private InstrumentSpecification lowerOption, centeredOption, upperOption;
	private InstrumentSpecification underlyingInstrument; 
	private double positionSizeLower = 1, positionSizeCenter = 2, positionSizeUpper = 1;
	
	
	public Butterfly(boolean longButterfly,
			InstrumentSpecification lowerOption,
			InstrumentSpecification centeredOption,
			InstrumentSpecification upperOption,
			InstrumentSpecification underlyingInstrument) {
		super();
		this.longButterfly = longButterfly;
		this.lowerOption = lowerOption;
		this.centeredOption = centeredOption;
		this.upperOption = upperOption;
		this.underlyingInstrument = underlyingInstrument;
	}

	public Butterfly(boolean longButterfly,
			InstrumentSpecification lowerOption,
			InstrumentSpecification centeredOption,
			InstrumentSpecification upperOption,
			InstrumentSpecification underlyingInstrument,
			double positionSizeLower, double positionSizeCenter,
			double positionSizeUpper) {
		super();
		this.longButterfly = longButterfly;
		this.lowerOption = lowerOption;
		this.centeredOption = centeredOption;
		this.upperOption = upperOption;
		this.underlyingInstrument = underlyingInstrument;
		this.positionSizeLower = positionSizeLower;
		this.positionSizeCenter = positionSizeCenter;
		this.positionSizeUpper = positionSizeUpper;
	}

	public double cashFlowOpenPosition(
			Map<InstrumentSpecification, Quote> rateSheet) {
		Quote q1 = rateSheet.get(lowerOption);
		Quote q2 = rateSheet.get(centeredOption);
		Quote q3 = rateSheet.get(upperOption);

		if (q1 == null || q2 == null || q3 == null)
			throw new IllegalArgumentException("Missing quote in rate sheet");

		if (longButterfly) {
			if (q1.getAskPrice() != NOT_SET && q2.getBidPrice() != NOT_SET
					&& q3.getAskPrice() != NOT_SET) {

				double cost = 0.0;
				cost = -(positionSizeLower * q1.getAskPrice())
						+ (positionSizeCenter * q2.getBidPrice())
						- (positionSizeUpper * q3.getAskPrice());
				return cost;

			} else {
				throw new IllegalArgumentException(
						"Missing quote in rate sheet");
			}

		} else {
			if (q1.getBidPrice() != NOT_SET && q2.getAskPrice() != NOT_SET
					&& q3.getBidPrice() != NOT_SET) {

				double cost = 0.0;
				cost = (positionSizeLower * q1.getBidPrice())
						- (positionSizeCenter * q2.getAskPrice())
						+ (positionSizeUpper * q3.getBidPrice());
				return cost;

			} else {
				throw new IllegalArgumentException(
						"Missing quote in rate sheet");
			}
		}

	}

	public double cashFlowClosePosition(
			Map<InstrumentSpecification, Quote> rateSheet) {

		longButterfly = !longButterfly;

		try {
			double cost = cashFlowOpenPosition(rateSheet);
			return cost;
		} finally {
			longButterfly = !longButterfly;
		}

	}

	public double cashFlowAtExpiry(Map<InstrumentSpecification, Quote> rateSheet) {

		Quote q1 = rateSheet.get(this.underlyingInstrument);
		if (q1 == null || q1.getBidPrice() == NOT_SET || q1.getAskPrice() == NOT_SET)
			throw new IllegalArgumentException("Missing quote in rate sheet");

		double price = (q1.getBidPrice() + q1.getAskPrice()) / 2;

		if (longButterfly) {
			double flow1 = positionSizeLower
					* (price - lowerOption.getStrike());
			double flow3 = positionSizeUpper
					* (price - upperOption.getStrike());
			double flow2 = positionSizeCenter
					* (centeredOption.getStrike() - price);

			if (flow2 > 0)
				flow2 = 0;
			if (flow1 < 0)
				flow1 = 0;
			if (flow3 < 0)
				flow3 = 0;

			return flow1 + flow2 + flow3;
		} else {
			double flow1 = positionSizeLower
					* (lowerOption.getStrike() - price);
			double flow3 = positionSizeUpper
					* (upperOption.getStrike() - price);
			double flow2 = positionSizeCenter
					* (price - centeredOption.getStrike());

			if (flow2 > 0)
				flow2 = 0;
			if (flow1 < 0)
				flow1 = 0;
			if (flow3 < 0)
				flow3 = 0;

			return flow1 + flow2 + flow3;
		}

	}

	public boolean isLongButterfly() {
		return longButterfly;
	}

	public void setLongButterfly(boolean longButterfly) {
		this.longButterfly = longButterfly;
	}

	public InstrumentSpecification getLowerOption() {
		return lowerOption;
	}

	public void setLowerOption(InstrumentSpecification lowerOption) {
		this.lowerOption = lowerOption;
	}

	public InstrumentSpecification getCenteredOption() {
		return centeredOption;
	}

	public void setCenteredOption(InstrumentSpecification centeredOption) {
		this.centeredOption = centeredOption;
	}

	public InstrumentSpecification getUpperOption() {
		return upperOption;
	}

	public void setUpperOption(InstrumentSpecification upperOption) {
		this.upperOption = upperOption;
	}

	public InstrumentSpecification getUnderlyingInstrument() {
		return underlyingInstrument;
	}

	public void setUnderlyingInstrument(
			InstrumentSpecification underlyingInstrument) {
		this.underlyingInstrument = underlyingInstrument;
	}

	public double getPositionSizeLower() {
		return positionSizeLower;
	}

	public void setPositionSizeLower(double positionSizeLower) {
		this.positionSizeLower = positionSizeLower;
	}

	public double getPositionSizeCenter() {
		return positionSizeCenter;
	}

	public void setPositionSizeCenter(double positionSizeCenter) {
		this.positionSizeCenter = positionSizeCenter;
	}

	public double getPositionSizeUpper() {
		return positionSizeUpper;
	}

	public void setPositionSizeUpper(double positionSizeUpper) {
		this.positionSizeUpper = positionSizeUpper;
	}	
}
