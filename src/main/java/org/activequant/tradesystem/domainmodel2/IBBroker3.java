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
package org.activequant.tradesystem.domainmodel2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.types.TimeStamp;
import org.activequant.core.util.TimeStampFormat;
import org.activequant.data.retrieval.integration.ib.IBEventListener;
import org.activequant.data.retrieval.integration.ib.IBTwsConnection;
import org.activequant.data.util.UniqueDateGenerator;
import org.activequant.tradesystem.domainmodel2.event.OrderCancelEvent;
import org.activequant.tradesystem.domainmodel2.event.OrderErrorEvent;
import org.activequant.tradesystem.domainmodel2.event.OrderExecutionEvent;
import org.activequant.tradesystem.domainmodel2.event.OrderRejectEvent;
import org.activequant.tradesystem.domainmodel2.event.OrderUpdateEvent;

import com.ib.client.Contract;

/**
 * a broker implementation for trading through ib. <br>
 * <br>
 * <b>History:</b><br> 
 * - [05.07.2007] Created (Ulrich Staudinger)<br> 
 * - [06.11.2007] adding order id mapping (Ulrich Staudinger)<br>
 * - [06.11.2007] Adding manual trailing stop facility (Ulrich Staudinger)<br>
 * - [13.11.2007] Order id cleanup (Erik Nijkamp)<br>
 * - [14.11.2007] Move to latest broker code (Mike Kroutikov) <br>
 * - [09.12.2007] Converted to light-weight broker api (Mike Kroutikov) <br>
 * 
 * @author Ulrich Staudinger
 * @author Mike Kroutikov
 */
public class IBBroker3 extends BrokerBase {

	private IBTwsConnection connection = null;
	
	public IBTwsConnection getConnection() {
		return connection;
	}
	public void setConnection(IBTwsConnection connection) {
		this.connection = connection;
	}

	private final IBEventListener ibEventListener  = new IBEventListener() {

		public void execDetails(int orderId, com.ib.client.Contract ibContract, com.ib.client.Execution ibExec) {
			OrderTracker tracker = orderMap.get(orderId);
			if(tracker != null) {
				tracker.handleExecDetails(ibContract, ibExec);
				return;
			}
			
			log.warn("orphan exec detail event: orderId=" + orderId + ", contract=" + ibContract + ", exec=" + ibExec);
		}

		public void orderStatus(int orderId, String status, int filled, int remaining,
				double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId) {
			OrderTracker tracker = orderMap.get(orderId);
			if(tracker != null) {
				tracker.handleOrderStatus(status, filled, remaining, 
						avgFillPrice, permId, parentId, lastFillPrice, clientId);
				return;
			}
			
			log.warn("orphaned event [orderStatus] " + orderId + " / " + filled);
		}

		public void updateAccountValue(String informationType, String amount,
				String currency, String account) {
			// FIXME: someday
		}

		public void updatePortfolio(com.ib.client.Contract instrument, int positionCount,
				double marketPrice, double marketValue, double avgCost,
				double unrealizedPNL, double realizedPNL, String accountName) {
			// FIXME: handleUpdatePortfolio(instrument, positionCount, marketPrice, marketValue, avgCost, unrealizedPNL, realizedPNL, accountName);
		}

		public void error(int orderId, int errorCode, String message) {
			OrderTracker tracker = orderMap.get(orderId);
			if(tracker != null) {
				tracker.error(errorCode, message);
				return;
			}
			
			log.warn("orphaned event [error] " + orderId + ", code=" + errorCode + ", " + message);
		}

		public void openOrder(int orderId, Contract contract,
				com.ib.client.Order order) {
			OrderTracker tracker = orderMap.get(orderId);
			if(tracker != null) {
				tracker.openOrder(contract, order);
				return;
			}
			
			log.warn("orphaned event [error] " + orderId + ", contract=" + contract + ", " + order);
		}
	};

	// for decoding events
	private final Map<Integer,OrderTracker> orderMap = new ConcurrentHashMap<Integer,OrderTracker>();
	
	public IBBroker3(IBTwsConnection connection) {
		this.connection = connection;
		connection.connect();
		connection.addIBEventListener(ibEventListener);
	}

