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
package org.activequant.data.retrieval.integration.ib;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.core.types.TimeFrame;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.ITradeIndicationSubscriptionSource;
import org.activequant.data.retrieval.integration.SubscriptionSourceBase;
import org.activequant.util.pattern.events.IEventListener;
import org.apache.log4j.Logger;


/**
 * 
 * TWS quote source. Requires a running tws connection. <br>
 * <br>
 * <b>History:</b><br>
 *  - [24.06.2007] Created (Ulrich Staudinger)<br>
 *  - [28.10.2007] moved to new subscription source data model (Mike Kroutikov)<br>
 *
 *  @author Ulrich Staudinger
 */
public class IBTradeIndicationSource extends SubscriptionSourceBase<TradeIndication> implements ITradeIndicationSubscriptionSource {

	private final IBTwsConnection connection;

	protected final static Logger log = Logger.getLogger(IBTradeIndicationSource.class);
	
	public IBTradeIndicationSource(IBTwsConnection monitor) {
		super("IB");
		connection = monitor; 
	}
	
	public ISubscription<TradeIndication> subscribe(InstrumentSpecification spec) {
		return subscribe(spec, TimeFrame.TIMEFRAME_1_TICK);
	}
	
	private class TradeSubscription extends Subscription {
		private final InstrumentSpecification spec;

		TradeSubscription(InstrumentSpecification s) {
			spec = s;
		}
		
		private final IEventListener<TradeIndication> listener = new IEventListener<TradeIndication>() {
			public void eventFired(TradeIndication event) throws Exception {
				fireEvent(event);
			}
		};

		@Override
		protected void handleActivate() {
			connection.subscribeToTradeIndication(listener, spec);
		}

		@Override
		protected void handleCancel() {
			connection.unsubscribeFromTradeIndication(listener, spec);
		}
	}

	@Override
	protected TradeSubscription createSubscription(
			InstrumentSpecification spec, TimeFrame timeFrame) {
		return new TradeSubscription(spec);
	}
}
