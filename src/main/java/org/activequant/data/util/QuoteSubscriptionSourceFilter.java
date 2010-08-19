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

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Quote;
import org.activequant.core.types.TimeFrame;
import org.activequant.data.retrieval.IQuoteSubscriptionSource;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.filtering.IDataFilter;
import org.apache.log4j.Logger;

/**
 * Generic filter of live quotes. Uses pluggable strategy to implement 
 * filtering decisions.
 *       <b>History:</b><br>
 * - [04.12.2007] Created (Mike Kroutikov)<br>
 * 
 * @author Mike Kroutikov
 */
public class QuoteSubscriptionSourceFilter extends SubscriptionSourceFilterBase<Quote> implements IQuoteSubscriptionSource {
	
	protected final Logger log = Logger.getLogger(getClass());
	
	private IQuoteSubscriptionSource quoteSubscriptionSource;
	
	public QuoteSubscriptionSourceFilter() {
		super("QUOTEFILTER");
	}
	
	public QuoteSubscriptionSourceFilter(IQuoteSubscriptionSource quoteSubscriptionSource,
			IDataFilter<Quote> filter) {
		this();
		this.quoteSubscriptionSource = quoteSubscriptionSource;
		super.setDataFilter(filter);
	}

	public IQuoteSubscriptionSource getQuoteSubscriptionSource() {
		return quoteSubscriptionSource;
	}
	public void setQuoteSubscriptionSource(IQuoteSubscriptionSource val) {
		quoteSubscriptionSource = val;
	}

	@Override
	protected ISubscription<Quote> openSubscription(
			InstrumentSpecification spec, TimeFrame timeFrame) throws Exception {
		return quoteSubscriptionSource.subscribe(spec);
	}

	public ISubscription<Quote> subscribe(InstrumentSpecification spec)
			throws Exception {
		return super.subscribe(spec, TimeFrame.TIMEFRAME_1_TICK);
	}
}