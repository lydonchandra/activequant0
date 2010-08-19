package org.activequant.data.retrieval.integration.ib;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Quote;
import org.activequant.core.types.TimeFrame;
import org.activequant.data.retrieval.IQuoteSubscriptionSource;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.integration.SubscriptionSourceBase;
import org.activequant.util.pattern.events.IEventListener;
import org.apache.log4j.Logger;


/**
 * 
 * Quote events subscription source. Uses {@link IBTwsConnection} class
 * as a backend for requesting and receiving Quote events from Interactive Brokers.
 * <br>
 * <b>History:</b><br>
 *  - [10.09.2007] Created (?)<br>
 *  - [29.09.2007] cleanup + moved to new domain model (Erik Nijkamp)<br>
 *  - [28.10.2007] moved to new subscription source data model (Mike Kroutikov)<br>
 *
 *  @author Activequant Contributors
 */
public class IBQuoteSource extends SubscriptionSourceBase<Quote> implements IQuoteSubscriptionSource {

	private final IBTwsConnection connection; 
	
	protected final static Logger log = Logger.getLogger(IBQuoteSource.class);
	
	public IBQuoteSource(IBTwsConnection connection){
		super("IB");
		this.connection = connection;
	}
	
	private class QuoteSubscription extends Subscription {
		private final InstrumentSpecification spec;
		
		QuoteSubscription(InstrumentSpecification s) {
			spec = s;
		}
		
		private final IEventListener<Quote> listener = new IEventListener<Quote>() {
			public void eventFired(Quote event) throws Exception {
				fireEvent(event);
			}
		};

		@Override
		protected void handleActivate() {
			connection.subscribeToQuote(listener, spec);
		}

		@Override
		protected void handleCancel() {
			connection.unsubscribeFromQuote(listener, spec);
		}
	}
	
	@Override
	protected QuoteSubscription createSubscription(InstrumentSpecification spec, TimeFrame timeFrame) {
		return new QuoteSubscription(spec);
	}

	public ISubscription<Quote> subscribe(InstrumentSpecification spec) {
		return subscribe(spec, TimeFrame.TIMEFRAME_1_TICK);
	}
}
