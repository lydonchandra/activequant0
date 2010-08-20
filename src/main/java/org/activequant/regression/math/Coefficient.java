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
package org.activequant.regression.math;

/**
 * @TODO.<br>
 * <br>
 * <b>History:</b><br>
 *  - [31.05.2006] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class Coefficient {
	
	private double value = 0.0f;
	private double error = 0.0f;
	private double variation = 0.0f;
	private String name = null;
	private boolean isAxisTranslation = false;
	
	public Coefficient(boolean isAxisTranslation, String name, double value,
			double var, double err) {
		this.isAxisTranslation = isAxisTranslation;
		this.name = name;
		this.value = value;
		this.variation = var;
		this.error = err;
	}
	
	public String getName() {
		return name;
	}
	
	public double getValue() {
		return value;
	}
	
	public double getVariation() {
		return variation;
	}
	
	public double getError() {
		return error;
	}
	
	public double getTStat() {
		return getValue()/getError();
	}

	/**
	 * @return the isAxisTranslation
	 */
	public boolean isAxisTranslation() {
		return isAxisTranslation;
	}
	

}
