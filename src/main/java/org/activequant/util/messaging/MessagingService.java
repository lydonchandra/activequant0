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

import java.util.HashMap;

import org.activequant.util.messaging.events.MessageEvent;
import org.activequant.util.pattern.producer.Producer;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 * 21.12.2006 Created (en)
 *
 *  @author Erik Nijkamp
 */
public class MessagingService implements IMessengerService {
    private HashMap<Protocol, IMessenger> messengers = new HashMap<Protocol, IMessenger>();
    private MessageEvent messageEvent = new MessageEvent();
    
	public MessageEvent getMessageEvent() {
		return messageEvent;
	}

    public MessagingService(MessengerConfig... configs) throws Exception {
       setConfiguration(configs);
    }
    
    public void setConfiguration(MessengerConfig... configs) throws Exception {
       Producer<IMessenger> producer = new Producer<IMessenger>();
       // For each backend
       for (MessengerConfig config : configs) {
          // Create implementation using reflection
          IMessenger impl = producer.create(config.implementationClass,
                config.accountName, config.accountPassword);
          // Register listener
          impl.getMessageEvent().forward(messageEvent);
          // Add it to container
          messengers.put(impl.getProtocol(), impl);
       }
    }
    
    public void connect() throws Exception {
    	for(IMessenger messenger: messengers.values()) {
    		messenger.connect();
    	}
    }
    
    public void disconnect() throws Exception {
    	for(IMessenger messenger: messengers.values()) {
    		messenger.disconnect();
    	}
    }

    public void sendMessage(String to, String subject, String message) throws Exception {
    	assert(!messengers.isEmpty());
    	for(IMessenger messenger: messengers.values()) {
    		messenger.sendMessage(to, subject, message);
    	}
	}

    public void sendMessage(String to, String message, Protocol protocol) throws Exception {
		assert (!messengers.isEmpty());
		messengers.get(protocol).sendMessage(to, message);
	}

	public void sendMessage(String[] to, String message, Protocol protocol) throws Exception {
		assert (!messengers.isEmpty());
		messengers.get(protocol).sendMessage(to, message);
	}

    public void sendMessage(String to, String subject, String message, Protocol protocol)
			throws Exception {
    	assert(!messengers.isEmpty());
		messengers.get(protocol).sendMessage(to, subject, message);
	}

	public void sendMessage(String[] to, String subject, String message, Protocol protocol)
			throws Exception {
		assert(!messengers.isEmpty());
		messengers.get(protocol).sendMessage(to, subject, message);
	}
}
