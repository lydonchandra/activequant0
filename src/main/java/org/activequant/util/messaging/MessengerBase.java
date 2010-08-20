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
import org.activequant.util.pattern.events.VoidEvent;

/**
 * TODO<br>
 * <br>
 * <b>History:</b><br>
 *  - [09.06.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public abstract class MessengerBase implements IMessenger {

	private String server;
	private String pass;
    private MessageEvent messageEvent = new MessageEvent();
    private VoidEvent connectionClosedEvent = new VoidEvent();
    private VoidEvent connectionEstablishedEvent = new VoidEvent();
    
    
	public MessageEvent getMessageEvent() {
		return messageEvent;
	}
	
	public IVoidEventSource getConnectionClosedEvent() {
		return connectionClosedEvent;
	}
	
	public IVoidEventSource getEstablishedClosedEvent() {
		return connectionEstablishedEvent;
	}


	/**
	 * Fire the newMessage event, if a new message is coming in
	 * 
	 * @param from
	 *            The messenger-id of the sender.
	 * @param to
	 *            The messenger-id of the receiver.
	 * @param message
	 *            The sent message.
	 */
	protected void fireOnMessageSent(String from, String to, String message,
			Protocol protocol) throws Exception {
		messageEvent.fire(from, to, message, protocol);
	}

	/**
	 * Check if it has an valid structure of an eMail address
	 * 
	 * @param mail
	 *            EMail address to check
	 * @return True, if is an eMail address, else false
	 */
	protected boolean isMailAdress(String mail) {
		int posAt = 0;
		int posDot = mail.lastIndexOf(".");
		String atChar = "@";
		if ((posAt = mail.indexOf(atChar)) <= 2
				|| mail.indexOf(atChar, posAt + 1) != -1
				|| posDot < posAt || posDot + 2 >= mail.length()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Return the server of the connection.
	 * 
	 * @return Return the server of the connection.
	 */
	public String getServer() {
		return this.server;
	}

	/**
	 * Set the server for the connection.
	 * 
	 * @param server
	 *            Server, which should communicate with us.
	 */
	public void setServer(String server) {
		this.server = server.trim();
	}

	/**
	 * Return the current set IM-password.
	 * 
	 * @return current set password
	 */
	public String getPassword() {
		return pass;
	}

	/**
	 * Set the password for the IM-connection.
	 * 
	 * @param pass
	 *            The new password for the connection.
	 */
	public void setPassword(String pass) {
		this.pass = pass.trim();
	}

	public boolean supports(Protocol protocol) {
		return protocol == getProtocol();
	}

	public abstract void sendMessage(String to, String subject, String msg) throws Exception;

	public void sendMessage(String to[], String subject, String msg) throws Exception {
		for (String user : to) {
			sendMessage(user, subject, msg);
		}
	}
	
	public void sendMessage(String to, String msg) throws Exception {
		sendMessage(to, null, msg);
	}

	public void sendMessage(String to[], String msg) throws Exception {
		for (String user : to) {
			sendMessage(user, null, msg);
		}
	}
}
