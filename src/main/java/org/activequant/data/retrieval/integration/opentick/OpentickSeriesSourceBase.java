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
package org.activequant.data.retrieval.integration.opentick;


import java.util.LinkedList;
import java.util.List;

import org.activequant.core.domainmodel.SeriesSpecification;
import org.apache.log4j.Logger;
import org.otfeed.IConnection;
import org.otfeed.IConnectionFactory;
import org.otfeed.IRequest;
import org.otfeed.event.IConnectionStateListener;
import org.otfeed.event.OTError;
import org.otfeed.event.OTHost;


/**
 * Retrieves historical data from OpenTick Corp. (http://www.opentick.com)<br>
 * THIS API IS NOT SUPPORTED BY OpenTick.<br>
 * <br>
 * <b>History:</b><br>
 *  - [14.09.2007] initial code write-up<br>
 *
 *  @author Mike Kroutikov
 */
abstract class OpentickSeriesSourceBase<T> {

	protected final Logger log = Logger.getLogger(getClass());


	/**
	 * Default constructor.
	 */
	public OpentickSeriesSourceBase() {
	}

	private IConnectionFactory factory;
	
	/**
	 * Sets the connection factory. <br>
	 * <em>IMPORTANT!</em>: Always use OTPooledConnectionFactory here (not raw OTConnectionFactory).
	 * This is needed to support multiple subscriptions. Every subscription will
	 * open its own connection. OTPooledConnectionFactory uses a single underlying 
	 * connection, conserving the resources and making the quote source more robust.
	 * 
	 * @param val connection factory.
	 */
	public void setConnectionFactory(IConnectionFactory val) {
		factory = val;
	}
	
	/**
	 * Returns connection factory.
	 * 
	 * @return factory.
	 */
	public IConnectionFactory getConnectionFactory() {
		return factory;
	}

	abstract OTRequest<T> submitRequest(final SeriesSpecification query) throws Exception;
	abstract T [] createArray(int length);
	
	static class OTRequest<T> {
		public final IRequest request;
		public final T list;
		public OTRequest(IRequest r, T l) {
			request = r;
			list = l;
		}
	}

	private final IConnectionStateListener connectionStateListener = new IConnectionStateListener() {

		public void onConnected() {
			log.info("connected");
		}

		public void onConnecting(OTHost host) {
			log.info("connecting to: " + host);
		}

		public void onError(OTError error) {
			log.info("error: " + error);
		}

		public void onLogin() {
			log.info("logged in");
		}

		public void onRedirect(OTHost host) {
			log.info("redirected to: " + host);
		}
	};
	
	protected IConnection connect() {
		return factory.connect(connectionStateListener);
	}

   	public T [] fetch(SeriesSpecification ... qq) throws Exception {
   		log.debug("Fetching series ...");
		T [] out = createArray(qq.length);

		List<OTRequest<T>> requestList = new LinkedList<OTRequest<T>>();

		// submit all 
		for(int i = 0; i < qq.length; i++) {
			OTRequest<T> r = submitRequest(qq[i]);
			requestList.add(r);
			out[i] = r.list;
		}

		// wait for completion
		for(OTRequest<T> r : requestList) {
			r.request.waitForCompletion();
			if(r.request.getError() != null) {
				log.error("incomplete series, error=" + r.request.getError());
			}
		}

        return out;
	}
    
    public T fetch(SeriesSpecification query) throws Exception {
		T [] out = fetch(new SeriesSpecification[] {query});
		log.debug("Returning "+out[0]+" objects.");
		return out[0];
	}

	public final static String VENDOR_NAME = "OPENTICK";
	  
	public String getVendorName() {
    	return VENDOR_NAME;
	}
}
