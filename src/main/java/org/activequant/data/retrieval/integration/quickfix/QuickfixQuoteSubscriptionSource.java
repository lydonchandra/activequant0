package org.activequant.data.retrieval.integration.quickfix;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Quote;
import org.activequant.core.types.TimeFrame;
import org.activequant.data.retrieval.IQuoteSubscriptionSource;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.integration.SubscriptionSourceBase;
import org.activequant.util.pattern.events.IEventListener;

/**
 * Quickfix quote feed source. Delegates actual work to {@link QuickfixConnection}.
 * <br>
 * <b>History:</b><br>
 *  - [Nov 4, 2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class QuickfixQuoteSubscriptionSource extends SubscriptionSourceBase<Quote> implements IQuoteSubscriptionSource {

	public QuickfixQuoteSubscriptionSource() {
		super("QUICKFIX");
	}
	
	private QuickfixConnection connection;
	public void setConnection(QuickfixConnection val) {
		connection = val;
	}
	public QuickfixConnection getConnection() { 
		return connection;
	}
	
	private class QuoteSubscription extends Subscription {
		private final IEventListener<Quote> listener = new IEventListener<Quote>() {
			public void eventFired(Quote event) throws Exception {
				fireEvent(event);
			}
		};
		
		private final InstrumentSpecification spec;
		
		public QuoteSubscription(InstrumentSpecification s) {
			spec = s;
		}

		private String reqId = null;

		@Override
		protected void handleActivate() throws Exception {
			reqId = connection.subscribeToQuote(spec, listener);
		}

		@Override
		protected void handleCancel() throws Exception {
			if(reqId != null) {
				connection.cancelRequest(reqId);
			}
		}
	}

	@Override
	protected QuoteSubscription createSubscription(
			InstrumentSpecification spec, TimeFrame timeFrame) {
		return new QuoteSubscription(spec);
	}

	public final ISubscription<Quote> subscribe(InstrumentSpecification spec)
			throws Exception {
		return super.subscribe(spec, TimeFrame.TIMEFRAME_1_TICK);
	}
}
