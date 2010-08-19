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
package org.activequant.data.retrieval.integration.opentick.mock;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.otfeed.IConnection;
import org.otfeed.IConnectionFactory;
import org.otfeed.IRequest;
import org.otfeed.command.AggregationSpan;
import org.otfeed.command.HistDataCommand;
import org.otfeed.command.HistTicksCommand;
import org.otfeed.command.TickStreamCommand;
import org.otfeed.event.IConnectionStateListener;
import org.otfeed.event.IDataDelegate;
import org.otfeed.event.OTBBO;
import org.otfeed.event.OTError;
import org.otfeed.event.OTMMQuote;
import org.otfeed.event.OTOHLC;
import org.otfeed.event.OTQuote;
import org.otfeed.event.OTTrade;
import org.otfeed.event.TradeSideEnum;
import org.otfeed.protocol.ICommand;

import org.apache.log4j.Logger;

/**
 * Mock Opentick driver. Useful for unit and intergation testing.
 * 
 * @author Mike Kroutikov.
 */
public class MockOpentickConnectionFactory implements IConnectionFactory {
	
	private final Logger log = Logger.getLogger(getClass());
	
	public IConnection connect(IConnectionStateListener list) {
		if(list != null) {
			list.onConnected();
			list.onLogin();
		}
		return connection;
	}

	private final IConnection connection = new IConnection() {

		public void runInEventThread(Runnable arg0) {
			arg0.run();
		}

		public void shutdown() {
		}

		public void waitForCompletion() {
		}

		public boolean waitForCompletion(long arg0) {
			return true;
		}
		
		public IRequest prepareRequest(final ICommand command) {
			
			return new IRequest() {

				public void cancel() {
				}

				public OTError getError() {
					return null;
				}

				public boolean isCompleted() {
					return true;
				}
				
				private final AtomicBoolean isRunning = new AtomicBoolean(true);

				public void submit() {
					(new Thread() {
						public void run() {
							generateMockEvents(command);

							isRunning.set(false);
							synchronized(isRunning) {
								isRunning.notifyAll();
							}
						}
					}).start();
				}

				public void waitForCompletion() {
					synchronized(isRunning) {
						while(isRunning.get()) {
							try {
								isRunning.wait();
							} catch(InterruptedException ex) {
							}
						}
					}
				}

				public boolean waitForCompletion(long arg0) {
					throw new UnsupportedOperationException();
				}
			};
		}

		private void generateMockEvents(ICommand command) {
			log.info("generating mock events for command: " + command);

			if(command instanceof TickStreamCommand) {
				TickStreamCommand c = (TickStreamCommand) command;
				generateMockTicks(c.getSymbolCode(), new Date(), 
						c.getQuoteDelegate(), 
						c.getMmQuoteDelegate(), 
						c.getTradeDelegate(), 
						c.getBboDelegate());
			} else if(command instanceof HistTicksCommand) {
				HistTicksCommand c = (HistTicksCommand) command;
				generateMockTicks(c.getSymbolCode(), c.getStartDate(),
						c.getQuoteDelegate(), 
						c.getMmQuoteDelegate(), 
						c.getTradeDelegate(), 
						c.getBboDelegate());
			} else if(command instanceof HistDataCommand) {
				generateMockHistData((HistDataCommand) command);
			} else {
				throw new UnsupportedOperationException("mock service does not (yet) support this type of command");
			}
		}
		
		private void generateMockTicks(
				String symbolCode,
				Date timestamp,
				IDataDelegate<OTQuote> quoteDelegate,
				IDataDelegate<OTMMQuote> mmQuoteDelegate,
				IDataDelegate<OTTrade> tradeDelegate,
				IDataDelegate<OTBBO> bboDelegate) {
			
			if(quoteDelegate != null) {
				
				OTQuote quote = new OTQuote();
				quote.setTimestamp(timestamp);
					
				quote.setAskPrice(100.);
				quote.setAskSize(10);
				quote.setBidPrice(99.);
				quote.setBidSize(20);
				quote.setSymbol(symbolCode);

				quoteDelegate.onData(quote);
			}
			
			if(mmQuoteDelegate != null) {
				
				OTMMQuote quote = new OTMMQuote();
				quote.setTimestamp(timestamp);
					
				quote.setAskPrice(100.5);
				quote.setAskSize(101);
				quote.setBidPrice(98.5);
				quote.setBidSize(203);
				quote.setSymbol(symbolCode);

				mmQuoteDelegate.onData(quote);
			}
			
			if(tradeDelegate != null) {

				OTTrade trade = new OTTrade();
				trade.setTimestamp(timestamp);
				
				trade.setPrice(99.85);
				trade.setSize(5);
				trade.setSymbol(symbolCode);
				
				tradeDelegate.onData(trade);
			}
			
			if(bboDelegate != null) {
				
				OTBBO bbo = new OTBBO();
				bbo.setTimestamp(timestamp);
				
				bbo.setPrice(99.7);
				bbo.setSize(20);
				bbo.setSide(TradeSideEnum.BUYER);
				bbo.setSymbol(symbolCode);
				
				bboDelegate.onData(bbo);
				
				bbo = new OTBBO();
				bbo.setTimestamp(timestamp);
				
				bbo.setPrice(100.3);
				bbo.setSize(10);
				bbo.setSide(TradeSideEnum.SELLER);
				bbo.setSymbol(symbolCode);
				
				bboDelegate.onData(bbo);
			}
		}
		
		private final void incrementCalendar(Calendar cal, AggregationSpan span) {
			
			switch(span.units) {
			case TICKS: // assume one tick per second rate
				cal.add(Calendar.SECOND, span.length);
				break;
			case MINUTES:
				cal.add(Calendar.MINUTE, span.length);
				break;
			case HOURS:
				cal.add(Calendar.HOUR_OF_DAY, span.length);
				break;
			case DAYS:
				cal.add(Calendar.DAY_OF_YEAR, span.length);
				break;
			case WEEKS:
				cal.add(Calendar.WEEK_OF_YEAR, span.length);
				break;
			case MONTHS:
				cal.add(Calendar.MONTH, span.length);
				break;
			case YEARS:
				cal.add(Calendar.YEAR, span.length);
				break;
			default:
				throw new IllegalArgumentException("unexpected");
			}
		}
		
		private void generateMockHistData(HistDataCommand command) {
			IDataDelegate<OTOHLC> delegate = command.getDataDelegate();
			Date startDate = command.getStartDate();
			Date endDate = command.getEndDate();
			
			AggregationSpan span = command.getAggregationSpan();
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			
			long volume = 100;
			while(calendar.getTime().compareTo(endDate) < 0) {
				
				OTOHLC ohlc = new OTOHLC();
				ohlc.setTimestamp(calendar.getTime());

				ohlc.setHighPrice(100.5);
				ohlc.setOpenPrice(99.85);
				ohlc.setLowPrice(99.02);
				ohlc.setClosePrice(99.89);
				ohlc.setVolume(volume);
				volume += 10;
				
				delegate.onData(ohlc);
				
				incrementCalendar(calendar, span);
			}
		}
	};
}
