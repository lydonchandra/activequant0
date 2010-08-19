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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.Symbol;
import org.activequant.core.types.TimeStamp;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.BalanceBook;
import org.activequant.tradesystem.domainmodel.BrokerAccount;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.domainmodel.Report;
import org.activequant.tradesystem.report.integration.birt.BirtEngine;
import org.activequant.tradesystem.report.integration.birt.BirtReportRendererImpl;
import org.activequant.tradesystem.report.integration.birt.BirtReportRendererImpl.DocumentType;
import org.eclipse.birt.report.engine.api.IReportEngine;


/**
 * initial test class for the report service. 
 * <br>
 * <b>History:</b><br>
 *  - [11.05.2007] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class ReportServiceTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ReportServiceTest.class);
    }
    
    
    @SuppressWarnings("deprecation")
	public void testCase1() throws Exception {
        // creating a simple account. 
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.YEAR, 2006);
        cal.set(Calendar.MONTH, 01);
        cal.set(Calendar.DATE, 01);
        
        
        Account account = new Account();
        account.setHolder("Ulrich Staudinger");
        
        // add a broker account. 
        BrokerAccount brokerAccount = new BrokerAccount();
        account.addBrokerAccount(brokerAccount);
        // add ten random orders. 
        for(int i=0;i<10;i++){
        	Order o = new Order();
        	Date d = new Date(); 
        	d.setDate(i+1);
        	o.setOrderTimeStamp(new TimeStamp(d));
        	o.setAveragePrice(Math.random());
        	o.setExecutedQuantity(Math.random()*100);
        	InstrumentSpecification cs = new InstrumentSpecification();
        	cs.setSymbol(new Symbol("DAX"+i));
        	o.setInstrumentSpecification(cs);
        	brokerAccount.getOrderBook().addOrder(o);
        }
       
        
        // TODO: render it. 
        
        IReportEngine engine = BirtEngine.start();
        
        
        BirtReportRendererImpl b = new BirtReportRendererImpl();
        b.setFilepath("test1.html");
        
        b.setReportDesign("reports/testccap3.rptdesign");
        
        Report r = new Report(account);
        
        
        // render html 
        b.setDocumentType(DocumentType.HTML);
        b.renderReport(r);
        BirtEngine.stop(engine);        
    }

    
    public void testCase2() throws Exception {
        // creating a simple account. 
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.YEAR, 2006);
        cal.set(Calendar.MONTH, 01);
        cal.set(Calendar.DATE, 01);
        
        Account account = new Account();
        
        BirtReportRendererImpl b = new BirtReportRendererImpl();
        b.setFilepath(".");
        b.setReportDesign("reports/list_accounts.rptdesign");
        
        Report r = new Report(account);
        // render html 
        b.setDocumentType(DocumentType.HTML);
        b.renderReport(r);

        // render pdf. 
        b.setDocumentType(DocumentType.PDF);
        b.renderReport(r);
    }

    /**
     * Testcase by Knut Linke
     *
     */
    public void testCase3() throws Exception {
         
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.YEAR, 2006);
        cal.set(Calendar.MONTH, 01);
        cal.set(Calendar.DATE, 01);
        
        double startBalance = 100000;       
        
        // creating a balance record
        BalanceBook balance = new BalanceBook();
        for(int i = 1;i<100;i++){   
            cal.add(Calendar.DATE, 1);
            balance.addBalanceEntry(Math.random()*startBalance, new TimeStamp(cal.getTime()));
        }
        
        Account account = new Account();
        
        BirtReportRendererImpl b = new BirtReportRendererImpl();
        b.setFilepath(".");
        b.setReportDesign("reports/new_report_2.rptdesign");
        
        Report r = new Report(account);
        // render html 
        b.setDocumentType(DocumentType.HTML);
        b.renderReport(r);

        // render pdf. 
        b.setDocumentType(DocumentType.PDF);
        b.renderReport(r);
        
    }    
}