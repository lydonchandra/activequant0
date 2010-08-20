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
package org.activequant.util.messaging.mock;

import org.activequant.util.messaging.IMessenger;
import org.activequant.util.messaging.Protocol;
import org.activequant.util.messaging.events.MessageEvent;
import org.activequant.util.pattern.events.VoidEvent;

/**
 * Mock Messenger - just prints the message to screen<br>
 * <br>
 * <b>History:</b><br>
 *  - [19.11.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class MockMessenger implements IMessenger {

	public void connect() throws Exception {
	}

	public void disconnect() {
	}

	public VoidEvent getConnectionClosedEvent() {
		return null;
	}

	public VoidEvent getEstablishedClosedEvent() {
		return null;
	}

	public String getId() {
		return null;
	}

	public MessageEvent getMessageEvent() {
		return null;
	}

	public boolean getOnlineStatus(String id) throws Exception {
		return false;
	}

	public Protocol getProtocol() {
		return null;
	}

	public void reconnect() throws Exception {
	}

	public void sendMessage(String to, String msg) throws Exception {
		sendMessage(to, "", msg);
	}

	public void sendMessage(String[] to, String msg) throws Exception {
		for(String address : to) {
			sendMessage(address, msg);
		}
	}

	public void sendMessage(String to, String subject, String msg)
			throws Exception {
		System.out.println("To: " + to);
		System.out.println("Subject: " + subject);
		System.out.println("Message: " + msg);
	}

	public void sendMessage(String[] to, String subject, String msg)
			throws Exception {
	}

	public void setId(String id) throws Exception {
	}

	public void setPassword(String pwd) {
	}

	public boolean supports(Protocol protocol) {
		return false;
	}
}
