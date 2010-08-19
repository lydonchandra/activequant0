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
package org.activequant.tradesystem.domainmodel2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * book that gathers all orders, both filled and open. <br>
 * <br>
 * <b>History:</b><br>
 *  - [09.06.2007] Created (Ulrich Staudinger)<br>
 *  - [14.07.2007] Added persistence (Erik Nijkamp)<br>
 *  - [29.09.2007] Removed annotations (Erik Nijkamp)<br>
 *  - [01.11.2007] Added constructor (Erik Nijkamp)<br>
 *  - [16.12.2007] Major cleanup (Mike Kroutikov)<br>
 *
 *  @author Ulrich Staudinger
 */
public class OrderBook implements Iterable<OrderTicket> {
	
	private Long id;

	private List<OrderTicket> tickets = new ArrayList<OrderTicket>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public boolean hasId() {
		return id != null;
	}
	
	public Iterator<OrderTicket> iterator() {
		return tickets.iterator();
	}
	
	public OrderTicket[] getOpenTickets() {
		List<OrderTicket> ticketList = new ArrayList<OrderTicket>();
		for (OrderTicket o : tickets) {
			if (o.getTicketCompletion() == null) {
				ticketList.add(o);
			}
		}
		return ticketList.toArray(new OrderTicket[0]);
	}
	
	public OrderTicket[] getClosedTickets() {
		List<OrderTicket> ticketList = new ArrayList<OrderTicket>();
		for (OrderTicket o : tickets) {
			if (o.getTicketCompletion() != null) {
				ticketList.add(o);
			}
		}
		return ticketList.toArray(new OrderTicket[0]);
	}
	
	public OrderTicket[] getCanceledOrRejectedTickets() {
		List<OrderTicket> ticketList = new ArrayList<OrderTicket>();
		for (OrderTicket o : tickets) {
			if (o.getTicketCompletion() != null && o.getTicketCompletion().getTerminalError() != null) {
				ticketList.add(o);
			}
		}
		return ticketList.toArray(new OrderTicket[0]);
	}
	
	public OrderTicket[] getTickets() {
		return tickets.toArray(new OrderTicket[] {});
	}

	public void addTicket(OrderTicket t){
		this.tickets.add(t);
	}
}