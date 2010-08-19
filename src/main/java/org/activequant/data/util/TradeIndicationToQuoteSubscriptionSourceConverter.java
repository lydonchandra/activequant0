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
package org.activequant.data.util;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Quote;
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.core.types.TimeFrame;
import org.activequant.data.retrieval.IQuoteSubscriptionSource;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.ITradeIndicationSubscriptionSource;
import org.activequant.data.retrieval.integration.SubscriptionSourceBase;
import org.activequant.util.pattern.events.IEventListener;

/**
 * Wraps trade indication subscription source and converts it to quote
 * subscription source. Trade indications come into this converter and 
 * quotes leave this converter. Can be used to build a processing chain. <br>
 * Builds quote by setting bid and ask price to the trade price,
 * and bid and ask quantities to that of the trade.
 * <br>
 * <b>History:</b><br> 
 * - [11.11.2007] Created (Mike Kroutikov)<br>
 * 
 * @author Mike Kroutikov
 */
public class TradeIndicationToQuoteSubscriptionSourceConverter extends SubscriptionSourceBase<Quote>
			implements IQuoteSubscriptionSource {

	private final ITradeIndicationSubscriptionSource source;

	public TradeIndicationToQuoteSubscriptionSourceConverter(ITradeIndicationSubscriptionSource source) {
		super(source.getVendorName());
		this.source = source;
	}

	private class QuoteSubscription extends Subscription {
		private final ISubscription<TradeIndication> tradeSubscription;
		
		private final IEventListener<TradeIndication> listener = new IEventListener<TradeIndication>() {
			public void eventFired(TradeIndication trade) throws Exception {
				log.debug("Trade event received : " + trade);
				Quote quote = convert(trade);
				if(quote != null) {
					fireEvent(quote);
				}
			}
		};
		
		private QuoteSubscription(InstrumentSpecification spec) throws Exception {
			this.tradeSubscription = source.subscribe(spec);
			this.tradeSubscription.addEventListener(listener);
		}

		@Override
		protected void handleActivate() throws Exception {
			tradeSubscription.activate();
		}

		@Override
		protected void handleCancel() throws Exception {
			tradeSubscription.cancel();
		}
	}
	
	private static Quote convert(TradeIndication ti) {
		Quote q = new Quote(ti.getTimeStamp());
		q.setInstrumentSpecification(ti.getInstrumentSpecification());
		q.setAskPrice(ti.getPrice());
		q.setBidPrice(ti.getPrice());
		q.setBidQuantity(ti.getQuantity());
		q.setAskQuantity(ti.getQuantity());
		
		return q;
	}

	public ITradeIndicationSubscriptionSource getTradeIndicationSource() {
		return source;
	}

	@Override
	protected QuoteSubscription createSubscription(InstrumentSpecification spec,
			TimeFrame timeFrame) {
		try {
			return new QuoteSubscription(spec);
		} catch(Exception ex) {
			log.error(ex);
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	public ISubscription<Quote> subscribe(InstrumentSpecification spec)
			throws Exception {
		return super.subscribe(spec, TimeFrame.TIMEFRAME_1_TICK);
	}
}
