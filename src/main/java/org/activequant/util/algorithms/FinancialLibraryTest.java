package org.activequant.util.algorithms;
import static java.lang.System.out;

import java.io.File;

import junit.framework.Assert;

import org.activequant.core.domainmodel.Candle;
import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.types.TimeStamp;
//import org.activequant.data.retrieval.integration.series.CsvCandleSeriesSource;
//import org.activequant.util.charting.CandlestickChart;

public class FinancialLibraryTest {

	public static void testSMA2() {
		double[] arr = { 1,2,3,4,3,2,1 };
		int period = 2;
		int startIndex = period;
		double avg2 = FinancialLibrary.SMA2(period, arr, startIndex++);
		Assert.assertEquals(avg2,1.5);
		Assert.assertEquals( FinancialLibrary.SMA2(period,arr,startIndex++), 2.5 );
		Assert.assertEquals( FinancialLibrary.SMA2(period,arr,startIndex++), 3.5 );
		Assert.assertEquals( FinancialLibrary.SMA2(period,arr,startIndex++), 3.5 );
		Assert.assertEquals( FinancialLibrary.SMA2(period,arr,startIndex++), 2.5 );
		Assert.assertEquals( FinancialLibrary.SMA2(period,arr,startIndex++), 1.5 );
	}
	
	public static TimeStampAndAverage getTimeStampAndAverage(CandleSeries candleSeries, int period) {

		double avg2[] = new double[candleSeries.getCandles().length];
		TimeStamp ts2[] = new TimeStamp[candleSeries.getCandles().length];
		int index = 0;
		for( Candle candle: candleSeries.getCandles()) {			
			double tempAvg = candle.getClosePrice();
			avg2[index] = tempAvg;
			ts2[index] = candle.getTimeStamp();
			index++;
		}
		
		double finalAvg2 [] = new double[candleSeries.getCandles().length];
		for(int index21=0; index21<(avg2.length-period); index21++) {
			double tmpAvg = FinancialLibrary.SMA(period, avg2, index21);
			finalAvg2[index21] = tmpAvg;			
		}

		TimeStampAndAverage tsa = new TimeStampAndAverage();
		tsa.average = finalAvg2;
		tsa.timestamps = ts2;
		return tsa;
		
	}
	
	public static TimeStampAndAverage getTimeStampAndAverage2(CandleSeries candleSeries, int period) {

		double avg2[] = new double[candleSeries.getCandles().length];
		TimeStamp ts2[] = new TimeStamp[candleSeries.getCandles().length];
		int index = 0;
		for( Candle candle: candleSeries.getCandles()) {			
			double tempAvg = candle.getClosePrice();
			avg2[index] = tempAvg;
			ts2[index] = candle.getTimeStamp();
			index++;
		}
		
		int startIndex = period;
		double finalAvg2 [] = new double[candleSeries.getCandles().length];
		for(int index21=period; index21<avg2.length; index21++) {
			double tmpAvg = FinancialLibrary.SMA2(period, avg2, startIndex++);
			finalAvg2[index21] = tmpAvg;			
		}

		TimeStampAndAverage tsa = new TimeStampAndAverage();
		tsa.average = finalAvg2;
		tsa.timestamps = ts2;
		return tsa;
		
	}

	
	static class TimeStampAndAverage {
		public double[] average;
		public TimeStamp[] timestamps;
	}
	
	public static void main(String[] args) throws Exception {

		testSMA2();
		
		int [] periods = { 3, 5 };
		
		//double [] vals = { 1, 2, 3, 4, 5, 5, 4, 3, 2, 1 };
		
		double [] vals = { 1, 2, 3, 4, 5 , 4, 3, 2, 1, 2, 3, 4, 5, 4, 3 ,2, 1 };
		
		int period1 = 2;
		int period2 = 4;
		out.println("sma period " + period1);
		for( int index=0; index<vals.length; index++ ) {
						
			if( (index + period1) <= vals.length &&
				(index + period1+1) <= vals.length
			) {
				
				double val0_0 = FinancialLibrary.SMA( period1, vals, index );				
				double val0_1 = FinancialLibrary.SMA( period1, vals, (index+1) );
				out.println(val0_0 + ":" + val0_1);
				
				if( (index + period2) <= vals.length &&
					(index + period2+1) <= vals.length
					) {
					
					double val1_0 = FinancialLibrary.SMA( period2, vals, index);
					double val1_1 = FinancialLibrary.SMA(period2, vals, (index+1));
					out.println(val1_0 + ":" + val1_1);
					
					
					if(val0_0 > val1_0 && val0_1 < val1_1) {
						out.println("Long");
						// long order.
					} else if(val0_0 < val1_0 && val0_1 > val1_1) {
						out.println("Short");
						// short order.					
					} else {
					}
					out.println(" ");
				}
			}
			
		}

//		CsvCandleSeriesSource csvSource = new CsvCandleSeriesSource();
//		csvSource.setDateFormat("yyyy-MM-dd");
//		csvSource.setDelimiter(",");
//		CandleSeries cs1 = csvSource.fetch2( new File("data/QQQQ2.csv") );
//
//		TimeStampAndAverage tsa0 = getTimeStampAndAverage2(cs1, 100);
//		TimeStampAndAverage tsa = getTimeStampAndAverage2(cs1, 50);
//
//		
//		
//		CandlestickChart chart = new CandlestickChart();
//		chart.addLineSeriesChart("Title", tsa0.timestamps, tsa0.average);
//		chart.addLineSeriesChart("Title2", tsa.timestamps, tsa.average);
//		chart.setCandleSeries(cs1);
//		chart.renderToPng("blah.png", 600, 600);
		
//		CandleSeries[] aSeries = null;
//		for(CandleSeries series : aSeries) {
//				
//				double val0_0 = FinancialLibrary.SMA((int)period1, series.getCloses(), 0);
//				double val0_1 = FinancialLibrary.SMA((int)period1, series.getCloses(), 1);
//				
//				double val1_0 = FinancialLibrary.SMA((int)period2, series.getCloses(), 0);
//				double val1_1 = FinancialLibrary.SMA((int)period2, series.getCloses(), 1);
//				
//				// order
//				if(val0_0 > val1_0 && val0_1 < val1_1) {	
//					// long order.
//				} else if(val0_0 < val1_0 && val0_1 > val1_1) {
//					// short order.					
//				} else {
//				}
//			}
//		}
	
	}
}
