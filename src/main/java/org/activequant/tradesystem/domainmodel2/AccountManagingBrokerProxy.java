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

import org.activequant.tradesystem.domainmodel.Position;
import org.activequant.tradesystem.domainmodel2.BrokerAccount;
import org.activequant.tradesystem.domainmodel2.Order;
import org.activequant.tradesystem.domainmodel2.event.OrderCompletionEvent;
import org.activequant.tradesystem.domainmodel2.event.OrderEvent;
import org.activequant.tradesystem.domainmodel2.event.OrderExecutionEvent;
import org.activequant.util.pattern.events.Event;
import org.activequant.util.pattern.events.IEventListener;
import org.activequant.util.pattern.events.IEventSource;

/**
 * Broker proxy that is responsible for updating account information:
 * OrderBook, Portfolio, TransactionBook, BalanceBook, etc.
 * <p>
 * <b>History:</b><br>
 *  - [16.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class AccountManagingBrokerProxy implements IBroker {
	
	private final IBroker engine;
	private final BrokerAccount brokerAccount;
	private final Map<String,OrderTrackerProxy> managedOrders = new ConcurrentHashMap<String,OrderTrackerProxy>();
	private final double EPSILON = 1e-6;

	public AccountManagingBrokerProxy(IBroker engine, BrokerAccount brokerAccount) {
		this.engine = engine;
		this.brokerAccount = brokerAccount;
	}

	class OrderTrackerProxy implements IOrderTracker {
		
		private final OrderTicket ticket = new OrderTicket();
		
		private final IOrderTracker tracker;
		private final Event<OrderEvent> orderEvent = new Event<OrderEvent>();
		
		public OrderTrackerProxy(Order order) {
			ticket.setOrder(order);
			tracker = engine.prepareOrder(order);

			tracker.getOrderEventSource().addEventListener(new IEventListener<OrderEvent>() {
				public void eventFired(OrderEvent event) throws Exception {
					ticket.getEvents().add(event);
					
					if(event instanceof OrderCompletionEvent) {
						// no more need to manage: order is final
						managedOrders.remove(tracker.getBrokerAssignedId());
					} else if(event instanceof OrderExecutionEvent) {
						processExecution((OrderExecutionEvent) event);
					}
					orderEvent.fire(event);
				}
			});
		}

		public void submit() {
			tracker.submit();
			managedOrders.put(tracker.getBrokerAssignedId(), this);
			brokerAccount.getOrderBook().addTicket(ticket);
		}
		
		public void update(Order newOrder) {
			tracker.update(newOrder);
		}

		public void cancel() {
			tracker.cancel();
		}
		
		private void processExecution(OrderExecutionEvent event) {
			Order order = tracker.getOrder();
			// get position for execution
			Position position; 
			if (brokerAccount.getPortfolio().hasPosition(order.getInstrumentSpecification())) {
				position = brokerAccount.getPortfolio().getPosition(order.getInstrumentSpecification());
			} else {
				position = new Position();
				position.setInstrumentSpecification(order.getInstrumentSpecification());
				brokerAccount.getPortfolio().addPosition(position);
			}
			
			// update the average price		
			double oldValue = position.getQuantity() * position.getAveragePrice();
			double newValue;
			double cashFlow;
			double cost;
			
			// mark-to-market 
			switch(order.getOrderSide()) {
			case SELL: case SHORT_SELL: case SHORT_SELL_EXEMPT:
				// decrease the amount of the position.
				position.setQuantity(position.getQuantity() - event.getExecutionQuantity());
				newValue = position.getQuantity() * event.getExecutionPrice();
				cost     = event.getExecutionQuantity() * event.getExecutionPrice();
				cashFlow = newValue - oldValue + cost; // FIXME: commission
				break;
			case BUY:
				// increase the position size.
				position.setQuantity(position.getQuantity() + event.getExecutionQuantity());
				newValue = position.getQuantity() * event.getExecutionPrice();
				cost     = event.getExecutionQuantity() * event.getExecutionPrice();
				cashFlow = newValue - oldValue - cost; // FIXME: commission
				break;
			default:
				throw new IllegalArgumentException("order side not supported/implemented: " + order.getOrderSide());
			}

			// clean out empty positions.
			if (Math.abs(position.getQuantity()) < EPSILON) {
				brokerAccount.getPortfolio().removePosition(position);

				brokerAccount.getBalanceBook().addBalanceEntry(cashFlow,
						event.getEventTimeStamp());
			} else {
				// calculate the new average price (from market-to-market value).
				position.setAveragePrice(Math.abs(newValue / position.getQuantity()));
			}
		}
		public OrderCompletionEvent getOrderCompletion() {
			return tracker.getOrderCompletion();
		}

		public IEventSource<OrderEvent> getOrderEventSource() {
			return orderEvent;
		}

		public Order getOrder() {
			return tracker.getOrder();
		}

		public String getBrokerAssignedId() {
			return tracker.getBrokerAssignedId();
		}
	}

	public IOrderTracker prepareOrder(Order order) {
		return new OrderTrackerProxy(order);
	}
	
	public IOrderTracker[] getOrders() {
		List<IOrderTracker> list = new ArrayList<IOrderTracker>();
		
		for(IOrderTracker t : engine.getOrders()) {
			if(managedOrders.containsKey(t.getBrokerAssignedId())) {
				list.add(managedOrders.get(t.getBrokerAssignedId()));
			} else {
				list.add(t); // ???
			}
		}

		return list.toArray(new IOrderTracker[0]);
	}
}
