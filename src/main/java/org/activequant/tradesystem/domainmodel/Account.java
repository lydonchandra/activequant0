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
package org.activequant.tradesystem.domainmodel;

import static org.activequant.util.tools.IdentityUtils.equalsTo;
import static org.activequant.util.tools.IdentityUtils.safeCompare;
import static org.activequant.util.tools.IdentityUtils.safeHashCode;

import java.util.ArrayList;
import java.util.List;

import org.activequant.tradesystem.types.BrokerId;
import org.activequant.util.tools.Arrays;



/**
 *  @TODO desc <br>
 * <br>
 * <b>History:</b><br>
 *  - [04.05.2006] Created (us)<br>
 *  - [05.05.2006] Refactored balance (en)<br>
 *  - [08.06.2007] Adding subaccount concept. (us)<br>
 *  - [11.06.2007] Some upgrades, removed "aggregated" postfix (en)<br>
 *  - [11.06.2007] Added find, has, getBrokerAccount (en)<br>  
 *  - [14.06.2007] added transaction book concept (us) <br>
 *  - [27.10.2007] Added proper object state code (Erik Nijkamp)<br>
 *  
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public class Account implements Comparable<Account> {

	private Long id;

	private String holder;

	private List<BrokerAccount> brokerAccounts = new ArrayList<BrokerAccount>();

	public Account(String holder) {
		this.holder = holder;
	}

	public Account(long id){
		this.id = id; 
	}
	
	public Account() {

	}

	public Account(long id, String holder) {
		this.id = id;
		this.holder = holder;
	}
	
	public Account(String holder, BrokerAccount...accounts) {
		this.holder = holder;
		this.setBrokerAccounts(accounts);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Portfolio getPortfolio() {
		if (brokerAccounts.size() > 0) {
			// TODO need to aggregate accounts if n > 1 (en)
			return brokerAccounts.iterator().next().getPortfolio();
		}
		return new Portfolio();
	}

	public BalanceBook getBalanceBook() {
		if (brokerAccounts.size() > 0) {
			BalanceBook ret = new BalanceBook();
			for(BrokerAccount brokerAccount : brokerAccounts){
				for(BalanceEntry be : brokerAccount.getBalanceBook().getBalanceEntries()){
					ret.addBalanceEntry(be.getValue(), be.getTimeStamp());
				}
			}
			return ret; 
		}
		return new BalanceBook();
	}

	public OrderBook getOrderBook() {
		// TODO need to aggregate accounts if n > 1 (en)
		if (brokerAccounts.size() > 0) {
			return brokerAccounts.iterator().next().getOrderBook();
		}
		return new OrderBook();
	}

	public ExecutionBook getExecutionBook() {
		if (brokerAccounts.size() > 0) {

			// TODO need to aggregate accounts if n > 1 (en)
			return brokerAccounts.iterator().next().getExecutionBook();
		}
		return new ExecutionBook();
	}

	public TransactionBook getTransactionBook() {
		if (brokerAccounts.size() > 0) {

			// TODO need to aggregate accounts if n > 1 (en)
			return brokerAccounts.iterator().next().getTransactionBook();
		}
		return new TransactionBook();

	}

	public String getHolder() {
		return holder;
	}

	public void setHolder(String holder) {
		this.holder = holder;
	}

	public BrokerAccount[] getBrokerAccounts() {
		return brokerAccounts.toArray(new BrokerAccount[] {});
	}

	public void setBrokerAccounts(BrokerAccount... accounts) {
		this.brokerAccounts = Arrays.asList(accounts);
	}
	
	public void setBrokerAccounts(List<BrokerAccount> accounts) {
		this.brokerAccounts = accounts;
	}

	public void addBrokerAccount(BrokerAccount account) {
		brokerAccounts.add(account);
	}

	public void removeBrokerAccount(BrokerAccount account) {
		brokerAccounts.remove(account);
	}
	
	/**
	 * Is there an account with the specified id?
	 * 
	 * @param broker
	 * @return
	 */
	public boolean hasBrokerAccount(BrokerId broker) {
		for (BrokerAccount account : brokerAccounts) {
			if (account.getBrokerID().equals(broker))
				return true;
		}
		return false;
	}

	/**
	 * Find an existing account
	 * 
	 * @param broker
	 * @return
	 */
	public BrokerAccount findBrokerAccount(BrokerId broker) {
		assert (!brokerAccounts.isEmpty());
		// iterate over the existing accounts and check if we find it. 
		for (BrokerAccount account : brokerAccounts) {
			if (account.getBrokerID().equals(broker))
				return account;
		}
		throw new IllegalArgumentException("Cannot find broker account for '"
				+ broker + "'.");
	}

	/**
	 * Get an existing account or create a new one if needed
	 * 
	 * @param broker
	 * @return
	 */
	public BrokerAccount getBrokerAccount(BrokerId broker) {
		if (hasBrokerAccount(broker)) {
			return findBrokerAccount(broker);
		} else {
			// creating a new broker account.
			BrokerAccount account = new BrokerAccount(broker);
			addBrokerAccount(account);
			return account;
		}
	}
	
	public BrokerAccount getBrokerAccount(String subAccountId) {
		// create a new broker id. 
		BrokerId brokerId = new BrokerId(subAccountId);
		return getBrokerAccount(brokerId);
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		// ATTENTION: keep in sync with compareTo()
		return safeHashCode(this.holder);
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(Account other) {
		// ATTENTION: keep in sync with hashCode();
		int rc;
		
		// TODO broker accounts (en)
		
		rc = safeCompare(this.holder, other.holder);
		if(rc != 0) return rc;
		
		return rc;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object other) {
		// NOTE: delegates to compareTo()
		return equalsTo(this, other);
	}

	public String toString() {
		return "id=" + id + ";holder=" + holder;
	}

}