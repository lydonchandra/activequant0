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

****/package org.activequant.tradesystem.system.integration;

import java.util.ArrayList;
import java.util.List;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.Market;
import org.activequant.core.types.TimeStamp;
import org.activequant.data.util.Tuple;
import org.activequant.tradesystem.domainmodel.Account;
import org.activequant.tradesystem.domainmodel.Order;
import org.activequant.tradesystem.system.TradeSystemBase;
import org.activequant.util.algorithms.BlackScholes;
import org.activequant.util.algorithms.FinancialLibrary;
import org.activequant.util.charting.CandlestickChart;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFrame;

/**
 * plain trade system that just charts upon evaluation.<br>
 * <br>
 * <b>History:</b><br>
 *  - [16.08.2007] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class VolatilityCharterTradeSystem extends TradeSystemBase {
	
	protected final static Logger log = Logger.getLogger(VolatilityCharterTradeSystem.class);

	private List<ChartFrame> charts = new ArrayList<ChartFrame>();
	CandleSeries refSeries = null; 
	CandleSeries volaSeriesCall = null; 
	CandleSeries volaSeriesPut = null;
	List<Tuple<TimeStamp, Double>> volaSeriesCallAverage1 = new ArrayList<Tuple<TimeStamp, Double>>();
	List<Tuple<TimeStamp, Double>> volaSeriesCallAverage2 = new ArrayList<Tuple<TimeStamp, Double>>();
	List<Tuple<TimeStamp, Double>> volaSeriesCallAverage3 = new ArrayList<Tuple<TimeStamp, Double>>();
	List<Tuple<TimeStamp, Double>> volaSeriesPutAverage1 = new ArrayList<Tuple<TimeStamp, Double>>();
	List<Tuple<TimeStamp, Double>> volaSeriesPutAverage2 = new ArrayList<Tuple<TimeStamp, Double>>();
	List<Tuple<TimeStamp, Double>> volaSeriesPutAverage3 = new ArrayList<Tuple<TimeStamp, Double>>();
	
	double interestRate = 0.0525;
	double timeToMaturity = 100/365.0; // corresponds to a quarter year
	
	private int window=0;
	public synchronized Order[] onMarket(Account account, Market market) throws Exception {
		log.info("[evaluate] using account '" + account.getId() + "'");
		log.info("[evaluate] candle series in market: "+market.getCandleSeries().length);

		List<Order> orders = new ArrayList<Order>();
		window = 0;
		for(ChartFrame cf : charts){
			cf.dispose();
		}
		charts.clear();
		//iterate over the candle series in the market object.
		for(CandleSeries cs : market.getCandleSeries()){
			if(!cs.isEmpty()){
				CandleSeries tempSeries = new CandleSeries(cs.getSeriesSpecification());
				// chart the latest 100 candles.
				int maxIndex = cs.size()>201?200:cs.size();
				for(int i=0;i<maxIndex-1;i++){
					tempSeries.add(cs.get(i));
				}
				
				
				// simply chart it. 
				chartIt(tempSeries);
				
				if(cs.getSeriesSpecification().getInstrumentSpecification().getSecurityType().equals("FUT")){
					refSeries = cs; 
				}
				
				if(cs.getSeriesSpecification().getInstrumentSpecification().getSecurityType().equals("OPT")){
					if(refSeries!=null){
						// calculate the volatilities. 
						Candle c = cs.get(0);
						final Candle refCandle = refSeries.getByTimeStamp(c.getTimeStamp());
						
						final double vola = BlackScholes.resolveImplicitVolatility(cs.getSeriesSpecification().getInstrumentSpecification().getContractRight().toLowerCase().charAt(0), 
								refCandle.getClosePrice(), cs.getSeriesSpecification().getInstrumentSpecification().getStrike(), 
								timeToMaturity, 0.0, interestRate, c.getClosePrice());
						
						Candle volaCandle = new Candle(c.getTimeStamp(), vola,vola,vola, vola);
						
						if(cs.getSeriesSpecification().getInstrumentSpecification().getContractRight().equalsIgnoreCase("C")){
							if(volaSeriesCall==null)volaSeriesCall = new CandleSeries(cs.getSeriesSpecification());
							volaSeriesCall.add(0, volaCandle);
							CandlestickChart chart = chartIt(volaSeriesCall);
							// 
							if(volaSeriesCall.size()>5){
								double ave5 = FinancialLibrary.SMA(5, volaSeriesCall.getCloses(), 0);
								Tuple<TimeStamp, Double> tuple = new Tuple<TimeStamp, Double>(volaCandle.getTimeStamp(), ave5);
								volaSeriesCallAverage1.add(0, tuple);
								if(volaSeriesCallAverage1.size()>201)volaSeriesCallAverage1 = volaSeriesCallAverage1.subList(0, 200);
								chart.addLineSeriesChart("Vola Ave 5", volaSeriesCallAverage1);
							}
							
							if(volaSeriesCall.size()>15){
								double ave5 = FinancialLibrary.SMA(15, volaSeriesCall.getCloses(), 0);
								Tuple<TimeStamp, Double> tuple = new Tuple<TimeStamp, Double>(volaCandle.getTimeStamp(), ave5);
								volaSeriesCallAverage2.add(0, tuple);
								if(volaSeriesCallAverage2.size()>201)volaSeriesCallAverage2 = volaSeriesCallAverage2.subList(0, 200);
								chart.addLineSeriesChart("Vola Ave 5", volaSeriesCallAverage2);
							}
							
							if(volaSeriesCall.size()>30){
								double ave5 = FinancialLibrary.SMA(30, volaSeriesCall.getCloses(), 0);
								Tuple<TimeStamp, Double> tuple = new Tuple<TimeStamp, Double>(volaCandle.getTimeStamp(), ave5);
								volaSeriesCallAverage3.add(0, tuple);
								if(volaSeriesCallAverage3.size()>201)volaSeriesCallAverage3 = volaSeriesCallAverage3.subList(0, 200);
								chart.addLineSeriesChart("Vola Ave 5", volaSeriesCallAverage3);
							}

						}
						
						if(cs.getSeriesSpecification().getInstrumentSpecification().getContractRight().equalsIgnoreCase("P")){
							if(volaSeriesPut==null)volaSeriesPut = new CandleSeries(cs.getSeriesSpecification());
							volaSeriesPut.add(0, volaCandle);
							CandlestickChart chart = chartIt(volaSeriesPut);
							
							// 
							if(volaSeriesPut.size()>5){
								double ave5 = FinancialLibrary.SMA(5, volaSeriesPut.getCloses(), 0);
								Tuple<TimeStamp, Double> tuple = new Tuple<TimeStamp, Double>(volaCandle.getTimeStamp(), ave5);
								volaSeriesPutAverage1.add(0, tuple);
								if(volaSeriesPutAverage1.size()>201)volaSeriesPutAverage1 = volaSeriesPutAverage1.subList(0, 200);
								chart.addLineSeriesChart("Vola Ave 5", volaSeriesPutAverage1);
							}
							if(volaSeriesPut.size()>15){
								double ave5 = FinancialLibrary.SMA(15, volaSeriesPut.getCloses(), 0);
								Tuple<TimeStamp, Double> tuple = new Tuple<TimeStamp, Double>(volaCandle.getTimeStamp(), ave5);
								volaSeriesPutAverage2.add(0, tuple);
								if(volaSeriesPutAverage2.size()>201)volaSeriesPutAverage2 = volaSeriesPutAverage2.subList(0, 200);
								chart.addLineSeriesChart("Vola Ave 5", volaSeriesPutAverage2);
							}

							if(volaSeriesPut.size()>30){
								double ave5 = FinancialLibrary.SMA(30, volaSeriesPut.getCloses(), 0);
								Tuple<TimeStamp, Double> tuple = new Tuple<TimeStamp, Double>(volaCandle.getTimeStamp(), ave5);
								volaSeriesPutAverage3.add(0, tuple);
								if(volaSeriesPutAverage3.size()>201)volaSeriesPutAverage3 = volaSeriesPutAverage3.subList(0, 200);
								chart.addLineSeriesChart("Vola Ave 5", volaSeriesPutAverage3);
							}
							
						}
					}
				}
			}
		}		

		return orders.toArray(new Order[]{});
	}
	
	
	private CandlestickChart chartIt(CandleSeries tempSeries){
		// simply chart it. 
		CandlestickChart chart = new CandlestickChart();
		chart.setCandleSeries(tempSeries);
		ChartFrame cf = new ChartFrame(""+tempSeries.getSeriesSpecification().toString(), chart.getChart());
		cf.setBounds(window*400, 0, 400, 300);
		cf.setVisible(true);
		charts.add(cf);
		window++;
		return chart; 
	}

	public String getDescription() {
		return "Simple real time charting trade system.";
	}

	public String getName() {
		return "ChartingTradeSystem";
	}



	public double getInterestRate() {
		return interestRate;
	}



	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}



	public double getTimeToMaturity() {
		return timeToMaturity;
	}



	public void setTimeToMaturity(double timeToMaturity) {
		this.timeToMaturity = timeToMaturity;
	}
}
