package org.activequant.tradesystem.broker.integration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.activequant.core.domainmodel.Quote;
import org.activequant.data.retrieval.IQuoteSubscriptionSource;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.util.pattern.events.IEventListener;

/**
 * Common base (or component) of Brokers that need to track market quotes
 * and evaluate orders per quote event.
 * <br>
 * <b>History:</b><br>
 *  - [16.11.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public abstract class QuoteTrackingBrokerBase {
	
	private final Map<Order,ISubscription<Quote>> managedOrders = new ConcurrentHashMap<Order,ISubscription<Quote>>();
	
	protected abstract void processOrder(Order order, Quote quote) throws Exception;

	private IQuoteSubscriptionSource source;
	
	public void setQuoteSubscriptionSource(IQuoteSubscriptionSource val) {
		source = val;
	}
	
	public IQuoteSubscriptionSource getQuoteSubscriptionSource() {
		return source;
	}
	
	/**
	 * This method destroys the object, closing all outstanding subscriptions.
	 * If using Spring, declare this method as bean's "destroy-method".
	 * 
	 * @throws Exception
	 */
	public void destroy() {
		for(ISubscription<Quote> s : managedOrders.values()) {
			s.cancel();
		}
	}
	
	public final void addToManagedOrders(final Order o) throws Exception {
		if(source == null) {
			throw new IllegalStateException("quoteSubscriptionSource not set");
		}
		
		final ISubscription<Quote> s = source.subscribe(o.getInstrumentSpecification());
		
		managedOrders.put(o, s);
		
		s.addEventListener(new IEventListener<Quote>() {

			public void eventFired(Quote quote) throws Exception {
				if(o.isCanceled() || o.isFilled()) {
					removeFromManagedOrders(o);
				} else {
					processOrder(o, quote);
				}
			}
			
		});
		
		s.activate();
	}
	
	public final boolean removeFromManagedOrders(Order o) {
		ISubscription<Quote> subscription = managedOrders.remove(o);
		if(subscription != null) {
			subscription.cancel();
			return true; // removed
		} else {
			return false; // not found
		}
	}
}
