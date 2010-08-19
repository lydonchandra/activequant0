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
import org.activequant.core.domainmodel.MarketDataEntity;
import org.activequant.core.types.TimeFrame;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.filtering.IDataFilter;
import org.activequant.data.retrieval.integration.SubscriptionSourceBase;
import org.activequant.util.exceptions.SubscriptionException;
import org.activequant.util.pattern.events.IEventListener;
import org.apache.log4j.Logger;

/**
 * Generic filter of live quotes. Uses pluggable strategy to implement 
 * filtering decisions.
 *       <b>History:</b><br>
 * - [04.12.2007] Created (Mike Kroutikov)<br>
 * 
 * @author Mike Kroutikov
 */
public abstract class SubscriptionSourceFilterBase<T extends MarketDataEntity> extends SubscriptionSourceBase<T> {
	
	protected final Logger log = Logger.getLogger(getClass());
	
	private IDataFilter<T> filter = new IDataFilter<T>() {
		public boolean evaluate(T data) {
			return true;
		}
	};	

	public SubscriptionSourceFilterBase(String vendor) {
		super(vendor);
	}
	
	public IDataFilter<T> getFilter() {
		return filter;
	}
	
	public void setDataFilter(IDataFilter<T> val) {
		filter = val;
	}

	class DataSubscription extends Subscription {
		private final ISubscription<T> subscription;

		DataSubscription(ISubscription<T> s) {
			subscription = s;
		}
		
		private final IEventListener<T> listener = new IEventListener<T>() {
			public void eventFired(T event) throws Exception {
				if(filter.evaluate(event)) {
					fireEvent(event);
				}
			}
		};
		
		@Override
		protected void handleActivate() {
			subscription.addEventListener(listener);
			subscription.activate();
		}

		@Override
		protected void handleCancel() {
			subscription.removeEventListener(listener);
			subscription.cancel();
		}
	}
	
	protected abstract ISubscription<T> openSubscription(InstrumentSpecification spec, TimeFrame timeFrame) throws Exception;
	
	@Override
	protected DataSubscription createSubscription(InstrumentSpecification spec, TimeFrame timeFrame) {
		try {
			return new DataSubscription(openSubscription(spec, timeFrame));
		} catch(Exception ex) {
			throw new SubscriptionException(ex);
		}
	}
}