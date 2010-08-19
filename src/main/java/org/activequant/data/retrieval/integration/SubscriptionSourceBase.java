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
package org.activequant.data.retrieval.integration;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.MarketDataEntity;
import org.activequant.core.types.TimeFrame;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.util.exceptions.SubscriptionException;
import org.activequant.util.pattern.events.Event;
import org.activequant.util.pattern.events.IEventListener;
import org.apache.log4j.Logger;

/**
 * Base class for subscription sources.
 * 
 * <p/>
 * This abstract class takes care of management of the collection of
 * open subscriptions. It also provides a standard base class for an 
 * individual subscription. 
 * 
 * <p/>
 * Implementations have to do the following steps:
 * <ul>
 * 		<li>derive a class from {@link #Subscription} to add members holding
 * 			implementation-specific subscription state.
 * 		<li>implement {@link #createSubscription} method, putting there all the preparation work
 * 			(like symbol name conversion to the native format), but not the actual
 * 			subscription. Then instantiate your subscription object and put there all the
 * 			necessary state. Finally, return it.
 * 		<li>implement {@link #handleActivate}. Here you do whatever is necessary
 * 			to actually activate the subscription.
 * 		<li>implement {@link #handleCancel}. Here you do whatever is necessary
 * 			to cancel the subscriptions.
 * </ul>
 * It is guaranteed that {@link #handleActivate} is called no more than once per subscription
 * object. Also, {@link #handleCancel} will be called exactly once.
 * 
 * <p/>
 * A sample skeleton of how to extend this class:
 * <pre>
 * 
 * public class SampleSubscriptionSource extends SubscriptionSourceBase<Quote> implements IQuoteSubscriptionSource {
 * 
 * 		// holds internal state (here its topic name)
 * 		private class QuoteSubscription extends Subscription implements MessageListener {
 * 
 * 			private String topicName;
 * 			public void setTopicName(String val) { topicName = val; }
 * 
 * 			public void onMessage(String text) {
 * 				Quote quote = parseQuote(text);
 * 
 * 				fireEvent(quote);
 * 			}
 *
 * 			protected void handleActivate() {
 * 				subscribeTopic(topicName, (MessageListener) this);
 * 			}
 * 
 * 			protected void handleCancel() {
 * 				unsubscribeTopic(topicName, (MessageListener) this);
 * 			}
 * 		}
 * 
 * 		public ISubscription<Quote> createSubscription(InstrumentSpecification spec, TimeFrame tf) {
 * 			// here goes all preparation work
 * 			// ....
 * 
 * 			QuoteSubscription subscription = new QuoteSubscription();
 * 
 * 			String topicName = ... // find out which topic distributes these quotes
 * 
 * 			subscription.setTopicName(topicName);
 *
 * 			return subscription;
 * 		}
 * }
 * 
 * </pre>  
 * <br>
 * <b>History:</b><br>
 *  - [Oct 24, 2007] Created (Mike Kroutikov)<br>
 *  - [26.10.2007] Added array trick + generic event (Erik Nijkamp)<br>
 *  - [10.11.2007] Added runtime exception to activate() and cancel() (Erik Nijkamp)<br>
 *  - [27.11.2007] Added getSubscriptions() method (Ulrich Staudinger)<br>
 *  
 *  @author Mike Kroutikov.
 */
public abstract class SubscriptionSourceBase<T extends MarketDataEntity> {

	protected final Logger log = Logger.getLogger(getClass());

	public final String vendorName;
	
	/**
	 * Contructor takes a string to be reported as the vendor for this class and its
	 * subscriptions.
	 * 
	 * @param vendorName vendor name.
	 */
	public SubscriptionSourceBase(String vendorName) {
		this.vendorName = vendorName;
	}
	  
