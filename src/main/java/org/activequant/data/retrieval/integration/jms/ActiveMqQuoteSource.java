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
package org.activequant.data.retrieval.integration.jms;

import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Quote;
import org.activequant.core.domainmodel.Symbol;
import org.activequant.core.types.TimeFrame;
import org.activequant.core.types.TimeStamp;
import org.activequant.data.retrieval.IQuoteSubscriptionSource;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.integration.SubscriptionSourceBase;
import org.activequant.util.pattern.events.Event;
import org.activequant.util.pattern.events.IEventListener;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.log4j.Logger;

/**
 * Quote source that gets quotes from JMS broadcasts.
 * Instrument specification must have JMS address set as
 * <code>vendor</code> property. All other properties
 * except symbol, securityType, currency must be null.
 * Also, length of symbol string must be 3 characters only
 * (FIXME: this is a weird limitation apparently original code
 * has been written to deal with FOREX and symbol names like "USD", "EUR", "AUD",
 * etc -mpk).
 * 
 * FIXME: this class needs testing!
 * <br>
 * <b>History:</b><br>
 *  - [03.05.2007] Created (Ulrich Staudinger)<br>
 *  - [25.09.2007] Comments (Erik Nijkamp)<br>
 *  - [31.10.2007] Converted to new data retrieval model (Mike Kroutikov)<br>    
 *
 *  @author Ulrich Staudinger
 *  @author Mike Kroutikov
 */
public class ActiveMqQuoteSource extends SubscriptionSourceBase<Quote> implements IQuoteSubscriptionSource {

	private final Logger log = Logger.getLogger(getClass());
	
	// JMS connection
	private transient Connection connection;
	
	// JMS session
	private transient Session session;
	
	// JMS url
	private transient String connectionPointUrl = null;

	private transient boolean connected = false;

	/**
	 * Creates new source.
	 * 
	 * @param connectionPointUrl the url of ActiveMQ server.
	 * @throws JMSException if something goes wrong.
	 */
	public ActiveMqQuoteSource(String connectionPointUrl) throws JMSException {
		super("ActiveMqQuoteSource (multi vendor)");

		this.connectionPointUrl = connectionPointUrl;
		connect();
	}

	/**
	 * Stops the service. Please, mark this as Spring's bean 
	 * "destroy-method" so that service is disconnected upon 
	 * Spring context destruction
	 * 
	 * @throws JMSException
	 */
	public void stop() throws JMSException {
		log.info("Stopping listener.");
		connection.close();
	}

	private final Map<String,Dispatcher> dispatcher = new ConcurrentHashMap<String,Dispatcher>();
	
	private static class EventAndSpecs extends Event<Quote> {
		public final InstrumentSpecification spec;
		public EventAndSpecs(InstrumentSpecification s) {
			spec = s;
		}
	}

	// this class listens to a unique topic/queue and dispatches
	// incoming events to the appropriate listener as per instumentSpecs.
	private class Dispatcher implements MessageListener {
		public  final String address; // jms address of this subscription
		private MessageConsumer consumer;
		
		public Dispatcher(String jmsAddress) {
			address = jmsAddress;
		}
		
		public void subscribe() throws JMSException {
			log.info("Subscribing to topic " + address);

			if (address.startsWith("topic://")) {
				Topic topic = session.createTopic(address.substring(8));
				consumer = session.createConsumer(topic);
			} else if (address.startsWith("queue://")) {
				Queue queue = session.createQueue(address.substring(8));
				consumer = session.createConsumer(queue);
			} else {
				throw new IllegalArgumentException("invalid jms address: " + address);
			}
				
			consumer.setMessageListener(this);
			log.info("Subscribed.");
		}
		
		public void close() {
			try {
				consumer.close();
			} catch(Exception ex) {
				log.warn(ex);
			}
		}
		
		private final Map<InstrumentSpecification,EventAndSpecs> events = new ConcurrentHashMap<InstrumentSpecification,EventAndSpecs>();
		public void addListener(InstrumentSpecification spec, IEventListener<Quote> listener) {
			EventAndSpecs eas = events.get(spec);
			if(eas == null) {
				eas = new EventAndSpecs(spec);
				events.put(spec, eas);
			}
			eas.addEventListener(listener);
		}

		public void removeListener(InstrumentSpecification spec, IEventListener<Quote> listener) {
			EventAndSpecs eas = events.get(spec);
			if(eas != null) {
				eas.removeEventListener(listener);
				if(eas.isEmpty()) {
					events.remove(spec);
				}
			}
		}
		
		public boolean isEmpty() {
			return events.isEmpty();
		}

		public void onMessage(Message message) {
			try {
				Quote quote = parseQuoteMessage(message);
				if(quote != null) {
					EventAndSpecs event = events.get(quote.getInstrumentSpecification());
					if(event != null) {
						try {
							// following is very important to keep the memory low
							// this class can produce millions of quotes
							// we want them to point to the same InstrumentSpecs
							// (not the copy that equals() to it). Therefore,
							// we re-set spec here to the one that is in the
							// events map. Number of elements there is
							// limited by the total number of subscriptions
							quote.setInstrumentSpecification(event.spec);
							event.fire(quote);
						} catch(Exception ex) {
							log.error(ex);
							ex.printStackTrace();
						}
					} else {
						log.debug("ignoring event we are not subscribed for: " + quote + " " + quote.getInstrumentSpecification());
					}
				}
			} catch(Exception ex) {
				log.error(ex);
				ex.printStackTrace();
			}
		}
	}
	
