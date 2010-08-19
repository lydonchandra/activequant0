package org.activequant.tradesystem.domainmodel3;

import org.activequant.core.types.TimeStamp;

/**
 * Common base fo all events. The only common property is timestamp.
 * <br>
 * <b>History:</b><br>
 *  - [11.12.2007] Created (Mike Kroutikov)<br>
 *  - [16.12.2007] Replaced OrderTicket with Order (Erik Nijkamp)<br>
 *
 *  @author Mike Kroutikov
 */
public abstract class OrderEvent {
	
	private Long id;
	private Order order;
	private TimeStamp eventTimeStamp;
	private String message;
	
	/**
	 * Creates empty event.
	 */
	public OrderEvent() { }
	
	/**
	 * Creates populated event.
	 * 
	 * @param stamp time stamp.
	 * @param message message.
	 */
	public OrderEvent(Order order, TimeStamp stamp, String message) {
		this.order = order;
		this.eventTimeStamp = stamp;
		this.message = message;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long val) {
		id = val;
	}
	
	/**
	 * Ticket this event belongs to.
	 * 
	 * @return ticket.
	 */
	public Order getOrder() {
		return order;
	}
	
	/**
	 * Sets ticket this event belongs to.
	 * 
	 * @param ticket
	 */
	public void setOrder(Order order) {
		this.order = order;
	}
	
	/**
	 * Time stamp of this event.
	 * 
	 * @return event time stamp.
	 */
	public TimeStamp getEventTimeStamp() {
		return eventTimeStamp;
	}
	
	/**
	 * Sets event time stamp.
	 * 
	 * @param val time stamp.
	 */
	public void setEventTimeStamp(TimeStamp val) {
		eventTimeStamp = val;
	}

	/**
	 * Message that accompany this event. May help to explain the reason
	 * (i.e. why order has been rejected).
	 * 
	 * @return message.
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Sets message.
	 * 
	 * @param val message.
	 */
	public void setMessage(String val) {
		message = val;
	}
}
