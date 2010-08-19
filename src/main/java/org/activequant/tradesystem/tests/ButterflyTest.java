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

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Quote;
import org.activequant.tradesystem.domainmodel.options.Butterfly;
import org.junit.Test;


/**
 * test class for the butterfly combination.<br>
 * <br>
 * <b>History:</b><br>
 *  - [20.09.2007] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class ButterflyTest {
	
	private static final double EPSILON = 0.00001; // accuracy for double comparison

	public Butterfly getButterfly(){
		InstrumentSpecification underlying = new InstrumentSpecification();
		
		InstrumentSpecification o1 = new InstrumentSpecification();
		o1.setStrike(95.0);
		InstrumentSpecification o2 = new InstrumentSpecification();
		o2.setStrike(100.0);
		InstrumentSpecification o3 = new InstrumentSpecification();
		o3.setStrike(105.0);
		
		Butterfly bf = new Butterfly(true,o1,o2,o3, underlying);
		return bf; 
	}
	
	@Test
	public void testCashFlowOpenPosition() throws Exception {
		
		Butterfly bf = getButterfly();
		
		HashMap<InstrumentSpecification, Quote> quotes = new HashMap<InstrumentSpecification, Quote>();
		
		Quote qU = new Quote(null, 1000, 0, 1001, 0);
		Quote q1 = new Quote(null, 1, 0, 2, 0);
		Quote q2 = new Quote(null, 1, 0, 2, 0);
		Quote q3 = new Quote(null, 1, 0, 2, 0);
		quotes.put(bf.getUnderlyingInstrument(), qU);
		quotes.put(bf.getLowerOption(), q1);
		quotes.put(bf.getCenteredOption(), q2);
		quotes.put(bf.getUpperOption(), q3);
		
		
		double profit = bf.cashFlowOpenPosition(quotes);
		System.out.println(profit);
		assertEquals(-2.0, profit, EPSILON);
	}

	@Test
	public void testCashFlowClosePosition() throws Exception {
		Butterfly bf = getButterfly();
		
		HashMap<InstrumentSpecification, Quote> quotes = new HashMap<InstrumentSpecification, Quote>();
		
		Quote qU = new Quote(null, 1000, 0, 1001, 0);
		Quote q1 = new Quote(null, 1, 0, 2, 0);
		Quote q2 = new Quote(null, 1, 0, 2, 0);
		Quote q3 = new Quote(null, 1, 0, 2, 0);
		quotes.put(bf.getUnderlyingInstrument(), qU);
		quotes.put(bf.getLowerOption(), q1);
		quotes.put(bf.getCenteredOption(), q2);
		quotes.put(bf.getUpperOption(), q3);
		
		
		double profit = bf.cashFlowClosePosition(quotes);
		System.out.println(profit);
		assertEquals(-2.0, profit, EPSILON);
	}

	@Test
	public void testCashFlowAtExpiry() throws Exception  {
		Butterfly bf = getButterfly();
		
		HashMap<InstrumentSpecification, Quote> quotes = new HashMap<InstrumentSpecification, Quote>();
		
		Quote qU = new Quote(null, 1000, 0, 1001, 0);
		Quote q1 = new Quote(null, 1, 0, 2, 0);
		Quote q2 = new Quote(null, 1, 0, 2, 0);
		Quote q3 = new Quote(null, 1, 0, 2, 0);
		quotes.put(bf.getUnderlyingInstrument(), qU);
		quotes.put(bf.getLowerOption(), q1);
		quotes.put(bf.getCenteredOption(), q2);
		quotes.put(bf.getUpperOption(), q3);
		
		
		double profit = bf.cashFlowAtExpiry(quotes);
		System.out.println(profit);
		assertEquals(0.0, profit, EPSILON);
		
		
		qU = new Quote(null, 1, 0, 2, 0);
		quotes.put(bf.getUnderlyingInstrument(), qU);
		profit = bf.cashFlowAtExpiry(quotes);
		System.out.println(profit);
		assertEquals(0.0, profit, EPSILON);
		
		
		qU = new Quote(null, 100, 0, 100, 0);
		quotes.put(bf.getUnderlyingInstrument(), qU);
		profit = bf.cashFlowAtExpiry(quotes);
		System.out.println(profit);
		assertEquals(5.0, profit, EPSILON);		
	}

}