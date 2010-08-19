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

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.types.TimeFrame;
import org.activequant.data.retrieval.ICandleSubscriptionSource;
import org.activequant.data.retrieval.ISubscription;

/**
 * Subscribes to the data feed and writes it to the CSV-formatted flat file.
 * <p>
 * <b>History:</b><br>
 *  - [20.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class CSVCandleWriter extends CSVWriterBase<Candle> {

	private ICandleSubscriptionSource candleSubscriptionSource;
	private InstrumentSpecification spec;
	private TimeFrame timeFrame;
	
	public ICandleSubscriptionSource getCandleSubscriptionSource() {
		return candleSubscriptionSource;
	}
	public void setCandleSubscriptionSource(ICandleSubscriptionSource val) {
		candleSubscriptionSource = val;
	}
	
	public InstrumentSpecification getInstrumentSpecification() {
		return spec;
	}
	public void setInstrumentSpecification(InstrumentSpecification val) {
		spec = val;
	}
    
	protected String formatEntity(Candle c, String delimiter) {
		return c.getTimeStamp().toString() + delimiter 
		+ c.getOpenPrice() + delimiter
		+ c.getHighPrice() + delimiter 
		+ c.getLowPrice() + delimiter
		+ c.getClosePrice() + delimiter
		+ c.getVolume() + delimiter
		+ c.getHighTimeStamp() + delimiter
		+ c.getLowTimeStamp() + "\n";
	}
	
	protected ISubscription<Candle> openSubscription() throws Exception {
		if(candleSubscriptionSource == null) {
			throw new IllegalStateException("candleSubscriptionSource not set");
		}
		if(spec == null) {
			throw new IllegalStateException("instrumentSpecification not set");
		}
		
		return candleSubscriptionSource.subscribe(spec, timeFrame);
	}

	protected void writeHeader(Writer writer) throws IOException {
		writer.write("##\n");
		writer.write("## Candle Recorder\n## Started: " + (new Date()) + "\n");
		writer.write("## InstrumentSpecification: " + spec + "\n");
		writer.write("## TimeFrame: " + timeFrame + "\n");
		writer.write("##\n");
		writer.write("## timeStamp, openPrice, highPrice, lowPrice, closePrice, volume, highTimeStamp, lowTimeStamp\n");
	}
}