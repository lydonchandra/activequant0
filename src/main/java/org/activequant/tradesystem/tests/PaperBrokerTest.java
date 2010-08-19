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
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Quote;
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.core.types.Symbols;
import org.activequant.data.retrieval.IQuoteSubscriptionSource;
import org.activequant.data.retrieval.mock.MockQuoteSubscriptionSource;
import org.activequant.data.retrieval.mock.MockTradeIndicationSubscriptionSource;
import org.activequant.data.util.QuoteMergingSubscriptionSource;
import org.activequant.data.util.TradeIndicationToQuoteSubscriptionSourceConverter;
import org.activequant.tradesystem.broker.integration.PaperBroker;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.Execution;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.types.OrderSide;
import org.activequant.tradesystem.types.OrderType;
import org.activequant.util.pattern.events.IEventListener;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for paper broker.<br>
 * Also tests QuoteMergingSubscriptionSource and TradeIndicationToSubscriptionSourceConverter.
 * <br>
 * <b>History:</b><br>
 *  - [20.09.2007] Created (Ulrich Staudinger)<br>
 *  - [10.11.2007] Added Subscription sources (Mke Kroutikov)<br>
 *
 *  @author Ulrich Staudinger
 *  @author Mike Kroutikov
 */
public class PaperBrokerTest {
	
	// precision of comparisons for doubles
	private static final double EPSILON = 0.00001;

	private final MockTradeIndicationSubscriptionSource mockTradeSource = new MockTradeIndicationSubscriptionSource();
	private final TradeIndicationToQuoteSubscriptionSourceConverter quoteSource = new TradeIndicationToQuoteSubscriptionSourceConverter(mockTradeSource);
	private final MockQuoteSubscriptionSource mockQuoteSource = new MockQuoteSubscriptionSource();
	private final QuoteMergingSubscriptionSource source = new QuoteMergingSubscriptionSource();
	{
		source.setSources(new IQuoteSubscriptionSource [] { quoteSource, mockQuoteSource});
	}

