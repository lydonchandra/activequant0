package org.activequant.data.util;

import java.util.ArrayList;
import java.util.List;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Quote;
import org.activequant.core.types.TimeFrame;
import org.activequant.data.retrieval.IQuoteSubscriptionSource;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.integration.SubscriptionSourceBase;
import org.activequant.util.pattern.events.IEventListener;

/**
 * Class that merges trade indication event streams coming from several
 * subscription sources.
 * When a subscription request is received, this class will open a subscription at
 * every configured source, events coming from all sources are merged and 
 * sent to the user.
 * <br>
 * <b>History:</b><br>
 *  - [11.11.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class QuoteMergingSubscriptionSource extends SubscriptionSourceBase<Quote> implements IQuoteSubscriptionSource {

	public QuoteMergingSubscriptionSource() {
		super("QUOTE-MERGER");
	}
	
	private IQuoteSubscriptionSource[] sources;
	public IQuoteSubscriptionSource [] getSources() { 
		return sources;
	}
	public void setSources(IQuoteSubscriptionSource [] val) {
		sources = val;
	}
	
	private class MultiSubscription extends Subscription implements IEventListener<Quote> {

		private final List<ISubscription<Quote>> subscriptions = new ArrayList<ISubscription<Quote>>();
		
		public MultiSubscription(InstrumentSpecification spec) throws Exception {
			for(IQuoteSubscriptionSource source : sources) {
				ISubscription<Quote> s = source.subscribe(spec); 
				subscriptions.add(s);
				s.addEventListener(this);
			}
		}

		@Override
		protected void handleActivate() throws Exception {
			for(ISubscription<Quote> s : subscriptions) {
				s.activate();
			}
		}

		@Override
		protected void handleCancel() throws Exception {
			for(ISubscription<Quote> s : subscriptions) {
				s.removeEventListener(this);
				s.cancel();
			}
		}

		public void eventFired(Quote event) throws Exception {
			fireEvent(event);
		}
	}

	@Override
	protected Subscription createSubscription(InstrumentSpecification spec,
			TimeFrame timeFrame) {
		try {
			return new MultiSubscription(spec);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public ISubscription<Quote> subscribe(InstrumentSpecification spec)
			throws Exception {
		return super.subscribe(spec, TimeFrame.TIMEFRAME_1_TICK);
	}
}
