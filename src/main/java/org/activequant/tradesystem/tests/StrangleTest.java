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

import java.util.HashMap;

import junit.framework.TestCase;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Quote;
import org.activequant.tradesystem.domainmodel.options.Strangle;
import org.activequant.tradesystem.types.OrderSide;
import org.junit.Test;


/**
 * test class for the butterfly combination.<br>
 * <br>
 * <b>History:</b><br>
 *  - [20.09.2007] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class StrangleTest extends TestCase {

	public Strangle getStrangle() throws Exception {
		InstrumentSpecification underlying = new InstrumentSpecification();
		
		InstrumentSpecification o1 = new InstrumentSpecification();
		o1.setStrike(100.0);
		o1.setContractRight("P");
		InstrumentSpecification o2 = new InstrumentSpecification();
		o2.setStrike(105.0);
		o2.setContractRight("C");
		Strangle strangle = new Strangle(underlying, o2, o1);
		return strangle; 
	}
	
	@Test
	public void testCashFlowOpenPosition() throws Exception {
		
		Strangle bf = getStrangle();
		
		HashMap<InstrumentSpecification, Quote> quotes = new HashMap<InstrumentSpecification, Quote>();
		
		Quote qU = new Quote(null, 1000, 0, 1001, 0);
		Quote q1 = new Quote(null, 1, 0, 2, 0);
		Quote q2 = new Quote(null, 1, 0, 2, 0);
		quotes.put(bf.getUnderlying(), qU);
		quotes.put(bf.getCall(), q1);
		quotes.put(bf.getPut(), q2);
		
		
		double profit = bf.cashFlowOpenPosition(quotes);
		System.out.println(profit);
		assertEquals(-4.0, profit);

		bf.setSide(OrderSide.BUY);
		profit = bf.cashFlowClosePosition(quotes);
		System.out.println(profit);
		assertEquals(2.0, profit);
		
	}

	@Test
	public void testCashFlowClosePosition() throws Exception {
		
		Strangle bf = getStrangle();
		
		HashMap<InstrumentSpecification, Quote> quotes = new HashMap<InstrumentSpecification, Quote>();
		
		Quote q1 = new Quote(null, 1, 0, 2, 0);
		Quote q2 = new Quote(null, 1, 0, 2, 0);
		
		quotes.put(bf.getCall(), q1);
		quotes.put(bf.getPut(), q2);
		
		
		double profit = bf.cashFlowClosePosition(quotes);
		System.out.println(profit);
		assertEquals(2.0, profit);
		
		bf.setSide(OrderSide.SELL);
		profit = bf.cashFlowClosePosition(quotes);
		System.out.println(profit);
		assertEquals(-4.0, profit);
		
	}

	@Test
	public void testCashFlowExpiry() throws Exception {
		
		Strangle bf = getStrangle();
		
		HashMap<InstrumentSpecification, Quote> quotes = new HashMap<InstrumentSpecification, Quote>();
		
		Quote qU = new Quote(null, 95, 0, 95, 0);
		Quote q1 = new Quote(null, 1, 0, 2, 0);
		Quote q2 = new Quote(null, 1, 0, 2, 0);
		quotes.put(bf.getUnderlying(), qU);
		quotes.put(bf.getCall(), q1);
		quotes.put(bf.getPut(), q2);
		
		double profit = bf.cashFlowAtExpiry(quotes);
		System.out.println(profit);
		assertEquals(5.0, profit);
		
		qU = new Quote(null, 105, 0, 105, 0);
		quotes.put(bf.getUnderlying(), qU);
		
		profit = bf.cashFlowAtExpiry(quotes);
		System.out.println(profit);
		assertEquals(0.0, profit);
		
		bf.setSide(OrderSide.SELL);
		profit = bf.cashFlowAtExpiry(quotes);
		System.out.println(profit);
		assertEquals(0.0, profit);
		
	}

	
}
