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

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.types.TimeFrame;
import org.activequant.data.retrieval.ICandleSubscriptionSource;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.filtering.IDataFilter;
import org.apache.log4j.Logger;

/**
 * Generic filter of live candles. Uses pluggable strategy to implement 
 * filtering decisions.
 *       <b>History:</b><br>
 * - [04.12.2007] Created (Mike Kroutikov)<br>
 * 
 * @author Mike Kroutikov
 */
public class CandleSubscriptionSourceFilter extends SubscriptionSourceFilterBase<Candle> implements ICandleSubscriptionSource {
	
	protected final Logger log = Logger.getLogger(getClass());
	
	private ICandleSubscriptionSource candleSubscriptionSource;
	
	public CandleSubscriptionSourceFilter() {
		super("CANDLEFILTER");
	}
	
	public CandleSubscriptionSourceFilter(ICandleSubscriptionSource candleSubscriptionSource,
			IDataFilter<Candle> filter) {
		this();
		this.candleSubscriptionSource = candleSubscriptionSource;
		super.setDataFilter(filter);
	}

	public ICandleSubscriptionSource getCandleSubscriptionSource() {
		return candleSubscriptionSource;
	}
	public void setCandleSubscriptionSource(ICandleSubscriptionSource val) {
		candleSubscriptionSource = val;
	}

	@Override
	protected ISubscription<Candle> openSubscription(
			InstrumentSpecification spec, TimeFrame timeFrame) throws Exception {
		return candleSubscriptionSource.subscribe(spec, timeFrame);
	}

}