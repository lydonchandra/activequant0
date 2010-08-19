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
package org.activequant.tradesystem.tests;

import java.util.Calendar;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.activequant.core.domainmodel.Market;
import org.activequant.core.domainmodel.Sample;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.types.Symbols;
import org.activequant.core.types.TimeFrame;
import org.activequant.core.util.TimeStampFormat;
import org.activequant.data.preparation.IChain;
import org.activequant.data.preparation.chains.Chain;
import org.activequant.data.preparation.filters.WeekdaysFilter;
import org.activequant.data.retrieval.ICandleSeriesSource;
import org.activequant.data.retrieval.integration.series.YahooCandleSeriesSource;
import org.activequant.tradesystem.market.IDataService;
import org.activequant.tradesystem.market.IMarketMonitorService;
import org.activequant.tradesystem.market.IPreparationService;
import org.activequant.tradesystem.market.integration.CandleDataService;
import org.activequant.tradesystem.market.integration.FilterPreparationService;
import org.activequant.tradesystem.market.integration.HistoricalMonitorService;
import org.activequant.util.pattern.events.IEventListener;
import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [05.06.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class HistoricalMonitorTest extends TestCase {
	
	private final static Logger log = Logger.getLogger(HistoricalMonitorTest.class);

	private final static TimeStampFormat format = new TimeStampFormat("dd.MM.yyyy");
	
	public static void main(String[] args) {
		try {
			junit.textui.TestRunner.run(suite());
		} catch(Throwable ex) {
			System.err.println("Throwable thrown: " + ex.getMessage());
		} finally {
			System.out.println("exit.");
		}
	}
	
	public static junit.framework.Test suite() {		
		return new TestSuite(HistoricalMonitorTest.class);
	}
	
	@Test
	public void testHistoricalMonitor() throws Exception {		
		SeriesSpecification spec = new SeriesSpecification(Symbols.MSFT, TimeFrame.TIMEFRAME_1_DAY);
		Sample sample = new Sample(format.parse("01.01.2000"), format.parse("01.02.2000"));
		ICandleSeriesSource source = new YahooCandleSeriesSource();
		IDataService data = new CandleDataService(source, spec);
		IMarketMonitorService monitor = new HistoricalMonitorService(sample, data);
		monitor.getNewMarketEvent().addEventListener(new IEventListener<Market>() {
			public void eventFired(Market event) throws Exception {
				printSample(event);
			}			
		});
		monitor.start();
	}
	
	private void printSample(Market market) {
		log.info("Sample start=" + format.format(market.getSample().getStartTimeStamp())
				+ " end=" + format.format(market.getSample().getEndTimeStamp()));
	}
	
	@Test
	public void testHistoricalMonitorWithPreparation() throws Exception {
		SeriesSpecification spec = new SeriesSpecification(Symbols.MSFT, TimeFrame.TIMEFRAME_1_DAY);
		Sample sample = new Sample(format.parse("01.01.2000"), format.parse("01.02.2000"));
		ICandleSeriesSource source = new YahooCandleSeriesSource();
		IDataService data = new CandleDataService(source, spec);
		IChain chain = new Chain(new WeekdaysFilter());
		IPreparationService preparation = new FilterPreparationService(chain);
		IMarketMonitorService monitor = new HistoricalMonitorService(preparation, sample, data);
		monitor.getNewMarketEvent().addEventListener(new IEventListener<Market>() {
			public void eventFired(Market event) throws Exception {
				printWeekday(event);
			}			
		});
		monitor.start();
	}	
	
	private void printWeekday(Market market) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(market.getCandleSeries()[0].firstElement().getTimeStamp().getDate());
		int weekday = calendar.get(Calendar.DAY_OF_WEEK);
		log.info("Weekday: " + weekdayToString(weekday));
	}
	
	private String weekdayToString(int weekday) {
		switch (weekday) {
		case Calendar.MONDAY:
			return "MONDAY";
		case Calendar.TUESDAY:
			return "TUESDAY";
		case Calendar.WEDNESDAY:
			return "WEDNESDAY";
		case Calendar.THURSDAY:
			return "THURSDAY";
		case Calendar.FRIDAY:
			return "FRIDAY";
		case Calendar.SATURDAY:
			return "SATURDAY";
		case Calendar.SUNDAY:
			return "SUNDAY";
		default:
			return "unknown";
		}
	}
}