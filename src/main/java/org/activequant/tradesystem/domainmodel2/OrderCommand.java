package org.activequant.tradesystem.domainmodel2;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * an order command ticket contains a list of orders or order commands 
 * that should be executed on a list of orders.  
 * <br>
 * <b>History:</b><br>
 *  - [15.12.2007] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class OrderCommand {
	
	/**
	 * commands to be done with the orders. 
	 */
	public enum Command {
		PLACE, UPDATE, CANCEL;
	}
	
	// 
	private List<Order> orders = new ArrayList<Order>();

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

}
