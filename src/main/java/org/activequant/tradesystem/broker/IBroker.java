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
package org.activequant.tradesystem.broker;

import org.activequant.tradesystem.domainmodel.BrokerAccount;
import org.activequant.tradesystem.domainmodel.Execution;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.types.BrokerId;
import org.activequant.util.pattern.events.IEventSource;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [26.04.2006] Created (Erik N.)<br>
 *  - [05.08.2006] Created (Ulrich S.)<br>
 *  - [10.06.2007] Removed getOpenPosition method (Ulrich Staudinger)<br>
 *  - [11.07.2007] Kicked implementation specific methods (Erik N.)<br>
 *  - [05.08.2007] Added events (Erik N.)<br>
 *  - [06.11.2007] Adding order cancel methods (Ulrich Staudinger)<br>
 * <br>
 *
 *  @author Erik Nijkamp
 *  @author Ulrich Staudinger
 */
public interface IBroker {
   
    /**
     * method to place an order into the processing / order queue. 
     * @param order
     * @throws Exception
     */
    public void placeOrder(Order order) throws Exception;    
    
    /**
     * method to place multiple orders into the processing / order queue. 
     * @param order
     * @throws Exception
     */
    public void placeOrders(Order... orders) throws Exception; 
    
    /**
     * method hook to cancel an order
     * @param orderId
     * @throws Exception
     */
    public void cancelOrder(Order order) throws Exception;
    
    /**
     * method hook to cancel an array of order ids.
     * @param orderId
     * @throws Exception
     */
    public void cancelOrders(Order... orders) throws Exception;
    
    
    /**
     * returns the specific broker id of this implementation. 
     * @return
     */
    public BrokerId getBrokerID(); 
    
	public IEventSource<Execution> getOnNewExecution();

	public IEventSource<Order> getOnNewOrder();
	
	// TODO decouple BrokerAccount from broker, introduce provider "view", aw "view" + sync (en)
	public IEventSource<BrokerAccount> getOnAccountUpdate();
    
}