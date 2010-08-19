package org.activequant.tradesystem.broker3;

import org.activequant.tradesystem.domainmodel.BrokerAccount;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.util.pattern.events.IEventListener;

/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [16.11.2007] Created (Erik N.)<br>
 * <br>
 *
 *  @author Erik Nijkamp
 *  @author Mike Kroutikov
 */
public abstract class BrokerAccountService implements IBroker {
	
	@SuppressWarnings("unused")
	private BrokerAccount account;
	@SuppressWarnings("unused")
	private IBroker broker;
	@SuppressWarnings("unused")
	private IEventListener<Order> listener;

}
