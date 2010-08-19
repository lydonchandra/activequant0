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
package org.activequant.tradesystem.evaluator.integration;

import org.activequant.core.domainmodel.Market;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.evaluator.IEvaluationService;


/**
 * Evaluation services are called after orders have been sent to a broker during 
 * a) backtesting or b) live trading of a trade system. <br>
 * <br>
 * <b>History:</b><br>
 *  - [05.06.2007] Created (Erik Nijkamp)<br>
 *  - [09.06.2007] moved to new account model (Ulrich Staudinger)<br>
 *  - [18.10.2007] Added doc (Ulrich Staudinger) <br>
 *
 *  @author Erik Nijkamp
 *  @author Ulrich Staudinger
 */
public class MockEvaluationService implements IEvaluationService {

	public Account evaluate(Account account, Order[] orders, Market market) throws Exception {
		return account;
	}

}
