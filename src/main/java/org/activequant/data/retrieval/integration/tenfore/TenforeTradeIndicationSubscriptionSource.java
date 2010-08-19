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
package org.activequant.data.retrieval.integration.tenfore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.StringTokenizer;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Symbol;
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.core.types.TimeFrame;
import org.activequant.core.types.TimeStamp;
import org.activequant.core.util.TimeStampFormat;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.ITradeIndicationSubscriptionSource;
import org.activequant.data.retrieval.integration.SubscriptionSourceBase;

/**
 * Tenfore trade indication subscription source. It needs tenfore's data file.
 * This subscription source reads data file and selects all records (if any) that
 * match the subscribed instrument specification, and fire them as events.
 * Note that all reading and all event processing happens in <code>activate()</code>
 * call on the subscription. After <code>activate()</code> call returns, no more
 * data is generated, and subscription should be canceled.<br>
 * For information on how to obtain tenfore data, please go to http://www.tenfore.com 
 * 
 * TODO: add functionality to configure the input format, as tenfore supports
 * several formats.
 * <br>
 * <b>History:</b><br>
 *  - [04.05.2007] Created (Ulrich Staudinger)<br>
 *  - [04.06.2007] Minor fixes (Erik Nijkamp)<br>
 *  - [25.09.2007] Switch to InstrumentQuery etc. (Erik Nijkamp)<br>
 *  - [29.09.2007] Refactored push/pull + fixed exception handling + timezone + speedup (Erik Nijkamp)<br>
 *  - [31.10.2007] Moved to proper location, renamed. (Ulrich Staudinger)<br>
 *  - [08.11.2007] Converted to subscription data model. (Mike Kroutikov)<br>
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 *  @author Mike Kroutikov
 */
public class TenforeTradeIndicationSubscriptionSource extends SubscriptionSourceBase<TradeIndication> implements ITradeIndicationSubscriptionSource {

    private static final TimeStampFormat sdf = new TimeStampFormat("dd.MM.yy HH:mm:ss");
    
    private final File tickFile;

    /**
     * Creates new object.
     * 
     * @param tickFile file containing TENFORE tick data.
     * @throws Exception if file can not be found or not readable.
     */
    public TenforeTradeIndicationSubscriptionSource(String tickFile) throws Exception {
    	super("TENFORE");
        this.tickFile = new File(tickFile);

        if(!this.tickFile.exists()) {
        	throw new IllegalArgumentException("File " + tickFile + " does not exist");
        }

        if(!this.tickFile.isFile() || !this.tickFile.canRead()) {
        	throw new IllegalArgumentException("File " + tickFile + " is not readable");
        }
    }
    
    private static String readLine(BufferedReader reader) throws Exception {
    	// loop until we get a valid header
		String line	= "";
		while(!isTickLine(line)) {
			line = reader.readLine();
			if(line == null) 
				return null;
		}
		
		return line;	
    }
    
    private static boolean isTickLine(String line) {
    	if (line.startsWith("T,")) {
			if ((!line.endsWith("IND")) && (!line.endsWith("OTH"))
					&& (!line.endsWith("POF")) && (!line.endsWith("BATCH"))) { 	
				return true;
			}
		}
    	return false;
    }
    
    /**
     * builds the date. 
     */
    private static TimeStamp buildDate(String date, String time) throws ParseException{
 		return sdf.parse(date + " " + time);
    }

	private class TenforeSubscription extends Subscription {
		
		private final InstrumentSpecification spec;
		private final BufferedReader reader;
		
		private TenforeSubscription(InstrumentSpecification spec, File tickFile) throws IOException {
			this.spec = spec;
			this.reader = new BufferedReader(new FileReader(tickFile));
		}

		@Override
		protected void handleActivate() {
			try {
				while(true) {
					TradeIndication ti = readTick();
					if(ti == null) break;
					
					try {
						fireEvent(ti);
					} catch(Exception ex) {
						log.error(ex);
						ex.printStackTrace();
					}
				}
			} catch(Exception ex) {
				log.error(ex);
				ex.printStackTrace();
			}
		}

		@Override
		protected void handleCancel() {
		}

	    private TradeIndication readTick() throws Exception {
			// read the tenfore data from file,skipping lines that do not match
	    	// the instrument specs.

	    	int lineno = 0;
	    	while(true) {
	    		String line = readLine(reader);
	    		if(line == null) {
	    			return null;
	    		}
	    		lineno++;
			
				StringTokenizer str = new StringTokenizer(line, ",");
				// work out new price line.
				str.nextToken(); // type
				Symbol symbol = new Symbol(str.nextToken());
				String market = str.nextToken();
				String security = str.nextToken();
				str.nextToken(); // sequenceId
				String date = str.nextToken();
				String time = str.nextToken();
				String last = str.nextToken();
				String volume = str.nextToken();
				
				// check that symbol matches
				if(!market.equals(spec.getExchange())
						|| !symbol.equals(spec.getSymbol())
						|| !security.equals(spec.getSecurityType())) {
					log.warn("skipping line " + lineno + " [" + line + "]: does not match target instument (" + spec + ")");
					continue;
				}
		
				// build quote and symbol definition.

				TradeIndication tradeIndication = new TradeIndication(spec);
				tradeIndication.setTimeStamp(buildDate(date, time));
				tradeIndication.setPrice(Double.parseDouble(last));
				tradeIndication.setQuantity((long) Double.parseDouble(volume));
				tradeIndication.setInstrumentSpecification(spec);

				log.debug("successfully read tick: " + tradeIndication);
				return tradeIndication;
	    	}
	    }
	}

	@Override
	protected TenforeSubscription createSubscription(
			InstrumentSpecification spec, TimeFrame timeFrame) {
		try {
			return new TenforeSubscription(spec, tickFile);
		} catch(Exception ex) {
			log.error(ex);
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ISubscription<TradeIndication> subscribe(InstrumentSpecification spec)
			throws Exception {
		return super.subscribe(spec, TimeFrame.TIMEFRAME_1_TICK);
	}
}