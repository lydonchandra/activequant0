/*******************************************************************************
 * Copyright (c) quickfixengine.org  All rights reserved. 
 * 
 * This file is part of the QuickFIX FIX Engine 
 * 
 * This file may be distributed under the terms of the quickfixengine.org 
 * license as defined by quickfixengine.org and appearing in the file 
 * LICENSE included in the packaging of this file. 
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING 
 * THE WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 * 
 * See http://www.quickfixengine.org/LICENSE for licensing information. 
 * 
 * Contact ask@quickfixengine.org if any conditions of this licensing 
 * are not clear to you.
 ******************************************************************************/

package org.activequant.data.retrieval.integration.quickfix;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Quote;
import org.activequant.core.domainmodel.TradeIndication;
import org.activequant.core.types.TimeStamp;
import org.activequant.util.pattern.events.Event;
import org.activequant.util.pattern.events.IEventListener;
import org.activequant.util.tools.StackTraceParser;
import org.apache.log4j.Logger;

import quickfix.Application;
import quickfix.CharField;
import quickfix.DefaultMessageFactory;
import quickfix.DoNotSend;
import quickfix.DoubleField;
import quickfix.FieldNotFound;
import quickfix.FileStoreFactory;
import quickfix.Group;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.MessageFactory;
import quickfix.RejectLogon;
import quickfix.ScreenLogFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.UnsupportedMessageType;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.MDReqID;
import quickfix.field.MDUpdateType;
import quickfix.field.MarketDepth;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.Password;
import quickfix.field.QuoteType;
import quickfix.field.Side;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import quickfix.field.Username;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.Logon;
import quickfix.fix44.MarketDataRequest;
import quickfix.fix44.MarketDataSnapshotFullRefresh;
import quickfix.fix44.QuoteStatusReport;

/**
 * Quote and TradeIndication feed via FIX protocol. Uses Quickfix
 * engine to do the protocol-related job.
 * <br>
 * FIXME: UNTESTED!
 * <br>
 * <b>History:</b><br>
 *  - [10.09.2007] Created (?)<br>
 *  - [29.09.2007] cleanup + moved to new domain model (Erik Nijkamp)<br>
 *  - [04.11.2007] converted to per-instument feed (Mike Kroutikov)<br>
 *  - [09.11.2007] class layout + exception handling (Erik Nijkamp)<br>
 *
 *  @author ?
 */
public class QuickfixConnection implements Application {
	
    private static Logger log = Logger.getLogger(QuickfixConnection.class);

	private SessionID sessionId = null;
    private Event<Quote> quoteEvent = new Event<Quote>();    
    private final AtomicInteger reqIdCounter = new AtomicInteger(0);
    private String username = null; 
    private String password = null;
    private final Map<String,IMarketDataSnapshotFullRefreshDispatcher> disp = 
    	new ConcurrentHashMap<String,IMarketDataSnapshotFullRefreshDispatcher>();
    
    private final MessageCracker cracker = new MessageCracker() {

    	@Override
    	public void onMessage(MarketDataSnapshotFullRefresh message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
    		handleOnMessage(message, sessionID);
    	}

     	@Override
        public void onMessage(QuoteStatusReport message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
    		handleOnMessage(message, sessionID);
        }    

    	@Override
        public void onMessage(ExecutionReport message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
    		handleOnMessage(message, sessionID);
    	}

    	/*
    	 * FIXME
    	 *
    	@Override
        public void onMessage(Notification message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
            log.info("Notification:" + message.getText().getValue());
        }
         *
         */

    	@Override
        public void onMessage(quickfix.fix44.Logout message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
            log.info("Logged out");
        }

    	/*
    	 * FIXME
    	 *
    	@Override
        public void onMessage(AccountInfo accountInfo, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
            log.info("accountInfo = " + accountInfo);
        }
        *
        */
        
    };
    
    private interface IMarketDataSnapshotFullRefreshDispatcher {
    	public void handleOnMessage(MarketDataSnapshotFullRefresh message) throws Exception;
    	public void cancel() throws SessionNotFound;
    }

    public void onCreate(SessionID sessionID) {
        log.info("Feed session created");
    }

    public void onLogon(SessionID sessionID) {
        log.info("Feed client logged in. Session id assigned: " + sessionID);
        this.sessionId = sessionID;
    }  

    private String generateReqId() {
    	int id = reqIdCounter.incrementAndGet();
    	return sessionId.toString() + ":" + id;
    }

