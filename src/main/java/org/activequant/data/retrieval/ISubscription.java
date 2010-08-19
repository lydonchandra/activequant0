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
package org.activequant.data.retrieval;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.MarketDataEntity;
import org.activequant.core.types.TimeFrame;
import org.activequant.util.exceptions.SubscriptionException;
import org.activequant.util.pattern.events.IEventListener;

/**
 * Interface that describes a subscription to live market data feed.
 * Subscription becomes active only after it is {@link #activate()}'d.
 * <p>
 * The life cycle of a subscription is:
 * <ul>
 *         <li> created (see parent interface {@link ISubscriptionSource}).
 *         <li> event listeners added
 *         <li> activated - only here actual subscription is sent to server and events may be generated.
 *         <li> canceled
 * </ul>
 * <br>
*/
public interface ISubscription<T extends MarketDataEntity> {
   
    /**
     * Returns instrument specification for this subscription.
     *
     * @return instrument specification.
     */
    public InstrumentSpecification getInstrumentSpecification();
   
    /**
     * Returns time frame for this subscription. For tick-based
     * events this must return {@link TimeFrame#TIMEFRAME_1_TICK}.
     * @return time frame.
     */
    public TimeFrame getTimeFrame();
   
    /**
     * Registers new event listener.
     *
     * @param listener event listener.
     */
    public void addEventListener(IEventListener<T> listener);
   
    /**
     * Removes event listener.
     *
     * @param listener event listener.
     */
    public void removeEventListener(IEventListener<T> listener);
   
    /**
     * Makes subscription active. No events may arrive before this
     * method is called.
     */
    public void activate() throws SubscriptionException;
   
    /**
     * Cancels subscription. This makes subscription object no longer usable.
     */
    public void cancel() throws SubscriptionException;
   
    /**
     * Checks whether this subscription is active or not. Subscription is not active
     * before first call to {@link #activate()}, and after first call to {@link #cancel()}.
     *
     * @return true if active.
     */
    public boolean isActive();
   
    /**
     * Returns vendor name of this subscription.
     *
     * @return vendor name.
     */
    public String getVendorName();
}