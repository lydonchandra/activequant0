/**
 * 
 */
package org.activequant.data.retrieval.integration.opentick.test;

import java.util.Calendar;

import org.otfeed.IConnectionFactory;
import org.otfeed.OTConnectionFactory;
import org.otfeed.event.OTHost;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.types.Symbols;
import org.activequant.core.types.TimeFrame;
import org.activequant.core.types.TimeStamp;
import org.activequant.data.retrieval.integration.opentick.OpentickCandleSeriesSource;
import org.activequant.data.retrieval.integration.opentick.mock.MockOpentickConnectionFactory;

import org.junit.Test;


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
public class OpentickCandleSeriesSourceTest {

	// mpk: commented this test out since it requires OpenTick username/password
	// configuration (and therefore is not a unit test, but an integration test).
	// same is duplicated below but uses mock connection. This code left as 
	// a sample of how opentick adapter has to be configured.
	//
	//@Test
	public void integrationOpentickTest() throws Exception {		
		Calendar calendar = Calendar.getInstance();
		TimeStamp endDate = new TimeStamp(calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, -7);
		TimeStamp startDate = new TimeStamp(calendar.getTime());

		InstrumentSpecification spec = new InstrumentSpecification(Symbols.MSFT);
		spec.setVendor("OPENTICK");
		SeriesSpecification query = new SeriesSpecification(spec, TimeFrame.TIMEFRAME_1_DAY);
		query.setStartTimeStamp(startDate);
		query.setEndTimeStamp(endDate);
		
		OpentickCandleSeriesSource reader = new OpentickCandleSeriesSource();
		OTConnectionFactory factory = new OTConnectionFactory();
		
		String username = System.getProperty("ot.username");
		assert username != null : "You must configure system property 'ot.username' with Opentick account username";
		String password = System.getProperty("ot.password");
		assert password != null : "You must configure system property 'ot.password' with Opentick account password";
		factory.setUsername(username);
		factory.setPassword(password);
		factory.getHostList().add(new OTHost("feed1.opentick.com", 10015));
		reader.setConnectionFactory(factory);
		
        CandleSeries series = reader.fetch(query);
        System.out.println("Candles: " + series.size());
        for(Candle c : series.getCandles()) {
        	System.out.println(c);
        }
	}

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
		
		OpentickCandleSeriesSource reader = new OpentickCandleSeriesSource();
		IConnectionFactory factory = new MockOpentickConnectionFactory();
		reader.setConnectionFactory(factory);
		
        CandleSeries series = reader.fetch(query);
        System.out.println("Candles: " + series.size());
        for(Candle c : series.getCandles()) {
        	System.out.println(c);
        }
	}
}
