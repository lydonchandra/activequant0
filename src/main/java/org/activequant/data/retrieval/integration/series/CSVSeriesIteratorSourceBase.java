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
package org.activequant.data.retrieval.integration.series;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import org.activequant.core.domainmodel.MarketDataEntity;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.data.retrieval.ISeriesDataIteratorSource;
import org.apache.log4j.Logger;

/**
 * Common base for CSV series readers.
 * <p>
 * <b>History:</b><br>
 *  - [20.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public abstract class CSVSeriesIteratorSourceBase<T extends MarketDataEntity> implements ISeriesDataIteratorSource<T> {

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
	
    public String getVendorName() {
		return "CSVQUOTESOURCE";
	}
	
	protected abstract T parseEntity(SeriesSpecification spec, String ... props);
	
	private T readNextEntity(BufferedReader reader, SeriesSpecification spec) throws IOException {

		while(true) {
			String line = reader.readLine();
			if(line == null) return null; // no more
			line = line.trim();
			if(line.startsWith("#")) continue; // skip comments
			
			return parseEntity(spec, line.split(delimiter));
		}
	}
	
	/**
	 * Regardless of start/end date we fetch the complete content of the
	 * CSV file.
	 */
	public Iterable<T> fetch(final SeriesSpecification spec)
			throws Exception {
		
		return new Iterable<T>() {

			final BufferedReader reader = new BufferedReader(new FileReader(fileName));

			public Iterator<T> iterator() {
				log.info("instantiated CSV reader for file: " + fileName);

				return new Iterator<T>() {

					private final AtomicReference<T> parsedEntity = new AtomicReference<T>();

					public boolean hasNext() {
						if(parsedEntity.get() != null) return true;
		
						try {
							parsedEntity.set(readNextEntity(reader, spec));
						} catch(Exception ex) {
							ex.printStackTrace();
							log.error(ex);
							return false;
						}
						return parsedEntity.get() != null;
					}

					public T next() {
						if(hasNext()) {
							return parsedEntity.getAndSet(null);
						} else {
							return null;
						}
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
					
				};
			}
			
		};
	}
}