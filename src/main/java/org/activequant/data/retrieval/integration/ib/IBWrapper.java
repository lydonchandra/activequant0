package org.activequant.data.retrieval.integration.ib;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.EWrapper;
import com.ib.client.Execution;
import com.ib.client.Order;

/**
 * Convenience class: implements all methods of EWrapper as no-ops.
 * When in need to implement just few methods of EWrapper, derive from this class
 * and override the methods you need.
 * 
 * <br>
 * <b>History:</b><br>
 *  - [Oct 28, 2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class IBWrapper implements EWrapper {

	public void bondContractDetails(ContractDetails contractDetails) {
	}

	public void contractDetails(ContractDetails contractDetails) {
	}

	public void currentTime(long time) {
	}

	public void execDetails(int orderId, Contract contract, Execution execution) {
	}

	public void historicalData(int reqId, String date, double open,
			double high, double low, double close, int volume, int count,
			double WAP, boolean hasGaps) {
	}

	public void managedAccounts(String accountsList) {
	}

	public void nextValidId(int orderId) {
	}

	public void openOrder(int orderId, Contract contract, Order order) {
	}

	public void orderStatus(int orderId, String status, int filled,
			int remaining, double avgFillPrice, int permId, int parentId,
			double lastFillPrice, int clientId, String whyHeld) {
	}

	public void realtimeBar(int reqId, long time, double open, double high,
			double low, double close, long volume, double wap, int count) {
	}

	public void receiveFA(int faDataType, String xml) {
	}

	public void scannerData(int reqId, int rank,
			ContractDetails contractDetails, String distance, String benchmark,
			String projection, String legsStr) {
	}

	public void scannerParameters(String xml) {
	}

	public void tickEFP(int tickerId, int tickType, double basisPoints,
			String formattedBasisPoints, double impliedFuture, int holdDays,
			String futureExpiry, double dividendImpact, double dividendsToExpiry) {
	}

	public void tickGeneric(int tickerId, int tickType, double value) {
	}

	public void tickOptionComputation(int tickerId, int field,
			double impliedVol, double delta, double modelPrice,
			double pvDividend) {
	}

	public void tickPrice(int tickerId, int field, double price,
			int canAutoExecute) {
	}

	public void tickSize(int tickerId, int field, int size) {
	}

	public void tickString(int tickerId, int tickType, String value) {
	}

	public void updateAccountTime(String timeStamp) {
	}

	public void updateAccountValue(String key, String value, String currency,
			String accountName) {
	}

	public void updateMktDepth(int tickerId, int position, int operation,
			int side, double price, int size) {
	}

	public void updateMktDepthL2(int tickerId, int position,
			String marketMaker, int operation, int side, double price, int size) {
	}

	public void updateNewsBulletin(int msgId, int msgType, String message,
			String origExchange) {
	}

	public void updatePortfolio(Contract contract, int position,
			double marketPrice, double marketValue, double averageCost,
			double unrealizedPNL, double realizedPNL, String accountName) {
	}

	public void connectionClosed() {
	}

	public void error(Exception e) {
	}

	public void error(String str) {
	}

	public void error(int id, int errorCode, String errorMsg) {
	}
}
