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
import java.io.InputStreamReader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.types.Symbols;
import org.activequant.core.types.TimeFrame;
import org.activequant.core.types.TimeStamp;
import org.activequant.core.util.TimeStampFormat;
import org.activequant.data.retrieval.integration.CandleSeriesSourceBase;
import org.activequant.data.util.SymbolMap;
import org.activequant.util.exceptions.NotImplementedException;
import org.apache.log4j.Logger;


/**
 * 
 * retrieves historical data from yahoo
 * 
 * 
 * ATTENTION: YOU ARE NOT ALLOWED TO USE QUOTATIONS FROM THIS SOURCE IN YOUR
 * PRODUCTS, REDISTRIBUTE THEM OR MAKE THEM PUBLIC IN ANY WAY. MAYBE EVEN
 * RESEARCH IS NOT ALLOWED. USE AT YOUR OWN RISK.
 * 
 * THIS API IS NOT SUPPORTED BY YAHOO IN ANY WAY AND IS NOT AFFILIATED WITH
 * YAHOO ALL TRADEMARKS AND BRANDS ARE THE PROPERTY OF THEIR RESPECTED OWNERS!
 * 

/**
 * retrieves historical data from yahoo<br>
 * <br>
 * <b>History:</b><br>
 *  - [03.12.2006] adding a bug report from greyform (Ulrich Staudinger)<br>
 *  - [04.12.2006] making the yahoo time series source (Ulrich Staudinger)<br>
 *  - [10.01.2007] added feedback from cronos (Ulrich Staudinger)<br>
 *  - [10.01.2007] MAJOR cleanup (Erik Nijkamp)<br>
 *  - [14.08.2007] adding array code (Ulrich Staudinger)<br>
 *  - [25.09.2007] Switch to InstrumentQuery etc. (Erik Nijkamp)<br>
 *  - [05.11.2007] Adding support for weekly data, removing system.out (Ulrich Staudinger)<br>
 *  - [09.11.2007] Moved to new data interfaces (Erik Nijkamp)<br>
 *  - [27.11.2007] Cleanup (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class YahooCandleSeriesSource extends CandleSeriesSourceBase {

	private final static Logger log = Logger.getLogger(YahooCandleSeriesSource.class);

	// Some symbol associations
	private final static String[][] ASSOCIATIONS = new String[][] { { Symbols.DAX.toString(), "^GDAXI" } };

	// Symbol map
	private static SymbolMap symbolMap = new SymbolMap(ASSOCIATIONS);

	// Yahoo URL
	private final static String YAHOO_URL = "http://table.finance.yahoo.com/table.csv?s={0}&a=01&b=01&c=1995" + "&d={1}&e={2}&f={3}&ignore=.csv&g={4}";

	// Yahoo date format
	private final static TimeStampFormat sdf = new TimeStampFormat("yyyy-MM-dd");
	static {
		sdf.setTimeZone(TimeZone.getDefault()); // FIXME: correct time zone?
	}

	public YahooCandleSeriesSource() {
	}

	@Override
	public SymbolMap getSymbolMap() {
		return symbolMap;
	}

	private static HashMap<String, CandleSeries> dataCache = new HashMap<String, CandleSeries>();

	/**
	 * actually fetches data from yahoo.
	 * 
	 * @param symbol
	 *            the yahoo identifier of your requested data, i.e. ^GDAXI
	 * @return CandleSeries
	 */
	private CandleSeries fetchEOD(SeriesSpecification spec) throws Exception {

		log.info("Fetching candles.");

		if (dataCache.get(spec.getInstrumentSpecification().getSymbol()) != null) {
			return dataCache.get(spec.getInstrumentSpecification().getSymbol().toString());
		}

		// parameters
		CandleSeries timeSeries = new CandleSeries(spec);
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);

		String resolution = "d";
		if (spec.getTimeFrame().equals(TimeFrame.TIMEFRAME_1_WEEK))
			resolution = "w";
		// open url
		String urlStr = MessageFormat.format(YAHOO_URL, spec.getInstrumentSpecification().getSymbol().toString(), 
				month, day, year, resolution);
		URL url = new URL(urlStr);
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line = reader.readLine();
		while ((line = reader.readLine()) != null) {
			log.debug(line);
			StringTokenizer str = new StringTokenizer(line, ",");
			String datestring = str.nextToken();
			// get data
			double open = Double.parseDouble(str.nextToken());
			double high = Double.parseDouble(str.nextToken());
			double low = Double.parseDouble(str.nextToken());
			double close = Double.parseDouble(str.nextToken());
			long volume = 0;
			if (str.hasMoreTokens())
				volume = Long.parseLong(str.nextToken());
			// Brutal workaround for date problems in backtesting,
			// we just let the price open on 9 o'clock so we add 9
			// hour to it.
			// Wessel
			TimeStamp date = sdf.parse(datestring);
			date = new TimeStamp(date.getNanoseconds() + 22 * 60 * 60 * 1000L * 1000000L);

			// new candle
			Candle candle = new Candle(spec.getInstrumentSpecification(),
					date, open, high, low, close, volume);
			candle.setTimeFrame(spec.getTimeFrame());
			timeSeries.add(candle);
		}

		log.info("Fetched " + timeSeries.size() + " candles.");

		// align dates and check
		alignDateOrder(timeSeries);

		dataCache.put(spec.getInstrumentSpecification().getSymbol().toString(), timeSeries);

		return timeSeries;
	}

	public CandleSeries fetch(SeriesSpecification query) throws Exception {
		if (!query.getTimeFrame().equals(TimeFrame.TIMEFRAME_1_DAY) && !query.getTimeFrame().equals(TimeFrame.TIMEFRAME_1_WEEK)) {
			// @TODO yahoo does support intraday data, too
			throw new NotImplementedException();
		}
		return fetchEOD(query);
	}

	public String getVendorName() {
		return "YAHOO";
	}
}
