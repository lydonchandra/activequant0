package org.activequant.data.retrieval.integration.ib;

import com.ib.client.Contract;
import com.ib.client.Execution;
import com.ib.client.Order;

/**
 * 
 * Interface for an event listener. <br>
 * <br>
 * <b>History:</b><br>
 *  - [05.07.2007] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public interface IBEventListener {
	
	/**
	 * execution details are routed through this. 
	 * @param orderId
	 * @param arg1
	 * @param arg2
	 */
	public void execDetails(int orderId, Contract arg1, Execution arg2);
	
	/**
	 * the order status arrives through this method.
	 * @param orderId
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 * @param arg5
	 * @param arg6
	 * @param arg7
	 * @param arg8
	 */
	public void orderStatus(int orderId, String arg1, int arg2, int arg3, double arg4, int arg5, int arg6, double arg7, int arg8);

	
	/**
	 * portfolio position come in through this
	 * @param contract
	 * @param positionCount
	 * @param marketPrice
	 * @param marketValue
	 * @param avgCost
	 * @param unrealizedPNL
	 * @param realizedPNL
	 * @param accountName
	 */
	public void updatePortfolio(Contract instrument, int positionCount, double marketPrice,
			double marketValue, double avgCost, double unrealizedPNL, double realizedPNL, 
			String accountName);
	
	
	/**
	 * account value. 
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public void updateAccountValue(String informationType, String amount, String currency, String account);

	
	public void error(int orderId, int errorCode, String message);

	public void openOrder(int orderId, Contract contract, Order order);
}
