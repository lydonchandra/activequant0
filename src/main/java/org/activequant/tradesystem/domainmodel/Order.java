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

package org.activequant.tradesystem.domainmodel;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Symbol;
import org.activequant.core.types.TimeStamp;
import org.activequant.tradesystem.types.BrokerId;
import org.activequant.tradesystem.types.OrderSide;
import org.activequant.tradesystem.types.OrderState;
import org.activequant.tradesystem.types.OrderTif;
import org.activequant.tradesystem.types.OrderType;

/**
 * 
 * order implementation, heavily based on the quickfixj implementation. <br>
 * <br>
 * <b>History:</b><br>
 *  - [10.06.2007] Created (Ulrich Staudinger)<br>
 *  - [11.06.2007] Moved to enums (Erik Nijkamp)<br>
 *  - [28.06.2007] Added order date (Ulrich Staudinger)<br>
 *  - [28.06.2007] Persistence (Erik Nijkamp)<br>
 *  - [29.09.2007] Removed annotations (Erik Nijkamp)<br>
 *  - [27.10.2007] Added proper object state code (Erik Nijkamp)<br>
 *  - [02.11.2007] Adding trailing distance again, got lost in the branch (Ulrich Staudinger)<br>
 *  - [02.11.2007] Finalizing clone method (Ulrich Staudinger)<br>
 *  - [02.11.2007] ID cleanup (Erik Nijkamp)<br>
 *  - [06.11.2007] Adding a placed property (Ulrich Staudinger)<br>
 *  - [13.11.2007] Adding order state enum (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public class Order implements Cloneable {
	
	public static final int NOT_SET = -1;
	
	private Long id;
	private String brokerAssignedId;
	private BrokerId brokerId;  
    private InstrumentSpecification instrumentSpecification = null;
    private OrderSide side = OrderSide.BUY;
    private OrderType type = OrderType.MARKET;
    private OrderTif timeInForce = OrderTif.DAY;      
    private OrderState state = OrderState.NEW;
    
    private double quantity = 0.0;
    private double openQuantity = NOT_SET;
    private double executedQuantity = 0.0;
    private double limitPrice = NOT_SET;
    private double stopPrice = NOT_SET;
    private double averagePrice = 0.0;
    private double trailingDistance = NOT_SET;
    private String message = null;
    private TimeStamp orderTimeStamp = null;    
    private TimeStamp executionTargetTimeStamp = null; 
    private TimeStamp expirationTimeStamp = null;

    public Order() {
    	
    }

    /**
     * with id for persistence layer. 
     * @param ID
     */
    public Order(long ID) {
        this.id = ID;
    }
    
    /**
     * with id for persistence layer. 
     * @param ID
     * @param Spec
     */
    public Order(long ID, InstrumentSpecification instrumentSpecification) {
        this.id = ID;
        this.instrumentSpecification = instrumentSpecification;
    }


    /**
	 * full object. 
	 * @param type
	 * @param side
	 * @param tif
	 * @param symbol
	 * @param quantity
	 * @return
	 */
	public Order(OrderType type, OrderSide side, OrderTif tif, InstrumentSpecification spec,
			double quantity) {
		this();
		setInstrumentSpecification(spec);
		setType(type);
		setSide(side);
		setTimeInForce(tif);
		setQuantity(quantity);
	}

	/**
	 * no symbol and no quantity
	 * @param type
	 * @param side
	 * @param tif
	 * @return
	 */
	public Order(OrderType type, OrderSide side, OrderTif tif) {
		this();
		setType(type);
		setSide(side);
		setTimeInForce(tif);
	}

	/**
	 * just symbol and quantity.
	 * 
	 * @param symbol
	 * @param quantity
	 * @return
	 */
	public Order(InstrumentSpecification spec, double quantity) {
		this();
		setInstrumentSpecification(spec);
		setQuantity(quantity);
	}
	
	public Order(InstrumentSpecification spec, TimeStamp date, double quantity) {
		this(spec, quantity);
		setOrderTimeStamp(date);
	}
	
	public boolean hasId() {
		return id != null;         
	}          
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
    
	/**
	 * Clones this Order. Note that only user-controlled parameters are
	 * copied to the cloned Order. 
	 * The parameters that are controlled by the Broker side are initialized to
	 * their defaults: <code>brokerAssignedId</code> is initially unset, 
	 * and <code>state</code> is <code>NEW</code>.
	 */
    public Order clone() {

		Order order = new Order();
		
		// never clone anything except user-supplied parameters
		// broker assigned id should be initially null
		// and state of the cloned order should be NEW
//		order.setBrokerAssignedId(getBrokerAssignedId());
		
		/// clone all other parameters
		order.setAveragePrice(averagePrice);
		order.setExecutedQuantity(executedQuantity);
		order.setExecutionTargetTimeStamp(executionTargetTimeStamp);
		order.setInstrumentSpecification(instrumentSpecification);
		order.setLimitPrice(limitPrice);
		order.setMessage(message);
		order.setOpenQuantity(openQuantity);
		order.setOrderTimeStamp(orderTimeStamp);
		order.setQuantity(quantity);
		order.setSide(side);
		order.setStopPrice(stopPrice);
		order.setTimeInForce(timeInForce);
		order.setTrailingDistance(trailingDistance);
		order.setType(type);
		order.setExpirationTimeStamp(expirationTimeStamp);
		
		return order;
	}

    public Symbol getSymbol() {
    	assert(instrumentSpecification != null);
    	return instrumentSpecification.getSymbol();
    }
  
    public InstrumentSpecification getInstrumentSpecification() {
        return instrumentSpecification;
    }
    
    public void setInstrumentSpecification(InstrumentSpecification instrumentSpecification) {
        this.instrumentSpecification = instrumentSpecification;
    }
    
    public double getQuantity() {
        return quantity;
    }
    
    public void setQuantity(double quantity) {
        this.quantity = quantity;
        this.openQuantity = quantity; 
    }
    
    /**
     * the quantity of open contracts. fully filled orders will have getOpen of 0. 
     * Whereas partially or not filled orders will have a different value. 
     * @return
     */
    public double getOpenQuantity() {
		return openQuantity;
	}

	public void setOpenQuantity(double open) {
		this.openQuantity = open;
	}

	public double getExecutedQuantity() {
		return executedQuantity;
	}

	public void setExecutedQuantity(double executed) {
		this.executedQuantity = executed;
	}

	public OrderSide getSide() {
		return side;
	}

	public void setSide(OrderSide side) {
		this.side = side;
	}

	public OrderType getType() {
		return type;
	}

	public void setType(OrderType type) {
		this.type = type;
	}

	public OrderTif getTimeInForce() {
		return timeInForce;
	}

	public void setTimeInForce(OrderTif tif) {
		this.timeInForce = tif;
	}

	public double getLimitPrice() {
		return limitPrice;
	}

	public void setLimitPrice(double limit) {
		this.limitPrice = limit;
	}

	public double getStopPrice() {
		return stopPrice;
	}

	public void setStopPrice(double stop) {
		this.stopPrice = stop;
	}

	public void setAveragePrice(double val) {
		this.averagePrice = val;
	}

	public double getAveragePrice() {
		return averagePrice;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setBrokerAssignedId(String val) {
		this.brokerAssignedId = val;
	}

	public String getBrokerAssignedId() {
		return brokerAssignedId;
	}
    
    public String toString() {
    	assert (instrumentSpecification != null);
		StringBuffer sb = new StringBuffer();
		sb.append("ID: ").append(this.id);
		sb.append(" Side: ").append(side.toString());
		sb.append(" Instrument: ==").append(instrumentSpecification.toString()).append("== ");
		if (limitPrice != NOT_SET)
			sb.append(" L: ").append(this.getLimitPrice());
		if (stopPrice != NOT_SET)
			sb.append(" S: ").append(this.getStopPrice());
		sb.append(" Cancelled: ").append(isCanceled());
		sb.append(" Quantity: ").append(quantity);
		return sb.toString();
    }

	public TimeStamp getOrderTimeStamp() {
		return orderTimeStamp;
	}

	public void setOrderTimeStamp(TimeStamp orderTimeStamp) {
		this.orderTimeStamp = orderTimeStamp;
	}

    /**
     * Specifies when an order must be executed.
     * This could be relevant when using the paper broker implementation.
     * It also reflects a "good starting at".   
     * <p/>
     * An executionPoint of null expresses that the order is executed as soon as possible (does 
     * not collide with Time In Force, as TIF is used at the broker's/exchange's end).  
     */
	public TimeStamp getExecutionTargetTimeStamp() {
		return executionTargetTimeStamp;
	}

	/**
	 * Sets execution target date.
	 * 
	 * @param val execution target date.
	 */
	public void setExecutionTargetTimeStamp(TimeStamp val) {
		this.executionTargetTimeStamp = val;
	}
	
	/**
	 * Order expiration date for the orders with the appropriate
	 * <code>timeInForce</code> value.
	 * 
	 * @return expiration date.
	 */
	public TimeStamp getExpirationTimeStamp() {
		return this.expirationTimeStamp;
	}
	
	/**
	 * Sets the expiration date.
	 * 
	 * @param val expiration date.
	 */
	public void setExpirationTimeStamp(TimeStamp val) {
		this.expirationTimeStamp = val;
	}
	
	/**
	 * Price distance to be used by <code>TRAILING_STOP</code> order types.
	 * For other order types should be left unset.
	 * 
	 * @return trailing distance.
	 */
	public double getTrailingDistance() {
		return trailingDistance;
	}

	/**
	 * Sets trailing distance (in price units).
	 * 
	 * @param trailingDistance distance value.
	 */
	public void setTrailingDistance(double trailingDistance) {
		this.trailingDistance = trailingDistance;
	}
	
	public boolean isPlaced() {
		return this.state != OrderState.NEW;
	}

	public boolean isRejected() {
		return this.state == OrderState.REJECTED;
	}

	public boolean isCanceled() {
		return this.state == OrderState.CANCELED;
	}

	// FIXME: there is a big problem with the test below,
	// because double "open" may have a small fractions there
	// (like 1e-12, result of rounding digital noise) that prevents order
	// from being recognized, so that execution engine would keep sending
	// an order for 1e-12 USD to the broker... has to be fixed -mk
    public boolean isFilled(){
    	if(getOpenQuantity() == 0)return true; 
    	return false;
    }

	public OrderState getState() {
		return state;
	}

	public void setState(OrderState state) {
		this.state = state;
	}

	public BrokerId getBrokerId() {
		return brokerId;
	}

	public void setBrokerId(BrokerId brokerId) {
		this.brokerId = brokerId;
	}
}