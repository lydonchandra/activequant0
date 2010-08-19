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
package org.activequant.data.util;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.core.types.TimeFrame;
import org.activequant.core.types.TimeStamp;
import org.activequant.data.retrieval.ICandleSubscriptionSource;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.ITradeIndicationSubscriptionSource;
import org.activequant.data.retrieval.integration.SubscriptionSourceBase;
import org.activequant.util.pattern.events.IEventListener;

/**
 * Converts a candle realtime feed to trade indication realtime feed
 * by faking the price move from Open to High, Low, and Close. The generation
 * of trade indication is controlled by parameter {@link #getNumSteps() numSteps}
 * that determines how many trades are generated per segment. Generated price always 
 * moves from Open to High (segment one), then from High to Low (segment two), and
 * finally from Low to Close (segment three).
 * The default value for <code>numSteps</code> is 1, which makes the code to generate
 * four trade indication events per candle: at Open, at High, at Low, and at Close.
 * The date and quantity are linearly interpolated.
 * <br>
 * <b>History:</b><br>
 *  - [10.07.2007] Created (Ulrich Staudinger)<br>
 *  - [05.08.2007] Cleanup (Erik Nijkamp)<br>
 *  - [29.09.2007] cleanup + moved to new domain model (Erik Nijkamp)<br>
 *  - [29.09.2007] Refactored push/pull approach (Erik Nijkamp)<br>
 *  - [09.11.2007] Moved to subscription data model (Mike Kroutikov)<br>
 *
 *  @author Ulrich Staudinger
 */
public class CandleToTradeIndicationConverter extends SubscriptionSourceBase<TradeIndication> implements ITradeIndicationSubscriptionSource {

	private final ICandleSubscriptionSource candleSource; 
	private final TimeFrame timeFrame;
	
	/**
	 * Creates new converter from candle subscription source and time frame.
	 * 
	 * @param candleSource source of candle subscription.
	 * @param timeFrame determines what time frame to request from candle subscription source.
	 */
	public CandleToTradeIndicationConverter(ICandleSubscriptionSource candleSource, TimeFrame timeFrame) {
		super(candleSource.getVendorName());
		this.candleSource = candleSource;
		this.timeFrame = timeFrame;
	}

	private int numSteps = 1; // default is one trade per step

	/**
	 * Determines how many trades to generate per segment.
	 * Default value is 1, which means that only one trade per segment is
	 * generated.
	 * 
	 * @return number of steps;
	 */
	public int getNumSteps() {
		return numSteps;
	}

	/**
	 * Sets number of steps.
	 * 
	 * @param val number of steps.
	 */
	public void setNumSteps(int val) {
		if(val < 1) {
			throw new IllegalArgumentException("numSteps must be positive");
		}
		numSteps = val;
	}

	private class CandleSubscription extends Subscription {
		private final ISubscription<Candle> subscription;
		
		private final IEventListener<Candle> listener = new IEventListener<Candle>() {
			public void eventFired(Candle candle) throws Exception {
				for(TradeIndication ti : generateTradeIndicationsFromCandle(candle)) {
					try {
						fireEvent(ti);
					} catch(Exception ex) {
						log.error(ex);
						ex.printStackTrace();
					}
				}
			}
		};
		
		public CandleSubscription(InstrumentSpecification spec) throws Exception {
			subscription = candleSource.subscribe(spec, timeFrame);
		}

		@Override
		protected void handleActivate() throws Exception {
			subscription.addEventListener(listener);
			subscription.activate();
		}

		@Override
		protected void handleCancel() throws Exception {
			subscription.cancel();
			subscription.removeEventListener(listener);
		}
	}
	
	private List<TradeIndication> generateTradeIndicationsFromCandle(Candle candle) {

		List<TradeIndication> out = new LinkedList<TradeIndication>();
		
		TimeFrame frame = candle.getTimeFrame();
		if(frame == null) {
			log.warn("candle duration not set? will use global timeFrame");
			frame = timeFrame;
		}

		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.setTime(candle.getTimeStamp().getDate()); // candle's date is CLOSE date!
		calendar.add(Calendar.MILLISECOND, -1); // to make sure that align does move date backwards
		frame.alignCalendar(calendar);
		
		long timeIncrement = candle.getTimeStamp().getDate().getTime() - calendar.getTimeInMillis();
		
		timeIncrement /= (3 * numSteps + 1);
		if(timeIncrement == 0L) {
			// paranoid
			timeIncrement = 1L;
		}
		
		double quantity = candle.getVolume() / (3 * numSteps + 1);
		
		long timestamp = calendar.getTimeInMillis();

		double priceChange;

		// build segment from Open to High
		priceChange = candle.getHighPrice() - candle.getOpenPrice();
		for(int i  = 0; i < numSteps; i++) {
    		double price = candle.getOpenPrice() + (priceChange * i) / numSteps;
    		TimeStamp date = new TimeStamp(new Date(timestamp));
    		timestamp += timeIncrement;
    		
    		out.add(new TradeIndication(date, price, quantity));
		}
		
		// build segment from High to Low
		priceChange = candle.getLowPrice() - candle.getHighPrice();
		for(int i  = 0; i < numSteps; i++) {
    		double price = candle.getHighPrice() + (priceChange * i) / numSteps;
    		TimeStamp date = new TimeStamp(new Date(timestamp));
    		timestamp += timeIncrement;
    		
    		out.add(new TradeIndication(date, price, quantity));
		}

		// build segment from Low to Close
		priceChange = candle.getClosePrice() - candle.getLowPrice();
		for(int i  = 0; i < numSteps; i++) {
    		double price = candle.getLowPrice() + (priceChange * i) / numSteps;
    		TimeStamp date = new TimeStamp(new Date(timestamp));
    		timestamp += timeIncrement;
    		
    		out.add(new TradeIndication(date, price, quantity));
		}
		
		// add close point
		out.add(new TradeIndication(new TimeStamp(new Date(timestamp)), candle.getClosePrice(), quantity));

		log.info("one candle " + candle + " generated " + out.size() + " trade indications");

		return out;
	}

	@Override
	protected CandleSubscription createSubscription(
			InstrumentSpecification spec, TimeFrame timeFrame) {
		try {
			return new CandleSubscription(spec);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ISubscription<TradeIndication> subscribe(InstrumentSpecification spec)
			throws Exception {
		return super.subscribe(spec, TimeFrame.TIMEFRAME_1_TICK);
	}
}