	/**
	 * this method converts an AQ order to a broker specific TWS order object.  
	 * @param o
	 * @return
	 */
	private com.ib.client.Order convertToTwsOrder(Order o, int orderId){
		// need to convert the order into an IB order object.

		com.ib.client.Order twsOrder = new com.ib.client.Order();

		twsOrder.m_orderId = orderId;
		twsOrder.m_clientId = connection.getClientId();
		twsOrder.m_transmit = true;
		switch(o.getOrderSide()) {
		case BUY:
			twsOrder.m_action = "BUY";
			break;
		case SELL:
			twsOrder.m_action = "SELL";
			break;
		case SHORT_SELL:
			twsOrder.m_action = "SSHORT";
			break;
		case SHORT_SELL_EXEMPT:
			twsOrder.m_action = "SSHORT";
			break;
		default:
			throw new IllegalArgumentException("unsupported order side value: " + o.getOrderSide());
		} 

		// set the quantity. IB supports only integers.
		twsOrder.m_totalQuantity = (int) o.getQuantity();

		switch(o.getOrderType()) {
		case MARKET: 
			twsOrder.m_orderType = "MKT";
			break;
		case LIMIT: 
			twsOrder.m_orderType = "LMT";
			twsOrder.m_lmtPrice = o.getLimitPrice();
			break;
		case STOP: 
			twsOrder.m_orderType = "STP";
			twsOrder.m_auxPrice = o.getStopPrice();
			break;
		case STOP_LIMIT:
			twsOrder.m_orderType = "STPLMT";
			twsOrder.m_lmtPrice = o.getLimitPrice();
			break;
		case TRAILING_STOP:
			twsOrder.m_auxPrice = o.getTrailingDistance();
			if(o.getLimitPrice() > 0.0 && o.getStopPrice() > 0) {
				// TODO: this needs more research
				twsOrder.m_orderType      = "TRAILLIMIT";
				twsOrder.m_lmtPrice       = o.getLimitPrice();
				twsOrder.m_trailStopPrice = o.getStopPrice();
			} else {
				twsOrder.m_orderType = "TRAIL";
			}
			break;
		default:
			throw new IllegalArgumentException("order type: " + o.getOrderType() + " is not supported by this broker");
		}
		
		return twsOrder;
	}

	private final UniqueDateGenerator timeStampGenerator = new UniqueDateGenerator();
	private synchronized TimeStamp currentTimeStamp() {
		return timeStampGenerator.generate(new Date());
	}

	class OrderTracker extends OrderTrackerBase {
		
		private final int orderId;
		private final InstrumentSpecification spec;
		
		public OrderTracker(Order order) {
			super(order);
			orderId = connection.getNextOrderId();
			spec = order.getInstrumentSpecification();
			orderMap.put(orderId, this);
		}

		protected String handleSubmit() {
			try {
				com.ib.client.Order twsOrder = convertToTwsOrder(getOrder(), orderId);
				connection.placeOrder(spec, twsOrder);
				return "IB-" + Integer.toString(orderId);
			} catch(Exception ex) {
				throw new RuntimeException(ex);
			}
		}

		public void handleUpdate(Order newOrder) {
			if(!pendingUpdate.compareAndSet(null, newOrder)) {
				throw new IllegalStateException("another update is pending: can not proceed");
			}
			
			try {
				com.ib.client.Order twsOrder = convertToTwsOrder(newOrder, orderId);
				connection.placeOrder(spec, twsOrder);
			} catch(Exception ex) {
				pendingUpdate.set(null);
				throw new RuntimeException(ex);
			}
		}

		public void handleCancel() {
			try {
				connection.cancelOrder(orderId);
			} catch(Exception ex) {
				throw new RuntimeException(ex);
			}
		}

		private final TimeStampFormat sdf = new TimeStampFormat("yyyyMMdd  HH:mm:ss");
		{
			// NB. tws sends date in the timezone of local machine!
			sdf.setTimeZone(TimeZone.getDefault());
		}
		
