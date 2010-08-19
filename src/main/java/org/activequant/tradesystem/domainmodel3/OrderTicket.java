package org.activequant.tradesystem.domainmodel3;

import java.util.ArrayList;
import java.util.List;

/**
 * Order ticket. Contains the order information, reference id, and list of order-related events:
 * executions, updates, etc. May represent still opened order
 * or completed order, depending on whether it has not-null <code>orderCompletion</code>.
 * <br>
 * <b>History:</b><br>
 *  - [22.11.2007] Created (Mike Kroutikov)<br>
 *  - [16.12.2007] Removed brokerOrderId (Erik Nijkamp)<br>
 *
 *  @author Mike Kroutikov
 */
public class OrderTicket {
	
	private Order order;
	private List<OrderEvent> events = new ArrayList<OrderEvent>();
	private OrderTicketCompletion completion;

	/**
	 * Order value for this ticket.
	 * If there were any successful order updates, this will be
	 * the most recent (updated) version. The original version
	 * can be found by looking for {@link OrderUpdateEvent}s.
	 * 
	 * @return
	 */
	public Order getOrder() {
		return order;
	}
	
	/**
	 * Sets the order.
	 * 
	 * @param val order.
	 */
	public void setOrder(Order val) {
		order = val;
	}

	/**
	 * List of all events in the chronological order.
	 * Note that the order is important for some reconstruction
	 * tasks (i.e. when trying to restore the series of updates that were
	 * applied to the order).
	 * 
	 * @return list of order events.
	 */
	public List<OrderEvent> getEvents() {
		return events;
	}
	
	/**
	 * Sets events.
	 * 
	 * @param val event list.
	 */
	public void setEvents(List<OrderEvent> val) {
		events = val;
	}
	
	/**
	 * Returns order ticket completion information. For opened orders, contains
	 * null.
	 * 
	 * @return completion info.
	 */
	public OrderTicketCompletion getTicketCompletion() {
		return completion;
	}
	
	/**
	 * Sets order ticket completion info.
	 * 
	 * @param val
	 */
	public void setTicketCompletion(OrderTicketCompletion val) {
		completion = val;
	}
}
