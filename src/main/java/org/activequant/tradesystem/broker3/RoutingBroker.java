package org.activequant.tradesystem.broker3;

import java.util.NoSuchElementException;

import org.activequant.dao.IOrderLinkDao;
import org.activequant.tradesystem.domainmodel3.Order;
import org.activequant.tradesystem.types.BrokerId;

/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [17.11.2007] Created (Erik N.)<br>
 * <br>
 *
 *  @author Erik Nijkamp
 */
public abstract class RoutingBroker extends BrokerBase {
	
	private IBroker[] brokers;
	@SuppressWarnings("unused")
	private IOrderLinkDao orderLinkDao;
	
	public RoutingBroker(IOrderLinkDao orderLinkDao, IBroker... brokers) {
		this.orderLinkDao = orderLinkDao;
		this.brokers = brokers;
	}	

	public void cancelOrder(Order order) throws Exception {
		//Order linkedOrder = orderLinkDao.getLinkedOrder(order);
		//IBroker broker = findBrokerById(linkedOrder.getBrokerId());
		//broker.cancelOrder(linkedOrder);		
	}
	
	public void placeOrder(Order order) throws Exception {
		
	}

	public Order[] getOrders() {
		return null;
	}
	
	@SuppressWarnings("unused")
	private IBroker findBrokerById(BrokerId id) {
		for(IBroker broker : brokers) {
			if(broker.getBrokerID().equals(id)) {
				return broker;
			}
		}
		throw new NoSuchElementException(id.toString());
	}

}
