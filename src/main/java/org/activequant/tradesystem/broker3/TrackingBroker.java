package org.activequant.tradesystem.broker3;

import org.activequant.tradesystem.domainmodel3.Order;
import org.activequant.tradesystem.types.BrokerId;
import org.activequant.tradesystem.types.OrderState;
import org.activequant.util.pattern.events.Event;

/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [17.11.2007] Created (Erik N.)<br>
 * <br>
 *
 *  @author Erik Nijkamp
 */
public abstract class TrackingBroker implements IBroker {

	public void cancelOrder(Order order) throws Exception {
		
	}

	public BrokerId getBrokerID() {
		return null;
	}

	public Event<Order> getOnStatusChange() {
		return null;
	}

	public Order[] getOrders(OrderState status) {
		return null;
	}

	public void placeOrder(Order order) throws Exception {
		
	}
}
