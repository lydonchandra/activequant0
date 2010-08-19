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
package org.activequant.tradesystem.broker.integration;

import org.activequant.tradesystem.broker.BrokerBase;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.BrokerAccount;
import org.activequant.tradesystem.domainmodel.Order;
import org.apache.log4j.Logger;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [06.06.2007] Created (Erik Nijkamp)<br>
 *  - [09.06.2007] moved to subaccount model (Ulrich)<br>
 *  - [10.06.2007] Removed getOpenPosition method (Ulrich Staudinger)<br>
 *
 *  @author Erik Nijkamp
 *  @author Ulrich Staudinger
 */
public class MockBroker extends BrokerBase {
	
	private final static Logger log = Logger.getLogger(MockBroker.class);
	
    public MockBroker() {
    	super();
    }
    
    public MockBroker(Account account) {    	
    	super(account);
    }
    
    public MockBroker(BrokerAccount account) {
    	super(account);
    }

	public void placeOrder(Order order) throws Exception {
		log.info("Placing order: " + order.toString());
	}

	public void placeOrders(Order... orders) throws Exception {
		for(Order order: orders) {
			placeOrder(order);
		}		
	}
}
