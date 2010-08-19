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
package org.activequant.tradesystem.tests;

import static org.junit.Assert.assertEquals;

import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.Report;
import org.activequant.tradesystem.report.integration.birt.BirtReportRendererImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



/**
 * class to test various birt report renderer methods, i.e. pdf generation<br>
 * <br>
 * <b>History:</b><br>
 *  - [07.06.2007] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class BirtReportRendererImplTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetDocumentType() throws Exception {
	
		BirtReportRendererImpl renderer = new BirtReportRendererImpl();
		assertEquals(renderer.getDocumentType().toString(), BirtReportRendererImpl.DocumentType.HTML.toString());
		renderer.setDocumentType(BirtReportRendererImpl.DocumentType.PDF);
		assertEquals(renderer.getDocumentType().toString(), BirtReportRendererImpl.DocumentType.PDF.toString());
		
	}

	@Test 
	public void testGetReportDesign() throws Exception {
		BirtReportRendererImpl renderer = new BirtReportRendererImpl();
		assertEquals(null, renderer.getReportDesign());
		renderer.setReportDesign("reports/testccap3.rptdesign");
		assertEquals("reports/testccap3.rptdesign", renderer.getReportDesign());		
	}
	
	@Test
	public void testRenderReport() throws Exception {
		BirtReportRendererImpl renderer = new BirtReportRendererImpl("reports/testccap3.rptdesign");
		renderer.setFilepath("test.html");
		renderer.setDocumentType(BirtReportRendererImpl.DocumentType.HTML);
		Report r = new Report(null, new Account());
		renderer.renderReport(r);
	}
	
}
