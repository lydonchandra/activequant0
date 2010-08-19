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
import static org.activequant.core.domainmodel.Quote.NOT_SET;

/**
 * Wraps quote subscription source and converts it to trade indication 
 * subscription source. Quotes come into this converter and trade indications 
 * leave this converter. Can be used to build a processing chain. <br>
 * By default, builds trade price as a midpoint of ask and bid prices, and 
 * trade quantity as min of the ask and bid quantities. 
 * This can be adjusted by setting 
 * {@link #getPolicy policy} property.
 * <br>
 * <b>History:</b><br> 
 * - [21.08.2007] Created (Ulrich Staudinger)<br>
 * - [08.11.2007] Converted to subscription data model. <br>
 * 
 * @author Ulrich Staudinger
 */
public class QuoteToTradeIndicationSubscriptionSourceConverter extends SubscriptionSourceBase<TradeIndication>
			implements ITradeIndicationSubscriptionSource {

	private final IQuoteSubscriptionSource quoteSource;

	public QuoteToTradeIndicationSubscriptionSourceConverter(IQuoteSubscriptionSource quoteSource) {
		super(quoteSource.getVendorName());
		this.quoteSource = quoteSource;
	}

	private class TradeSubscription extends Subscription {
		private final ISubscription<Quote> quoteSubscription;
		
		private final IEventListener<Quote> listener = new IEventListener<Quote>() {
			public void eventFired(Quote quote) throws Exception {
				log.debug("Quote received : " + quote);
				TradeIndication ti = convert(quote);
				if(ti != null) {
					fireEvent(ti);
				}
			}
		};
		
		private TradeSubscription(InstrumentSpecification spec) throws Exception {
			this.quoteSubscription = quoteSource.subscribe(spec);
			this.quoteSubscription.addEventListener(listener);
		}

		@Override
		protected void handleActivate() throws Exception {
			quoteSubscription.activate();
		}

		@Override
		protected void handleCancel() throws Exception {
			quoteSubscription.cancel();
		}
	}
	
	public enum Policy {
		MIDPOINT,
		ASK,
		BID
	}
	
	private Policy policy = Policy.MIDPOINT;
	
	/**
	 * Controls the conversion policy. Default is <em>MIDPOINT</em>.
	 * <ul>
	 *    <li><em>MIDPOINT</em>: price is built as a midpoint of bid and ask prices,
	 *                           quantity is chosen to be min of ask and bid ones.
	 *    <li><em>ASK</em>: price and quantity are taken from ask side of the quote.
	 *    <li><em>BID</em>: price and quantity are taken from bid side of the quote.
	 * </ul>
	 * 
	 * @return policy value.
	 */
	public Policy getPolicy() {
		return policy;
	}

	/**
	 * Sets the policy.
	 * 
	 * @param val policy value.
	 */
	public void setPolicy(Policy val) {
		policy = val;
	}
	
	private TradeIndication convert(Quote quote) {
		switch(policy) {
		case MIDPOINT:
			if (quote.getAskPrice() != NOT_SET && quote.getBidPrice() != NOT_SET 
					&& quote.getAskQuantity()!= NOT_SET && quote.getBidQuantity() != NOT_SET) {
				// build a midprice.
				double price = (quote.getAskPrice() + quote.getBidPrice()) / 2.;
				double size  = Math.min(quote.getAskQuantity(), quote.getBidQuantity());
				return new TradeIndication(quote.getInstrumentSpecification(), 
						quote.getTimeStamp(), price, size);
			}
			break;
		case ASK:
			if (quote.getAskPrice() != NOT_SET && quote.getAskQuantity()!= NOT_SET) {
				// build an askprice.
				return new TradeIndication(quote.getInstrumentSpecification(), 
						quote.getTimeStamp(), quote.getAskPrice(), quote.getAskQuantity());
			} 
			break;
		case BID:
			if (quote.getBidPrice() != NOT_SET && quote.getBidQuantity()!= NOT_SET) {
				// build an askprice.
				return new TradeIndication(quote.getInstrumentSpecification(), 
						quote.getTimeStamp(), quote.getBidPrice(), quote.getBidQuantity());
			} 
			break;
		}
		return null;
	}

	public IQuoteSubscriptionSource getQuoteSource() {
		return quoteSource;
	}

	@Override
	protected TradeSubscription createSubscription(InstrumentSpecification spec,
			TimeFrame timeFrame) {
		try {
			return new TradeSubscription(spec);
		} catch(Exception ex) {
			log.error(ex);
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	public ISubscription<TradeIndication> subscribe(InstrumentSpecification spec)
			throws Exception {
		return super.subscribe(spec, TimeFrame.TIMEFRAME_1_TICK);
	}
}
