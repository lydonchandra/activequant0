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

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.activequant.core.domainmodel.Quote;
import org.activequant.core.types.TimeStamp;
import org.activequant.data.retrieval.IQuoteSubscriptionSource;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.util.UniqueDateGenerator;
import org.activequant.tradesystem.domainmodel2.BrokerBase;
import org.activequant.tradesystem.domainmodel2.Order;
import org.activequant.tradesystem.domainmodel2.event.OrderCancelEvent;
import org.activequant.tradesystem.domainmodel2.event.OrderExecutionEvent;
import org.activequant.tradesystem.domainmodel2.event.OrderUpdateEvent;
import org.activequant.util.pattern.events.IEventListener;

/**
 * Paper broker: gets subscription to the quotes, and executed incoming orders
 * according to the quotes. Supports only MARKET, LIMIT, and STOP orders.
 * <p>
 * <b>History:</b><br>
 *  - [13.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class PaperBroker extends BrokerBase {

	private IQuoteSubscriptionSource subscriptionSource;
	
	public IQuoteSubscriptionSource getQuoteSubscriptionSource() {
		return subscriptionSource;
	}
	public void setQuoteSubscriptionSource(IQuoteSubscriptionSource val) {
		this.subscriptionSource = val;
	}

	private final UniqueDateGenerator timeStampGenerator = new UniqueDateGenerator();
	private final AtomicLong orderId = new AtomicLong();
	private long getNextOrderId() {
		return orderId.incrementAndGet();
	}
	
	private AtomicReference<TimeStamp> currentTimeStamp = new AtomicReference<TimeStamp>(new TimeStamp(0L));
	private Date currentDate() {
		return currentTimeStamp.get().getDate();
	}

	class OrderTracker extends OrderTrackerBase {
		
		private final long orderId;
		private Order localOrder; // will change for trailing stop strategy
								  // therefore, keep separate from the 
								  // one in base class...

		private ISubscription<Quote> subscription;
		private double openQuantity;
		
		public OrderTracker(Order order) {
			super(order);
			localOrder = new Order(order);
			orderId = getNextOrderId();
			openQuantity = order.getQuantity();
		}

		protected String handleSubmit() {
			try {
				subscription = subscriptionSource.subscribe(localOrder.getInstrumentSpecification());
				subscription.addEventListener(new IEventListener<Quote>() {
					public void eventFired(Quote event) throws Exception {
						currentTimeStamp.set(event.getTimeStamp());
						checkOrder(event);
					}
				});
				subscription.activate();
				return Long.toString(orderId);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public void handleUpdate(Order newOrder) {
			Order order = getOrder();
			double quantityDiff = newOrder.getQuantity() - order.getQuantity();
			if(openQuantity + quantityDiff < 0.) {
				throw new IllegalArgumentException("can not update to negative open quantity");
			}
			
			localOrder = new Order(newOrder);
			super.fireOrderEvent(new OrderUpdateEvent(
					timeStampGenerator.generate(currentDate()),
					"ok",
					newOrder
					)
			);
		}

		public void handleCancel() {
			super.fireOrderEvent(new OrderCancelEvent(
					timeStampGenerator.generate(currentDate()),
					"at your request"
					)
			);
			subscription.cancel();
		}
		
		private static final double EPSILON = 1e-6;
		
		private void fireExecution(double quantity, double price) {
			if(quantity > openQuantity) {
				quantity = openQuantity;
			}
			
			if(quantity < EPSILON) {
				// order filled, unsubscribe
				subscription.cancel();
				return;
			}

			OrderExecutionEvent execution = new OrderExecutionEvent();

			execution.setEventTimeStamp(timeStampGenerator.generate(currentDate()));
			execution.setExecutionPrice(price);
			execution.setExecutionQuantity(quantity);

			openQuantity -= quantity;
			fireOrderEvent(execution);
		}

		private void checkOrder(Quote quote) {

			double quantity;
			double price;
			if(localOrder.getOrderSide() == Order.Side.BUY) {
				quantity = quote.getAskQuantity();
				price = quote.getAskPrice();
			} else {
				quantity = quote.getBidQuantity();
				price = quote.getBidPrice();
			}
			if(quantity <= 0. || price <= 0.) {
				return;
			}

			switch(localOrder.getOrderType()) {
			case MARKET:
				// fill it
				fireExecution(quantity, price);
				break;
				
			case LIMIT:
				if(localOrder.getOrderSide() == Order.Side.BUY) {
					if(price <= localOrder.getLimitPrice()) {
						fireExecution(quantity, price);
					} 
				} else {
					if(price >= localOrder.getLimitPrice()) {
						fireExecution(quantity, price);
					} 
				}
				break;

			case STOP:
				if(localOrder.getOrderSide() == Order.Side.BUY) {
					if(price >= localOrder.getStopPrice()) {
						fireExecution(quantity, price);
					} 
				} else {
					if(price <= localOrder.getStopPrice()) {
						fireExecution(quantity, price);
					} 
				}
				break;
			case TRAILING_STOP:
				if(localOrder.getStopPrice() <= 0.0) {
					// start trailing
					if(localOrder.getOrderSide() == Order.Side.BUY) {
						localOrder.setStopPrice(price - localOrder.getTrailingDistance());
					} else {
						localOrder.setStopPrice(price + localOrder.getTrailingDistance());
					}
				} else {
					if(localOrder.getOrderSide() == Order.Side.BUY) {
						if(price >= localOrder.getStopPrice()) {
							log.info("trailing stop order with id: " + orderId + " hit STOP at " + price + ", converted to MARKET order");
							localOrder.setOrderType(Order.Type.MARKET);
							fireExecution(quantity, price);
						} else if(price < localOrder.getStopPrice() - localOrder.getTrailingDistance()) {
							localOrder.setStopPrice(price + localOrder.getTrailingDistance());
							log.info("trailing stop order with id: " + orderId + " adjusted to stop=" + localOrder.getStopPrice());
						} 
					} else {
						if(price <= localOrder.getStopPrice()) {
							log.info("trailing stop order with id: " + orderId + " hit STOP at " + price + ", converted to MARKET order");
							localOrder.setOrderType(Order.Type.MARKET);
							fireExecution(quantity, price);
						} else if(price > localOrder.getStopPrice() + localOrder.getTrailingDistance()) {
							localOrder.setStopPrice(price - localOrder.getTrailingDistance());
							log.info("trailing stop order with id: " + orderId + " adjusted to stop=" + localOrder.getStopPrice());
						}
					}
				}
				break;
			default:
				throw new IllegalStateException("only MARKET/LIMIT/STOP/TRAILING_STOP orders are supported, received :" + localOrder.getOrderType());
			}
		}
	}

	@Override
	protected OrderTracker createOrderTracker(Order order) {
		switch(order.getOrderType()) {
		case MARKET: case LIMIT: case STOP: case TRAILING_STOP:
			break;
		default:
			throw new IllegalArgumentException("only MARKET/LIMIT/STOP/TRAILING_STOP orders are supported, received :" + order.getOrderType());
		}
		return new OrderTracker(order);
	}
}
