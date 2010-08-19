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
package org.activequant.data.retrieval;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Quote;

/**
 * Interface for a candle live data feed.
 */

/**
 * Interface for a candle live data feed.<br>
 * <br>  
 * <b>History:</b><br>
 *  - [23.10.2007] Created based on Mike Kroutikov's code + IDataSource (Erik Nijkamp)<br>
 *  - [04.11.2007] Renaming quoteListener to IQuoteListener to have it naming conform (Ulrich Staudinger)<br> 
 *  
 *  @author Mike Kroutikov
 *  @author Erik Nijkamp
 *  @author Ulrich Staudinger
 *
 */
public interface IQuoteSubscriptionSource {

	ISubscription<Quote> subscribe(InstrumentSpecification spec) throws Exception;

	/**
	 * Returns vendor name
	 *
	 * @return vendor name.
	 */
	String getVendorName();

	/**
	 * Returns collection of current subscriptions (excluding the canceled ones).
	 *
	 * @return subscriptions.
	 */
	ISubscription<Quote>[] getSubscriptions();

}
