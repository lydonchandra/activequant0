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
package org.activequant.data.retrieval.mock;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.core.types.TimeFrame;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.ITradeIndicationSubscriptionSource;

/**
 * Mock source of trade indication subscriptions.
 * Use it for unit testing services that depend on
 * subscription service.
 * Supports only one outstanding subscription.
 * <p>
 * To use, instantiate the mock subscription source, then hook it into the
 * application. Then, use {@link #fireEvent} method to simulate incoming 
 * entities.
 * <br>
 * <br>
 * <b>History:</b><br>
 *  - [09.10.2007] Created (Mike Kroutikov)<br>
 *  - [09.11.2007] Converted to subscription model (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class MockTradeIndicationSubscriptionSource extends MockSubscriptionSourceBase<TradeIndication> implements ITradeIndicationSubscriptionSource {

	public ISubscription<TradeIndication> subscribe(InstrumentSpecification spec)
			throws Exception {
		return super.subscribe(spec, TimeFrame.TIMEFRAME_1_TICK);
	}
}
