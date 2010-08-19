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
import java.util.Iterator;
import java.util.List;

import org.activequant.core.types.TimeStamp;
import org.activequant.util.tools.Arrays;
import org.apache.log4j.Logger;


/**
 * holds balance entries for an account. <br>
 * <br>
 * <b>History:</b><br> 
 *  - [03.05.2007] Created (Ulrich Staudinger)<br> 
 *  - [06.05.2007] Cleanup (Erik Nijkamp)<br>
 *  - [08.06.2007] Renaming and extending (Ulrich)<br>
 *  - [14.07.2007] Added persistence (Erik Nijkamp)<br>
 *  - [29.09.2007] Removed annotations + balance (Erik Nijkamp)<br>
 * 
 * 
 * @author Ulrich Staudinger
 */
public class BalanceBook {
	
	protected final static Logger log = Logger.getLogger(BalanceBook.class);
	
	private Long id;

	private double currentBalance = 0.0;
	private List<BalanceEntry> balanceEntries = new ArrayList<BalanceEntry>();

	public BalanceBook() {

	}
	
	public BalanceBook(BalanceEntry... balanceEntries) {
		this.setBalanceEntries(balanceEntries);
	}
	
	public BalanceBook(double balance, BalanceEntry... balanceEntries) {
		this.currentBalance = balance;
		this.setBalanceEntries(balanceEntries);
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

	public void setCurrentBalance(double balance) {
		this.currentBalance = balance;
	}

	public double getCurrentBalance() {
		return currentBalance;
	}

	public void addBalanceEntry(double value, TimeStamp date) {
		this.currentBalance += value;
		// check if there is a balance entry for this specific date already. 
		for(BalanceEntry entry : balanceEntries){
			if(entry.getTimeStamp().equals(date)){
				entry.setValue(entry.getValue()+value);
				return;
			}
		}
		// still here, no existing entry found. 
		balanceEntries.add(new BalanceEntry(value, date));
	}

	public BalanceEntry[] getBalanceEntries() {
		return balanceEntries.toArray(new BalanceEntry[] {});
	}
	
	public void setBalanceEntries(List<BalanceEntry> balanceEntries) {
		this.balanceEntries = balanceEntries;
	}
	
	public void setBalanceEntries(BalanceEntry... balanceEntries) {
		this.balanceEntries = Arrays.asList(balanceEntries);
	}

	public Iterator<BalanceEntry> getBalanceEntryIterator(){
		return balanceEntries.iterator();
	}

	public double[] getEquityCurve() {
		assert (balanceEntries.size() > 0);
		double[] curve = new double[balanceEntries.size()];
		int idx = 0;
		for (BalanceEntry entry : balanceEntries) {
			curve[idx++] = entry.getValue();
		}
		return curve;
	}

	public BalanceEntry getBiggestGain() {
		assert (balanceEntries.size() > 0);
		BalanceEntry ret = balanceEntries.get(0);
		for (BalanceEntry t : balanceEntries) {
			if (t.getValue() > ret.getValue())
				ret = t;
		}
		return ret;
	}

	public BalanceEntry getBiggestLoss() {
		assert (balanceEntries.size() > 0);
		BalanceEntry ret = balanceEntries.get(0);
		for (BalanceEntry t : balanceEntries) {
			if (t.getValue() < ret.getValue())
				ret = t;
		}
		return ret;
	}
}
