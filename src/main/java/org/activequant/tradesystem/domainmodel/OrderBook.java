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
package org.activequant.tradesystem.domainmodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.tradesystem.types.OrderState;
import org.activequant.util.tools.Arrays;


/**
 * book that gathers all orders, both filled and open. <br>
 * <br>
 * <b>History:</b><br>
 *  - [09.06.2007] Created (Ulrich Staudinger)<br>
 *  - [14.07.2007] Added persistence (Erik Nijkamp)<br>
 *  - [29.09.2007] Removed annotations (Erik Nijkamp)<br>
 *  - [01.11.2007] Added constructor (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 */
public class OrderBook {
	
	private Long id;

	private List<Order> orders = new ArrayList<Order>();

	public OrderBook(){

	}
	
	public OrderBook(Order...orders) {
		this.orders.addAll(Arrays.asList(orders));
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public boolean hasId() {
		return id != null;
	}
	
	public Iterator<Order> getOrderIterator(){
		return orders.iterator();
	}
	
	public Order[] getOpenOrders() {
		List<Order> openOrders = new ArrayList<Order>();
		for (Order o : orders) {
			if (o.getOpenQuantity() > 0.0 && !o.isCanceled() && !o.isRejected()) {
				openOrders.add(o);
			}
		}
		return openOrders.toArray(new Order[] {});
	}
	
	public Order[] getCanceledOrders(){
		List<Order> canceledOrders = new ArrayList<Order>();
		for(Order o : orders){
			if(o.isCanceled() ){
				canceledOrders.add(o);
			}
		}
		return canceledOrders.toArray(new Order[] {});
	}
	
	public Order[] getRejectedOrders() {
		List<Order> rejectedOrders = new ArrayList<Order>();
		for (Order o : orders) {
			if (o.isRejected()) {
				rejectedOrders.add(o);
			}
		}
		return rejectedOrders.toArray(new Order[orders.size()]);
	}
	
	public Order[] getOrders(OrderState state) {
		// TODO finalize, remove getters above (en)
		return null;
	}
	
	public Order[] getOrders() {
		return orders.toArray(new Order[] {});
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
	
	public void setOrders(Order... orders) {
		this.orders = Arrays.asList(orders);
	}
	
	public void addOrder(Order order){
		this.orders.add(order);
	}
	

	public Order[] getOpenOrdersByInstrumentSpecification(InstrumentSpecification spec) {
		List<Order> openOrders = new ArrayList<Order>();
		for (Order o : getOpenOrders()) {
			if(o.getInstrumentSpecification().equals(spec)){
				openOrders.add(o);
			}
		}
		return openOrders.toArray(new Order[] {});
	}
	
	/**
	 * method to fetch a list of orders by contract specification
	 * @param spec
	 * @return
	 */
	public Order[] getOrdersByContractSpecification(InstrumentSpecification spec) {
		List<Order> ret = new ArrayList<Order>();

		for (Order o : orders) {
			if (o.getInstrumentSpecification().equals(spec))
				ret.add(o);
		}

		return ret.toArray(new Order[] {});
	}
	
	public Order findByBrokerAssignedId(String orderId) {		
		for(Order order: orders){
			if(order.getBrokerAssignedId().equals(orderId))
				return order; 
		}
		throw new NoSuchElementException();
	}
}