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
package org.activequant.util.algorithms;

/**
 * 
 * Does a black scholes pricing.<br/> 
 * <br/>
 * - [24.05.2007] Created (Ulrich Staudinger). <br/>
 * 
 * @author Ulrich Staudinger
 * 
 * 
 */
public class BlackScholes {
	
	/**
	 * black-scholes computation.
	 * 
	 * @param CallPutFlag 'c' or 'p' 
	 * @param S the current price of the underlying
	 * @param X the strike
	 * @param T time to maturity in years, i.e. 5.0/252.0 = five business days
	 * @param b cost of carry
	 * @param r annual interest rate
	 * @param sigma volatility, either historical or implicit. 
	 * @return the price
	 */
	public static double computePrice(char CallPutFlag, double S, double X, double T,
			double b, double r, double sigma) {
		double d1, d2;
		
		d1 = (Math.log(S / X) + (b + sigma * sigma / 2.0) * T) / (sigma * Math.sqrt(T));
		d2 = d1 - sigma * Math.sqrt(T);

		if (CallPutFlag == 'c') {
			return S * Math.exp((b - r) * T) * CND(d1) - X * Math.exp(-r * T) * CND(d2);
		} else {
			return X * Math.exp(-r * T) * CND(-d2) - S * Math.exp(( b - r ) * T ) * CND(-d1);
		}
	}

	/**
	 * uses a simple approximation algorithm to retrieve the implicit vola. 
     * http://www.stat.purdue.edu/~achronop/STAT598W/In-Class%20Assignment_2.pdf
     *  
     * iterative approximation. 
     *  
	 * @param callPutFlag
	 * @param S
	 * @param X
	 * @param T
	 * @param b
	 * @param r
	 * @param optionPrice
	 * @return
	 */
	public static double resolveImplicitVolatility(char callPutFlag, double S, double X, double T, double b, double r, double optionPrice){
		double lowVola = 0.0; 
		double midVola = 0.5;
		double highVola = 1.0;
		double vola = 0.0; 
		for(int i=0;i<50;i++){
			
			//double computedPriceLow = computePrice(callPutFlag, S, X, T, b, r, lowVola); 
			double computedPriceMid = computePrice(callPutFlag, S, X, T, b, r, midVola);
			//double computedPriceHigh = computePrice(callPutFlag, S, X, T, b, r, highVola);
			
			if (optionPrice > computedPriceMid) {
				lowVola = midVola;
				midVola = (midVola + highVola) / 2.0;
				// highVola = highVola;
			} else {
				// lowVola = lowVola;
				highVola = midVola;
				midVola = (lowVola + midVola) / 2.0;

			}
			
			vola = midVola; 
			
		}
		return vola;
	}

	// The cumulative normal distribution function
	public static double CND(double X) {
		double K, w;
		double 	a1 = 0.31938153, a2 = -0.356563782, 
				a3 = 1.781477937, a4 = -1.821255978, 
				a5 = 1.330274429;

		K = 1.0 / (1.0 + 0.2316419 * Math.abs(X));
		w = NDF(X) * (a1 * K + a2 * K * K + a3 * Math.pow(K, 3) + a4
						* Math.pow(K, 4) + a5 * Math.pow(K, 5)) - 0.5;

		w = 0.5 - (w * Math.signum(X));
		
		return w;
	}
	
	public static double NDF(double x){
		return Math.exp( (-x) * x/2.0 ) / Math.sqrt(8 * Math.atan(1));
	}
	
	
}
