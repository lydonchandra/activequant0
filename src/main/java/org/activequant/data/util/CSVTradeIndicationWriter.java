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
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.ITradeIndicationSubscriptionSource;

/**
 * Subscribes to the data feed and writes it to the CSV-formatted flat file.
 * <p>
 * <b>History:</b><br>
 *  - [20.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class CSVTradeIndicationWriter extends CSVWriterBase<TradeIndication> {

	private ITradeIndicationSubscriptionSource tradeSubscriptionSource;
	private InstrumentSpecification spec;
	
	public ITradeIndicationSubscriptionSource getTradeIndicationSubscriptionSource() {
		return tradeSubscriptionSource;
	}
	public void setTradeIndicationSubscriptionSource(ITradeIndicationSubscriptionSource val) {
		tradeSubscriptionSource = val;
	}
	
	public InstrumentSpecification getInstrumentSpecification() {
		return spec;
	}
	public void setInstrumentSpecification(InstrumentSpecification val) {
		spec = val;
	}
    
	protected String formatEntity(TradeIndication t, String delimiter) {
		return t.getTimeStamp().toString() + delimiter 
		+ t.getPrice() + delimiter
		+ t.getQuantity() + "\n";
	}
	
	protected ISubscription<TradeIndication> openSubscription() throws Exception {
		if(tradeSubscriptionSource == null) {
			throw new IllegalStateException("tradeIndicationSubscriptionSource not set");
		}
		if(spec == null) {
			throw new IllegalStateException("instrumentSpecification not set");
		}
		
		return tradeSubscriptionSource.subscribe(spec);
	}

	protected void writeHeader(Writer writer) throws IOException {
		writer.write("##\n");
		writer.write("## Trade Indication Recorder\n## Started: " + (new Date()) + "\n");
		writer.write("## InstrumentSpecification: " + spec + "\n");
		writer.write("##\n");
		writer.write("## timeStamp, price, quantity\n");
	}
}