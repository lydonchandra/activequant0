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
package org.activequant.tradesystem.tests;

import junit.framework.TestCase;

import org.activequant.core.types.TimeStamp;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.BrokerAccount;
import org.junit.Test;


/**
 * Test class to match ... <br>
 * <br>
 * <b>History:</b><br>
 *  - [07.06.2007] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class AccountTest extends TestCase {

	@Test
	public void testBalanceBookAggregation(){
		Account full = new Account();
		BrokerAccount a = full.getBrokerAccount("account a");
		BrokerAccount b = full.getBrokerAccount("account b");
		
		a.getBalanceBook().setCurrentBalance(0);
		b.getBalanceBook().setCurrentBalance(0);
		
		assertEquals(0.0, full.getBalanceBook().getCurrentBalance());
		
		a.getBalanceBook().addBalanceEntry(10.0, new TimeStamp());
		assertEquals(10.0, full.getBalanceBook().getCurrentBalance());
		
		a.getBalanceBook().addBalanceEntry(10.0, new TimeStamp());
		assertEquals(20.0, full.getBalanceBook().getCurrentBalance());
		
		b.getBalanceBook().addBalanceEntry(10.0, new TimeStamp());
		assertEquals(30.0, full.getBalanceBook().getCurrentBalance());
	
		a.getBalanceBook().addBalanceEntry(-5, new TimeStamp());
		b.getBalanceBook().addBalanceEntry(5, new TimeStamp());
		assertEquals(30.0, full.getBalanceBook().getCurrentBalance());
		
	}	
}
