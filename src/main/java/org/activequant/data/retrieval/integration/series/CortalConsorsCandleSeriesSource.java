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
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.types.TimeFrame;
import org.activequant.core.util.TimeStampFormat;
import org.activequant.data.retrieval.integration.CandleSeriesSourceBase;
import org.activequant.util.exceptions.NotImplementedException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

/**
 * 
 * Reimported from very old ccapi code and polished.<br>
 * <br>
 * <b>History:</b><br> 
 * - [07.11.2007] Created (Ulrich Staudinger)<br>
 * - [09.11.2007] Moved to new data interfaces (Erik Nijkamp)<br>
 * - [13.11.2007] Adding fetching of XID (Ulrich Staudinger)<br>
 * 
 * @author Ulrich Staudinger
 */
public class CortalConsorsCandleSeriesSource extends CandleSeriesSourceBase {

	private static final transient Logger log = Logger.getLogger(CortalConsorsCandleSeriesSource.class);
	private String xId; 
	
	public CortalConsorsCandleSeriesSource(){
		// 
	}
	
	/**
	 * this init method fetches an XID from consors before proceeding. 
	 */
	private void init() throws Exception {
		if(xId == null){
			// 
			String url = "https://www.cortalconsors.de/euroWebDe/-?$part=financeinfosHome.Desks.stocks.Desks.java_charts_popup_desk";
			HttpClient h = new HttpClient();
			GetMethod m = new GetMethod(url);
			h.executeMethod(m);
			BufferedReader br = new BufferedReader(new InputStreamReader(m.getResponseBodyAsStream()));
			String l = br.readLine();
			while(l!=null){
				if(l.indexOf("name=\"XID\"")!=-1){
					// 
					xId = l.substring(l.indexOf("value=")+"value=".length()+1);
					xId = xId.substring(0, xId.indexOf("\""));
				}
				l = br.readLine();
			}
		}
	}
	
	/**
	 * retrieves the history and returns a vector of candles. works very
	 * reliable. You must at first obtain the consors ISIN through a search (see
	 * search).
	 * 
	 * @param isin
	 *            this must be the consors isin ( differs from public isin ).
	 * @return vector of candles
	 */
	@SuppressWarnings("deprecation")
	protected CandleSeries fetchEod(SeriesSpecification seriesSpecification, String consorsid) throws Exception {
		// 
		assert(seriesSpecification!=null);
		init();
		// 
		CandleSeries ret = new CandleSeries();
		ret.setSeriesSpecification(seriesSpecification);

		URL url = new URL("http://mdgtools.is-teledata.com/interactive-charts/4.1/goo/internal/objects.csv?NR_REQUESTS=2&RID=1138615995540&XID="+xId);

		HttpClient h = new HttpClient();
		PostMethod m = new PostMethod(url.toString());

		m.setParameter("Accept-Encoding", "gzip");
		m.setParameter("JDMG-Version", "2.1.7");
		m.setParameter("Host", "mdgtools.is-teledata.com");
		m.setParameter("User-Agent", "Mozilla/4.0 (Linux 2.6.10-5-386) Java/1.5.0_05");
		m.setParameter("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*;  q=.2");
		m.setParameter("Connection", "keep-alive");
		m.setParameter("Content-type", "application/x-www-form-urlencoded");
		m.setParameter("Cookie", "");

		String span = "96M";

		m.setRequestContentLength(PostMethod.CONTENT_LENGTH_AUTO);

		String request = "/general/quote_history_list.csv?ID_NOTATION=" + consorsid + "&LANG=de&RANGE=" + span + "&RESOLUTION=D&VERSION=1&XID="+xId;
		m.setRequestBody(request);

		h.executeMethod(m);

		BufferedReader din = new BufferedReader(new InputStreamReader(m.getResponseBodyAsStream()));

		// simple date format. 
		TimeStampFormat sdf = new TimeStampFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getDefault()); // FIXME: correct timezone ?

		// read all line wise and append to ret.
		String l = din.readLine();
		boolean quotes = false;

