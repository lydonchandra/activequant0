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
package org.activequant.util.messaging;

import org.activequant.util.messaging.events.MessageEvent;
import org.activequant.util.pattern.events.IVoidEventSource;

/**
 * TODO<br>
 * <br>
 * <b>History:</b><br>
 *  - [09.06.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public interface IMessenger {
	
	public MessageEvent getMessageEvent();
	public IVoidEventSource getConnectionClosedEvent();
	public IVoidEventSource getEstablishedClosedEvent();
	
	/**
	 * Connect to the IM-server.
	 * 
	 * @throws
	 */
	public void connect() throws Exception;

	/**
	 * Close the connection from the IM-server.
	 */
	public void disconnect();

	/**
	 * Close the connection and connect new to the IM-server.
	 * 
	 * @throws
	 */
	public void reconnect() throws Exception;

	/**
	 * Send a message.
	 * 
	 * @param msg
	 *            Message, which should going to send.
	 * @param to
	 *            Receiver of the message.
	 * @throws
	 */
	
	public void sendMessage(String to, String msg) throws Exception;

	public void sendMessage(String to[], String msg) throws Exception;
	
	public void sendMessage(String to, String subject, String msg) throws Exception;

	public void sendMessage(String to[], String subject, String msg) throws Exception;

	/**
	 * Return the current set IM-ID.
	 * 
	 * @return current set IM-ID
	 */
	public String getId();

	/**
	 * Set the ID for the connection to the IM-server
	 * 
	 * @param id
	 *            The new IM-ID.
	 * @throws
	 */
	public void setId(String id) throws Exception;
	
	public void setPassword(String pwd);

	public boolean supports(Protocol protocol);

	public Protocol getProtocol();

	public boolean getOnlineStatus(String id) throws Exception;
}
