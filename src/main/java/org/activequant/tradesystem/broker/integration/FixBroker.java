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
package org.activequant.tradesystem.broker.integration;

import java.util.Date;

import org.activequant.data.retrieval.integration.quickfix.QuickfixConnection;
import org.activequant.tradesystem.broker.BrokerBase;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.types.OrderSide;
import org.activequant.tradesystem.types.OrderType;
import org.apache.log4j.Logger;

import quickfix.Session;
import quickfix.field.ClOrdID;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;
import quickfix.fix44.NewOrderSingle;
import quickfix.fix44.component.Instrument;

/**
 * 
 * This is a FIX broker implementation, however it is not 100% generic as it is
 * meant to connect to dukascopy. As you propably know, the different brokers
 * have sometimes manners different from the norm, same with dukascopy's forex
 * stuff. You really need to review this from your point of view before using
 * it. Speak up on the mailing lists if you want to discuss it. Does only market
 * orders for now. <br>
 * <br>
 * <b>History:</b><br> 
 * - [10.09.2007] Created (Ulrich Staudinger)<br>
 * - [03.11.2007] Fixed some super-dangerous exception handling (Erik Nijkamp)<br>
 * 
 * @author Ulrich Staudinger
 */
public class FixBroker extends BrokerBase {

	protected final static Logger log = Logger.getLogger(FixBroker.class);

	private QuickfixConnection connection = null;

	public FixBroker(QuickfixConnection connection) throws Exception {
		this.connection = connection;

		// request a position.
		requestPositionList();
	}

	public void requestPositionList() throws Exception {
		/*
		 * FIXME
		 *
		AccountInfo ai = new AccountInfo();
		if (connection.getSessionId() != null) {
			Session.sendToTarget(ai, connection.getSessionId());
		}
		*
		*/
	}

	public void placeOrder(Order o) throws Exception {
		if(o.getType() != OrderType.MARKET) {
			throw new IllegalArgumentException("unsupported order type: " + o);
		}
		
		NewOrderSingle newOrderSingle = new NewOrderSingle();

		newOrderSingle.set(new ClOrdID(Long
				.toString(System.currentTimeMillis())));
		Instrument instrument = new Instrument();

		// dukascopy oriented!!!! This class needs refactoring for your own
		// needs.

		instrument.set(new Symbol(o.getSymbol() + "/"
				+ o.getInstrumentSpecification().getCurrency()));
		newOrderSingle.set(instrument);

		Side orderSide = new Side();

		// 
		if (o.getSide().equals(OrderSide.BUY))
			orderSide.setValue(Side.BUY);
		else if (o.getSide().equals(OrderSide.SELL))
			orderSide.setValue(Side.SELL);
		else if (o.getSide().equals(OrderSide.SHORT_SELL))
			orderSide.setValue(Side.SELL_SHORT);
		else if (o.getSide().equals(OrderSide.SHORT_SELL_EXEMPT))
			orderSide.setValue(Side.SELL_SHORT_EXEMPT);

		newOrderSingle.set(orderSide);
		newOrderSingle.set(new Price());
		newOrderSingle.setChar(OrdType.FIELD, OrdType.MARKET);

		// set the quantity.
		newOrderSingle.set(new OrderQty((int) (o.getQuantity())));

		TransactTime transactTime = new TransactTime();
		transactTime.setValue(new Date());
		newOrderSingle.set(new TransactTime());

		if (connection.getSessionId() != null) {
			Session.sendToTarget(newOrderSingle, connection.getSessionId());
			log.info("order sent : " + o.toString());
		}
	}
}
