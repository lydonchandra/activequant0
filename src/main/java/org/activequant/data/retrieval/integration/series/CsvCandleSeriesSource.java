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
import java.util.Collections;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.types.TimeStamp;
import org.activequant.core.util.TimeStampFormat;
import org.activequant.data.retrieval.integration.CandleSeriesSourceBase;
import org.activequant.util.tools.CheckDateOrder;
import org.apache.log4j.Logger;


/**
 * csv reader. reads a list of candles from a csv file.<br>
 * <br>
 * <b>History:</b><br>
 * - [03.05.2007] Created (Ulrich Staudinger)<br>
 * - [07.05.2007] Polished (Erik Nijkamp)<br>
 * - [10.05.2007] Removed cryptic OHLC set, adding skip rows again, fixing(Ulrich Staudinger)<br>
 * - [11.08.2007] Added array functions (Erik Nijkamp)<br>
 * - [23.10.2007] Applying bug fix by neverfox (Ulrich Staudinger)<br>
 * - [09.11.2007] Moved to new data interfaces (Erik Nijkamp)<br>
 * - [27.11.2007] Cleanup (Ulrich Staudinger)
 *    
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public class CsvCandleSeriesSource extends CandleSeriesSourceBase {

    protected final static Logger log = Logger.getLogger(CsvCandleSeriesSource.class);
    
    private final static String DEFAULT_DELIMITER = ";";
    private final static String DEFAULT_DATE_FORMAT = "MM/dd/yyyy";

	private String delimiter = DEFAULT_DELIMITER;
	private String dateFormat = DEFAULT_DATE_FORMAT;
	private int skipRows = 0; 
    private int skipColumns = 0;

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
    
    public String getVendorName(){
        return "CCAPI CSV";
    }


    public int getSkipColumns() {
        return skipColumns;
    }


    public void setSkipColumns(int skipColumns) {
        this.skipColumns = skipColumns;
    }
    
	/**
	 * @return the dateFormat
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * @param dateFormat the dateFormat to set
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

    public int getSkipRows() {
        return skipRows;
    }


    public void setSkipRows(int skipRows) {
        this.skipRows = skipRows;
    }

    public CandleSeries fetch2(File file) throws Exception {
    	return this.fetch(file);
    }
    
	private CandleSeries fetch(File file) throws Exception {
		TimeStampFormat format = new TimeStampFormat(dateFormat);
		format.setTimeZone(TimeZone.getDefault());
		FileInputStream fin = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
		String line = "";
		CandleSeries timeSeries = new CandleSeries();
		HashMap<String, Candle> candles = new HashMap<String, Candle>();
		int row = 0;
        // skipping rows. i.e. header
        while(row<skipRows){
            line = reader.readLine();
            row++;
        }
        // working out the lines. 
		while ((line = reader.readLine()) != null) {
			// fix
			if (!delimiter.equals(","))
				line = line.replaceAll(",", ".");
			// tokenize
			StringTokenizer str = new StringTokenizer(line, delimiter);
			// candle
			Candle candle = new Candle();
			// skip
			for (int i = 0; i < skipColumns; i++) {
				str.nextToken();
			}
			// date
			String dateStr = str.nextToken();
            // need to make sure dateStr does start or end with quotes. 
            if(dateStr.startsWith("\""))dateStr = dateStr.substring(1);
            if(dateStr.endsWith("\""))dateStr = dateStr.substring(0, dateStr.length()-1);
            // finally parse the date. 
			TimeStamp date = format.parse(dateStr);
			candle.setTimeStamp(date);
            
            // setting open, hi and low. 
            candle.setOpenPrice(Double.parseDouble(str.nextToken()));
            candle.setHighPrice(Double.parseDouble(str.nextToken()));
            candle.setLowPrice(Double.parseDouble(str.nextToken()));
            candle.setClosePrice(Double.parseDouble(str.nextToken()));
            
			// volume
			if (str.hasMoreTokens()) {
				int volume = Integer.parseInt(str.nextToken());
				candle.setVolume(volume);
			}
			
			
			
			// check if we have a low date in the csv file. 
			if (str.hasMoreTokens()) {
				String lowDate = str.nextToken();
				
				// need to check if parsing works. 
				boolean isDate = true;
				try{
					format.parse(lowDate);
				}
				catch(Exception x){
					isDate = false; 
				}
				
				if(isDate){
					if (lowDate.indexOf(".") != -1) {
						// seems to be a yahoo source. Therefore this token may be ignored. 
						// need to check for next token.
						if (str.hasMoreTokens()) {
							lowDate = str.nextToken();
							candle.setLowTimeStamp(format.parse(lowDate));
						}
					} else {
						candle.setLowTimeStamp(format.parse(lowDate));
	                }
					// check if we have a hi date in the csv file. 
					if (str.hasMoreTokens()) {
						candle.setHighTimeStamp(format.parse(str.nextToken()));
					}
				}
			}
			
            // have to check if i have a duplicate candle.
            if (!candles.containsKey(candle.getTimeStamp().toString())) {
				timeSeries.add(0, candle);
				candles.put(candle.getTimeStamp().toString(), candle);
			}

		}

		// align dates and check
		alignDateOrder(timeSeries);		
		
		if( timeSeries.getSeriesSpecification() != null ) {
			log.debug("Loaded series "+timeSeries.getSeriesSpecification().toString());
		}
		return timeSeries;
	}
	
	protected void alignDateOrder(CandleSeries timeSeries) {
		// need to check if we need to reverse the order due to date stuff.  
		if (timeSeries.size() > 1 && CheckDateOrder.isOrderValid(timeSeries)) {
			Collections.reverse(timeSeries);
		}
		assert(CheckDateOrder.isOrderValid(timeSeries));
	}



	public CandleSeries fetch(SeriesSpecification instrumentQuery)
			throws Exception {
		return fetch(new File(instrumentQuery.getInstrumentSpecification().getSymbol().toString()));
	}
}