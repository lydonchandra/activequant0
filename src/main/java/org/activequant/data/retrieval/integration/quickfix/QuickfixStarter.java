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

import java.io.InputStream;

import quickfix.Application;
import quickfix.DefaultMessageFactory;
import quickfix.FileStoreFactory;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.SLF4JLogFactory;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
import quickfix.SocketInitiator;

/**
 * Spring-friendly helper to start Quickfix application (client or server).
 * <br>
 * <b>History:</b><br>
 *  - [25.11.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class QuickfixStarter {
	
	private String settingsCfg;
	private Application application;
	private boolean server = false;

	private SocketAcceptor acceptor;
	private SocketInitiator initiator;

	/**
	 * Starts the application. If using Spring, declare this method as
	 * bean's "init-method".
	 * 
	 * @throws Exception if something goes wrong.
	 */
	public void start() throws Exception {
		
		if(settingsCfg == null) {
			throw new IllegalStateException("settingsCfg not set");
		}
    	
		if(application == null) {
			throw new IllegalStateException("application not set");
		}

		SessionSettings settings;

		try {
			InputStream in = getClass().getResourceAsStream(settingsCfg);
			settings = new SessionSettings(in);
			in.close();
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new AssertionError("can not read config file: " + settingsCfg);
		}
        
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new SLF4JLogFactory(settings);
        MessageFactory messageFactory = new DefaultMessageFactory();

        if(server) {
        	acceptor = new SocketAcceptor(
           		application, 
           		messageStoreFactory, 
           		settings, 
           		logFactory,
           		messageFactory
                );
            
           	acceptor.start();
        } else {
        	initiator = new SocketInitiator(
        		application, 
        		messageStoreFactory, 
        		settings, 
        		logFactory,
                messageFactory
                );
        
        	initiator.start();
        }
    }
    
	/**
	 * Stops running application. If using Spring, declare this method as
	 * bean's "destroy-method".
	 * 
	 * @throws Exception if something goes wrong.
	 */
    public void stop() throws Exception {
    	if(initiator != null) {
    		initiator.stop();
    		initiator = null;
    	}
    	
    	if(acceptor != null) {
    		acceptor.stop();
    		acceptor = null;
    	}
    }

    /**
     * Path to quickfix settings file. See Quickfix/J documentation for
     * file format and content.
     * 
	 * @return settings file.
     */
	public String getSettingsCfg() {
		return settingsCfg;
	}
	
	/**
	 * Sets path to setting file.
	 * 
     * @param val settings file path.
	 */
	public void setSettingsCfg(String val) {
		settingsCfg = val;
	}
	
	/**
	 * Determines is this starter launches client or server application.
	 * Default is <code>false</code> (client).
	 * 
	 * @return server flag.
	 */
	public boolean isServer() {
		return server;
	}
	
	/**
	 * Set this to true if this bean starts server application.
	 * 
	 * @param val server flag.
	 */
	public void setServer(boolean val) {
		server = val;
	}
	
	/**
	 * Application to run.
	 * 
	 * @return application.
	 */
	public Application getApplication() {
		return application;
	}
	
	/**
	 * Sets application to run.
	 * 
	 * @param val application.
	 */
	public void setApplication(Application val) {
		application = val;
	}
}