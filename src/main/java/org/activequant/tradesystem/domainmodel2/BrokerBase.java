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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.activequant.tradesystem.domainmodel2.event.OrderCancelEvent;
import org.activequant.tradesystem.domainmodel2.event.OrderCompletionEvent;
import org.activequant.tradesystem.domainmodel2.event.OrderEvent;
import org.activequant.tradesystem.domainmodel2.event.OrderExecutionEvent;
import org.activequant.tradesystem.domainmodel2.event.OrderRejectEvent;
import org.activequant.tradesystem.domainmodel2.event.OrderUpdateEvent;
import org.activequant.util.pattern.events.Event;
import org.activequant.util.pattern.events.IEventSource;
import org.apache.log4j.Logger;

/**
 * Base for Broker implementations. Takes care of tracker management.<br>
 * <br>
 * <b>History:</b><br> 
 * - [11.12.2007] Created (Mike Kroutikov) <br>
 * 
 * @author Mike Kroutikov
 */
public abstract class BrokerBase implements IBroker {

	protected final Logger log = Logger.getLogger(getClass());

	// precision for double comparisons
	private static final double EPSILON = 1.0e-6;

	/**
	 * Implementation must return OrderTrackerBase possibly extended with 
	 * implementation-specific information.
	 * 
	 * @param order order passed to <code>prepareTracker</code>.
	 * 
	 * @return implementation of tracker.
	 * @throws Exception if something goes wrong (i.e. order validation fails).
	 */
	protected abstract OrderTrackerBase createOrderTracker(Order order);

	private final Map<String,OrderTrackerBase> orderMap = new ConcurrentHashMap<String,OrderTrackerBase>();

	public final IOrderTracker[] getOrders() {
		return (IOrderTracker[]) orderMap.values().toArray();
	}
	
	public final IOrderTracker prepareOrder(final Order order) {
		return createOrderTracker(order);
	}

	/**
	 * Extend this class to store implementation-specific 
	 * per-order private information, and implement order lifetime 
	 * transitions. 
	 */
	protected abstract class OrderTrackerBase implements IOrderTracker {

		protected final Logger log = Logger.getLogger(getClass());
		
		private final Event<OrderEvent> orderEvent = new Event<OrderEvent>();
		
		private Order order;
		
		private final AtomicBoolean isSubmitted = new AtomicBoolean(false);
		private final AtomicBoolean isCanceled  = new AtomicBoolean(false);
		
		private String brokerAssignedId;
		private final AtomicReference<OrderCompletionEvent> completion = new AtomicReference<OrderCompletionEvent>();

		// to track when the order is filled/completed
		private double openQuantity;
		
		private final List<OrderExecutionEvent> executions = new ArrayList<OrderExecutionEvent>();
		
		/**
		 * Creates new tracker.
		 * 
		 * @param order order.
		 */
		public OrderTrackerBase(Order order) {
			if(order.getOrderType() == null) {
				throw new NullPointerException("orderType can not be null");
			}

			if(order.getOrderSide() == null) {
				throw new NullPointerException("orderSide can not be null");
			}
		
			this.order = order;
			openQuantity = order.getQuantity();
			if(openQuantity < EPSILON) {
				throw new IllegalArgumentException("illegal order quantity value: " + openQuantity);
			}
		}

		/**
		 * Implement response to user calling <code>submit()</code>.
		 * 
		 * @return broker assigned id (must me unique).
		 * @throws Exception if something does not go right.
		 */
		protected abstract String handleSubmit();
		
		/**
		 * Implement response to user calling <code>update()</code>.
		 * 
		 * @param newOrder new order.
		 * @throws Exception if something goes wrong.
		 */
		protected abstract void handleUpdate(Order newOrder);

		/**
		 * Implement response to user calling <code>cancel()</code>.
		 * @throws Exception if something goes wrong.
		 */
		protected abstract void handleCancel();
		
		public final void submit() {
			if(isCanceled.get()) {
				throw new IllegalStateException("order was canceled: no more action expected");
			}
			if(isSubmitted.getAndSet(true)) {
				throw new IllegalStateException("order was already submitted");
			}
			
			brokerAssignedId = handleSubmit();
			if(brokerAssignedId == null) {
				throw new NullPointerException("broker assigned id can not be null!");
			}

			if(orderMap.containsKey(brokerAssignedId)) {
				throw new IllegalStateException("duplicate broker assigned id: " + brokerAssignedId);
			}
			
			orderMap.put(brokerAssignedId, this);
		}
		