    public String subscribeToQuote(final InstrumentSpecification spec, 
    		final IEventListener<Quote> listener) throws SessionNotFound {
    	final String reqId = generateReqId();
    	
        MarketDataRequest marketDataRequest = new MarketDataRequest(
        		new MDReqID(reqId), 
        		new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES), 
        		new MarketDepth(1)
        );
        marketDataRequest.set(new MDUpdateType(MDUpdateType.FULL_REFRESH));
        MarketDataRequest.NoMDEntryTypes et = new MarketDataRequest.NoMDEntryTypes();
        et.set(new MDEntryType(MDEntryType.BID));
        marketDataRequest.addGroup(et);
        et.set(new MDEntryType(MDEntryType.OFFER));
        marketDataRequest.addGroup(et);
        MarketDataRequest.NoRelatedSym rs = new MarketDataRequest.NoRelatedSym();
        rs.set(new Symbol(spec.getSymbol().toString()));
        marketDataRequest.addGroup(rs);

        Session.sendToTarget(marketDataRequest, sessionId);
        
        IMarketDataSnapshotFullRefreshDispatcher d = new IMarketDataSnapshotFullRefreshDispatcher() {

			public void cancel() throws SessionNotFound {
		        MarketDataRequest marketDataRequest = new MarketDataRequest(
		        		new MDReqID(reqId), 
		        		new SubscriptionRequestType(SubscriptionRequestType.DISABLE_PREVIOUS_SNAPSHOT_PLUS_UPDATE_REQUEST), 
		        		new MarketDepth(1)
		        );
		        marketDataRequest.set(new MDUpdateType(MDUpdateType.FULL_REFRESH));
		        MarketDataRequest.NoMDEntryTypes et = new MarketDataRequest.NoMDEntryTypes();
		        et.set(new MDEntryType(MDEntryType.BID));
		        marketDataRequest.addGroup(et);
		        et.set(new MDEntryType(MDEntryType.OFFER));
		        marketDataRequest.addGroup(et);
		        MarketDataRequest.NoRelatedSym rs = new MarketDataRequest.NoRelatedSym();
		        rs.set(new Symbol(spec.getSymbol().toString()));
		        marketDataRequest.addGroup(rs);
		        
				Session.sendToTarget(marketDataRequest, sessionId);
			}

			public void handleOnMessage(MarketDataSnapshotFullRefresh message) throws Exception {
				Quote q = parseQuoteMessage(message, spec);
	        	listener.eventFired(q);
			}
        };
        
    	disp.put(reqId, d);
        
