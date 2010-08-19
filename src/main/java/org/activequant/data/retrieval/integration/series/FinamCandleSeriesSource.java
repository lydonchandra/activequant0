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
import org.activequant.core.types.TimeStamp;
import org.activequant.core.util.TimeStampFormat;
import org.activequant.data.retrieval.integration.CandleSeriesSourceBase;

/**
 * Url: http://www.fin-rus.com/analysis/export/_eng_/default.asp<br>
 * Format: daily, DD/MM/YY, ',', no, "Ticker, Per, Date, OHLC, Vol", no header<br>
 * Sample: EURUSD,D,16/02/01,000000,0.91210,0.91820,0.91020,0.91530,0<br>
 * <br>
 * <b>History:</b><br>
 *  - [14.10.2007] Created (Erik Nijkamp)<br>
 *  - [09.11.2007] Moved to new data interfaces (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class FinamCandleSeriesSource extends CandleSeriesSourceBase {
	
	private final static String DELIMITER = ",";
	
	
	public CandleSeries fetch(SeriesSpecification query) throws Exception {
		return fetch(query.getInstrumentSpecification());
	}

	public CandleSeries fetch(InstrumentSpecification instrument) throws Exception {
		String filename = instrument.getSymbol().toString();
		return fetch(new File(filename));
	}
	
	public CandleSeries fetch(Symbol symbol) throws Exception {
		String filename = symbol.toString();
		return fetch(new File(filename));
	}

	public CandleSeries fetch(File file) throws Exception {
		// EURUSD,D,16/02/01,000000,0.91210,0.91820,0.91020,0.91530,0
		TimeStampFormat format = new TimeStampFormat("dd/MM/yy");
		format.setTimeZone(TimeZone.getDefault());
		FileInputStream fin = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
		String line = "";
		CandleSeries CandleSeries = new CandleSeries();
		while ((line = reader.readLine()) != null) {
			StringTokenizer str = new StringTokenizer(line, DELIMITER);
			// candle
			Candle candle = new Candle();
			// skip
			str.nextToken(); // symbol
			str.nextToken(); // ticker
			// date
			String dateStr = str.nextToken();
			TimeStamp date = format.parse(dateStr);
			candle.setTimeStamp(date);
			// skip
			str.nextToken(); // time
			// ohlc
			double[] ohlc = new double[4]; 
			for (int i = 0; i < ohlc.length; i++) {
				String valueStr = str.nextToken();
				ohlc[i] = Double.parseDouble(valueStr);
			}
			candle.setDoubles(ohlc);
			// volume
			long volume = Long.parseLong(str.nextToken());
			candle.setVolume(volume);
			// attach
			CandleSeries.add(candle);
		}
		
		// add contract
		CandleSeries.setSeriesSpecification(getSpecification(file));
		
		// align dates and check
		alignDateOrder(CandleSeries);		
		
		return CandleSeries;
	}
	
	public SeriesSpecification getSpecification(File file) {
		return new SeriesSpecification(new Symbol(file.getName()));
	}

	public String getVendorName() {
		return "Finam";
	}
}