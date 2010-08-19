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
package org.activequant.tradesystem.domainmodel2;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.activequant.core.domainmodel.Quote;
import org.activequant.data.retrieval.IQuoteSubscriptionSource;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.tradesystem.domainmodel2.Order;
import org.activequant.tradesystem.domainmodel2.event.OrderCompletionEvent;
import org.activequant.tradesystem.domainmodel2.event.OrderEvent;
import org.activequant.tradesystem.domainmodel2.event.OrderUpdateEvent;
import org.activequant.util.pattern.events.Event;
import org.activequant.util.pattern.events.IEventListener;
import org.activequant.util.pattern.events.IEventSource;

/**
 * Paper broker: gets subscription to the quotes, and executed incoming orders
 * according to the quotes. Supports only MARKET, LIMIT, and STOP orders.
 * <p>
 * <b>History:</b><br>
 *  - [13.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class TrailingStopSimulatingBroker implements IBroker {
	
	private final IBroker engine;
	private final IQuoteSubscriptionSource subscriptionSource;

	public TrailingStopSimulatingBroker(IBroker engine, IQuoteSubscriptionSource subscriptionSource) {
		this.engine = engine;
		this.subscriptionSource = subscriptionSource;
	}

	private static final double EPSILON = 1e-6;

	private static Order convertToStopOrder(Order trailingOrder) {
		Order stopOrder = new Order();
		
		stopOrder.setOrderTimeStamp(trailingOrder.getOrderTimeStamp());
		stopOrder.setInstrumentSpecification(trailingOrder.getInstrumentSpecification());
		stopOrder.setStopPrice(trailingOrder.getStopPrice());
		stopOrder.setOrderSide(trailingOrder.getOrderSide());
		stopOrder.setOrderType(Order.Type.STOP);
		stopOrder.setQuantity(trailingOrder.getQuantity());
		
		return stopOrder;
	}

	class OrderTracker implements IOrderTracker {
		
		private final Order order;
		private final IOrderTracker stopTracker;
		private ISubscription<Quote> subscription;
		
		private Order updatedOrder; // trail-stop order
		private Order updatedStopOrder; // most recent view of the stopOrder
		
		private final Event<OrderEvent> orderEvent = new Event<OrderEvent>();
		
		public OrderTracker(Order order) {
			this.order = this.updatedOrder = order;

			updatedStopOrder = convertToStopOrder(order);
			stopTracker = engine.prepareOrder(updatedStopOrder);

			stopTracker.getOrderEventSource().addEventListener(new IEventListener<OrderEvent>() {
				public void eventFired(OrderEvent event) throws Exception {
					if(event instanceof OrderCompletionEvent) {
						subscription.cancel();
					} else if(event instanceof OrderUpdateEvent) {
						updatedStopOrder = ((OrderUpdateEvent) event).getUpdatedOrder();
						// translate update info from stop to trailing stop.
						OrderUpdateEvent ue = new OrderUpdateEvent(event.getEventTimeStamp(), event.getMessage(), updatedOrder);
						orderEvent.fire(ue);
						return;
					}
					orderEvent.fire(event);
				}
			});
		}

		public void cancel() {
			stopTracker.cancel();
			subscription.cancel();
		}

		public OrderCompletionEvent getOrderCompletion() {
			return stopTracker.getOrderCompletion();
		}

		public IEventSource<OrderEvent> getOrderEventSource() {
			return orderEvent;
		}

		public Order getOrder() {
			return order;
		}

		public String getBrokerAssignedId() {
			return stopTracker.getBrokerAssignedId();
		}

		public void submit() {
			try {
				stopTracker.submit();
				subscription = subscriptionSource.subscribe(order.getInstrumentSpecification());
				subscription.addEventListener(new IEventListener<Quote>() {
					public void eventFired(Quote event) throws Exception {
						checkOrder(event);
					}
				});
				subscription.activate();
			} catch(Exception ex) {
				throw new RuntimeException(ex);
			}
		}

		private void checkOrder(Quote quote) {

			if(updatedOrder.getOrderSide() == Order.Side.BUY) {
				double price = quote.getAskPrice();
				if(price <= 0.) {
					return;
				}
				if(price < updatedStopOrder.getStopPrice() - updatedOrder.getTrailingDistance()) {
					Order newOrder = new Order(updatedStopOrder);
					newOrder.setStopPrice(price + updatedOrder.getTrailingDistance());
					stopTracker.update(newOrder);
				}
			} else {
				double price = quote.getBidPrice();
				if(price <= 0.) {
					return;
				}
				if(price > updatedStopOrder.getStopPrice() + updatedOrder.getTrailingDistance()) {
					Order newOrder = new Order(updatedStopOrder);
					newOrder.setStopPrice(price - updatedOrder.getTrailingDistance());
					stopTracker.update(newOrder);
				}
			}
		}
		
		public void update(Order newOrder) {
			// only trailing distance and quantity can be updated
			double quantityDiff = newOrder.getQuantity() - updatedOrder.getQuantity();

			updatedOrder = new Order(newOrder);
			if(Math.abs(quantityDiff) > EPSILON) {
				stopTracker.update(convertToStopOrder(newOrder));
			}
		}
	}

	public IOrderTracker prepareOrder(Order order) {
		if(order.getOrderType() == Order.Type.TRAILING_STOP) {
			// intercept. place stop order with the engine
			return new OrderTracker(order);
		} else {
			// do not care, just return the original
			return engine.prepareOrder(order);
		}
	}
	
	private final Map<IOrderTracker,OrderTracker> managedOrders = new IdentityHashMap<IOrderTracker,OrderTracker>(); 

	public IOrderTracker[] getOrders() {
		List<IOrderTracker> list = new ArrayList<IOrderTracker>();
		
		for(IOrderTracker t : engine.getOrders()) {
			if(managedOrders.containsKey(t)) {
				list.add(managedOrders.get(t));
			} else {
				list.add(t);
			}
		}

		return list.toArray(new IOrderTracker[0]);
	}
}
