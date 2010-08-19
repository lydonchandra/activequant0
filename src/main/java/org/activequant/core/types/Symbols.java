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
package org.activequant.core.types;

import org.activequant.core.domainmodel.Symbol;

/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [30.09.2007] Created (Erik Nijkamp)<br>
 *  - [30.09.2007] Added categories (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public interface Symbols {
	
    // forex
	Symbol EURUSD = new Symbol("EURUSD");
	Symbol GBPUSD = new Symbol("GBPUSD");
	Symbol USDJPY = new Symbol("USDJPY");
	Symbol USDCHF = new Symbol("USDCHF");
	Symbol USDCAD = new Symbol("USDCAD");
	Symbol AUDUSD = new Symbol("AUDUSD");
	Symbol EURJPY = new Symbol("EURJPY");
	Symbol EURCHF = new Symbol("EURCHF");
	Symbol EURGBP = new Symbol("EURGBP");
	Symbol EURCAD = new Symbol("EURCAD");
	Symbol GBPJPY = new Symbol("GBPJPY");
	Symbol GBPCHF = new Symbol("GBPCHF");
	Symbol EURUSDCROSS = new Symbol("EURUSDCROSS");
	Symbol GBPUSDCROSS = new Symbol("GBPUSDCROSS");
	Symbol USDCHFCROSS = new Symbol("USDCHFCROSS");
	Symbol CHFJPY = new Symbol("CHFJPY");
	Symbol NZDUSD = new Symbol("NZDUSD");
	Symbol USDZAR = new Symbol("USDZAR");
	Symbol USDNOK = new Symbol("USDNOK");
	Symbol EURNOK = new Symbol("EURNOK");
	Symbol USDSEK = new Symbol("USDSEK");
	Symbol EURSEK = new Symbol("EURSEK");
	Symbol USDMXN = new Symbol("USDMXN");
	Symbol USDINR = new Symbol("USDINR");
	Symbol GBPINR = new Symbol("GBPINR");
	Symbol USDRMB = new Symbol("USDRMB");
	
	// index
	Symbol DAX = new Symbol("DAX");
	Symbol SP500 = new Symbol("SP500");
	Symbol NASDAQ = new Symbol("NASDAQ");
	Symbol NIKKEI225 = new Symbol("NIKKEI225");
	Symbol CRB = new Symbol("CRB");
	Symbol CAC40 = new Symbol("CAC40");
	
	// bonds
	Symbol GERMANBUND = new Symbol("GERMANBUND");
	Symbol TNOTES = new Symbol("TNOTES");
	
	// commodity
	Symbol CRUDEOIL = new Symbol("CRUDEOIL");
	Symbol NATURALGAS = new Symbol("NATURALGAS");
	Symbol COFFEE = new Symbol("COFFEE");
	Symbol AMGEN = new Symbol("AMGEN");
	Symbol MERCK = new Symbol("MERCK");
	Symbol SOYBEANS = new Symbol("SOYBEANS");
	Symbol LUMBER = new Symbol("LUMBER");
	Symbol GOLD = new Symbol("GOLD");
	Symbol SILVER = new Symbol("SILVER");
	Symbol OIL = new Symbol("OIL");
	Symbol GAS = new Symbol("GAS");
	Symbol COPPER = new Symbol("COPPER");

	// stocks
	Symbol MSFT = new Symbol("MSFT");
	Symbol GOOG = new Symbol("GOOG");
	
	// others
	Symbol US30 = new Symbol("US30");
	Symbol US10 = new Symbol("US10");
}
