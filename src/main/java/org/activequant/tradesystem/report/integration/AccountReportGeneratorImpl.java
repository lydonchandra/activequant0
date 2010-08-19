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
package org.activequant.tradesystem.report.integration;

import org.activequant.core.domainmodel.Market;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.BalanceBook;
import org.activequant.tradesystem.domainmodel.BalanceEntry;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.domainmodel.Report;
import org.activequant.tradesystem.report.IReportGeneratorService;


/**
 * creates a report from an account.
 * <br>
 * <b>History:</b><br>
 * - [23.05.2007] Created (Ulrich Staudinger)<br>
 * - [09.06.2007] moved to new account model (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class AccountReportGeneratorImpl implements IReportGeneratorService {

	public AccountReportGeneratorImpl() {
		super();
	}

	public double getHitRate(BalanceBook balance) {
		double total = 0;
		double correct = 0;
		for (BalanceEntry entry : balance.getBalanceEntries()) {
			if (entry.getValue() > 0) {
				correct++;
				total++;
			} else if (entry.getValue() < 0) {
				total++;
			}
		}
		return correct / total;
	}

	public String getBalanceDump(BalanceBook balance) {
		StringBuilder s = new StringBuilder();
		for (BalanceEntry entry : balance.getBalanceEntries()) {
			s.append(entry.getTimeStamp());
			s.append(" : ");
			s.append(entry.getValue());
			s.append("\n");
		}
		return s.toString();
	}

	public String getOrderDump(Order[] orders) {
		StringBuilder s = new StringBuilder();
		for (Order order : orders) {
			s.append(order.toString());
			s.append("\n");
		}
		return s.toString();
	}

	public Report generateReport(Market market, Account account) {
		Report report = new Report(market, account);

		// TODO polish (en)
		// - need to calculate sharpe ratio, etc. as in ccapi2.
		// - add generic reporting approach to ccapi3
	
		
		// TODO: RESTORE FUNCTIONALITY
		
		/*	report.getReportValues().put("Holder", account.getHolder());
		report.getReportValues().put("Biggest gain",
				account.getBalance().getBiggestGain());
		report.getReportValues().put("Biggest loss",
				account.getBalance().getBiggestLoss());
		report.getReportValues().put("Gain", account.getCurrentBalance());
		report.getReportValues().put("Orders placed",
				account.getOrders().length);
		report.getReportValues().put("Account Balance",
				getBalanceDump(account.getBalance()));
		report.getReportValues().put("Hitrate",
				getHitRate(account.getBalance()));
		// report.getReportValues().put("Orders",
		// getOrderDump(account.getOrders()));

		// add decos
		report.getReportValues().putAll(account.getDecorations());
*/
		// set

		return report;
	}
}
