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
package org.activequant.tradesystem.domainmodel2;

import static org.activequant.util.tools.IdentityUtils.equalsTo;
import static org.activequant.util.tools.IdentityUtils.safeCompare;
import static org.activequant.util.tools.IdentityUtils.safeHashCode;

import org.activequant.tradesystem.domainmodel.BalanceBook;
import org.activequant.tradesystem.domainmodel.Portfolio;
import org.activequant.tradesystem.domainmodel.TransactionBook;

/**
 * subaccount holds all information for a specific broker or bank account. 
 * details on the documentation are available in the pdf/html documentation. 
 * <br>
 * <b>History:</b><br>
 *  - [09.06.2007] Created (Ulrich Staudinger)<br>
 *  - [14.06.2007] added transaction book (Ulrich Staudinger)<br>
 *  - [14.07.2007] Added persistence (Erik Nijkamp)<br>
 *  - [29.09.2007] Removed annotations (Erik Nijkamp)<br>
 *  - [16.12.2007] Cleanup (Mike Kroutikov)<br>
 *
 *  @author Ulrich Staudinger
 */
public class BrokerAccount implements Comparable<BrokerAccount> {
	
	private Long id;
	private String holder;

	private Portfolio portfolio = new Portfolio();
	private BalanceBook balanceBook = new BalanceBook();
	private OrderBook orderBook = new OrderBook();
	private TransactionBook transactionBook = new TransactionBook();
	
	public BrokerAccount() {
	}
	
	public BrokerAccount(String holder) {
		this.holder = holder; 
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public BalanceBook getBalanceBook() {
		return balanceBook;
	}

	public void setBalanceBook(BalanceBook balanceBook) {
		this.balanceBook = balanceBook;
	}

	public OrderBook getOrderBook() {
		return orderBook;
	}

	public void setOrderBook(OrderBook orderBook) {
		this.orderBook = orderBook;
	}
	
	public TransactionBook getTransactionBook() {
		return transactionBook;
	}

	public void setTransactionBook(TransactionBook transactionBook) {
		this.transactionBook = transactionBook;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public String getHolder() {
		return holder;
	}

	public void setHolder(String holder) {
		this.holder = holder;
	}
	
	public int compareTo(BrokerAccount other) {
		int rc;
		
		rc = safeCompare(holder, other.holder);
		if(rc != 0) return rc;
		
		return 0;
	}
	
	public int hashCode() {
		return safeHashCode(holder);
	}
	
	public boolean equals(Object o) {
		return equalsTo(this, o);
	}
}