	/**
	 * Cancels all outstanding subscriptions.
	 * <p/>
	 * <em>IMPORTANT</em><br/>
	 * If using Spring, please, declare this method as Spring's bean "destroy-method".
	 * This will ensure that all resources and threads associated with
	 * subscription source are released.
	 * 
	 */
	public void close() throws SubscriptionException {
		List<ISubscription<T>> list = new ArrayList<ISubscription<T>>();
		
		synchronized(activeSubscriptions) {
			list.addAll(activeSubscriptions);
		}
		
		for(ISubscription<T> s : list) {
			s.cancel();
		}
	}

	/**
	 * Returns vendor name.
	 * 
	 * @return vendor name.
	 */
	public String getVendorName() {
    	return vendorName;
	}
	
	private static class Topic {
		
		public final InstrumentSpecification spec;
		public final TimeFrame timeFrame;
		
		public Topic(InstrumentSpecification s, TimeFrame tf) {
			spec = s; timeFrame = tf;
		}
		
		public int hashCode() {
			return spec.hashCode() + timeFrame.hashCode();
		}
		
		public boolean equals(Object o) {
			if(! (o instanceof Topic)) {
				return false;
			}
			
			Topic s = (Topic) o;
			
			return spec.equals(s.spec) && timeFrame.equals(s.timeFrame);
		}
	}
	
	private final Map<Topic, Subscription> activeBackends = new ConcurrentHashMap<Topic, Subscription>();
	private final Queue<SubscriptionDelegate> activeSubscriptions = new ConcurrentLinkedQueue<SubscriptionDelegate>();
	
	/**
	 * The only need for this class is to allow generics-friendly
	 * instantiation of an array (see <code>getSubscriptions</code> below).
	 * The trick avoids using parameterized type in the cast by casting to
	 * an array of <code>SubscriptionDelegate</code>, where <code>SubscriptionDelegate</code> 
	 * is an inner class that "inherits" generic paramater from the superclass.
	 * <p/>
	 * Thanks to Erik for finding this elegant solution. Alternatives are ugly:
	 * either suppress warning, or create an abstract method for array creation.
	 */
	private abstract class SubscriptionDelegate implements ISubscription<T> {}
	
	/**
	 * Returns an array of outstanding subscriptions (the ones that have been
	 * created and registered, but not yet canceled.
	 * 
	 * @return array of outstanding subscriptions.
	 */
	public final ISubscription<T> [] getSubscriptions() {
		   ISubscription<T>[] array = (SubscriptionDelegate[]) Array.newInstance(SubscriptionDelegate.class, 0); 
		   return activeSubscriptions.toArray(array);
	}
	
	/**
	 * using this method it is possible to fetch the subscriptions for a single specification <-> timeframe pair. 
	 * Basically ment as a helper method. 
	 * @param spec
	 * @param timeFrame
	 * @return
	 */
	public final ISubscription<T> [] getSubscriptions(final InstrumentSpecification spec, final TimeFrame timeFrame) {
		List<ISubscription<T>> subscriptions = new ArrayList<ISubscription<T>>();
		// iterate over all subscriptions and search for matching subscriptions ...
		for(ISubscription<T> subscription : activeSubscriptions){
			if(subscription.getInstrumentSpecification().equals(spec) && subscription.getTimeFrame().equals(timeFrame)){
				// ... and add them to the list of returned subscriptions. 
				subscriptions.add(subscription); 
			}
		}
		// have to take way across Delegate. 
		ISubscription<T>[] array = (SubscriptionDelegate[]) Array.newInstance(SubscriptionDelegate.class, 0);
		return subscriptions.toArray(array); 
	}
	
