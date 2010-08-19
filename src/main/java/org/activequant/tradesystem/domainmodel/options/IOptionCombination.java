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
 * Interface all option combinations must implement<br>
 * <br>
 * <b>History:</b><br>
 *  - [20.09.2007] Created (Ulrich Staudinger)<br>
 *  - [22.09.2007] Switched to Map<..> and IllegalArgumentException (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 */
public interface IOptionCombination {
	/**
	 * calculates the cost for opening this combination at the given rate sheet. 
	 * @param rateSheet
	 * @return
	 * @throws NoSuchParameter thrown when a quote is missing in the rate sheet, or when ie. bid or ask is null
	 */
	double cashFlowOpenPosition(Map<InstrumentSpecification, Quote> rateSheet) throws IllegalArgumentException; 
	
	/**
	 * calculates the profit when closing the position at the given rate sheet.
	 * @param rateSheet
	 * @return
	 * @throws NoSuchParameter thrown when a quote is missing in the rate sheet, or when ie. bid or ask is null
	 */
	double cashFlowClosePosition(Map<InstrumentSpecification, Quote> rateSheet) throws IllegalArgumentException;	
	
	/**
	 * this method is only there to calculate the cashflow at expiry. It could be that this method is 
	 * going to be removed again (time will show).
	 * @param rateSheet
	 * @return
	 * @throws NoSuchParameter
	 */
	double cashFlowAtExpiry(Map<InstrumentSpecification, Quote> rateSheet) throws IllegalArgumentException;
	
}
