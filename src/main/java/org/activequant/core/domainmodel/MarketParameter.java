package org.activequant.core.domainmodel;

import org.activequant.core.types.TimeStamp;

/**
 * Represents a market parameter.
 * <br>
 * <b>History:</b><br>
 *  - [05.12.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class MarketParameter {
	
	private TimeStamp timeStamp;
	private String key;
	private String value;
	
	public MarketParameter(TimeStamp stamp, String key, String value) {
		this.timeStamp = stamp;
		this.key = key;
		this.value = value;
	}

	public TimeStamp getTimeStamp() {
		return timeStamp;
	}

	public void setTImeStamp(TimeStamp stamp) {
		this.timeStamp= stamp;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