	/**
	 * method to subscribe ... TODO: Describe ... 
	 * @param spec
	 * @param timeFrame
	 * @return
	 */
	public final ISubscription<T> subscribe(final InstrumentSpecification spec, final TimeFrame timeFrame) {
		final Topic topic = new Topic(spec, timeFrame);

		synchronized(activeSubscriptions) {
			// lookup backend subscription
			Subscription subscription = activeBackends.get(topic);
			if(subscription == null) {
				subscription = createSubscription(spec, timeFrame);
				activeBackends.put(topic, subscription);
			}

			final Subscription backend = subscription;
			backend.refCount.getAndIncrement();
			
			// instantiate new delegate to backend 
			SubscriptionDelegate out = new SubscriptionDelegate() {

				private final AtomicBoolean isActive   = new AtomicBoolean(false);
				private final AtomicBoolean isCanceled = new AtomicBoolean(false);

				public void activate() throws SubscriptionException {
					if(isCanceled.get()) {
						throw new IllegalStateException("subscription was canceled");
					}

					if(isActive.getAndSet(true)) {
						return; // already active
					}

					for(IEventListener<T> l : listeners) {
						backend.addEventListener(l);
					}

					log.debug("activating subscription " + this);
					backend.activate();
				}

				public void cancel() throws SubscriptionException {

					if(isCanceled.getAndSet(true)) {
						return; // ignore duplicate cancel requests
					}

					if(isActive.getAndSet(false)) {
						for(IEventListener<T> l : listeners) {
							backend.removeEventListener(l);
						}
					}

					listeners.clear();

					synchronized(activeSubscriptions) {
						activeSubscriptions.remove(this);

						if(backend.refCount.decrementAndGet() == 0) {
							activeBackends.remove(topic);
							log.debug("canceling subscription " + this);
							backend.cancel();
						}
					}
				}

				public InstrumentSpecification getInstrumentSpecification() {
					return spec;
				}

				public TimeFrame getTimeFrame() {
					return timeFrame;
				}

				public String getVendorName() {
					return vendorName;
				}

				public boolean isActive() {
					return isActive.get();
				}

				private final Queue<IEventListener<T>> listeners = new ConcurrentLinkedQueue<IEventListener<T>>();

				public void addEventListener(IEventListener<T> listener) {
					listeners.add(listener);
					if(isActive.get()) {
						backend.addEventListener(listener);
					}
				}

				public void removeEventListener(IEventListener<T> listener) {
					if(!listeners.remove(listener)) return;

					if(isActive.get()) {
						backend.removeEventListener(listener);
					}
				}

				@Override
				public String toString() {
					return "Subscription(spec=" + spec + ", timeFrame=" + timeFrame + ")";
				}
			};

			activeSubscriptions.add(out);
			return out;
		}
	}
	
	protected abstract Subscription createSubscription(InstrumentSpecification spec, TimeFrame timeFrame);
	
	protected abstract class Subscription {

		private final AtomicBoolean isActive = new AtomicBoolean(false);
		private final AtomicInteger refCount = new AtomicInteger(0);
		
		/**
		 * Override this to implement activation. This method is guaranteed to be called
		 * no more than once per object lifetime (i.e. till cancel() is called).
		 */
		protected abstract void handleActivate() throws Exception; 
		
		/**
		 * Override this to implement subscription cancelation. This method is guaranteed
		 * to be called only once.
		 */
		protected abstract void handleCancel() throws Exception;

		
		private void activate() throws SubscriptionException {
			if(isActive.getAndSet(true)) {
				return;
			}
			try {
				handleActivate();
			} catch(Throwable ex) {
				throw new SubscriptionException(ex);
			}
			
		}
		
		private void cancel() throws SubscriptionException {
			try {
				handleCancel();
			} catch(Throwable ex) {
				throw new SubscriptionException(ex);
			}
		}

		private final Event<T> event = new Event<T>();

		/**
		 * Registers new event listener.
		 * 
		 * @param listener event listener.
		 */
		private void addEventListener(IEventListener<T> listener) {
			event.addEventListener(listener);
		}

		/**
		 * Unregisters new event listener.
		 * 
		 * @param listener event listener.
		 */
		private void removeEventListener(IEventListener<T> listener) {
			event.removeEventListener(listener);
		}

		/**
		 * Implementations will use this method to fire market event.
		 * 
		 * @param event market event.
		 */
		protected final void fireEvent(T data) {
			try {
				event.fire(data);
			} catch (Exception e) {				
				log.error(e);
				throw new RuntimeException(e);
			}
		}
	}
}