	private final Account account = new Account();
	private final PaperBroker pb = new PaperBroker(account, source);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("Setup class.");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("Tear down class.");
	}
	
	@Test
	public void testPlain() throws Exception {
		assertTrue(pb.getBrokerAccount().getBalanceBook().getBalanceEntries().length == 0); 
		assertTrue(pb.getBrokerAccount().getPortfolio().getPositions().length == 0);
		assertTrue(pb.getBrokerAccount().getExecutionBook().getExecutions().length == 0);
		assertTrue(pb.getBrokerAccount().getOrderBook().getOrders().length == 0);
	}
	
	@Test
	public void testOrderWorkout() throws Exception {
		System.out.println("Testing order processing");
		
		Order o = new Order();
		InstrumentSpecification cs = new InstrumentSpecification();
		cs.setSymbol(Symbols.AMGEN);
		cs.setExchange("DTB");
		cs.setCurrency("EUR");
		
		o.setInstrumentSpecification(cs);
		o.setSide(OrderSide.BUY);
		o.setType(OrderType.MARKET);
		o.setQuantity(100);
		
		// 
		pb.placeOrder(o);
		
		// 
		assertTrue(pb.getBrokerAccount().getOrderBook().getOrders().length == 1);
		assertTrue(pb.getBrokerAccount().getOrderBook().getOpenOrders().length == 1);
		assertTrue(pb.getBrokerAccount().getExecutionBook().getExecutions().length == 0);
	
		// 
		
		TradeIndication q = new TradeIndication(cs, 100.0);
		q.setQuantity(100);
		mockTradeSource.fireEvent(q);
		
		assertTrue(pb.getBrokerAccount().getOrderBook().getOrders().length == 1);
		assertTrue(pb.getBrokerAccount().getOrderBook().getOpenOrders().length == 0);
		assertEquals(pb.getBrokerAccount().getOrderBook().getOrders()[0].getQuantity(), 100.0, EPSILON);
		assertEquals(pb.getBrokerAccount().getOrderBook().getOrders()[0].getExecutedQuantity(), 100.0, EPSILON);
		
		// checking for the execution.		
		assertTrue(pb.getBrokerAccount().getExecutionBook().getExecutions().length == 1);
		assertEquals(pb.getBrokerAccount().getExecutionBook().getExecutions()[0].getExecutionQuantity(), 100.0, EPSILON);
		assertEquals(pb.getBrokerAccount().getExecutionBook().getExecutions()[0].getExecutionPrice(), 100.0, EPSILON);
		
		// checking for the resulting position. 
		assertEquals(pb.getBrokerAccount().getPortfolio().getPosition(cs).getQuantity(), 100.0, EPSILON);
		assertEquals(pb.getBrokerAccount().getPortfolio().getPosition(cs).getAveragePrice(), 100.0, EPSILON);
	}
	
	@Test
	public void testCloseLongBySellTrailingStops() throws Exception {

		final List<Execution> executionList = new LinkedList<Execution>();
		
		pb.getOnNewExecution().addEventListener(new IEventListener<Execution>() {
			public void eventFired(Execution event) throws Exception {
				executionList.add(event);
			}
		});
		
		Order o = new Order();
		InstrumentSpecification cs = new InstrumentSpecification();
		cs.setSymbol(Symbols.AMGEN);
		cs.setExchange("DTB");
		cs.setCurrency("EUR");
		
		
		o.setInstrumentSpecification(cs);
		o.setSide(OrderSide.SELL);
		o.setType(OrderType.TRAILING_STOP);
		o.setTrailingDistance(2.0);
		
		// trigger the trailing stop starting at 100. 
		o.setLimitPrice(100.0);
		o.setQuantity(100.0);
		
		// checking that no trailing was done, yet. 
		assertEquals(-1.0, o.getStopPrice(), EPSILON);
		
		pb.placeOrder(o);
		
		TradeIndication q = new TradeIndication(cs, 100.0, 100);
		mockTradeSource.fireEvent(q);
		assertEquals(-1.0, o.getStopPrice(), EPSILON);
		
		q = new TradeIndication(cs, 101.0, 100);
		mockTradeSource.fireEvent(q);
		assertEquals(99.0, o.getStopPrice(), EPSILON);
		
		// 
		q = new TradeIndication(cs, 102.0, 100);
		mockTradeSource.fireEvent(q);
		assertEquals(100.0, o.getStopPrice(), EPSILON);
		
		// 
		q = new TradeIndication(cs, 105.0, 100);
		mockTradeSource.fireEvent(q);
		assertEquals(103.0, o.getStopPrice(), EPSILON);
		
		assertTrue(executionList.size() == 0);

		// build a quote. 
		Quote quote = new Quote(cs); 
		quote.setAskPrice(103.5);
		quote.setBidPrice(102);
		quote.setAskQuantity(50.0);
		quote.setBidQuantity(50.0);
		mockQuoteSource.fireEvent(quote);

		assertTrue(executionList.size() == 1);
		assertEquals(102.0, executionList.get(0).getExecutionPrice(), EPSILON);
		assertEquals(50.0, o.getExecutedQuantity(), EPSILON);
		assertEquals(102.0, o.getAveragePrice(), EPSILON);
		
		// do the second fill
		quote.setAskPrice(103.5);
		quote.setBidPrice(100);
		quote.setAskQuantity(50.0);
		quote.setBidQuantity(50.0);
		mockQuoteSource.fireEvent(quote);
		
		assertTrue(executionList.size() == 2);
		assertEquals(100.0, executionList.get(1).getExecutionPrice(), EPSILON);
		assertEquals(100.0, o.getExecutedQuantity(), EPSILON);
		assertEquals(101.0, o.getAveragePrice(), EPSILON);
	}

	@Test
	public void testCloseShortBySellTrailingStops() throws Exception {

		final List<Execution> executionList = new LinkedList<Execution>();
		
		pb.getOnNewExecution().addEventListener(new IEventListener<Execution>() {
			public void eventFired(Execution event) throws Exception {
				executionList.add(event);
			}
		});
		
		Order o = new Order();
		InstrumentSpecification cs = new InstrumentSpecification();
		cs.setSymbol(Symbols.AMGEN);
		cs.setExchange("DTB");
		cs.setCurrency("EUR");
		
		
		o.setInstrumentSpecification(cs);
		o.setSide(OrderSide.BUY);
		o.setType(OrderType.TRAILING_STOP);
		o.setTrailingDistance(2);
		// trigger the trailing stop starting at 100. 
		o.setLimitPrice(100);
		o.setQuantity(100.0);
		
		// checking that no trailing was done, yet. 
		assertEquals(-1.0, o.getStopPrice(), EPSILON);
		
		// 
		pb.placeOrder(o);
		
		// 
		
		TradeIndication q = new TradeIndication(cs, 100.0, 100);
		mockTradeSource.fireEvent(q);
		assertEquals(-1.0, o.getStopPrice(), EPSILON);
		
		// 
		q = new TradeIndication(cs, 99.0, 100);
		mockTradeSource.fireEvent(q);
		assertEquals(101.0, o.getStopPrice(), EPSILON);
		
		// 
		q = new TradeIndication(cs, 98.0, 100);
		mockTradeSource.fireEvent(q);
		assertEquals(100.0, o.getStopPrice(), EPSILON);
		
		// 
		q = new TradeIndication(cs, 95.0, 100);
		mockTradeSource.fireEvent(q);
		assertEquals(97.0, o.getStopPrice(), EPSILON);
		
		// trigger the stop. 
		q = new TradeIndication(cs, 98.0, 50);
		mockTradeSource.fireEvent(q);
			
		assertTrue(executionList.size() == 1);
		assertEquals(98.0, executionList.get(0).getExecutionPrice(), EPSILON);
		assertEquals(50.0, o.getExecutedQuantity(), EPSILON);
		assertEquals(98.0, o.getAveragePrice(), EPSILON);

		// build a quote. 
		Quote quote = new Quote(cs); 
		quote.setAskPrice(98.5);
		quote.setBidPrice(98.2);
		quote.setAskQuantity(120.0);
		quote.setBidQuantity(100.0);
		mockQuoteSource.fireEvent(quote);
			
		assertTrue(executionList.size() == 2);
		assertEquals(98.5, executionList.get(1).getExecutionPrice(), EPSILON);
		assertEquals(50.0, executionList.get(1).getExecutionQuantity(), EPSILON);
		assertEquals(100.0, o.getExecutedQuantity(), EPSILON);
	}

	@Test
	public void testPartialFilling() throws Exception {

		final List<Execution> executionList = new LinkedList<Execution>();
		
		pb.getOnNewExecution().addEventListener(new IEventListener<Execution>() {
			public void eventFired(Execution event) throws Exception {
				executionList.add(event);
			}
		});

		Order o = new Order();
		InstrumentSpecification cs = new InstrumentSpecification();
		cs.setSymbol(Symbols.AMGEN);
		cs.setExchange("DTB");
		cs.setCurrency("EUR");
		
		o.setInstrumentSpecification(cs);
		o.setSide(OrderSide.BUY);
		o.setType(OrderType.MARKET);
		o.setQuantity(100);
		
		// 
		pb.placeOrder(o); 
		
		//
		TradeIndication t = new TradeIndication(cs, 100.0, 50);
		mockTradeSource.fireEvent(t);
		
		assertTrue(executionList.size() > 0);
		
		assertEquals(50.0, executionList.get(0).getExecutionQuantity(), EPSILON);
		assertEquals(50.0, o.getExecutedQuantity(), EPSILON);
		assertEquals(50.0, o.getOpenQuantity(), EPSILON);
		
		// build a quote. 
		Quote q = new Quote(cs);
		q.setAskPrice(100);
		q.setAskQuantity(50.0);
		mockQuoteSource.fireEvent(q);
		
		assertEquals(100.0, o.getExecutedQuantity(), EPSILON);
		assertEquals(0.0, o.getOpenQuantity(), EPSILON);
	}
	
	@Test
	public void testBuyLimitOrder() throws Exception{

		final List<Execution> executionList = new LinkedList<Execution>();
		
		pb.getOnNewExecution().addEventListener(new IEventListener<Execution>() {
			public void eventFired(Execution event) throws Exception {
				executionList.add(event);
			}
		});

		Order o = new Order();
		InstrumentSpecification cs = new InstrumentSpecification();
		cs.setSymbol(Symbols.AMGEN);
		cs.setExchange("DTB");
		cs.setCurrency("EUR");
		
		o.setInstrumentSpecification(cs);
		o.setSide(OrderSide.BUY);
		o.setType(OrderType.LIMIT);
		o.setLimitPrice(105);
		o.setQuantity(100);
		
		// 
		pb.placeOrder(o); 
		
		// build a quote. 
		Quote q = new Quote(cs);
		q.setAskPrice(110.5);
		q.setBidPrice(110.0);
		q.setAskQuantity(100.0);
		q.setBidQuantity(100.0);
		mockQuoteSource.fireEvent(q);
		
		assertTrue(executionList.size() == 0);
			
		// set the price below limit. 
		q = new Quote(cs);
		q.setAskPrice(104.5);
		q.setBidPrice(104.0);
		q.setAskQuantity(100.0);
		q.setBidQuantity(99.0);
		mockQuoteSource.fireEvent(q);
		
		assertTrue(executionList.size() > 0);
		assertEquals(104.5, executionList.get(0).getExecutionPrice(), EPSILON);
		assertEquals(100.0, executionList.get(0).getExecutionQuantity(), EPSILON);
	}
	

	@Test
	public void testSellLimitOrder() throws Exception{

		final List<Execution> executionList = new LinkedList<Execution>();
		
		pb.getOnNewExecution().addEventListener(new IEventListener<Execution>() {
			public void eventFired(Execution event) throws Exception {
				executionList.add(event);
			}
		});

		Order o = new Order();
		InstrumentSpecification cs = new InstrumentSpecification();
		cs.setSymbol(Symbols.AMGEN);
		cs.setExchange("DTB");
		cs.setCurrency("EUR");
		
		o.setInstrumentSpecification(cs);
		o.setSide(OrderSide.SELL);
		o.setType(OrderType.LIMIT);
		o.setLimitPrice(95);
		o.setQuantity(100);
		
		// 
		pb.placeOrder(o); 

		// build a quote below limit, may not result in execution. 
		Quote q = new Quote(cs);
		q.setAskPrice(90.5);
		q.setBidPrice(90.0);
		q.setAskQuantity(100.0);
		q.setBidQuantity(100.0);
		mockQuoteSource.fireEvent(q);
		
		assertTrue(executionList.size() == 0);
			
		// set the price above limit. 
		q = new Quote(cs);
		q.setAskPrice(104.5);
		q.setBidPrice(104.0);
		q.setAskQuantity(100.0);
		q.setBidQuantity(99.0);
		mockQuoteSource.fireEvent(q);
		
		assertTrue(executionList.size() > 0);
		// check if we sold at bid price. 
		assertEquals(104.0, executionList.get(0).getExecutionPrice(), EPSILON);
		assertEquals(99.0, executionList.get(0).getExecutionQuantity(), EPSILON);
		
		mockQuoteSource.fireEvent(q);

		assertEquals(104.0, executionList.get(1).getExecutionPrice(), EPSILON);
		assertEquals(1.0, executionList.get(1).getExecutionQuantity(), EPSILON);
	}
	

	@Test
	public void testSellStopOrder() throws Exception{

		final List<Execution> executionList = new LinkedList<Execution>();
		
		pb.getOnNewExecution().addEventListener(new IEventListener<Execution>() {
			public void eventFired(Execution event) throws Exception {
				executionList.add(event);
			}
		});

		Order o = new Order();
		InstrumentSpecification cs = new InstrumentSpecification();
		cs.setSymbol(Symbols.AMGEN);
		cs.setExchange("DTB");
		cs.setCurrency("EUR");
		
		o.setInstrumentSpecification(cs);
		o.setSide(OrderSide.SELL);
		o.setType(OrderType.STOP);
		o.setStopPrice(95);
		o.setQuantity(100);
		
		// 
		pb.placeOrder(o); 

		// build a quote above stop, may not result in execution. 
		Quote q = new Quote(cs);
		q.setAskPrice(104.5);
		q.setBidPrice(104.0);
		q.setAskQuantity(100.0);
		q.setBidQuantity(99.0);
		mockQuoteSource.fireEvent(q);
		
		assertTrue(executionList.size() == 0);
			
		// set the price below stop. 
		q = new Quote(cs);
		q.setAskPrice(90.5);
		q.setBidPrice(90.0);
		q.setAskQuantity(100.0);
		q.setBidQuantity(100.0);
		mockQuoteSource.fireEvent(q);
		
		assertTrue(executionList.size() > 0);
		// check if we sold at bid price. 
		assertEquals(90.0, executionList.get(0).getExecutionPrice(), EPSILON);
		assertEquals(100.0, executionList.get(0).getExecutionQuantity(), EPSILON);
	}

	@Test
	public void testBuyStopOrder() throws Exception{

		final List<Execution> executionList = new LinkedList<Execution>();
		
		pb.getOnNewExecution().addEventListener(new IEventListener<Execution>() {
			public void eventFired(Execution event) throws Exception {
				executionList.add(event);
			}
		});

		Order o = new Order();
		InstrumentSpecification cs = new InstrumentSpecification();
		cs.setSymbol(Symbols.AMGEN);
		cs.setExchange("DTB");
		cs.setCurrency("EUR");
		
		o.setInstrumentSpecification(cs);
		o.setSide(OrderSide.BUY);
		o.setType(OrderType.STOP);
		o.setStopPrice(95.00);
		o.setQuantity(100.);
		
		// 
		pb.placeOrder(o); 

		// build a quote below stop, may not result in execution. 
		Quote q = new Quote(cs);
		q.setAskPrice(90.5);
		q.setBidPrice(90.0);
		q.setAskQuantity(100.0);
		q.setBidQuantity(100.0);
		mockQuoteSource.fireEvent(q);
		
		assertTrue(executionList.size() == 0);
			
		// set the price above stop. 
		q = new Quote(cs);
		q.setAskPrice(104.5);
		q.setBidPrice(104.0);
		q.setAskQuantity(100.0);
		q.setBidQuantity(100.0);
		mockQuoteSource.fireEvent(q);
		
		assertTrue(executionList.size() == 1);
		// check if we sold at bid price. 
		assertEquals(104.5, executionList.get(0).getExecutionPrice(), EPSILON);
		assertEquals(100.0, executionList.get(0).getExecutionQuantity(), EPSILON);
	}
}