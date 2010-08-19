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
package org.activequant.data.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.activequant.core.domainmodel.MarketDataEntity;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.util.pattern.events.IEventListener;
import org.apache.log4j.Logger;

/**
 * Common base for CSV series writers.
 * <p>
 * <b>History:</b><br>
 *  - [20.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public abstract class CSVWriterBase<T extends MarketDataEntity> {

    protected final Logger log = Logger.getLogger(getClass());
    
    private final static String DEFAULT_DELIMITER = ",";

	private String delimiter = DEFAULT_DELIMITER;
	private String fileName;

	public String getDelimiter() {
		return delimiter;
	}
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
    
	public String getFileName() { 
		return fileName;
	}
	public void setFileName(String val) {
		fileName = val;
	}
	
	protected abstract String formatEntity(T entity, String delimiter);
	protected abstract ISubscription<T> openSubscription() throws Exception;
	protected abstract void writeHeader(Writer writer) throws IOException ;

	private BufferedWriter writer;
	private ISubscription<T> subscription;

	/**
	 * Initializes the object by subscribing to the data feed, and preparing
	 * to write to the file. If using Spring, declare this method as bean's "init-method".
	 * 
	 * @throws Exception if can not open file for writing.
	 */
	public void init() throws Exception {
		writer = new BufferedWriter(new FileWriter(fileName));
		writeHeader(writer);
		
		subscription = openSubscription();
		subscription.addEventListener(new IEventListener<T>() {

			public void eventFired(T event) throws Exception {
				log.debug("entity: " + event);
				writer.write(formatEntity(event, delimiter));
				writer.flush();
			}
		});
		subscription.activate();
	}
	
	/**
	 * De-initializes the object by canceling data feed subscription and
	 * flushing data to the disk.
	 * If using Spring, declare this method as bean's "destroy-method".
	 * 
	 * @throws IOException on IO error.
	 */
	public void destroy() throws IOException {
		subscription.cancel();
		writer.flush();
		writer.close();
	}
}