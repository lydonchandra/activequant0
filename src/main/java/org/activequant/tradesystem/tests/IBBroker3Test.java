package org.activequant.tradesystem.tests;


import static org.junit.Assert.*;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.types.Symbols;
import org.activequant.data.retrieval.integration.ib.IBTwsConnection;
import org.activequant.tradesystem.domainmodel2.BrokerSupport;
import org.activequant.tradesystem.domainmodel2.IBBroker3;
import org.activequant.tradesystem.domainmodel2.IOrderTracker;
import org.activequant.tradesystem.domainmodel2.Order;
import org.activequant.tradesystem.domainmodel2.event.OrderCompletionEvent;
import org.junit.Test;

public class IBBroker3Test {

	private static final IBTwsConnection connection = new IBTwsConnection("127.0.0.1", 7496, 200);
	private static final IBBroker3 broker = new IBBroker3(connection);
	
	private InstrumentSpecification getInstrumentSpecification() {
		// build a contract specification
		InstrumentSpecification ts = new InstrumentSpecification();
		ts.setCurrency("USD");
		ts.setExchange("ARCA");
		ts.setSecurityType("STK");
		ts.setSymbol(Symbols.GOOG);
		return ts;
	}
	
	@Test
	public void testNothing() { }
	
//	@Test
	public void testPlaceTrailingOrder() throws Exception {
		InstrumentSpecification ispec = getInstrumentSpecification(); 
		Order o = new Order();
		o.setInstrumentSpecification(ispec);
		o.setLimitPrice(800.0);
		o.setTrailingDistance(2.0);
		o.setQuantity(1.0);
		o.setOrderSide(Order.Side.BUY);
		o.setOrderType(Order.Type.TRAILING_STOP);
		
		Thread.sleep(2000);

		IOrderTracker tracker = broker.prepareOrder(o);
		tracker.submit();

		BrokerSupport.waitForTicketCompletion(tracker, 10000);
		tracker.cancel();
		BrokerSupport.waitForTicketCompletion(tracker);
	}
	

//	@Test
	public void testPlaceLongMarketOrder() throws Exception {
		InstrumentSpecification ispec = getInstrumentSpecification(); 

		Order o = new Order();
		o.setInstrumentSpecification(ispec);
		o.setQuantity(1.0);
//		o.setQuantity(1000.0);
		o.setOrderSide(Order.Side.SELL);
		o.setOrderType(Order.Type.MARKET);
		
		Thread.sleep(2000);

		IOrderTracker tracker = broker.prepareOrder(o);
		tracker.submit();

		BrokerSupport.waitForTicketCompletion(tracker, 10000);
		tracker.cancel();
		BrokerSupport.waitForTicketCompletion(tracker);
		
		OrderCompletionEvent completion = tracker.getOrderCompletion();
		assertTrue(completion != null);
		
		assertTrue(completion.getTerminalError() != null);
	}
	
//	@Test
	public void testPlaceShortMarketOrder() throws Exception {
		InstrumentSpecification ispec = getInstrumentSpecification(); 
		Order o = new Order();
		o.setInstrumentSpecification(ispec);
		o.setQuantity(1.0);
		o.setOrderSide(Order.Side.SELL);
		o.setOrderType(Order.Type.MARKET);
		
		IOrderTracker tracker = broker.prepareOrder(o);
		tracker.submit();
	
		Thread.sleep(10000);
	}
	
	@Test
	public void testPlaceLongStopOrder() throws Exception {
		InstrumentSpecification ispec = getInstrumentSpecification(); 
		Order o = new Order();
		o.setInstrumentSpecification(ispec);
		o.setQuantity(1.0);
		o.setStopPrice(750.0);
		o.setOrderSide(Order.Side.BUY);
		o.setOrderType(Order.Type.STOP);

		Thread.sleep(2000);

		IOrderTracker tracker = broker.prepareOrder(o);
		tracker.submit();

		Thread.sleep(10000);
		Order oo = new Order();
		oo.setInstrumentSpecification(ispec);
		oo.setQuantity(2.0);
		oo.setStopPrice(740.0);
		oo.setOrderSide(Order.Side.BUY);
		oo.setOrderType(Order.Type.STOP);
		tracker.update(oo);

		BrokerSupport.waitForTicketCompletion(tracker, 10000);
		
		try {
			oo.setOrderType(Order.Type.MARKET);
			tracker.update(oo);
			Thread.sleep(2000);

			fail();
		} catch(IllegalArgumentException ex) {
		}

		tracker.cancel();
		BrokerSupport.waitForTicketCompletion(tracker);
	}
	

//	@Test
	public void testPlaceShortStopOrder() throws Exception {
		InstrumentSpecification ispec = getInstrumentSpecification(); 
		Order o = new Order();
		o.setInstrumentSpecification(ispec);
		o.setQuantity(1.0);
		o.setStopPrice(7800.0);
		o.setOrderSide(Order.Side.SELL);
		o.setOrderType(Order.Type.STOP);

		IOrderTracker tracker = broker.prepareOrder(o);
		tracker.submit();
		
		Thread.sleep(10000);
	}
	

//	@Test
	public void testPlaceLongLimitOrder() throws Exception {
		InstrumentSpecification ispec = getInstrumentSpecification(); 
		Order o = new Order();
		o.setInstrumentSpecification(ispec);
		o.setQuantity(1.0);
		o.setLimitPrice(7800.0);
		o.setOrderSide(Order.Side.BUY);
		o.setOrderType(Order.Type.LIMIT);

		IOrderTracker tracker = broker.prepareOrder(o);
		tracker.submit();
		Thread.sleep(10000);
	}
	
//	@Test
	public void testPlaceShortLimitOrder() throws Exception {
		InstrumentSpecification ispec = getInstrumentSpecification(); 
		Order o = new Order();
		o.setInstrumentSpecification(ispec);
		o.setQuantity(1.0);
		o.setLimitPrice(7800.0);
		o.setOrderSide(Order.Side.SELL);
		o.setOrderType(Order.Type.LIMIT);
		
		IOrderTracker tracker = broker.prepareOrder(o);
		tracker.submit();
		Thread.sleep(10000);
	}
	
//	@Test
	public void testPortfolioReception() throws Exception {
		
		
		
		
		Thread.sleep(1000000);
	}
}