		while (l != null) {

			if (quotes) {
				if (l.equals("#")) {
					break;
				} else if (l.startsWith("HTTP/1.1"))
					break;

				StringTokenizer str = new StringTokenizer(l, ";");
				Candle c = new Candle();

				// 
				String dateString = str.nextToken();
				c.setTimeStamp(sdf.parse(dateString));
				c.setHighTimeStamp(c.getTimeStamp());
				c.setLowTimeStamp(c.getTimeStamp());
				c.setInstrumentSpecification(seriesSpecification.getInstrumentSpecification());
				c.setTimeFrame(seriesSpecification.getTimeFrame());

				c.setOpenPrice(Double.parseDouble(str.nextToken()));
				c.setClosePrice(Double.parseDouble(str.nextToken()));
				c.setHighPrice(Double.parseDouble(str.nextToken()));
				c.setLowPrice(Double.parseDouble(str.nextToken()));

				c.setVolume(Double.parseDouble(str.nextToken()));

				ret.add(c);
			}

			if (l.startsWith("general/quote_history")) {
				quotes = true;
				din.readLine();
			}

			// l+= new String(l.getBytes("UTF-8"));
			l = din.readLine();
		}

		log.debug("ConsorsQuoteRetriever Read " + ret.size() + " candles");

		// am inverting the read lines. Today i am not so sure if this decision
		// has been so good when i did it years ago.
		CandleSeries r1 = new CandleSeries();
		for (int i = 0; i < ret.size(); i++) {
			r1.add(ret.get(ret.size() - 1 - i));
		}

		return r1;
	}

	@SuppressWarnings("deprecation")
	/**
	 * 
	 * @param isin
	 *            should be the isin of a symbol.
	 * @param type
	 *            type must be of STO, FUN, IND, BON, FUT, CER, WAR, CUR
	 * 
	 */
	protected String searchForConsorsId(String isin, String type, String exchange) throws Exception {
		init();
		URL url = new URL("http://mdgtools.is-teledata.com/interactive-charts/4.1/goo/internal/objects.csv?NR_REQUESTS=8&RID=1138617011094&XID="+xId);

		HttpClient h = new HttpClient();
		PostMethod m = new PostMethod(url.toString());

		// build header for this request.
		m.setParameter("Accept-Encoding", "gzip");
		m.setParameter("JDMG-Version", "2.1.7");
		m.setParameter("Host", "mdgtools.is-teledata.com");
		m.setParameter("User-Agent", "Mozilla/4.0 (Linux 2.6.10-5-386) Java/1.5.0_05");
		m.setParameter("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*;  q=.2");
		m.setParameter("Connection", "keep-alive");
		m.setParameter("Content-type", "application/x-www-form-urlencoded");
		m.setParameter("Cookie", "");

		m.setRequestContentLength(PostMethod.CONTENT_LENGTH_AUTO);

		// build the request body.
		String requestUrl = "/general/notation_search_list.csv?BLOCKSIZE=45&ID_TOOL=" + type + "&LANG=de&OFFSET=0&QUALITY=BST&QUOTES_REQUIRED=1&SEARCH_VALUE=" + isin
				+ "&VERSION=1&WITH_QUOTES_TYPE=VOLUME&XID="+xId;

		m.setRequestBody(requestUrl);
		h.executeMethod(m);

		BufferedReader din = new BufferedReader(new InputStreamReader(m.getResponseBodyAsStream()));

		// read all data line wise and append to ret.
		String l = din.readLine();
		// skip until body.
		while (l != null) {
			while (!l.trim().equals("")) {
				l = din.readLine();
			}
			if(l.trim().equals(""))break;
		}

		while (l != null) {
			log.info(l);

			String[] str = l.split(";");
			if (str.length > 5) {
				String id = str[0];

				if (str[str.length - 4].equals(exchange) || str[str.length - 3].equals(exchange)) {
					// ok. found.
					log.info("CHR: Returning " + id);
					return id.trim();
				}
			}

			l = din.readLine();
		}

		throw new Exception("Symbol not found.");

	}

	public CandleSeries fetch(SeriesSpecification seriesSpecification) throws Exception {
		init();
		if(!seriesSpecification.getTimeFrame().equals(TimeFrame.TIMEFRAME_1_DAY)){
			throw new NotImplementedException("Unsupported resolution");
		}
		
		// 
		String consorsId = this.searchForConsorsId(seriesSpecification.getInstrumentSpecification().getSymbol().toString(),
				seriesSpecification.getInstrumentSpecification().getSecurityType(), seriesSpecification.getInstrumentSpecification().getExchange());
		//
		assert(consorsId!=null);
		//
		return this.fetchEod(seriesSpecification, consorsId);
	}

	public String getVendorName() {
		return "IS-TELEDATA";
	}

}