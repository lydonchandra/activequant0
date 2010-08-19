package org.activequant.data.util;

import java.util.ArrayList;
import java.util.List;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.core.types.TimeFrame;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.ITradeIndicationSubscriptionSource;
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
public class TradeIndicationMergingSubscriptionSource extends SubscriptionSourceBase<TradeIndication> implements ITradeIndicationSubscriptionSource {

	public TradeIndicationMergingSubscriptionSource() {
		super("TRADE-INDICATION-MERGER");
	}
	
	private ITradeIndicationSubscriptionSource[] sources;
	public ITradeIndicationSubscriptionSource [] getSources() { 
		return sources;
	}
	public void setSources(ITradeIndicationSubscriptionSource [] val) {
		sources = val;
	}
	
	private class MultiSubscription extends Subscription implements IEventListener<TradeIndication> {

		private final List<ISubscription<TradeIndication>> subscriptions = new ArrayList<ISubscription<TradeIndication>>();
		
		public MultiSubscription(InstrumentSpecification spec) throws Exception {
			for(ITradeIndicationSubscriptionSource source : sources) {
				ISubscription<TradeIndication> s = source.subscribe(spec); 
				subscriptions.add(s);
				s.addEventListener(this);
			}
		}

		@Override
		protected void handleActivate() throws Exception {
			for(ISubscription<TradeIndication> s : subscriptions) {
				s.activate();
			}
		}

		@Override
		protected void handleCancel() throws Exception {
			for(ISubscription<TradeIndication> s : subscriptions) {
				s.removeEventListener(this);
				s.cancel();
			}
		}

		public void eventFired(TradeIndication event) throws Exception {
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
	public ISubscription<TradeIndication> subscribe(InstrumentSpecification spec)
			throws Exception {
		return super.subscribe(spec, TimeFrame.TIMEFRAME_1_TICK);
	}
}
