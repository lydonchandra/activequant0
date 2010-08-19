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

/**
 * Interface of a broker. Represents the view of the remote broker, and
 * provides an api to request actions from the remote broker.
 * <p>
 * The process of submitting an order has two steps:
 * <ul>
 *    <li>Preparation. At this step order is validated and broker returns
 *        {@link IOrderTracker} handle. If user is interested in receiving
 *        order-related events, she should register order event listeners
 *        with this handle. At this time nothing has yet been submitted to
 *        the physical broker. The broker-assigned order id is not yet known.
 *    <li>Submission. By calling a {@link IOrderTracker#submit()} user sends
 *        the order for validation, evaluation, and processing to the physical
 *        broker.
 * </ul>
 * Example:
 * <pre class="code">
 * IBroker broker = ... // get access to a broker implementation
 * Order order = new Order();
 * order.setOrderType(OrderType.MARKET);
 * order.setOrderSide(OrderSide.BUY);
 * 
 * final IOrderTracker tracker = broker.prepareOrder(order);
 * tracker.addEventListener(new IEventListener<OrderEvent>() {
 *    public void eventFired(OrderEvent event) {
 *       // dispatch to event processor
 *       processOrderEvent(tracker, event);
 *    }
 * });
 * 
 * // submit the order
 * tracker.submit();
 * ...
 * </pre>
 * Note that order processing api is strictly asynchronous: it is not guaranteed
 * that order will be validated or accepted, even if <code>submit()</code>
 * returns without throwing an error. If problem occurs (for example, order
 * exceeds available funds), physical broker will communicate it to the
 * implementation and an appropriate order event will be issued.
 * <p>
 * Sometimes user will want to wait till the order processing completes. There
 * are two helper functions int <code>IOrderTracker</code> interface to facilitate 
 * this: {@link IOrderTracker#waitForCompletion()} and {@link IOrderTracker#waitForCompletion(long)}.
 * Use these methods with caution, because certain order types can not be expected
 * to complete immediately (for example, limit order far from the market).
 * <p>
 * Following example demonstrates code that gives broker 10 seconds to process
 * an order, then cancels it:
 * <pre class="code">
 * IBroker broker = ... // get access to a broker implementation
 * Order order = new Order();
 * order.setOrderType(OrderType.LIMIT);
 * order.setOrderSide(OrderSide.BUY);
 * order.setLimitPrice(755.52);
 * 
 * final IOrderTracker tracker = broker.prepareOrder(order);
 * // submit the order
 * tracker.submit();
 * BrokerSupport.waitForTicketCompletion(ticket, 10000);
 * if(tracker.getOrderCompletion() == null) {
 *    // not executed
 *    tracker.cancel();
 *    tracker.waitForCompletion();
 * }
 * 
 * OrderTicketCompletion completion = tracker.getOrderTicketCompletion();
 * if(completion.getQuantity() == 0.0) {
 *   // not filled
 * } else {
 *   // filled or partially filled
 * }
 * </pre>
 * Note that after we send <code>cancel()</code> event, order still may complete
 * with fill or partial fill. This is because of the asynchronous nature of
 * communication with the physical broker: the fill message from broker may
 * cross with the cancel message to broker, and cancel will then not be processed.
 * Code example above takes care of this by checking filled quantity after
 * order has been fully completed, event after the code path that sent
 * <code>cancel</code> signal.
 * <p>
 * <h2>Order life cycle</h1>
 * Generally, order state can be in one of two broad categories:
 * <ul>
 *   <li>Opened order. Order has been submitted but not yet completed. This can
 *       be further sub-divided into the following states:
 *       <ul>
 *         <li>Order not received by physical broker but not yet confirmed.
 *         <li>Order received and being in process of executing.
 *         <li>Order partially filled.
 *         <li>Order updated by the user.
 *       </ul>
 *   <li>Closed order (finished order). This is a terminal state of the order. 
 *       Following paths lead to this state:
 *       <ul>
 *         <li>Order did not pass evaluation by the physical broker and has been
 *             immediately rejected (i.e. unsupported security, or insufficient funds).
 *         <li>Order received completely filled by the physical broker.
 *         <li>Order canceled by user.
 *       </ul>
 * </ul> 
 * More formally, order starts with a <em>Pre-validation state</em>, when it has been
 * submitted but not yet confirmed or validated by the physical broker.
 * From this state order can go to the <em>Rejected</em> state (terminal state),
 * or may generate one or more <em>ExecutionEvent</em> events. If execution
 * fills the order completely, it moves to the terminal <em>Filled</em> state.
 * User may apply order updates. Update is also an asynchronous message sent
 * to the physical broker. It may result in <em>OrderUpdated</em> event, or
 * an <em>OrderErrorEvent</em>. The latter does not move the order to the
 * terminal state. Instead, it just tells that order update was rejected, and 
 * pre-update order is still in effect. 
 * <br>
 * <b>History:</b><br>
 *  - [09.12.2007] Created (Mike Kroutikov)<br>
 *  - [12.12.2007] Adding getOrders(..) (Ulrich Staudinger)<br>
 *
 *  @author Mike Kroutikov
 *  @author Ulrich Staudinger
 */
public interface IBroker {
   
    /**
     * Validates order and prepares for submission.
     * Actual submission is done via <code>IOrderTracker</code>
     * interface.
     *  
     * @param order order to be placed.
     * @throws Exception when something goes wrong.
     */
    public IOrderTracker prepareOrder(Order order);
    
    /**
     * Returns all orders that are kept in broker history (including
     * opened orders, filled orders, and canceled orders).
     * Normally, broker keeps a recent history of orders. For how long - it
     * is highly broker-dependent. Also implementation may at its discretion
     * limit the number of returned closed orders (if broker keeps a very 
     * long history). Opened orders are guaranteed to be returned by this method.
     * 
     * @return array of order tracking handlers.
     */
    public IOrderTracker[] getOrders();
}