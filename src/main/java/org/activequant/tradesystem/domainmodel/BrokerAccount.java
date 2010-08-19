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

import org.activequant.tradesystem.types.BrokerId;
import org.activequant.util.tools.DecorationsMap;


/**
 * subaccount holds all information for a specific broker or bank account. 
 * details on the documentation are available in the pdf/html documentation. 
 * <br>
 * <b>History:</b><br>
 *  - [09.06.2007] Created (Ulrich Staudinger)<br>
 *  - [14.06.2007] added transaction book (Ulrich Staudinger)<br>
 *  - [14.07.2007] Added persistence (Erik Nijkamp)<br>
 *  - [29.09.2007] Removed annotations (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 */
public class BrokerAccount implements Comparable<BrokerAccount> {
	
	private Long id;

	private BrokerId brokerID;
	private String holder;

	private Portfolio portfolio = new Portfolio();
	private BalanceBook balanceBook = new BalanceBook();
	private OrderBook orderBook = new OrderBook();
	private ExecutionBook executionBook = new ExecutionBook();
	private TransactionBook transactionBook = new TransactionBook();
	private DecorationsMap decorations = new DecorationsMap();
	
	public BrokerAccount() {
	}
	
	public BrokerAccount(TransactionBook transactionBook) {
		this.transactionBook = transactionBook; 
	}
	
	public BrokerAccount(BrokerId broker) {
		this.brokerID = broker;
		portfolio = new Portfolio();
		balanceBook = new BalanceBook();
		orderBook = new OrderBook();
		executionBook = new ExecutionBook();
	}

	public BrokerAccount(String holder, BrokerId broker) {
		this(broker);
		this.holder = holder; 
	}
	
	public BrokerAccount(String holder, BrokerId broker, TransactionBook transactionBook) {
		this(holder, broker);
		this.transactionBook = transactionBook; 
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public boolean hasId() {
		return id != null;
	}
	
	public BalanceBook getBalanceBook() {
		return balanceBook;
	}

	public void setBalanceBook(BalanceBook balanceBook) {
		this.balanceBook = balanceBook;
	}

	public BrokerId getBrokerID() {
		return brokerID;
	}

	public void setBrokerID(BrokerId broker) {
		this.brokerID = broker;
	}

	public ExecutionBook getExecutionBook() {
		return executionBook;
	}

	public void setExecutionBook(ExecutionBook executionBook) {
		this.executionBook = executionBook;
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

	public DecorationsMap getDecorations() {
		return decorations;
	}

	public void setDecorations(DecorationsMap decorations) {
		this.decorations = decorations;
	}
	
	public int compareTo(BrokerAccount other) {
		int rc;
		
		rc = safeCompare(brokerID, other.brokerID);
		if(rc != 0)	return rc;
		
		rc = safeCompare(holder, other.holder);
		if(rc != 0) return rc;
		
		return 0;
	}
	
	public int hashCode() {
		return safeHashCode(brokerID) + safeHashCode(holder);
	}
	
	public boolean equals(Object o) {
		return equalsTo(this, o);
	}
}