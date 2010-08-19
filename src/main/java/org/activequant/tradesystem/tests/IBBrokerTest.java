package org.activequant.tradesystem.tests;


import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.types.Expiry;
import org.activequant.core.types.Symbols;
import org.activequant.core.util.TimeStampFormat;
import org.activequant.data.retrieval.integration.ib.IBTwsConnection;
import org.activequant.tradesystem.broker.integration.ib.IBBroker;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.types.OrderSide;
import org.activequant.tradesystem.types.OrderType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * don't run this test unless you have a working IB connection. <br>
 * <br>
 * <b>History:</b><br>
 *  - [05.11.2007] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class IBBrokerTest {

	private static IBBroker broker = null; 
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		Account account = new Account();
		IBTwsConnection connection = new IBTwsConnection("192.168.6.13", 7496, 200);
		
		broker = new IBBroker(connection, account);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	
	private InstrumentSpecification getInstrumentSpecification() {
		// build a contract specification
		InstrumentSpecification ts = new InstrumentSpecification();
		ts.setCurrency("EUR");
		ts.setExchange("DTB");
		ts.setSecurityType("FUT");
		TimeStampFormat sdf = new TimeStampFormat("yyyyMMdd");
		ts.setExpiry(new Expiry(sdf.parse("20071221")));
		ts.setSymbol(Symbols.DAX);
		return ts;
	}
	
	@Test
	public void testPlaceTrailingOrder() throws Exception {
		InstrumentSpecification ispec = getInstrumentSpecification(); 
		Order o = new Order();
		o.setInstrumentSpecification(ispec);
		o.setLimitPrice(7850.0);
		o.setTrailingDistance(2.0);
		o.setQuantity(1.0);
		o.setSide(OrderSide.BUY);
		o.setType(OrderType.TRAILING_STOP);
		
		broker.placeOrder(o);

		Thread.sleep(10000);
	}
	

	@Test
	public void testPlaceLongMarketOrder() throws Exception {
		InstrumentSpecification ispec = getInstrumentSpecification(); 
		Order o = new Order();
		o.setInstrumentSpecification(ispec);
		o.setQuantity(1.0);
		o.setSide(OrderSide.BUY);
		o.setType(OrderType.MARKET);
		
		broker.placeOrder(o);
	
		Thread.sleep(10000);
	}
	
	@Test
	public void testPlaceShortMarketOrder() throws Exception {
		InstrumentSpecification ispec = getInstrumentSpecification(); 
		Order o = new Order();
		o.setInstrumentSpecification(ispec);
		o.setQuantity(1.0);
		o.setSide(OrderSide.SELL);
		o.setType(OrderType.MARKET);
		
		broker.placeOrder(o);
	
		Thread.sleep(10000);
	}
	
	@Test
	public void testPlaceLongStopOrder() throws Exception {
		InstrumentSpecification ispec = getInstrumentSpecification(); 
		Order o = new Order();
		o.setInstrumentSpecification(ispec);
		o.setQuantity(1.0);
		o.setStopPrice(7800.0);
		o.setSide(OrderSide.BUY);
		o.setType(OrderType.STOP);
		broker.placeOrder(o);
		Thread.sleep(10000);
	}
	

	@Test
	public void testPlaceShortStopOrder() throws Exception {
		InstrumentSpecification ispec = getInstrumentSpecification(); 
		Order o = new Order();
		o.setInstrumentSpecification(ispec);
		o.setQuantity(1.0);
		o.setStopPrice(7800.0);
		o.setSide(OrderSide.SELL);
		o.setType(OrderType.STOP);
		broker.placeOrder(o);
		Thread.sleep(10000);
	}
	

	@Test
	public void testPlaceLongLimitOrder() throws Exception {
		InstrumentSpecification ispec = getInstrumentSpecification(); 
		Order o = new Order();
		o.setInstrumentSpecification(ispec);
		o.setQuantity(1.0);
		o.setLimitPrice(7800.0);
		o.setSide(OrderSide.BUY);
		o.setType(OrderType.LIMIT);
		broker.placeOrder(o);
		Thread.sleep(10000);
	}
	
	@Test
	public void testPlaceShortLimitOrder() throws Exception {
		InstrumentSpecification ispec = getInstrumentSpecification(); 
		Order o = new Order();
		o.setInstrumentSpecification(ispec);
		o.setQuantity(1.0);
		o.setLimitPrice(7800.0);
		o.setSide(OrderSide.SELL);
		o.setType(OrderType.LIMIT);
		broker.placeOrder(o);
		Thread.sleep(10000);
	}
	
	@Test
	public void testPortfolioReception() throws Exception {
		
		
		
		
		Thread.sleep(1000000);
	}
}
