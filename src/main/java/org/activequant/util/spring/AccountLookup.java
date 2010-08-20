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
package org.activequant.util.spring;

import org.activequant.dao.IAccountDao;
import org.activequant.tradesystem.domainmodel.Account;
import org.springframework.beans.factory.FactoryBean;




/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [04.08.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class AccountLookup implements FactoryBean {
	
	private IAccountDao accountDao = null;
	private long accountId = 0l;

	public Object getObject() throws Exception {
		assert(accountDao != null);
		return accountDao.find(accountId);
	}

	public Class<Account> getObjectType() {
		return Account.class;
	}

	public boolean isSingleton() {
		return false;
	}

	/**
	 * @return the accountId
	 */
	public long getAccountId() {
		return accountId;
	}

	/**
	 * @param accountId the accountId to set
	 */
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	/**
	 * @return the dao
	 */
	public IAccountDao getAccountDao() {
		return accountDao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setAccountDao(IAccountDao dao) {
		this.accountDao = dao;
	}
}
