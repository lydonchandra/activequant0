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
package org.activequant.tradesystem.system.integration;

import org.activequant.core.domainmodel.Market;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.Execution;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.domainmodel.Portfolio;
import org.activequant.tradesystem.domainmodel.Position;
import org.activequant.tradesystem.system.TradeSystemBase;
import org.apache.log4j.Logger;

/**
 * plain trade system that just dumps out all account info it has.<br>
 * <br>
 * <b>History:</b><br>
 *  - [09.12.2007] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class AccountDumpingSystem extends TradeSystemBase {
	
	protected final static Logger log = Logger.getLogger(AccountDumpingSystem.class);
	
	
	public Order[] onMarket(Account account, Market market) throws Exception {
		dumpAccount(account);
		return null; 
	}
	
	@Override
	public Order[] onExecution(Account account, Execution execution, Order changedOrder) throws Exception {
		dumpAccount(account);
		return null; 
	}
	
	public void dumpAccount(Account account){
		assert(account!=null);
		// dump out portfolio
		log.info(" === Portfolio dump start === ");
		Portfolio p = account.getPortfolio();
		for(Position pos : p.getPositions()){
			log.info(pos.toString());
		}
		log.info(" === Portfolio dump end === ");

	}
	
	public String getDescription() {
		return "Dumps out all account data it can.";
	}

	public String getName() {
		return "AccountDumper";
	}
}
