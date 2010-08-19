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
package org.activequant.data.retrieval.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.MarketDataEntity;
import org.activequant.core.types.TimeFrame;
import org.activequant.data.retrieval.integration.SubscriptionSourceBase;


/**
 * Mock source of quote and trade subscriptions.
 * Use it for unit testing services that depend on
 * subscription service.
 * Supports only one outstanding subscription.
 * <p>
 * To use, instantiate the mock subscription source, then hook it into the
 * application. Then, use {@link #fireEvent} method to simulate incoming qutes or trades.
 * <br>
 * <br>
 * <b>History:</b><br>
 *  - [09.10.2007] Created (Mike Kroutikov)<br>
 *  - [09.11.2007] Converted to subscription model (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
class MockSubscriptionSourceBase<T extends MarketDataEntity> extends SubscriptionSourceBase<T> {
	
	public MockSubscriptionSourceBase() {
		super("MOCK");
	}

	/**
	 * Call this method from the Unit test to simulate
	 * entities arrival.
	 * 
	 * @param entities list of entities.
	 */
	public void fireEvents(List<T> entities) {
		for(T t : entities) {
			fireEvent(t);
		}
	}

	/**
	 * Call this method from the Unit test to simulate
	 * entity arrival.
	 * 
	 * @param entity entity.
	 */
	public void fireEvent(T entity) {
		log.info("firing event: " + entity);
		try {
			MockSubscription ms = subscriptions.get(entity.getInstrumentSpecification());
			if(ms != null) {
				ms.fire(entity);
			}
		} catch(Exception ex) {
			log.error(ex);
			ex.printStackTrace();
		}
	}

	// used to distribute events to correct handler
	private final Map<InstrumentSpecification,MockSubscription> subscriptions 
					= new HashMap<InstrumentSpecification,MockSubscription>();
	
	private class MockSubscription extends Subscription {
		private final InstrumentSpecification spec;
		
		public MockSubscription(InstrumentSpecification s) {
			spec = s;
		}

		@Override
		protected void handleActivate() {
			subscriptions.put(spec, this);
		}
		
		@Override
		protected void handleCancel() {
			subscriptions.remove(spec);
		}
		
		public void fire(T data) {
			super.fireEvent(data);
		}
	}
	

	@Override
	protected MockSubscription createSubscription(
			InstrumentSpecification spec, TimeFrame timeFrame) {
		return new MockSubscription(spec);
	}
}
