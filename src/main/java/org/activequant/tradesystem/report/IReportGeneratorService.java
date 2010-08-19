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
package org.activequant.tradesystem.report;

import org.activequant.core.domainmodel.Market;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.Report;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [27.10.2006] Created for P1 (Erik Nijkamp)<br>
 *  - [23.05.2007] Created (Ulrich Staudinger)<br>
 *  - [09.06.2007] moved to new account model (Ulrich Staudinger)<br>
 *   
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 *  
 */
public interface IReportGeneratorService {
	
	public Report generateReport(Market market, Account account);
}
