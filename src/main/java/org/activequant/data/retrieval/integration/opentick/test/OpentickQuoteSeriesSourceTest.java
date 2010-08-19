/**
 * 
 */
package org.activequant.data.retrieval.integration.opentick.test;

import java.util.Calendar;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Quote;
import org.activequant.core.domainmodel.QuoteSeries;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.types.Symbols;
import org.activequant.core.types.TimeFrame;
import org.activequant.core.types.TimeStamp;
import org.activequant.data.retrieval.integration.opentick.OpentickQuoteSeriesSource;
import org.activequant.data.retrieval.integration.opentick.mock.MockOpentickConnectionFactory;

import org.junit.Test;
import org.otfeed.IConnectionFactory;

/**
 * 
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - ? Created (Mike Kroutikov)<br>
 *  - [18.10.2007] Fixing to final domain model (Ulrich Staudinger)<br>
 *
 *  @author Mike Kroutikov
 */
public class OpentickQuoteSeriesSourceTest {

	@Test
	public void testOpentickUsingMockFactory() throws Exception {		
		Calendar calendar = Calendar.getInstance();
		TimeStamp endDate = new TimeStamp(calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, -7);
		TimeStamp startDate = new TimeStamp(calendar.getTime());

		InstrumentSpecification spec = new InstrumentSpecification(Symbols.MSFT);
		spec.setVendor("OPENTICK");
		SeriesSpecification query = new SeriesSpecification(spec, TimeFrame.TIMEFRAME_1_DAY);	
		query.setStartTimeStamp(startDate);
		query.setEndTimeStamp(endDate);
		
		OpentickQuoteSeriesSource reader = new OpentickQuoteSeriesSource();
		IConnectionFactory factory = new MockOpentickConnectionFactory();
		reader.setConnectionFactory(factory);
		
        QuoteSeries series = reader.fetch(query);
        System.out.println("Series size: " + series.size());
        for(Quote c : series.getQuotes()) {
        	System.out.println(c);
        }
	}
}
