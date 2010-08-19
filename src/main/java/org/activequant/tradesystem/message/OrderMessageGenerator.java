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
package org.activequant.tradesystem.message;

import java.util.Date;

import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.system.ITradeSystem;
import org.activequant.util.messaging.MessagingService;

/**
 * 
 * generates messages from orders and publishes / distributes these. <br>
 * <br>
 * <b>History:</b><br>
 *  - [21.07.2007] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class OrderMessageGenerator implements INotificationService {

	private MessagingService facade; 
	private String[] tos;
	
	public OrderMessageGenerator(MessagingService facade, String... tos) throws Exception {
		this.facade = facade;
		this.tos = tos; 
		facade.connect();
	}
	

	public void publishOrders(ITradeSystem tradeSystem, Order... orders) throws Exception  {
		
		StringBuffer sb = new StringBuffer();
		sb.append("Orders generated at ").append(new Date()).append("\n");
		sb.append("by trade system : ").append(tradeSystem.getName()).append("\n\n");
		
		for(Order o : orders) {
			sb.append(o.toString()).append("\n");
		}
		for(String to: tos){
			facade.sendMessage(to, "LATEST ORDERS", sb.toString());
		}
	}

}
