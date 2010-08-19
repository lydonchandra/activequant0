package org.activequant.data.retrieval.integration.opentick.test;

import org.activequant.data.retrieval.ICandleSeriesSource;
import org.activequant.util.spring.ServiceLocator;
import org.otfeed.IConnectionFactory;

import org.junit.Assert;
import org.junit.Test;


/**
 * open tick test class to test spring initialization<br>
 * <br>
 * <b>History:</b><br>
 *  - [18.10.2007] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class OpentickSpringConfigurationTest {
	
	@Test
	public void testInitialization() throws Exception {
		IConnectionFactory connectionFactory = (IConnectionFactory)ServiceLocator.instance("data/opentick/configuration.xml").getContext().getBean("connectionFactory");
		Assert.assertTrue(connectionFactory != null);
	}
	
	@Test
	public void testCandleSeriesSourceInstantiation() throws Exception {
		ICandleSeriesSource s = (ICandleSeriesSource)ServiceLocator.instance("data/opentick/configuration.xml").getContext().getBean("candleSeriesSource");
		Assert.assertTrue(s != null);		
	}
}
