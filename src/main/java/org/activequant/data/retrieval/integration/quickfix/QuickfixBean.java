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
package org.activequant.data.retrieval.integration.quickfix;

import org.activequant.util.spring.ServiceLocator;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileStoreFactory;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.ScreenLogFactory;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;

/**
 * The QuickFixBean gives us a convenient way to boostrap quickfix from Spring.<br>
 * <br>
 * <b>History:</b><br>
 *  - [03.05.2007] Created (Ulrich Staudinger)<br>
 *  - [25.09.2007] Cleanup (Erik Nijkamp)<br>
 *  - [09.11.2007] Fixes (Erik Nijkamp)<br>
 *    
 *
 *  @author Ulrich Staudinger
 */
public class QuickfixBean extends Thread implements DisposableBean, InitializingBean {

	private SessionSettings sessionSettings;
	private Application application = null;
	private SocketInitiator feedInitiator;
	private quickfix.LogFactory logFactory = null;
	private MessageStoreFactory messageStoreFactory = null;
	private boolean started = false;

	public static void main(String[] args) {
		try {
			ServiceLocator.instance("data/quickfix/factory.xml").getContext();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public void afterPropertiesSet() throws ConfigError {
		if(!started){
            LogFactory logFactory = new ScreenLogFactory(sessionSettings);
            FileStoreFactory storeFactory = new FileStoreFactory(sessionSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            feedInitiator = new SocketInitiator(
            		application, 
            		storeFactory, 
            		sessionSettings, 
            		logFactory, 
            		messageFactory);
            feedInitiator.start();
		}
		started = true; 
	}
	
	public void run(){
		try{
			while(true){
				sleep(1000);
			}
		}
		catch(Exception x){
			
		}
	}
	
	public void testCall(){
		System.out.println("  *********** HERE WE GO.");
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public quickfix.LogFactory getLogFactory() {
		return logFactory;
	}

	public void setLogFactory(quickfix.LogFactory logFactory) {
		this.logFactory = logFactory;
	}

	public MessageStoreFactory getMessageStoreFactory() {
		return messageStoreFactory;
	}

	public void setMessageStoreFactory(MessageStoreFactory messageStoreFactory) {
		this.messageStoreFactory = messageStoreFactory;
	}

	public SessionSettings getSessionSettings() {
		return sessionSettings;
	}

	public void setSessionSettings(SessionSettings sessionSettings) {
		this.sessionSettings = sessionSettings;
	}
}