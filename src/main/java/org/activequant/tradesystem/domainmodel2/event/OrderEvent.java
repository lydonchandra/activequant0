package org.activequant.tradesystem.domainmodel2.event;

import org.activequant.core.types.TimeStamp;

/**
 * Common base fo all events. The only common property is timestamp.
 * <br>
 * <b>History:</b><br>
 *  - [11.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public abstract class OrderEvent {
	
	private Long id;
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
	public OrderEvent(TimeStamp stamp, String message) {
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
