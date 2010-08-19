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
package org.activequant.tradesystem.domainmodel;

import org.activequant.core.domainmodel.Market;
import org.activequant.util.tools.DecorationsMap;


/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [25.02.2007] Created (Erik Nijkamp)<br>
 *  - [15.07.2007] Added market object (Ulrich Staudinger) <br>
 *
 *  @author Erik Nijkamp
 *  @author Ulrich Staudinger
 */
public class Report {
	
	private String testData;
	private int[] xData;
	private double[] yData;
	
    private Account account;
    private Market market; 
    
    private DecorationsMap reportValues = new DecorationsMap();
	

    public Report(Account account){ 
        this.account = account; 
    }
    
    public Report(Market market, Account account){
    	this.market = market; 
        this.account = account; 
    }
    
    public DecorationsMap getReportValues() {
        return reportValues;
    }

    public void setReportValues(DecorationsMap reportValues) {
        this.reportValues = reportValues;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

	public String getTestData() {
		return testData;
	}

	public void setTestData(String testData) {
		this.testData = testData;
	}

	public int[] getXData() {
		return xData;
	}

	public void setXData(int[] data) {
		xData = data;
	}

	public double[] getYData() {
		return yData;
	}

	public void setYData(double[] data) {
		yData = data;
	}

	public Market getMarket() {
		return market;
	}

	public void setMarket(Market market) {
		this.market = market;
	}
}
