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
package org.activequant.tradesystem.system;

import org.activequant.core.domainmodel.Market;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.Order;
import org.jruby.RubyArray;


/**
 * Base class for ruby trade systems, all ruby trade systems should extend this class<br>
 * <br>
 * <b>History:</b><br> 
 * - [10.08.2007] Created. (Ulrich Staudinger) <br>
 * - [24.08.2007] Refactored (Erik Nijkamp)<br> 
 * 
 * @author Erik Nijkamp
 * @author Ulrich Staudinger
 */
public abstract class TradeSystemRubyBase extends TradeSystemBase {

	public abstract RubyArray evaluateMarket(Account account, Market market) throws Exception;

	public Order[] onMarket(Account account, Market market) throws Exception {
		RubyArray array = evaluateMarket(account, market);
		Order[] orders = new Order[array.size()];
		int i = 0;
		for (Object object : array) {
			orders[i++] = (Order) object;
		}
		return orders;
	}

}

