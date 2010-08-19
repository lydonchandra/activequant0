package org.activequant.data.retrieval.integration.ib;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Quote;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.domainmodel.Symbol;
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.core.types.Expiry;
import org.activequant.core.types.TimeFrame;
import org.activequant.core.types.TimeStamp;
import org.activequant.core.util.CandleSeriesUtil;
import org.activequant.core.util.TimeStampFormat;
import org.activequant.data.util.UniqueDateGenerator;
import org.activequant.util.pattern.events.Event;
import org.activequant.util.pattern.events.IEventListener;
import org.activequant.util.tools.StackTraceParser;
import org.apache.log4j.Logger;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.EClientSocket;
import com.ib.client.EWrapper;
import com.ib.client.Execution;
import com.ib.client.ExecutionFilter;
import com.ib.client.Order;
import com.ib.client.TickType;

/**
 * 
 * Don't use this class directly, always wrap it as in IBCandleSeriesSource. 
 * moved here from ccapi2.  <br>
 * <br>
 * <b>History:</b><br>
 *  - [14.06.2007] Created (Ulrich Staudinger)<br>
 *  - [23.06.2007] Moved to new domain model (Erik Nijkamp)<br>
 *  - [24.06.2007] renaming and abstraction (Ulrich Staudinger)<br>
 *  - [18.07.2007] small fix for daily data (Ulrich Staudinger)<br>
 *  - [29.09.2007] cleanup + moved to new domain model (Erik Nijkamp)<br>
 *  - [28.10.2007] moved to new data subscription model (Mike Kroutikov)<br>
 *  
 *  @author Ulrich Staudinger
 */
public class IBTwsConnection extends Thread {

	/**
	 * logger. 
	 */
	protected final static Logger log = Logger.getLogger(IBTwsConnection.class);

	/**
	 * demultiplexes server response and sends it to the
	 * corresponding EWrapper implementation
	 */
	private final Map<Integer,EWrapper> interceptors = new ConcurrentHashMap<Integer,EWrapper>();
	
	/**
	 * private flag indicating whether the monitor is connected or not. 
	 */
	private boolean connected = false;

	private boolean autoReconnect = true;
	private boolean reconnectInProgress = false; 
	
	private Queue<IBEventListener> ibEventListeners = new ConcurrentLinkedQueue<IBEventListener>(); 
	
	private EClientSocket ecs;
	private String ip;

	/**
	 * this connection's client id.
	 */
	private int clientId;
	private int port;

	/**
	 * the latest order id. Will be filled up automatically shortly after connection
	 * by nextValidOrderId callback.
	 */
	private int orderId = 0;
	
