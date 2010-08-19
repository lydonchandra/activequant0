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
 * This is a generic pricer. It requires any number of long options and short options. <br>
 * But all must be working with the same underlying and all must have the same expiry. <br>
 * <br>
 * <b>History:</b><br>
 *  - [22.09.2007] Created (Ulrich Staudinger)<br>
 *  - [22.09.2007] Switched to Map<..> and IllegalArgumentException (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 */
public class GenericOptionCombinationPrizer implements IOptionCombination {

	InstrumentSpecification underlying; 
	InstrumentSpecification[] longOptions, shortOptions;
	double[] longQuantities, shortQuantities; 

	public GenericOptionCombinationPrizer(InstrumentSpecification underlying,
			InstrumentSpecification[] longOptions,
			InstrumentSpecification[] shortOptions) {
		this.underlying = underlying;
		this.longOptions = longOptions;
		this.shortOptions = shortOptions;

		longQuantities = new double[longOptions.length];
		shortQuantities = new double[shortOptions.length];
		for (int i = 0; i < longQuantities.length; i++)
			longQuantities[i] = 1;
		for (int i = 0; i < shortQuantities.length; i++)
			shortQuantities[i] = 1;

	}

	public GenericOptionCombinationPrizer(InstrumentSpecification underlying,
			InstrumentSpecification[] longOptions, double[] longQuantities,
			InstrumentSpecification[] shortOptions, double[] shortQuantities) {
		this.underlying = underlying;
		this.longOptions = longOptions;
		this.shortOptions = shortOptions;
		this.longQuantities = longQuantities;
		this.shortQuantities = shortQuantities;
	}

	public double cashFlowOpenPosition(
			Map<InstrumentSpecification, Quote> rateSheet) {
		// TODO.
		return 0;
	}

	public double cashFlowClosePosition(
			Map<InstrumentSpecification, Quote> rateSheet)
			 {
		// TODO: 
		return 0;
	}

	public double cashFlowAtExpiry(
			Map<InstrumentSpecification, Quote> rateSheet)
			 {
		// TODO. 
		return 0;
	}

}