		private void handleExecDetails(com.ib.client.Contract ibContract, com.ib.client.Execution ibExec) {
			try {
				SimpleDateFormat ff = new SimpleDateFormat("yyyyMMdd   HH:mm:ss");
				ff.setTimeZone(TimeZone.getDefault());
				
				// FIXME: should i use IB date or my date? not sure
				
				log.info("handleExecDetails: exec_id=" + ibExec.m_execId + ", symbol=" + ibContract.m_symbol + ", shares=" + ibExec.m_shares + ", price=" + ibExec.m_price + ", time=" + ibExec.m_time);
				OrderExecutionEvent execution = new OrderExecutionEvent();
				// parse the execution date.
//				TimeStamp stamp = timeStampGenerator.generate(sdf.parse(ibExec.m_time).getDate());
//				log.info("compare stamps: " + stamp + ", now=" + new TimeStamp());
//				log.info("in text: " + sdf.format(stamp) + " " + sdf.format(new TimeStamp()));

				// does not use ib-supplied time!
				execution.setEventTimeStamp(currentTimeStamp());
				execution.setExecutionPrice(ibExec.m_price);
				execution.setExecutionQuantity(ibExec.m_shares);
				
				fireOrderEvent(execution);
			} catch (Exception x) {
				log.warn("[execDetails] ", x);
			}
		}
		
		private void handleOrderStatus(String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId) {
			try {
				log.info("[orderStatus] " + orderId + ": statis=" + status 
						+ ", filled=" + filled + ", remaining=" + remaining
						+ ", avgFillPrice=" + avgFillPrice + ", permId=" + permId
						+ ", parentId=" + parentId + ", lastFillPrice=" + lastFillPrice
						+ ", clientId=" + clientId);
				if (status.equals("filled")) {
					// partial fill.
				} else if (status.equals("Filled")) {
					// complete fill.
				} else if (status.equals("Submitted")) {
					// order accepted through IB and working.
				} else if (status.equals("PreSubmitted")) {
					// order accepted through IB and working.
				} else if (status.equals("PendingCancel")) {
					// cancel request received
				} else if (status.equals("Cancelled")) {
					// canceled upon user's request.
					super.fireOrderEvent(new OrderCancelEvent(
							currentTimeStamp(),
							"at your request"
							)
					);
					return;
				}
				
				// if not cancel confirmation, check for the pending
				// update: if there, it means that no error has been issued
				// between update was sent and this "orderStatus" has been
				// received. This we use as indication that order update was
				// successful.
				Order updatedOrder = pendingUpdate.getAndSet(null);
				if(updatedOrder != null) {
					super.fireOrderEvent(new OrderUpdateEvent(
							currentTimeStamp(),
							"",
							updatedOrder
						)
					);
				}
			} catch (Exception x) {
				log.debug("[orderStatus] details about an already filled order received. Could have been sent from another or and old IB connection.");
			}
		}
		
		private final AtomicReference<Order> pendingUpdate = new AtomicReference<Order>();

		private void error(int errorCode, String message) {
			log.info("[error] " + orderId + " / " + message + "(" + errorCode + ")");
			if(errorCode == 202) {
				// canceled from IB side
				super.fireOrderEvent(new OrderCancelEvent(
						currentTimeStamp(),
						message + " (IB error code " + errorCode + ")"
						)
				);
			} else if(errorCode == 201) {
				// rejected from IB side
				super.fireOrderEvent(new OrderRejectEvent(
						currentTimeStamp(),
						message + " (IB error code " + errorCode + ")"
						)
				);
			} else {
				Order updatedOrder = pendingUpdate.getAndSet(null);
				if(updatedOrder != null) {
					// error doing update!
					// its not fatal!
					super.fireOrderEvent(new OrderErrorEvent(
							currentTimeStamp(),
							message + " (IB error code " + errorCode + ")"
							)
					);
				} else {
					// FIXME: catch-all error. Assume its terminal state.
					// Maybe need to differentiate?
					super.fireOrderEvent(new OrderCancelEvent(
							currentTimeStamp(),
							message + " (IB error code " + errorCode + ")"
							)
					);
				}
			}
		}
		
		private void openOrder(com.ib.client.Contract contract, com.ib.client.Order order) {
			log.info("[openOrder] " + orderId + " / " + contract + "(" + order + ")");
			log.info("Order: action=" + order.m_action 
					+ ", openClose=" + order.m_openClose
					+ ", " + order.m_totalQuantity
					+ ", " + order.m_trailStopPrice
					);
		}
	}

	@Override
	protected OrderTracker createOrderTracker(Order order) {
		return new OrderTracker(order);
	}
}