		public final void update(Order newOrder) {
			if(!isSubmitted.get()) {
				// prior to order submission update is transparent and purely local
				order = new Order(newOrder);
				return;
			}

			// validate update
			if(order.getOrderType() != newOrder.getOrderType()) {
				throw new IllegalArgumentException("Can not update order type");
			}
			if(order.getOrderSide() != newOrder.getOrderSide()) {
				throw new IllegalArgumentException("Can not update order side");
			}

			if(isCanceled.get()) {
				throw new IllegalStateException("order was canceled: no more action expected");
			}

			if(completion.get() != null) {
				// completed
				return;
			}

			handleUpdate(new Order(newOrder));
		}

		public final void cancel() {
			if(!isSubmitted.get()) {
				throw new IllegalStateException("order was not submitted");
			}
			
			if(completion.get() != null) {
				// completed
				return;
			}

			if(isCanceled.getAndSet(true)) {
				return;
			}
			
			handleCancel();
		}
		
		private OrderCompletionEvent prepareCompletion(OrderEvent errorEvent) {
			OrderCompletionEvent completion = new OrderCompletionEvent();
			completion.setTerminalError(errorEvent);
			
			// compute total price and total quantity of the executions
			double totalQuantity = 0.0;
			double totalCost = 0.0;
			double totalCommission = 0.0;
			
			for(OrderExecutionEvent e : executions) {
				OrderExecutionEvent execution = (OrderExecutionEvent) e;
				totalQuantity += execution.getExecutionQuantity();
				totalCost += execution.getExecutionQuantity() * execution.getExecutionPrice();
				totalCommission += execution.getCommission();
			}
			
			if(totalQuantity > EPSILON) {
				completion.setAveragePrice(totalCost / totalQuantity);
			} else {
				completion.setAveragePrice(0.0);
			}
			
			completion.setTotalQuantity(totalQuantity);
			completion.setTotalCommission(totalCommission);

			return completion;
		}

		// FIXME: incorrect OO - should use polymorfphism instead
		private OrderCompletionEvent checkForCompletion(OrderEvent event) {
			
			if(event instanceof OrderRejectEvent) {
				return prepareCompletion(event);
			} else if(event instanceof OrderCancelEvent) {
				return prepareCompletion(event);
			} else if(event instanceof OrderUpdateEvent) {
				Order newOrder = ((OrderUpdateEvent) event).getUpdatedOrder();
				openQuantity = newOrder.getQuantity();
				return null;
			} else if(event instanceof OrderExecutionEvent) {
				executions.add((OrderExecutionEvent) event);
				openQuantity -= ((OrderExecutionEvent) event).getExecutionQuantity();
				if(openQuantity < EPSILON) {
					// completed!
					return prepareCompletion(null);
				}
			}
			return null;
		}

		/**
		 * Use this method to fire one of the order events. Note that
		 * base will take care of adding it to the OrderTicket, and
		 * generating completion, if necessary.
		 * 
		 * @param event order-related event.
		 */
		protected final synchronized void fireOrderEvent(OrderEvent event) {
			log.info("dispatching new order event: " + event);

			if(completion.get() != null) {
				log.warn("new event arrived on a completed ticket (dropped): " + event);
				return;
			}
			
			try {
				orderEvent.fire(event);
			} catch(Exception ex) {
				ex.printStackTrace();
				log.error(ex);
			}

			// was it a terminal event?
			OrderCompletionEvent completion = checkForCompletion(event);
			if(completion != null) {
				this.completion.set(completion);

				log.info("order completed (about to dispatch completion): " + completion);
				try {
					orderEvent.fire(completion);
				} catch(Exception ex) {
					ex.printStackTrace();
					log.error(ex);
				}
			}
		}
		
		public final String getBrokerAssignedId() {
			return brokerAssignedId;
		}
		
		public final Order getOrder() {
			return order;
		}

		public IEventSource<OrderEvent> getOrderEventSource() {
			return orderEvent;
		}

		public final OrderCompletionEvent getOrderCompletion() {
			return completion.get();
		}
	}
}
