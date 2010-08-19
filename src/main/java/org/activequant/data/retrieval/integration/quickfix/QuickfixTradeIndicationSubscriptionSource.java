package org.activequant.data.retrieval.integration.quickfix;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.core.types.TimeFrame;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.ITradeIndicationSubscriptionSource;
import org.activequant.data.retrieval.integration.SubscriptionSourceBase;
import org.activequant.util.pattern.events.IEventListener;

/**
 * Quickfix trade indication feed source. Delegates actual work to {@link QuickfixConnection}.
 * <br>
 * <b>History:</b><br>
 *  - [Nov 4, 2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class QuickfixTradeIndicationSubscriptionSource extends SubscriptionSourceBase<TradeIndication> implements ITradeIndicationSubscriptionSource {

	public QuickfixTradeIndicationSubscriptionSource() {
		super("QUICKFIX");
	}
	
	private QuickfixConnection connection;
	public void setConnection(QuickfixConnection val) {
		connection = val;
	}
	public QuickfixConnection getConnection() { 
		return connection;
	}
	
	private class TradeSubscription extends Subscription {
		
		private final IEventListener<TradeIndication> listener = new IEventListener<TradeIndication>() {
			public void eventFired(TradeIndication event) throws Exception {
				fireEvent(event);
			}
		};
		
		private final InstrumentSpecification spec;
		
		public TradeSubscription(InstrumentSpecification s) {
			spec = s;
		}

		private String reqId = null;

		@Override
		protected void handleActivate() throws Exception {
			reqId = connection.subscribeToTrade(spec, listener);
		}

		@Override
		protected void handleCancel() throws Exception {
			if(reqId != null) {
				connection.cancelRequest(reqId);
			}
		}
	}

	@Override
	protected TradeSubscription createSubscription(
			InstrumentSpecification spec, TimeFrame timeFrame) {
		return new TradeSubscription(spec);
	}

	public final ISubscription<TradeIndication> subscribe(InstrumentSpecification spec)
			throws Exception {
		return super.subscribe(spec, TimeFrame.TIMEFRAME_1_TICK);
	}
}