        return reqId;
    }
    
    public String subscribeToTrade(final InstrumentSpecification spec, 
    		final IEventListener<TradeIndication> listener) throws SessionNotFound {
    	final String reqId = generateReqId();
    	
        MarketDataRequest marketDataRequest = new MarketDataRequest(
        		new MDReqID(reqId), 
        		new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES), 
        		new MarketDepth(1)
        );
        marketDataRequest.set(new MDUpdateType(MDUpdateType.FULL_REFRESH));
        MarketDataRequest.NoMDEntryTypes et = new MarketDataRequest.NoMDEntryTypes();
        et.set(new MDEntryType(MDEntryType.TRADE));
        marketDataRequest.addGroup(et);
        MarketDataRequest.NoRelatedSym rs = new MarketDataRequest.NoRelatedSym();
        rs.set(new Symbol(spec.getSymbol().toString()));
        marketDataRequest.addGroup(rs);

        Session.sendToTarget(marketDataRequest, sessionId);
        
        IMarketDataSnapshotFullRefreshDispatcher d = new IMarketDataSnapshotFullRefreshDispatcher() {

			public void cancel() throws SessionNotFound {
		        MarketDataRequest marketDataRequest = new MarketDataRequest(
		        		new MDReqID(reqId), 
		        		new SubscriptionRequestType(SubscriptionRequestType.DISABLE_PREVIOUS_SNAPSHOT_PLUS_UPDATE_REQUEST), 
		        		new MarketDepth(1)
		        );
		        marketDataRequest.set(new MDUpdateType(MDUpdateType.FULL_REFRESH));
		        MarketDataRequest.NoMDEntryTypes et = new MarketDataRequest.NoMDEntryTypes();
		        et.set(new MDEntryType(MDEntryType.TRADE));
		        marketDataRequest.addGroup(et);
		        MarketDataRequest.NoRelatedSym rs = new MarketDataRequest.NoRelatedSym();
		        rs.set(new Symbol(spec.getSymbol().toString()));
		        marketDataRequest.addGroup(rs);
				Session.sendToTarget(marketDataRequest, sessionId);
			}

			public void handleOnMessage(MarketDataSnapshotFullRefresh message) throws Exception {
				TradeIndication t = parseTradeMessage(message, spec);
	        	listener.eventFired(t);
			}
        };
        
    	disp.put(reqId, d);
        
        return reqId;
    }

    private Quote parseQuoteMessage(MarketDataSnapshotFullRefresh message, InstrumentSpecification spec) 
    		throws Exception {
        
        Quote q = new Quote(spec);
        
        Iterator<?> groupsKeys = message.groupKeyIterator();
        while (groupsKeys.hasNext()) {
            int i = 1;
            int groupCountTag = (Integer) groupsKeys.next();
            Group group = new Group(groupCountTag, 0);
            while (message.hasGroup(i, groupCountTag)) {
                message.getGroup(i, group);
                CharField charField = group.getField(new CharField(MDEntryType.FIELD));
                DoubleField price   = group.getField(new DoubleField(MDEntryPx.FIELD));
                DoubleField value   = group.getField(new DoubleField(MDEntrySize.FIELD));
                
                // expect only one bid and one offer values
                char eventType = charField.getValue();
                switch(eventType) {
                case MDEntryType.OFFER:
                	if(q.getAskPrice() != Quote.NOT_SET) {
                		log.error("duplicate ASK entry in qfx group");
                	}
                	q.setAskPrice(price.getValue());
                	q.setAskQuantity(value.getValue());
                	break;
                case MDEntryType.BID:
                	if(q.getBidPrice() != Quote.NOT_SET) {
                		log.error("duplicate BID entry in qfx group");
                	}
                	q.setBidPrice(price.getValue());
                	q.setBidQuantity(value.getValue());
                	break;
                default: 
                	log.warn("unrecognized field type in quote response " + eventType);
                }
                i++;
            }
        }

        q.setTimeStamp(new TimeStamp());
        
        return q;
    }
    
    private TradeIndication parseTradeMessage(MarketDataSnapshotFullRefresh message,
			InstrumentSpecification spec) throws Exception {

		TradeIndication t = new TradeIndication(spec);

		Iterator<?> groupsKeys = message.groupKeyIterator();
		while (groupsKeys.hasNext()) {
			int i = 1;
			int groupCountTag = (Integer) groupsKeys.next();
			Group group = new Group(groupCountTag, 0);
			while (message.hasGroup(i, groupCountTag)) {
				message.getGroup(i, group);
				CharField charField = group.getField(new CharField(
						MDEntryType.FIELD));
				DoubleField price = group.getField(new DoubleField(
						MDEntryPx.FIELD));
				DoubleField value = group.getField(new DoubleField(
						MDEntrySize.FIELD));

				// expect only one trade?
				char eventType = charField.getValue();
				switch (eventType) {
				case MDEntryType.TRADE:
					if (t.getPrice() != Quote.NOT_SET) {
						log.error("duplicate TRADE entry in qfx group");
					}
					t.setPrice(price.getValue());
					t.setQuantity((long) value.getValue());
					break;
				default:
					log.warn("unrecognized field type in quote response " + eventType);
				}
				i++;
			}
		}

		t.setTimeStamp(new TimeStamp());

		return t;
	}

    public void cancelRequest(String reqId) throws Exception {
    	
    	IMarketDataSnapshotFullRefreshDispatcher d = disp.remove(reqId);
    	if(d != null) {
    		d.cancel();
    		return;
    	}
    	
    	log.warn("cancel: id not found: " + reqId);
    }


    public void onLogout(SessionID sessionID) {
        log.info("[onLogout]: Feed logged out");
    }

    public void toAdmin(Message message, SessionID sessionID) {
    	log.info("[toAdmin]: " + message + ", sessionId=" + sessionID);
    	if (message instanceof Logon) {
    		Logon logon = (Logon) message;
            logon.set(new Username(username));
            logon.set(new Password(password));
                
            log.info("[toAdmin]: " + message + ", sessionId=" + sessionID);
    	} 
    }

    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
    	log.info("[fromAdmin]: " + message + ", sessionId=" + sessionID);
    }

    public void toApp(Message message, SessionID sessionID) throws DoNotSend {
    	log.info("[toApp]: " + message + ", sessionId=" + sessionID);
    }

    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
    	log.info("[fromApp]: " + message + ", sessionId=" + sessionID);
        cracker.crack(message, sessionID);
    }
    
    /** 
     * bid and ask come through this method. 
     */
	private void handleOnMessage(MarketDataSnapshotFullRefresh message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
    	log.info("[onMessage]: " + message + ", sessionId=" + sessionID);
    	
        MDReqID reqId = message.get(new MDReqID());
        
        IMarketDataSnapshotFullRefreshDispatcher d = disp.get(reqId.getValue());
        if(d != null) {
        	try { d.handleOnMessage(message); }
        	catch(Exception ex) { 
        		log.warn(ex); 
        		log.warn(StackTraceParser.getStackTrace(ex)); 
        	}
        	return;
        }

        log.warn("orphan message: " + message + ", sessionId=" + sessionID);

    }

    private void handleOnMessage(QuoteStatusReport message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        Symbol instrument = message.get(new Symbol());
        QuoteType quoteType = message.get(new QuoteType());
        log.debug(instrument + ":" + (quoteType.getValue() == QuoteType.TRADEABLE ? "Tradable" : "Not tradable"));
    }    

    private void handleOnMessage(ExecutionReport message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
    	log.info("Execution Report received.");
        OrderID orderID = message.get(new OrderID());
        Symbol symbol = message.get(new Symbol());
        Side side = message.get(new Side());
        OrdStatus ordStatus = message.get(new OrdStatus());
        AvgPx priceClient = message.get(new AvgPx());
        CumQty cumQty = message.get(new CumQty());
        OrdType ordType = message.get(new OrdType());
        ClOrdID clOrdID = message.get(new ClOrdID());
        Map<String, String> newOrder = new HashMap<String, String>();
        newOrder.put("id", orderID.getValue());
        newOrder.put("execId", message.getString(ExecID.FIELD));
        newOrder.put("clId", clOrdID.getValue());
        newOrder.put("instrument", symbol.getValue());
        newOrder.put("type", OrdType.STOP_LIMIT == ordType.getValue() ? "Order" : "Quote");
        newOrder.put("price", Double.toString(priceClient.getValue()));
        newOrder.put("exAmount", Double.toString(cumQty.getValue()));
        newOrder.put("state", getOrderStatus(ordStatus));
        
        // FIXME: double-check the following -mpk
        if (OrdType.PREVIOUSLY_QUOTED == ordType.getValue()) {
            newOrder.put("date", message.getExpireTime().getValue().toString());
            newOrder.put("side", side.valueEquals(Side.BUY) ? "BID" : "OFFER");
        } else {
            newOrder.put("side", side.valueEquals(Side.BUY) ? "BUY" : "SELL");
            newOrder.put("date", message.getTransactTime().getValue().toString());
        }

        for (String key : newOrder.keySet()) {
        	log.info(key + ":" + newOrder.get(key));
        }
    }

    private String getOrderStatus(OrdStatus ordStatus) {
        char value = ordStatus.getValue();
        if (OrdStatus.CANCELED == value) {
            return "Cancelled";
        } else if (OrdStatus.NEW == value) {
            return "New";
        } else if (OrdStatus.CALCULATED == value) {
            return "Calculated";
        } else if (OrdStatus.FILLED == value) {
            return "Filled";
        } else if (OrdStatus.REJECTED == value) {
            return "Rejected";
        } else if (OrdStatus.PENDING_NEW == value) {
            return "Pending";
        } else {
            return "Unknown";
        }
    }

    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Event<Quote> getQuoteEvent() {
		return quoteEvent;
	}

	public SessionID getSessionId() {
		return sessionId;
	}

	public void setSessionId(SessionID sessionId) {
		this.sessionId = sessionId;
	}

	public static void main(String ... av) throws Exception {
		String settingsFilename = "/tmp/qfx.config";
		SessionSettings sessionSettings = new SessionSettings(settingsFilename);
        LogFactory logFactory = new ScreenLogFactory(sessionSettings);
        FileStoreFactory storeFactory = new FileStoreFactory(sessionSettings);
        MessageFactory messageFactory = new DefaultMessageFactory();
        
        QuickfixConnection app = new QuickfixConnection();
        app.setUsername("marketcetera");
        app.setPassword("marketcetera");
        
        Initiator initiator = new SocketInitiator(
        		app, 
        		storeFactory, 
        		sessionSettings, 
        		logFactory, 
        		messageFactory);
        initiator.start();
        
        log.info("started");
        
        Thread.sleep(Integer.MAX_VALUE);
	}
}
