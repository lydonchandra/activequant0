package org.activequant.data.retrieval.integration.quickfix.test;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Quote;
import org.activequant.core.types.Symbols;
import org.activequant.data.retrieval.ISubscription;
import org.activequant.data.retrieval.integration.quickfix.QuickfixConnection;
import org.activequant.data.retrieval.integration.quickfix.QuickfixQuoteSubscriptionSource;
import org.activequant.data.retrieval.integration.quickfix.QuickfixStarter;
import org.activequant.util.pattern.events.IEventListener;
import org.apache.log4j.Logger;
import org.junit.Test;

public class QuickfixConnectionTest {
	
	private final Logger log = Logger.getLogger(getClass());
	
	private final InstrumentSpecification INSTRUMENT1 = new InstrumentSpecification(Symbols.MSFT);
	
	@Test
	public void testConfiguration() throws Exception {
		
		// start mock FIX server
		MockFixServer serverApplication = new MockFixServer();
		QuickfixStarter serverStarter = new QuickfixStarter();
		serverStarter.setSettingsCfg("/data/quickfix/test/server.cfg");
		serverStarter.setApplication(serverApplication);
		serverStarter.setServer(true);
		
		serverStarter.start();
		Thread.sleep(2000);
		
		QuickfixConnection connection = new QuickfixConnection();
		connection.setUsername("ulrich");
		connection.setPassword("ulrich");
		QuickfixStarter clientStarter = new QuickfixStarter();
		clientStarter.setSettingsCfg("/data/quickfix/test/client.cfg");
		clientStarter.setApplication(connection);
		
		QuickfixQuoteSubscriptionSource quoteSource = new QuickfixQuoteSubscriptionSource();
		quoteSource.setConnection(connection);
		
		clientStarter.start();
		log.info("started");
		
		Thread.sleep(3000);
        
		ISubscription<Quote> subscription = quoteSource.subscribe(INSTRUMENT1);
		subscription.addEventListener(new IEventListener<Quote>() {

			public void eventFired(Quote event) throws Exception {
				log.info(event);
			}
		});
		subscription.activate();
		
        Thread.sleep(10000);
        
        subscription.cancel();
        
        clientStarter.stop();
        
        Thread.sleep(2000);
        serverStarter.stop();
	}
}
