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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.MarketDataEntity;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.types.TimeFrame;
import org.activequant.data.retrieval.integration.SubscriptionSourceBase;
import org.activequant.util.pattern.events.Event;
import org.activequant.util.pattern.events.IEventSink;
import org.activequant.util.pattern.events.IEventSource;
import org.apache.log4j.Logger;

/**
 * Abstract base file for misc dispatching subscription sources.
 * Exposes standard subscription interface, accepts any subscription.
 * User may use it as an event sink, throwing here different market
 * events. These events will be dispatched to the appropriate subscription
 * (if exists).
 * <p/>
 * Additionally, exposes event sources for tracking subscribe/unsubscribe
 * events.
 *       <br>
 *       <b>History:</b><br>
 * - [09.11.2007] Created (Mike Kroutikov)<br>
 * 
 * @author Mike Kroutikov
 */
public abstract class DispatchingSubscriptionSourceBase<T extends MarketDataEntity> extends SubscriptionSourceBase<T> implements IEventSink<T> {
	
	protected final Logger log = Logger.getLogger(getClass()); 

	public DispatchingSubscriptionSourceBase(String vendorName) {
		super(vendorName);
	}
	
	protected abstract SeriesSpecification inferSeriesSpecification(T entity); 
	
	private final Map<SeriesSpecification,IEventSink<T>> events = new ConcurrentHashMap<SeriesSpecification,IEventSink<T>>();
	private final Event<SeriesSpecification> subscribeEvent = new Event<SeriesSpecification>();
	private final Event<SeriesSpecification> unsubscribeEvent = new Event<SeriesSpecification>();
	
	private void addEventSink(SeriesSpecification specs, IEventSink<T> sink) {
		try {
			subscribeEvent.fire(specs);
		} catch(Exception ex) {
			ex.printStackTrace();
			log.error(ex);
		}
		events.put(specs, sink);
	}
	private void removeEventSink(SeriesSpecification specs) {
		events.remove(specs);
		try {
			unsubscribeEvent.fire(specs);
		} catch(Exception ex) {
			ex.printStackTrace();
			log.error(ex);
		}
	}
	
	class DispatchingSubscription extends Subscription implements IEventSink<T> {
		private final SeriesSpecification specs;
		
		public DispatchingSubscription(SeriesSpecification ss) {
			specs = ss;
		}

		@Override
		protected void handleActivate() { 
			addEventSink(specs, this);
		}

		@Override
		protected void handleCancel() { 
			removeEventSink(specs);
		}

		public void fire(T e) throws Exception {
			super.fireEvent(e);
		}
	}
	
	@Override
	protected DispatchingSubscription createSubscription(InstrumentSpecification spec, TimeFrame timeFrame) {
		
		SeriesSpecification shortSs = new SeriesSpecification(spec, timeFrame);
		
		return new DispatchingSubscription(shortSs);
	}

	public void fire(T e) throws Exception {
		SeriesSpecification spec = inferSeriesSpecification(e);
		IEventSink<T> sink = events.get(spec);
		if(sink != null) {
			try {
				sink.fire(e);
			} catch(Exception ex) {
				ex.printStackTrace();
				log.error(ex);
			}
		}
	}
	
	/**
	 * Event source that tracks subscribe actions.
	 * 
	 * @return event source for subscription notifications.
	 */
	public IEventSource<SeriesSpecification> getSubscribeEventSource() {
		return subscribeEvent;
	}

	/**
	 * Event source that tracks unsubscribe actions.
	 * 
	 * @return event source for unsubscribe actions.
	 */
	public IEventSource<SeriesSpecification> getUnsubscribeEventSource() {
		return unsubscribeEvent;
	}
}
