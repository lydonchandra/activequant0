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

import java.util.ArrayList;
import java.util.List;

import org.activequant.util.tools.Arrays;


/**
 * 
 * Stores all transactions. <br>
 * <br>
 * <b>History:</b><br>
 *  - [13.06.2007] Created (Ulrich Staudinger)<br>
 *  - [14.07.2007] Added persistence + Arrays (Erik Nijkamp)<br>
 *  - [29.09.2007] Removed annotations (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 */
public class TransactionBook {

	private Long id;
	private List<Transaction> transactions = new ArrayList<Transaction>();	

	public TransactionBook() {

	}
	
	public TransactionBook(Transaction... transactions) {
		setTransactions(transactions);
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

	public Transaction[] getTransactions() {
		return transactions.toArray(new Transaction[] {});
	}
	
	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}


	public void setTransactions(Transaction... transactions) {
		this.transactions = Arrays.asList(transactions);
	}
}