	private static boolean isOrderRelatedErrorCode(int code) {
		switch(code) {
		case 103: // duplicate order id
		case 104: // can't modify filled order
		case 105: // order being modified does not match the original order
		case 106: // can't transmit order id
		case 107: // can't transmit incomplete order
		case 109: // price is insane. order will not be submitted
		case 110: // price does not conform to min price variation for this contract
		case 111: // tif type and order type are incompatible
		case 112: // exchange not compatible with order type
		case 113: // unrecognized tif option
		case 114: // relative orders are only valid for stocks
		case 115: // relative orders for stocks can only be submitted to SMART or INet.
		case 116: // dead exchange
		case 117: // block order size must be at least 50
		case 118: // VWAP orders must be routed thru VWAP exchange
		case 119: // only VWAP orders may be placed on VWAP exchange
		case 120: // its too late to place VWAP order for today
		case 121: // incorrect BD flag for the order
		case 122: // no request tag has been found for the order
		case 133:
		case 135: 
		case 201: // order rejected
		case 202: // order canceled
		case 203: // security is not awailable for this account 
		case 321: case 329:
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * central event dispatcher: sends events to registered interceptors.
	 * handles global events and errors.
	 */
	private final EWrapper myWrapper = new EWrapper() {

		public void bondContractDetails(ContractDetails arg0) {
			log.warn("bond cotract details. " + arg0);
		}

		public void connectionClosed() {
			connected = ecs.isConnected();
			log.warn("[connectionClosed] connection between IB and TWS has been closed, can happen.");
			
			if(autoReconnect){
				log.fatal("[disconnect] Connection TraderWorkstation lost. Attempting a reconnect.");
				reconnect();
			}
			else{
				reconnectInProgress = false; 
			}
		}

		public void contractDetails(ContractDetails contractDetails) {
			log.info("contract details: " + contractDetails);
		}
		
		public void currentTime(long time) {
			log.info("[currentTime] " + time);
		}

		public void error(Exception e) {
			log.fatal("[error] error 1: " + e);
			e.printStackTrace();
		}

		public void error(String message) {
			log.fatal("[error] error 1: " + message);
		}

		public void error(int reqId, int errorCode, String arg2) {
			
			if(errorCode == 165) {
				log.info("informational error message: " + arg2);
				return;
			} else if(isOrderRelatedErrorCode(errorCode)) {
				// dispatch it. 
				for(IBEventListener listener : ibEventListeners){
					listener.error(reqId, errorCode, arg2);
				}
				return;
			}
			
			EWrapper w = interceptors.get(reqId);
			if(w != null) {
				w.error(reqId, errorCode, arg2);
				return;
			}

			if(errorCode < 2104 && errorCode > 2108) {
				log.fatal("[error] error 2: " + reqId + " / " + errorCode + " / " + arg2);
			} else if (errorCode == 1102 || errorCode == 1101 || errorCode == 504) {
				log.info("[error] reconnect");
				// this is a reconnect.
				reconnect();
			} 
			
			log.error("[feedback from tws]: " + reqId + " / " + errorCode + " / " + arg2);
		}
		
		public void execDetails(int orderId, com.ib.client.Contract ibContract, Execution ibExec) {
			log.info("[execDetails]: exec_id=" + ibExec.m_execId + ", symbol=" + ibContract.m_symbol + ", shares=" + ibExec.m_shares + ", price=" + ibExec.m_price + ", time=" + ibExec.m_time);

			// dispatch it. 
			for(IBEventListener listener : ibEventListeners){
				listener.execDetails(orderId, ibContract, ibExec);
			}
		}

		public void historicalData(int reqId, String date, double open,
				double high, double low, double close, int volume, int count,
				double WAP, boolean hasGaps) {
			EWrapper w = interceptors.get(reqId);
			if(w != null) {
				w.historicalData(reqId, date, open, high, low, close,volume, count, WAP, hasGaps);
				return;
			}
			
			log.warn("[historiclaData] orphan event: " + reqId + " / " + date + " / " + open + " / " + high + " / " + low + " / " + close + " / " + volume + " / " + count + " / " + WAP + "/" + hasGaps);
		}

		public void managedAccounts(String arg0) {
			log.info("[managedAccounts] " + arg0);
		}
		
		/**
		 * IB software calls this immediately after connecting to set reference
		 * orderID value.
		 */
		public void nextValidId(int id) {
			log.debug("[nextValidId] next valid id :" + id);
			orderId = id;
		}

		/**
		 * this method is the callback hook for all open orders (unfilled?)
		 */
		public void openOrder(int orderId, Contract contract, Order order) {
			// dispatch it. 
			for(IBEventListener listener : ibEventListeners){
				// fixme: 'whyHeld' information better be dispatched 
				listener.openOrder(orderId, contract, order);
			}
		}

		/**
		 * see http://chuckcaplan.com/twsapi/index.php/void%20orderStatus%28%29
		 */
		public void orderStatus(int orderId, String status, int filled,
				int remaining, double avgFillPrice, int permId, int parentId,
				double lastFillPrice, int clientId, String whyHeld) {
			log.info("***[orderStatus] " + orderId + " / " + status + " / " + filled 
					+ " / " + remaining + " / " + avgFillPrice + " / " 
					+ permId + " / " + parentId + " / " + lastFillPrice + " / " 
					+ clientId + "/" + whyHeld);
		
			// dispatch it. 
			for(IBEventListener listener : ibEventListeners){
				// fixme: 'whyHeld' information better be dispatched 
				listener.orderStatus(orderId, status, filled, remaining, 
						avgFillPrice, permId, parentId, lastFillPrice, clientId);
			}
		}

		public void realtimeBar(int reqId, long time, double open, double high,
				double low, double close, long volume, double wap, int count) {
			EWrapper w = interceptors.get(reqId);
			if(w != null) {
				w.realtimeBar(reqId, time, open, high, low, close, volume, wap, count);
				return;
			}

			log.warn("[realtimeBar] orphan event: " + reqId + "/" + time + "/" 
					+ open + "/" + high + "/" + low + "/" + close + "/" 
					+ volume + "/" + wap + "/" + count);
		}

		public void receiveFA(int arg0, String arg1) {
			log.info("[receiveFA] " + arg0 + "/ " + arg1);
		}

		public void scannerData(int reqId, int rank,
				ContractDetails contractDetails, String distance,
				String benchmark, String projection, String legsStr) {
			EWrapper w = interceptors.get(reqId);
			if(w != null) {
				w.scannerData(reqId, rank, contractDetails, distance, benchmark, projection, legsStr);
				return;
			}
			
			log.warn("[scannerData] orphan event: " + reqId + "/" + rank + "/" 
					+ contractDetails + "/" + distance + "/" + benchmark + "/" 
					+ projection + "/" + legsStr);
		}

		public void scannerParameters(String arg0) {
			log.info("[scannerParameters] " + arg0);
		}

		public void tickEFP(int reqId, int tickType, double basisPoints,
				String formattedBasisPoints, double impliedFuture,
				int holdDays, String futureExpiry, double dividendImpact,
				double dividendsToExpiry) {
			EWrapper w = interceptors.get(reqId);
			if(w != null) {
				w.tickEFP(reqId, tickType, basisPoints, formattedBasisPoints, impliedFuture, holdDays, futureExpiry, dividendImpact, dividendsToExpiry);
				return;
			}

			log.warn("[tickEFP] orphan event: " + reqId + "/" + tickType + "/" 
					+ basisPoints + "/" + formattedBasisPoints + "/"
					+ impliedFuture + "/" + holdDays + "/" + futureExpiry + "/" 
					+ dividendImpact + "/" + dividendsToExpiry);

		}

		public void tickGeneric(int reqId, int tickType, double value) {
			EWrapper w = interceptors.get(reqId);
			if(w != null) {
				w.tickGeneric(reqId, tickType, value);
				return;
			}

			log.warn("[tickGeneric] orphan event: " + reqId + "/" + tickType + "/" + value);
		}

		public void tickOptionComputation(int reqId, int field,
				double impliedVol, double delta, double modelPrice,
				double pvDividend) {
			log.info("[tickOptionComputation] " + reqId + " / " + field 
					+ " / " + impliedVol + " / " + delta + "/" + modelPrice 
					+ "/" + pvDividend);
			
			EWrapper w = interceptors.get(reqId);
			if(w != null) {
				w.tickOptionComputation(reqId, field, impliedVol, delta, modelPrice, pvDividend);
				return;
			}

			log.warn("[tickOptionComputation] orphan event: id=" + reqId);
		}

		// this method updates server clock value
		// in my test it was off by 1 hour (probably because server does not
		// do DST while my computer does). It can be used to put better
		// timestamp on the received events.
		private final AtomicLong lastTimestamp = new AtomicLong();
		public void tickString(int reqId, int tickType, String value) {
			if(tickType == TickType.LAST_TIMESTAMP) {
				try {
					long ts = Long.parseLong(value);
					lastTimestamp.set(ts);
//					log.info(new Date(ts * 1000));
				} catch(Exception ex) {
					log.warn(ex);
				}
				
				return;
			}
			
			log.info("[tickString] " + reqId + "/" + tickType + "/" + value);
		}
		
		public void tickPrice(int tickId, int tickType, double price, int canAutoExecute) {
			EWrapper w = interceptors.get(tickId);
			if(w != null) {
				w.tickPrice(tickId, tickType, price, canAutoExecute);
				return;
			}
			
			log.warn("[tickPrice] orphan event: " + tickId + "/" + decodeTickType(tickType)
					+ "/" + price + "/" + canAutoExecute);
		}

		public void tickSize(int tickId, int tickType, int size) {
			EWrapper w = interceptors.get(tickId);
			if(w != null) {
				w.tickSize(tickId, tickType, size);
				return;
			}

			log.warn("[tickSize] orphan event: " + tickId + "/" + decodeTickType(tickType) + "/" + size);
		}

		public void updateAccountTime(String arg0) {
			log.info("[updateAccountTime] " + arg0);
		}

		public void updateAccountValue(String arg0, String arg1, String arg2, String arg3) {
			log.info("[updateAccountValue] " + arg0 + "/ " + arg1 + " / " + arg2 + " / " + arg3);
				if (arg0.equals("CashBalance") && arg2.equals("EUR")) {

					// dispatch it. 
					for(IBEventListener listener : ibEventListeners){
						listener.updateAccountValue(arg0, arg1, arg2, arg3);
					}
				}
		}

		public void updateMktDepth(int reqId, int position, int operation, int side, 
				double price, int size) {
			EWrapper w = interceptors.get(reqId);
			if(w != null) {
				w.updateMktDepth(reqId, position, operation, side, price, size);
				return;
			}

			log.warn("[updateMktDepth] orphan event: " + reqId + "/" + position 
					+ "/" + operation + "/" + side + "/" + price + "/" + size);
		}

		public void updateMktDepthL2(int reqId, int position, String marketMaker, 
				int operation, int side, double price, int size) {
			EWrapper w = interceptors.get(reqId);
			if(w != null) {
				w.updateMktDepthL2(reqId, position, marketMaker, operation, side, price, size);
				return;
			}

			log.warn("[updateMktDepthL2] orphan event: " + reqId + "/" + position 
					+ "/" + marketMaker + "/" + operation + "/" + side + "/" 
					+ price + "/" + size);
		}

		public void updateNewsBulletin(int reqId, int msgType, String message, 
				String origExchange) {
			EWrapper w = interceptors.get(reqId);
			if(w != null) {
				w.updateNewsBulletin(reqId, msgType, message, origExchange);
				return;
			}

			log.warn("[updateNewsBulletin] orphan event: " + reqId + "/" + msgType 
					+ "/" + message + "/" + origExchange);
		}

		public void updatePortfolio(Contract contract, int positionCount, double marketPrice,
				double marketValue, double avgCost, double unrealizedPNL, double realizedPNL, 
				String accountName) {
			log.info("[updatePortfolio] " + contract + "/ pos sum : " + positionCount + " / " + marketPrice
					+ " / " + marketValue + " / " + avgCost + " / " + unrealizedPNL + " / " 
					+ realizedPNL + " / " + accountName);
		
			// dispatch it. 
			for(IBEventListener listener : ibEventListeners){
				listener.updatePortfolio(contract, positionCount, marketPrice, marketValue, avgCost, unrealizedPNL, realizedPNL, accountName);
			}
		}
	};

	public IBTwsConnection(String ip, int port, int clientId) {
		this.ecs = new EClientSocket(myWrapper);

		this.ip = ip;
		this.port = port;
		this.clientId = clientId;
		
		log.info("[TwsApiMonitor] constructed.");
	}

	/**
	 * internal method to connect to TWS. 
	 * 
	 */
	public synchronized void connect() {
		if (!connected) {

			// doing a safety sleep for 2 seconds. 
			while(!connected){
				
				try{
					Thread.sleep(2000);
				} catch(Exception x) { }
				
				ecs.eConnect(ip, port, clientId);
				log.info("[connect] connected: " + ecs.isConnected());
				connected = ecs.isConnected();
				
				if(connected){
					ecs.reqExecutions(new ExecutionFilter());
					ecs.reqAccountUpdates(true, "");
				}
			}
		}
	}

	/**
	 * internal method to disconnect from IB.
	 *
	 */
	public void disconnect() {
		log.info("[disconnect]");
		ecs.eDisconnect();
		connected = ecs.isConnected();
	}

	/**
	 * internal method to trigger a reconnect. 
	 *
	 */
	private void reconnect() {
		if(!reconnectInProgress){
			disconnect();
			connect();
			// 
			resubscribe();
		}
	}
	
	public void resubscribe(){
		//
		
		/*for(Tuple<Integer, InstrumentSpecification> t : subscriptions){
			Contract contract = IBUtil.convertToContract(t.getObject2());
			getEcs().reqMktData(t.getObject1(), contract);
			log.info("[subscribe "+instanceId+"] mapped "+t.getObject2()+ " --> "+t.getObject2().toString());
		}*/
	}
	
	// for tracing purposes
	private static String decodeTickType(int type) {
		return TickType.getField(type);
	}
	
	private final AtomicInteger requestId = new AtomicInteger(0);
	private int generateRequestId() {
		return requestId.incrementAndGet();
	}
	
	/**
	 * Sets initial value for the request id. Useful when running two or more
	 * connections against single TWS: make sure that initial request ids are
	 * very different, so that there is no chance that they will interfere with
	 * each other.
	 * 
	 * @param val initial request id value.
	 */
	public void setRequestId(int val) {
		requestId.set(val);
	}
	
	// class that can act as a listener for tick events (IB ticks are 
	// activequant's Quote or TradeIndication objects).
	private static class TickWrapper extends IBWrapper {
		
		// request id
		public final int id;
		
		// request specification
		private final InstrumentSpecification spec;
		
		private final UniqueDateGenerator quoteGenerator = new UniqueDateGenerator(); 
		private final UniqueDateGenerator tradeGenerator = new UniqueDateGenerator(); 
		
		TickWrapper(int id, InstrumentSpecification spec) {
			this.id = id;
			this.spec = spec;
		}

		// accumulate quote parameters here
		private final Quote  quote = new Quote();
			
		// accumulate trade parameters here
		private final TradeIndication trade = new TradeIndication();

		// quote event dispatcher
		public final Event<Quote> quoteEvent = new Event<Quote>();

		// trade event dispatcher
		public final Event<TradeIndication> tradeEvent = new Event<TradeIndication>();

		private void distributeQuote(Quote quote) {
			try {
				Quote q = quote.clone();
				
				q.setInstrumentSpecification(spec);
				q.setTimeStamp(quoteGenerator.generate(new Date()));
			
				quoteEvent.fire(q);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
			}
		}

		private void distributeTrade(TradeIndication trade) {
			try {
				TradeIndication t = trade.clone();
				
				t.setInstrumentSpecification(spec);
				t.setTimeStamp(tradeGenerator.generate(new Date()));
			
				tradeEvent.fire(t);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
			}
		}

		/*
		 * the following logic related to "trade.setQuantity(0L);"
		 * tries to filter out duplicate tickSize()
		 * messages. This is because IB often generates duplicate tickSize()
		 * events, so that a trade of price 100. and size 3 may be reported as:
		 * 
		 * tickPrice(100.)
		 * tickSize(3)
		 * tickSize(3)
		 * 
		 * This happens not always, but approx in 50% events.
		 * 
		 * The logic below will distribute trade at first tickSize() event, but
		 * remember the size, and if next message is tickSize() one and
		 * tickSize() has the same exact size, it will be ignored.
		 */

		@Override
		public void tickPrice(int tickId, int tickType, double price, int canAutoExecute) {
			log.debug("[tick id=" + tickId + ": " + decodeTickType(tickType) + "/" + price + "/" + canAutoExecute);
			
			trade.setQuantity(0L);
			
			if (price <= 0.0) {
				// ignore obviously wrong thing
				// forex stream tend to start with ASK(0) BID(0) LAST(0) 
				return;
			}
				
			/**
			 * NOTE: IB software always calls tickSize() immediately after 
			 * tickPrice() for ticks of type BID/ASK/LAST (see source of their client). 
			 * Therefore I delay emitting the quote till we receive tickSize() call.
			 * Testing also shows that this pair is often accompanied by
			 * independent tickSize() event with the same parameters.
			 * This creates a possibility of generating duplicate events.
			 * To fight this duplication, BID/ASK events are generated only if
			 * event actually did change something (either price or size).
			 * This explains the logic below that says:
			 * if(price has changed) {
			 * 		force event distribution from tickSize() by setting size to -1
			 * }
			 * 
			 * We can not remove duplicates using this technique for LAST events,
			 * because its quite possible that two trades with same price and size 
			 * will occur in a row. This is still an unresolved problem.
			 * Presently, duplicate trade events are allowed to escape this code.
			 * 
			 * Also, I am researching the volume update events as a candidate for
			 * event generation point. Still work in progress.
			 * 
			 * -mpk
			 */
			switch (tickType) {
			case TickType.BID:
				if(price != quote.getBidPrice()) {
					quote.setBidPrice(price);
					quote.setBidQuantity(-1); // force tickSize() to emit this quote
				}
				break;
			case TickType.ASK:
				if(price != quote.getAskPrice()) {
					quote.setAskPrice(price);
					quote.setAskQuantity(-1); // force tickSize() to emit this quote
				}
				break;
			case TickType.LAST:
				trade.setPrice(price);
				break;
			case TickType.LOW:
			case TickType.HIGH:
			case TickType.CLOSE:
				log.debug("ignoring LOW/HIGH/CLOSE price event");
				break;
			default:
				log.warn("unhandled tick type: " + tickType);
//					throw new AssertionError("unexpected tick type: " + tickType);
			}				
		}
		
		@Override
		public void tickSize(int tickId, int tickType, int size) {
			log.debug("[tick id=" + tickId + ": " + decodeTickType(tickType) + "/" + size);
			
			if(size == 0) {
				return; // obviously wrong thing: ignore
			}

			double lastSize = trade.getQuantity();
			trade.setQuantity(0L);
			
			switch (tickType) {
			case TickType.ASK_SIZE:
				// do not flood with the same quotes!
				if(size != quote.getAskQuantity()) {
					quote.setAskQuantity(size);
					if(quote.getAskPrice() != Quote.NOT_SET) {
						// do not distribute incomplete quote
						if(quote.getBidQuantity() != Quote.NOT_SET ) {
							distributeQuote(quote);
						}
					} else {
						log.warn("orphan size message");
					}
				}
				break;

			case TickType.BID_SIZE:
				if(size != quote.getBidQuantity()) {
					quote.setBidQuantity(size);
					if(quote.getBidPrice() != Quote.NOT_SET) {
						// do not distribute incomplete quote
						if(quote.getAskQuantity() != Quote.NOT_SET ) {
							distributeQuote(quote);
						}
					} else {
						log.warn("orphan size message");
					}
				}
				break;
					
			case TickType.LAST_SIZE:
				if(trade.getPrice() != TradeIndication.NOT_SET) {
					if(lastSize != size) {
						trade.setQuantity(size);
						distributeTrade(trade);
					} else {
						log.debug("skipping duplicate message");
					}
				} else {
					log.warn("orphan size message");
				}
				break;
					
			case TickType.VOLUME:
				log.debug("ignoring volume update: " + size);
				break;
					
			default:
				log.warn("unhandled tick type: " + tickType);
//					throw new AssertionError("unexpected tick type");
			}
		}
	}
	
	private final Map<InstrumentSpecification,TickWrapper> tickWrappers = new HashMap<InstrumentSpecification,TickWrapper>();
	
	public void subscribeToTradeIndication(final IEventListener<TradeIndication> listener, final InstrumentSpecification spec){
		log.info("subscribe to trade indication: " + spec);
	
		synchronized(tickWrappers) {
			TickWrapper w = tickWrappers.get(spec);
			if(w == null) {
				int requestId = generateRequestId();
				
				w = new TickWrapper(requestId, spec);
				tickWrappers.put(spec, w);
				interceptors.put(requestId, w);
				
				Contract contract = convertToContract(spec);

				connect();

				ecs.reqMktData(requestId, contract, "");
				log.info("reqested market data, request id=" + w.id);
			}

			w.tradeEvent.addEventListener(listener);
		}
	}

	public void unsubscribeFromTradeIndication(IEventListener<TradeIndication> listener, InstrumentSpecification spec) {
		log.info("unsubscribe from trade indication: " + spec);

		synchronized(tickWrappers) {
			TickWrapper w = tickWrappers.get(spec);
			if(w == null) {
				log.warn("not subscribed");
				return;
			}

			w.tradeEvent.removeEventListener(listener);
			if(w.quoteEvent.isEmpty() && w.tradeEvent.isEmpty()) {
				ecs.cancelMktData(w.id);
				interceptors.remove(w.id);
				log.info("canceled market data, request id=" + w.id);
			}
		}
	}

	public void subscribeToQuote(final IEventListener<Quote> listener, final InstrumentSpecification spec){
		log.info("subscribe to quote: " + spec);
	
		synchronized(tickWrappers) {
			TickWrapper w = tickWrappers.get(spec);
			if(w == null) {
				int requestId = generateRequestId();
				
				w = new TickWrapper(requestId, spec);
				tickWrappers.put(spec, w);
				interceptors.put(requestId, w);
				
				Contract contract = convertToContract(spec);

				connect();

				ecs.reqMktData(requestId, contract, "");
				log.info("requested market data for request id: " + w.id);
			}

			w.quoteEvent.addEventListener(listener);
		}
	}

	public void unsubscribeFromQuote(IEventListener<Quote> listener, InstrumentSpecification spec) {
		log.info("unsubscribe from quote: " + spec);

		synchronized(tickWrappers) {
			TickWrapper w = tickWrappers.get(spec);
			if(w == null) {
				log.warn("not subscribed");
				return;
			}

			w.quoteEvent.removeEventListener(listener);
			if(w.quoteEvent.isEmpty() && w.tradeEvent.isEmpty()) {
				ecs.cancelMktData(w.id);
				interceptors.remove(w.id);
				log.info("canceled market data for request id: " + w.id);
			}
		}
	}
	
	// work in progress (unfinished: can not make demo IB tws to generate these 
	// events
	public int subscribeToCandle(final IEventListener<Candle> listener, 
			final InstrumentSpecification spec, final TimeFrame timeFrame, 
			final String whatToShow) {
		log.info("subscribeToCandle: " + spec + ", timeFrame=" + timeFrame);
		
		int reqId = generateRequestId();
		
		Contract contract = convertToContract(spec);
		int barSize = timeFrameToBarSizeCode(timeFrame);
		
		interceptors.put(reqId, new IBWrapper() {
			@Override
			public void realtimeBar(int reqId, long time, double open, double high,
					double low, double close, long volume, double wap, int count) {
				log.info("[realtimeBar] " + reqId + "/" + time + "/" 
						+ open + "/" + high + "/" + low + "/" + close + "/" 
						+ volume + "/" + wap + "/" + count);
				
				Candle candle = new Candle();
				candle.setInstrumentSpecification(spec);
				candle.setOpenPrice(open);
				candle.setHighPrice(high);
				candle.setLowPrice(low);
				candle.setClosePrice(close);
				candle.setTimeStamp(new TimeStamp(time * 1000L * 1000000L));
				candle.setVolume(volume);
				candle.setTimeFrame(timeFrame);
				
				try {
					listener.eventFired(candle);
				} catch(Exception ex) {
					log.error(ex);
					ex.printStackTrace();
				}
			}

			@Override
			public void error(int reqId, int errorCode, String message) {
				log.error("[error]: " + reqId + "/" + errorCode + "/" + message);
			}
		});
		
		log.info("calling reqRealTimeBars: reqId=" + reqId);
		ecs.reqRealTimeBars(reqId, contract, barSize, whatToShow, false);
		
		return reqId;
	}
	
	public void unsubscribeFromCandle(int reqId) {
		if(interceptors.remove(reqId) != null) {
			ecs.cancelRealTimeBars(reqId);
		}
	}
	
	public boolean isConnected() {
		return connected;
	}

	/**
	 * required thread to keep this connection open and to fetch the list
	 * of executions once every so often... 
	 */
	public void run() {
		while (true) {
			connect();
			try {
				Thread.sleep(30000);
				// check if some orders are open ....
				if(this.connected) ecs.reqExecutions(new ExecutionFilter());

			} catch (Exception x) {
				log.warn(StackTraceParser.getStackTraceMessage(x.getStackTrace()));
			}
		}
	}
	
	/**
	 * helper method. 
	 */
	public Contract getContractFromStringDefinition(String symbol) {
		StringTokenizer str = new StringTokenizer(symbol, ",");
		Contract contract = new Contract();
		contract.m_currency = str.nextToken().trim();
		contract.m_secType = str.nextToken().trim();
		contract.m_exchange = str.nextToken().trim();
		contract.m_primaryExch = contract.m_exchange;
		contract.m_symbol = str.nextToken().trim();
		contract.m_expiry = str.nextToken().trim();
		return contract;
	}
	
	/**
	 * helper method. 
	 */
	public String getContractString(Contract c){
		String ret = ""; 
		ret += c.m_currency +"/";
		ret += c.m_secType +"/";
		ret += c.m_exchange +"/";
		ret += c.m_primaryExch +"/";
		ret += c.m_symbol  +"/";
		ret += c.m_expiry  +"/";
		return ret; 
	}

	public String getVendorName() {
		return "IB";
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public int getOrderId() {
		return orderId;
	}
	
	public synchronized int getNextOrderId() {
		orderId++; 
		return orderId; 
	}

	/**
	 * adds an event listener, this is to be used only for IB specific events!!!
	 * @param listener
	 */
	public void addIBEventListener(IBEventListener listener){
		if(!ibEventListeners.contains(listener))
			this.ibEventListeners.add(listener);
	}

	// prevents from two fetch requests running simultaneously
	// TODO: allow request queueing instead of throwing an exception
	private final AtomicBoolean inFetch = new AtomicBoolean(false);
	
	public CandleSeries fetch(
			TimeStamp startDate,
			TimeStamp endDate,
			String barSize,
			String method,
			SeriesSpecification spec) throws Exception {
		
		if(startDate == null) {
			throw new NullPointerException("startDate");
		}
		
		if(endDate == null) {
			throw new NullPointerException("endDate");
		}
		
		if(method == null) {
			throw new NullPointerException("method");
		}

		if(spec == null) {
			throw new NullPointerException("spec");
		}
		
		if(inFetch.getAndSet(true)) {
			throw new IllegalStateException("another fetch request is still running. IB allows only one fetch at a time");
		}
		
		try {
			return doFetch(startDate.getDate(), endDate.getDate(), barSize, method, spec);
		} finally {
			inFetch.set(false);
		}
	}
	
	private final AtomicBoolean isFetched = new AtomicBoolean(false);
	private final AtomicReference<CandleSeries> seriesBag = new AtomicReference<CandleSeries>();

	private static final int DURATION_SECOND = 1;
	private static final int DURATION_DAY    = 24 * 60 * 60 * DURATION_SECOND;
	private static final int DURATION_WEEK   = 7 * DURATION_DAY;
	
	private int duration = 5 * DURATION_DAY;
	
	private String fetchGranularity = "5 D";
	
	/**
	 * Fetch granularity. This is a parameter that is specific to how historic data
	 * is implemented in IB. Their interface allow to request historical data
	 * ending at a particular date and having a particular duration. The duration
	 * values are limited. Therefore, long time spans are split in multiple requests
	 * each one fetching only for the duration, until all time span is covered.
	 * This duration is called <code>fetchGranularity</code>.
	 * Known values that are accepted are:
	 * "1 D" (one day), "1 W" (one week), "3600 S" (one hour).
	 * 
	 * @return fetch granularity string.
	 */
	public String getFetchGranularity() {
		return fetchGranularity;
	}
	
	/**
	 * Sets fetch granularity string. May throw an exception if invalid value
	 * is passed.
	 * 
	 * @param val fetch granularity string.
	 */
	public void setFetchGranularity(String val) {
		String [] vv = val.split(" ");
		if(vv.length != 2) {
			throw new IllegalArgumentException("failed to parse granularity.");
		}
		int value = Integer.parseInt(vv[0]);
		if("D".equals(vv[1])) {
			duration = value * DURATION_DAY;
		} else if("W".equals(vv[1])) {
			duration = value * DURATION_WEEK;
		} else if("S".equals(vv[1])) {
			duration = value * DURATION_SECOND;
		} else {
			throw new IllegalArgumentException("failed to parse units");
		}

		fetchGranularity = val;
	}

	// callback to receive historic events.
	private final EWrapper histCallback = new IBWrapper() {

		@Override
		public void historicalData(int reqId, String date, double open,
				double high, double low, double close, int volume, int count,
				double WAP, boolean hasGaps) {
			log.debug("[historiclaData] " + reqId + " / " + date + " / " + open + " / " + high + " / " + low + " / " + close + " / " + volume + " / " + count + " / " + WAP + "/" + hasGaps);

			if (open >= 0.0) {
				try {
					// checking the format found in the data set.
					TimeStampFormat tsf = (date.length()== 8)
						? new TimeStampFormat("yyyyMMdd") 
						: new TimeStampFormat("yyyyMMdd HH:mm:ss");
					tsf.setTimeZone(TimeZone.getDefault()); // FIXME: which timezone IB uses?

					// constructing a new candle.
					Candle c = new Candle();
					c.setOpenPrice(open);
					c.setHighPrice(high);
					c.setLowPrice(low);
					c.setClosePrice(close);
					c.setVolume(volume);
					c.setTimeStamp(tsf.parse(date));

					// get the current timeseries.
					CandleSeries ts = seriesBag.get();
					ts.add(0, c);
				} catch (Exception x) {
					log.warn(StackTraceParser.getStackTraceMessage(x.getStackTrace()));
				}
			} else {
				// finished
				isFetched.set(true);
				synchronized(isFetched) {
					isFetched.notifyAll();
				}
			}
		}

		@Override
		public void error(int reqId, int errorCode, String message) {
			log.info("[error]: " + reqId + " / " + errorCode + " / " + message);
				
			switch(errorCode){
			case 162:
			case 200:
			case 203:
				isFetched.set(true);
				synchronized(isFetched) {
					isFetched.notifyAll();
				}
				break;

			default:
				log.error("unhandled error: code=" + errorCode);
			}
		}
	};
	
	// IB requires to sleep at least 8 seconds before backfill requests :(
	private final static long PACING_DELAY = 8000;
	private final AtomicLong lastFetch = new AtomicLong(0L);
	private void doPacingSleep() throws InterruptedException {
		long old = lastFetch.get();
		if(old == 0L) return; // no need to sleep: first call
		
		long diff = PACING_DELAY - (System.currentTimeMillis() - old);
		if(diff > 0) Thread.sleep(diff);
	}
	
	private CandleSeries doFetch(
		Date startDate,
		Date endDate,
		String barSize,
		String method,
		SeriesSpecification query) throws Exception {
		
		connect();

		log.info("[fetch] about to fetch : " + query);
		log.info("[fetch] Data type: " + method);

		// build a new IB contract object;
		Contract contract = convertToContract(query.getInstrumentSpecification());

		System.out.println("BarSize " + barSize);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss zzz");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		log.info("[fetch] Fetching  " 
				+ getContractString(contract) + " / " + barSize + " / "
				+ method);

		// calculate the amount of days between start and end date.

		CandleSeries tempSeries = new CandleSeries();

		Calendar endCal = GregorianCalendar.getInstance(TimeZone
				.getTimeZone("UTC"));
		endCal.setTimeInMillis(endDate.getTime());

		while (endCal.getTime().compareTo(startDate) > 0) {
			
			log.info("[fetch] iterating.");

			String startString = sdf.format(endCal.getTime());
			log.info("[fetch] about to fetch a tranche " + startString);

			// fetch it.
			seriesBag.set(new CandleSeries());
			
			int tickId = generateRequestId();

			interceptors.put(tickId, histCallback);
			
			doPacingSleep();

			log.info("put into map: " + tickId + ", " + histCallback);
			
			log.info("[reqHistoricalData: " + tickId + "/" + contract + "/" + startString
					+ "/" + fetchGranularity + "/" + barSize + "/" + method);
			ecs.reqHistoricalData(tickId, contract, startString,
						fetchGranularity, barSize, method, 0, 1);
			
			isFetched.set(false);
			synchronized(isFetched) {
				try { 
					isFetched.wait(120 * 1000);
				} catch(InterruptedException ex) {
				}
			}
			if(!isFetched.get()) {
				log.error("timeout waiting for candles");
			}

			interceptors.remove(tickId);
			log.info("removed from map: " + tickId);

			log.info("[fetch] tranche fetched. About to merge. This may take a while ... Please be patient. ");
			// merge it with the temporary time series object.
			CandleSeries tempSeries2 = seriesBag.get();
			log.info("merging: base.size=" + tempSeries.size() + ", incoming.size=" + tempSeries2.size());
			tempSeries = CandleSeriesUtil.merge(tempSeries, tempSeries2);

			log.info("[fetch] timeseries merged. result.size=" + tempSeries.size());

			lastFetch.set(System.currentTimeMillis());
			endCal.add(Calendar.SECOND, -duration);
		}
			
		log.info("[fetch] CandleSeries fetched.");

		for (Candle c : tempSeries) {
			c.setHighTimeStamp(c.getTimeStamp());
			c.setLowTimeStamp(c.getTimeStamp());
			c.setTimeFrame(query.getTimeFrame());
			c.setInstrumentSpecification(query.getInstrumentSpecification());
		}

		// setting the series specification.  
		tempSeries.setSeriesSpecification(query);
		
		// disconnect
		return tempSeries;
	}

	private static Contract convertToContract(InstrumentSpecification spec){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		com.ib.client.Contract newContract = new com.ib.client.Contract();
		newContract.m_currency = spec.getCurrency();
		newContract.m_secType = spec.getSecurityType();
		newContract.m_exchange = spec.getExchange();
		newContract.m_primaryExch = spec.getExchange();
		newContract.m_symbol = spec.getSymbol().toString();
		if(spec.getExpiry() != null){
			newContract.m_expiry = sdf.format(spec.getExpiry().getTimeStamp().getDate());
		}
		if(spec.hasStrike()){
			newContract.m_strike = spec.getStrike();
		}
		if(spec.hasContractRight()){
			newContract.m_right = spec.getContractRight();
		}
		return newContract;
	}
	
	public static InstrumentSpecification convertToInstrument(Contract contract) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		InstrumentSpecification spec = new InstrumentSpecification();
		
		String cur = contract.m_currency;
		spec.setCurrency(cur);
		
		String symbol = contract.m_symbol;
		spec.setSymbol(new Symbol(symbol));
		
		if(contract.m_strike!=0.0){
			spec.setStrike(contract.m_strike);
		}
		
		spec.setSecurityType(contract.m_secType);
		
		// the expiry
		if(contract.m_expiry != null){
			spec.setExpiry(new Expiry(new TimeStamp(sdf.parse(contract.m_expiry))));
		}
		
		if(contract.m_primaryExch != null){
			spec.setExchange(contract.m_primaryExch);
		}
		
		if(contract.m_right != null){
			spec.setContractRight(contract.m_right);
		}
		
		return spec;
	}

	public static String timeFrameToBarSize(TimeFrame timeFrame) {
		String out = Integer.toString(timeFrame.length);
		switch (timeFrame.unit) {
		case SECOND: 
			return out + " sec";
		case MINUTE:
			if(timeFrame.length>1)
				return out + " mins";
			else
				return out + " min";
		case HOUR:
			return out + " hour";
		case DAY:
			return out + " day";
		case WEEK:
			return out + " week";
		case MONTH:
			if(timeFrame.length>1)
				return out + " months";
			else 
				return out + " month";
		case YEAR:
			return out + " year";
		}
		
		throw new IllegalArgumentException("unsupported time frame: " + timeFrame);
	}

	public static int timeFrameToBarSizeCode(TimeFrame timeFrame) {
		switch (timeFrame.unit) {
		case SECOND:
			if(timeFrame.length == 1) {
				return 1;
			} else if(timeFrame.length == 5) {
				return 2;
			} else if(timeFrame.length == 15) {
				return 3;
			} else if(timeFrame.length == 30) {
				return 4;
			} 
			break;
		case MINUTE:     
			if(timeFrame.length == 1) {
				return 5;
			} else if(timeFrame.length == 2) {
				return 6;
			} else if(timeFrame.length == 5) {
				return 7;
			} else if(timeFrame.length == 15) {
				return 8;
			} else if(timeFrame.length == 30) {
				return 9;
			}
			break;
		case HOUR:
			if(timeFrame.length == 1) {
				return 10;
			}
			break;
		case DAY:
			if(timeFrame.length == 1) {
				return 11;
			}
			break;
		case WEEK:
			if(timeFrame.length == 1) {
				return 12;
			}
			break;
		case MONTH:
			if(timeFrame.length == 1) {
				return 13;
			} else if(timeFrame.length == 3) {
				return 14;
			}
			break;
		case YEAR:
			if(timeFrame.length == 1) {
				return 15;
			}
			break;
		}
		
		throw new IllegalArgumentException("unsupported time frame: " + timeFrame);
	}
	
/*
	// Broker methods
	private com.ib.client.Order convertToTwsOrder(org.activequant.tradesystem.domainmodel.Order o){
		// need to convert the order into an IB order object.

		com.ib.client.Order twsOrder = new com.ib.client.Order();
		// get the next implementation specific order id and set it in the order
		// object.
		int orderId = getNextOrderId(); // generate next valid order id
		o.setOriginalId((long) orderId);

		twsOrder.m_orderId = orderId;
		twsOrder.m_clientId = getClientId();
		twsOrder.m_transmit = true;
		
		switch(o.getSide()) {
		case BUY:
			twsOrder.m_action = "BUY";
			break;
		case SELL:
			twsOrder.m_action = "SELL";
			break;
		case SHORT_SELL: case SHORT_SELL_EXEMPT:
			twsOrder.m_action = "SSHORT";
			break;
		default:
			throw new IllegalArgumentException("unsupported order side: " + o);
		}

		// set the quantity. IB supports only integers.
		twsOrder.m_totalQuantity = (int) o.getQuantity();

		switch(o.getType()) {
		case MARKET:
			twsOrder.m_orderType = "MKT";
			break;
			
		case LIMIT:
			twsOrder.m_orderType = "LMT";
			twsOrder.m_lmtPrice = o.getLimit();
			break;
			
		case STOP:
			twsOrder.m_orderType = "STP";
			twsOrder.m_auxPrice = o.getStop();
			break;
			
		case STOP_LIMIT:
			twsOrder.m_orderType = "STPLMT";
			twsOrder.m_lmtPrice = o.getLimit();
			break;
			
		default:
			throw new IllegalArgumentException("unsupported order type: " + o);
		}
		
		return twsOrder;
	}
*/
	
	public void placeOrder(InstrumentSpecification spec, com.ib.client.Order twsOrder) throws Exception {
		log.info("placing tws order " + twsOrder.m_orderId);
		ecs.placeOrder(twsOrder.m_orderId, convertToContract(spec), twsOrder);
	}

	public void cancelOrder(int twsOrderId) throws Exception {
		log.info("canceling tws order " + twsOrderId);
		ecs.cancelOrder(twsOrderId);
	}
}