	// this class maps instrumentSpecs onto topic/queue name and adds
	// itself as message receiver to the corresponding Dispatcher.
	private class QuoteSubscription extends Subscription {
		private final String jmsAddress;
		private final InstrumentSpecification spec;
		
		public QuoteSubscription(InstrumentSpecification s, String addr) {
			spec = s;
			jmsAddress = addr;
		}
		
		private IEventListener<Quote> listener = new IEventListener<Quote> () {
			public void eventFired(Quote event) throws Exception {
				fireEvent(event);
			}
		};
		
		@Override
		protected void handleActivate() {
			Dispatcher d = dispatcher.get(jmsAddress);
			try {
				if(d == null) {
					d = new Dispatcher(jmsAddress);
					dispatcher.put(d.address, d);
					d.subscribe();
				}
				d.addListener(spec, listener);
			} catch (Exception e) {
				log.error(e);
				e.printStackTrace();
			}
		}

		@Override
		protected void handleCancel() {
			Dispatcher d = dispatcher.get(jmsAddress);
			if(d == null) {
				log.error("unexpected: dispatcher not found for address " + jmsAddress);
				return;
			}
			d.removeListener(spec, listener);
			if(d.isEmpty()) {
				dispatcher.remove(jmsAddress);
				d.close();
			}
		}
	}

	private InstrumentSpecification getSpec(String symbol, String originatingTopic) {
		// split symbol.
		String instrument = symbol.substring(0, 3);
		String currency = symbol.substring(3);

		InstrumentSpecification spec = new InstrumentSpecification();
		spec.setSymbol(new Symbol(instrument));
		spec.setCurrency(currency);
		spec.setVendor(originatingTopic);

		return spec;
	}

	private Quote parseQuoteMessage(Message message) throws JMSException {
		log.debug("Message: " + message);

		ActiveMQTextMessage textMessage = (ActiveMQTextMessage) message;
		// cut the topic ...
		String destination = message.getJMSDestination().toString();
		// destination looks like this : topic://ldn-01/rate
		if (destination.length() <= 8) {
			log.error("garbled destination: " + destination);
			return null;
		}

		String originatingTopic = destination;//.substring(8);
		// build a quote.

		// message looks like this: "USDEUR;1.455;1.457"
		StringTokenizer str = new StringTokenizer(textMessage.getText(), ";");
		String symbol = str.nextToken();
		Double bid = Double.parseDouble(str.nextToken());
		Double ask = Double.parseDouble(str.nextToken());

		// getting the corresponding security from our map.
		InstrumentSpecification is = getSpec(symbol, originatingTopic);

		// ok, a subscription has been found, now build a quote ...
		Quote quote = new Quote(is);
		quote.setAskPrice(ask);
		quote.setBidPrice(bid);
		quote.setAskQuantity(0);
		quote.setBidQuantity(0);
		quote.setTimeStamp(new TimeStamp());
		quote.setInstrumentSpecification(is);
		
		log.debug("received: " + quote);

		return quote;
	}

	// this class waits for bad things to happen (disconnect) and
	// tries to reconnect intelligently (i.e. re-subscribing all subscribers)
	private final ExceptionListener exceptionListener = new ExceptionListener() {
		public void onException(JMSException ex) {
			log.warn("Exception from JMS Connection received. ", ex);
			connected = false;
			while (!connected) {
				try {
					Thread.sleep(2000);
					connect();
					for (Dispatcher d: dispatcher.values()) {
						d.subscribe();
					}
				} catch (Exception x) {
					log.warn("Problem while restarting connection", x);
				}
			}
		}
	};

	public void connect() throws JMSException {
		connected = false;
		try {
			if (connection != null) {
				connection.stop();
				connection.close();
			}
		} catch (Exception x) {
			log.warn("Problem while stopping and closing connection", x);
		}
		
		log.info("Constructing factory with connection url "
				+ connectionPointUrl);
		final ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(
				connectionPointUrl);
		log.info("Creating connection ... ");
		connection = factory.createConnection();
		connection.setExceptionListener(exceptionListener);
		
		log.info("Creating session ...");
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		log.info("Starting connection ... ");
		connection.start();
		connected = true;
	}

	@Override
	protected QuoteSubscription createSubscription(
			InstrumentSpecification spec, TimeFrame timeFrame) {

		return new QuoteSubscription(spec, spec.getVendor());
	}

	/**
	 * {@inheritDoc}
	 */
	public ISubscription<Quote> subscribe(InstrumentSpecification spec) {

		if(spec.getVendor() == null) {
			throw new NullPointerException("vendor");
		}
		
		if(spec.getSymbol() == null) {
			throw new NullPointerException("symbol");
		}
		
		if(spec.getCurrency() == null) {
			throw new NullPointerException("currency");
		}

		return super.subscribe(spec, TimeFrame.TIMEFRAME_1_TICK);
	}
}
