package org.activequant.data.retrieval.integration.quickfix.test;

import org.apache.log4j.Logger;

import quickfix.ApplicationAdapter;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecType;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.Price;

/**
 * Mock FIX server for embedding into the Junit tests.
 * <br>
 * <b>History:</b><br>
 *  - [25.11.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class MockFixServer extends ApplicationAdapter {
	
	private final Logger log = Logger.getLogger(getClass());
	
	private final MessageCracker cracker = new MessageCracker() {

	    public void onMessage(quickfix.fix44.NewOrderSingle order, SessionID sessionID) 
	    	throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
    
	    	OrderQty orderQty = order.getOrderQty();
	    	Price price = new Price(order.getDouble(Price.FIELD));

	    	quickfix.fix43.ExecutionReport accept = new quickfix.fix43.ExecutionReport(
	    			genOrderID(), 
	    			genExecID(),
	    			new ExecType(ExecType.FILL), 
	    			new OrdStatus(OrdStatus.NEW), 
	    			order.getSide(), 
	    			new LeavesQty(0),
	    			new CumQty(0), 
	    			new AvgPx(0)
	    			);

	    	accept.set(order.getClOrdID());
	    	sendMessage(sessionID, accept);

    		quickfix.fix44.ExecutionReport executionReport = new quickfix.fix44.ExecutionReport(
    				genOrderID(),
	    			genExecID(), 
	    			new ExecType(ExecType.FILL), 
	    			new OrdStatus(OrdStatus.FILLED), 
	    			order.getSide(),
	    			new LeavesQty(0), 
	    			new CumQty(orderQty.getValue()), 
	    			new AvgPx(price.getValue())
	    			);

    		executionReport.set(order.getClOrdID());
    		executionReport.set(order.getSymbol());
    		executionReport.set(orderQty);
    		executionReport.set(new LastQty(orderQty.getValue()));
    		executionReport.set(new LastPx(price.getValue()));

    		sendMessage(sessionID, executionReport);
	    }
	};
	
    private void sendMessage(SessionID sessionID, Message message) {
        try {
        	log.info("[sendMessage]: message=" + message + ", sessionID=" + sessionID);
            Session.sendToTarget(message, sessionID);
        } catch (SessionNotFound e) {
        	e.printStackTrace();
            log.error(e.getMessage(), e);
        }
    }

    private int m_orderID = 0;
    public OrderID genOrderID() {
        return new OrderID(Integer.valueOf(++m_orderID).toString());
    }

    private int m_execID = 0;
    public ExecID genExecID() {
        return new ExecID(Integer.valueOf(++m_execID).toString());
    }
	
	@Override
	public void fromAdmin(Message message, SessionID sessionID)
			throws FieldNotFound, IncorrectDataFormat,
			IncorrectTagValue, RejectLogon {
		log.info("[fromAdmin]: message=" + message + ", sessionID=" + sessionID);
	}

	@Override
	public void fromApp(Message message, SessionID sessionID)
			throws FieldNotFound, IncorrectDataFormat,
			IncorrectTagValue, UnsupportedMessageType {
		log.info("[fromApp]: message=" + message + ", sessionID=" + sessionID);
		cracker.crack(message, sessionID);
	}

	@Override
	public void onCreate(SessionID sessionID) {
		log.info("[onCreate]: sessionID=" + sessionID);
	}

	@Override
	public void onLogon(SessionID sessionID) {
		log.info("[onLogon]: sessionID=" + sessionID);
	}

	@Override
	public void onLogout(SessionID sessionID) {
		log.info("[onLogout]: sessionID=" + sessionID);
	}

	@Override
	public void toAdmin(Message message, SessionID sessionID) {
		log.info("[toAdmin]: message=" + message + ", sessionID=" + sessionID);
	}

	@Override
	public void toApp(Message message, SessionID sessionID) throws DoNotSend {
		log.info("[toApp]: message=" + message + ", sessionID=" + sessionID);
	}
}
