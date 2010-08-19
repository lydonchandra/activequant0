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
import org.activequant.tradesystem.broker.IBroker;
import org.activequant.tradesystem.broker.integration.SoftBroker;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.BrokerAccount;
import org.activequant.tradesystem.domainmodel.Execution;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.types.BrokerId;
import org.activequant.tradesystem.types.OrderSide;
import org.activequant.tradesystem.types.OrderType;
import org.activequant.util.pattern.events.Event;
import org.junit.Test;

/**
 * Test class for soft broker.<br>
 * Also tests QuoteMergingSubscriptionSource and TradeIndicationToSubscriptionSourceConverter.
 * <br>
 * <b>History:</b><br>
 *  - [12.11.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class SoftBrokerTest {
	
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
	private final List<Order> placedOrders = new LinkedList<Order>();
	private final List<Order> canceledOrders = new LinkedList<Order>();
	private final IBroker mockBroker = new IBroker() {

		public void cancelOrder(Order order) throws Exception {
			canceledOrders.add(order);
		}

		public void cancelOrders(Order... orders) throws Exception {
			throw new UnsupportedOperationException();
		}

		public BrokerId getBrokerID() {
			throw new UnsupportedOperationException();
		}

		public Event<BrokerAccount> getOnAccountUpdate() {
			throw new UnsupportedOperationException();
		}

		public Event<Execution> getOnNewExecution() {
			throw new UnsupportedOperationException();
		}

		public Event<Order> getOnNewOrder() {
			throw new UnsupportedOperationException();
		}

		public void placeOrder(Order order) throws Exception {
			placedOrders.add(order);
		}

		public void placeOrders(Order... orders) throws Exception {
			throw new UnsupportedOperationException();
		}
	};

	private final SoftBroker pb = new SoftBroker(account.getBrokerAccount(new BrokerId(SoftBroker.class.getName())), mockBroker);
	{
		pb.setQuoteSubscriptionSource(source);
	}
	
	public void cleanup() {
		placedOrders.clear();
		canceledOrders.clear();
	}
	
	@Test
	public void testMarketOrder() throws Exception {
		System.out.println("Testing order processing");
		cleanup();
		
		Order o = new Order();
		InstrumentSpecification cs = new InstrumentSpecification();
		cs.setSymbol(Symbols.AMGEN);
		cs.setExchange("DTB");
		cs.setCurrency("EUR");
		
		o.setInstrumentSpecification(cs);
		o.setSide(OrderSide.BUY);
		o.setType(OrderType.MARKET);
		o.setQuantity(100);

		// MARKET order should go directly to the physical broker
		placedOrders.clear();
		pb.placeOrder(o);
		assertTrue(placedOrders.size() == 1);
	}
	
	@Test
	public void testCloseLongBySellTrailingStops() throws Exception {
		cleanup();

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
		
		placedOrders.clear();
		pb.placeOrder(o);
		
		// should not yet be placed
		assertTrue(placedOrders.size() == 0);
		
		TradeIndication q = new TradeIndication(cs, 100.0, 100);
		mockTradeSource.fireEvent(q);
		// should not yet be placed
		assertTrue(placedOrders.size() == 0);
		
		q = new TradeIndication(cs, 101.0, 100);
		mockTradeSource.fireEvent(q);
		assertEquals(99.0, o.getStopPrice(), EPSILON);
		// should not yet be placed
		assertTrue(placedOrders.size() == 0);
		
		// 
		q = new TradeIndication(cs, 102.0, 100);
		mockTradeSource.fireEvent(q);
		assertEquals(100.0, o.getStopPrice(), EPSILON);
		// should not yet be placed
		assertTrue(placedOrders.size() == 0);
		
		q = new TradeIndication(cs, 105.0, 100);
		mockTradeSource.fireEvent(q);
		assertEquals(103.0, o.getStopPrice(), EPSILON);
		// should not yet be placed
		assertTrue(placedOrders.size() == 0);
		
		// build a quote. 
		Quote quote = new Quote(cs); 
		quote.setAskPrice(103.5);
		quote.setBidPrice(102);
		quote.setAskQuantity(50.0);
		quote.setBidQuantity(50.0);
		mockQuoteSource.fireEvent(quote);

		// now it should get to the physical broker as market order
		assertTrue(placedOrders.size() == 1);
		assertTrue(placedOrders.get(0).getType() == OrderType.MARKET);
	}

	@Test
	public void testCloseShortBySellTrailingStops() throws Exception {
		cleanup();

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
		
		// should not yet be placed
		assertTrue(placedOrders.size() == 0);
		
		pb.placeOrder(o);
		
		// should not yet be placed
		assertTrue(placedOrders.size() == 0);
		
		TradeIndication q = new TradeIndication(cs, 100.0, 100);
		mockTradeSource.fireEvent(q);
		assertEquals(-1.0, o.getStopPrice(), EPSILON);
		// should not yet be placed
		assertTrue(placedOrders.size() == 0);
		
		// 
		q = new TradeIndication(cs, 99.0, 100);
		mockTradeSource.fireEvent(q);
		assertEquals(101.0, o.getStopPrice(), EPSILON);
		// should not yet be placed
		assertTrue(placedOrders.size() == 0);
		
		// 
		q = new TradeIndication(cs, 98.0, 100);
		mockTradeSource.fireEvent(q);
		assertEquals(100.0, o.getStopPrice(), EPSILON);
		// should not yet be placed
		assertTrue(placedOrders.size() == 0);
		
		// 
		q = new TradeIndication(cs, 95.0, 100);
		mockTradeSource.fireEvent(q);
		assertEquals(97.0, o.getStopPrice(), EPSILON);
		// should not yet be placed
		assertTrue(placedOrders.size() == 0);
		
		// trigger the stop. 
		q = new TradeIndication(cs, 98.0, 50);
		mockTradeSource.fireEvent(q);
			
		// should be placed as MARKET order
		assertTrue(placedOrders.size() == 1);
		assertTrue(placedOrders.get(0).getType() == OrderType.MARKET);
	}

	@Test
	public void testBuyLimitOrder() throws Exception{
		cleanup();

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
		
		assertTrue(placedOrders.size() == 0);
		// 
		pb.placeOrder(o); 
		assertTrue(placedOrders.size() == 0);
		
		// build a quote. 
		Quote q = new Quote(cs);
		q.setAskPrice(110.5);
		q.setBidPrice(110.0);
		q.setAskQuantity(100.0);
		q.setBidQuantity(100.0);
		mockQuoteSource.fireEvent(q);
		
		assertTrue(placedOrders.size() == 0);
			
		// set the price below limit. 
		q = new Quote(cs);
		q.setAskPrice(104.5);
		q.setBidPrice(104.0);
		q.setAskQuantity(100.0);
		q.setBidQuantity(99.0);
		mockQuoteSource.fireEvent(q);
		
		assertTrue(placedOrders.size() == 1);
		assertTrue(placedOrders.get(0).getType() == OrderType.MARKET);
	}
	

	@Test
	public void testSellLimitOrder() throws Exception{
		cleanup();

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
		
		assertTrue(placedOrders.size() == 0);
		// 
		pb.placeOrder(o); 
		assertTrue(placedOrders.size() == 0);

		// build a quote below limit, may not result in execution. 
		Quote q = new Quote(cs);
		q.setAskPrice(90.5);
		q.setBidPrice(90.0);
		q.setAskQuantity(100.0);
		q.setBidQuantity(100.0);
		mockQuoteSource.fireEvent(q);
		
		assertTrue(placedOrders.size() == 0);
			
		// set the price above limit. 
		q = new Quote(cs);
		q.setAskPrice(104.5);
		q.setBidPrice(104.0);
		q.setAskQuantity(100.0);
		q.setBidQuantity(99.0);
		mockQuoteSource.fireEvent(q);
		
		assertTrue(placedOrders.size() == 1);
		assertTrue(placedOrders.get(0).getType() == OrderType.MARKET);
	}
	

	@Test
	public void testSellStopOrder() throws Exception{
		cleanup();

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
		
		assertTrue(placedOrders.size() == 0);
		pb.placeOrder(o); 

		assertTrue(placedOrders.size() == 0);

		// build a quote above stop, may not result in execution. 
		Quote q = new Quote(cs);
		q.setAskPrice(104.5);
		q.setBidPrice(104.0);
		q.setAskQuantity(100.0);
		q.setBidQuantity(99.0);
		mockQuoteSource.fireEvent(q);
		
		assertTrue(placedOrders.size() == 0);
			
		// set the price below stop. 
		q = new Quote(cs);
		q.setAskPrice(90.5);
		q.setBidPrice(90.0);
		q.setAskQuantity(100.0);
		q.setBidQuantity(100.0);
		mockQuoteSource.fireEvent(q);
		
		assertTrue(placedOrders.size() == 1);
		assertTrue(placedOrders.get(0).getType() == OrderType.MARKET);
	}

	@Test
	public void testBuyStopOrder() throws Exception{
		cleanup();

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
		assertTrue(placedOrders.size() == 0);
		pb.placeOrder(o); 
		assertTrue(placedOrders.size() == 0);

		// build a quote below stop, may not result in execution. 
		Quote q = new Quote(cs);
		q.setAskPrice(90.5);
		q.setBidPrice(90.0);
		q.setAskQuantity(100.0);
		q.setBidQuantity(100.0);
		mockQuoteSource.fireEvent(q);
		
		assertTrue(placedOrders.size() == 0);
			
		// set the price above stop. 
		q = new Quote(cs);
		q.setAskPrice(104.5);
		q.setBidPrice(104.0);
		q.setAskQuantity(100.0);
		q.setBidQuantity(100.0);
		mockQuoteSource.fireEvent(q);
		
		assertTrue(placedOrders.size() == 1);
		assertTrue(placedOrders.get(0).getType() == OrderType.MARKET);
	}
	
	@Test
	public void testCancelOrder() throws Exception {
		cleanup();

		InstrumentSpecification cs = new InstrumentSpecification();
		cs.setSymbol(Symbols.AMGEN);
		cs.setExchange("DTB");
		cs.setCurrency("EUR");
		
		Order o1 = new Order();
		o1.setInstrumentSpecification(cs);
		o1.setSide(OrderSide.BUY);
		o1.setType(OrderType.STOP);
		o1.setStopPrice(95.00);
		o1.setQuantity(10.);
		
		Order o2 = new Order();
		o2.setInstrumentSpecification(cs);
		o2.setSide(OrderSide.BUY);
		o2.setType(OrderType.STOP);
		o2.setStopPrice(95.00);
		o2.setQuantity(20.);

		assertTrue(placedOrders.size() == 0);
		
		// place both orders (identical, except for the size)
		pb.placeOrder(o1);
		pb.placeOrder(o2);
		assertTrue(placedOrders.size() == 0);

		// cancel first order
		pb.cancelOrder(o2);

		// verify that when STOP condition is satisfied and order is
		// submitted, its the first one that survived the cancelation
		TradeIndication q = new TradeIndication(cs, 104.0, 100);
		mockTradeSource.fireEvent(q);

		assertTrue(placedOrders.size() == 1);
		assertEquals(placedOrders.get(0).getQuantity(), 10., EPSILON);
		
		// now trigger the order execution, and make sure the cancel signal is
		// passe on to the physical broker
		
		// set the price above stop. 
		Quote qq = new Quote(cs);
		qq.setAskPrice(104.5);
		qq.setBidPrice(104.0);
		qq.setAskQuantity(100.0);
		qq.setBidQuantity(100.0);
		mockQuoteSource.fireEvent(qq);
		
		assertTrue(placedOrders.size() == 1);
		
		pb.cancelOrder(o1);
		
		assertTrue(canceledOrders.size() == 1);
	}
}