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
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.domainmodel.Symbol;
import org.activequant.core.types.Symbols;
import org.activequant.core.types.TimeStamp;
import org.activequant.core.util.TimeStampFormat;
import org.activequant.data.retrieval.integration.CandleSeriesSourceBase;
import org.activequant.data.util.SymbolMap;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [04.05.2007] Created (Erik Nijkamp)<br>
 *  - [05.08.2007] Added symbol => file mapping (Erik Nijkamp)<br>
 *  - [07.10.2007] Fixed fetch(SeriesSpec...) (Erik Nijkamp)<br>
 *  - [09.11.2007] Moved to new data interfaces (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class PinnacleCandleSeriesSource extends CandleSeriesSourceBase {
	
    // Some symbol associations
    private final static String[][] ASSOCIATIONS = {
            { Symbols.EURUSD.toString(), "FX_RAD.CSV" },
            { Symbols.GERMANBUND.toString(), "DT_RAD.CSV" },
            { Symbols.TNOTES.toString(), "TA_RAD.CSV" },
            { Symbols.SP500.toString(), "SP_RAD.CSV" },
            { Symbols.CRB.toString(), "CR_RAD.CSV" },
            { Symbols.OIL.toString(), "CL_RAD.CSV" }      
        };
    
    private static SymbolMap symbolMap = new SymbolMap(ASSOCIATIONS);
	
	private final static String DELIMITER = ",";
	
	@Override
	public SymbolMap getSymbolMap() {
		return symbolMap;
	}
	
	private String fileBase = "";
	/**
	 * Path to the directory that contains the CSV data files.
	 * Default value is empty string.
	 * 
	 * @return fileBase value.
	 */
	public String getFileBase() { return fileBase; }
	
	/**
	 * Sets file base value.
	 * 
	 * @param val
	 */
	public void setFileBase(String val) { fileBase = val; }
	
	public CandleSeries fetch(SeriesSpecification query) throws Exception {
		return fetch(query.getInstrumentSpecification());
	}

	public CandleSeries fetch(InstrumentSpecification instrument) throws Exception {
		return fetch(instrument.getSymbol());
	}
	
	public CandleSeries fetch(Symbol symbol) throws Exception {
		String filename;
		// use symbol map if available, otherwise it's a filename
		if(getSymbolMap().hasNativeSymbolName(symbol)) {
			filename = getSymbolMap().getNativeSymbolName(symbol);
		} else {
			filename = symbol.toString();
		}
		
		if(fileBase.length() > 0) {
			filename = fileBase + "/" + filename;
		}

		return fetch(new File(filename));
	}

	public CandleSeries fetch(File file) throws Exception {
		TimeStampFormat format = new TimeStampFormat("MM/dd/yyyy");
		format.setTimeZone(TimeZone.getDefault()); // FIXME: correct timezone?
		
		FileInputStream fin = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
		String line = "";
		CandleSeries candleSeries = new CandleSeries();
		while ((line = reader.readLine()) != null) {
			StringTokenizer str = new StringTokenizer(line, DELIMITER);
			// candle
			Candle candle = new Candle();
			// date
			String dateStr = str.nextToken();
			TimeStamp date = format.parse(dateStr);
			candle.setTimeStamp(date);
			// ohlc
			double[] ohlc = new double[4]; 
			for (int i = 0; i < ohlc.length; i++) {
				String valueStr = str.nextToken();
				ohlc[i] = Double.parseDouble(valueStr);
			}
			candle.setDoubles(ohlc);
			// attach
			candleSeries.add(candle);
		}
		
		// add contract
		candleSeries.setSeriesSpecification(new SeriesSpecification(new InstrumentSpecification(getContract(file)), null));
		
		// align dates and check
		alignDateOrder(candleSeries);		
		
		return candleSeries;
	}
	
	public InstrumentSpecification getContract(File file) {
		return new InstrumentSpecification(new Symbol(file.getName()));
	}

	public String getVendorName() {
		return "Pinnacle";
	}
}