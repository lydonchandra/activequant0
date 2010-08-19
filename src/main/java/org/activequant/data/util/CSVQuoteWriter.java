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

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Quote;
import org.activequant.data.retrieval.IQuoteSubscriptionSource;
import org.activequant.data.retrieval.ISubscription;

/**
 * Subscribes to the data feed and writes it to the CSV-formatted flat file.
 * <p>
 * <b>History:</b><br>
 *  - [20.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class CSVQuoteWriter extends CSVWriterBase<Quote> {

	private IQuoteSubscriptionSource quoteSubscriptionSource;
	private InstrumentSpecification spec;
	
	public IQuoteSubscriptionSource getQuoteSubscriptionSource() {
		return quoteSubscriptionSource;
	}
	public void setQuoteSubscriptionSource(IQuoteSubscriptionSource val) {
		quoteSubscriptionSource = val;
	}
	
	public InstrumentSpecification getInstrumentSpecification() {
		return spec;
	}
	public void setInstrumentSpecification(InstrumentSpecification val) {
		spec = val;
	}
    
	protected String formatEntity(Quote q, String delimiter) {
		return q.getTimeStamp().toString() + delimiter 
		+ q.getBidPrice() + delimiter
		+ q.getBidQuantity() + delimiter 
		+ q.getAskPrice() + delimiter
		+ q.getAskQuantity() + "\n";
	}
	
	protected ISubscription<Quote> openSubscription() throws Exception {
		if(quoteSubscriptionSource == null) {
			throw new IllegalStateException("quoteSubscriptionSource not set");
		}
		if(spec == null) {
			throw new IllegalStateException("instrumentSpecification not set");
		}
		
		return quoteSubscriptionSource.subscribe(spec);
	}

	protected void writeHeader(Writer writer) throws IOException {
		writer.write("##\n");
		writer.write("## Quote Recorder\n## Started: " + (new Date()) + "\n");
		writer.write("## InstrumentSpecification: " + spec + "\n");
		writer.write("##\n");
		writer.write("## timeStamp, bidPrice, bidQuantity, askSize, askQuantity\n");
	}
}