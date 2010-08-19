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
package org.activequant.tradesystem.domainmodel.options;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.tradesystem.types.OrderSide;


/**
 * 
 * Strangle option combination<br>
 * <br>
 * <b>History:</b><br>
 *  - [21.09.2007] Created (Ulrich Staudinger)<br>
 *  - [27.09.2007] Fixed exceptions ... (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 */
public class Strangle extends Straddle {

	public Strangle(InstrumentSpecification underlying,
			InstrumentSpecification call, InstrumentSpecification put,
			OrderSide side) {
		super(underlying, call, put, side);
	}

	public Strangle(InstrumentSpecification underlying,
			InstrumentSpecification call, InstrumentSpecification put) {
		super(underlying, call, put);
	}

	public Strangle(InstrumentSpecification underlying,
			InstrumentSpecification call, InstrumentSpecification put,
			double positionSizeCall, double positionSizePut,
			OrderSide side) {

		super(underlying, call, put, positionSizeCall, positionSizePut,
				side);
	}
	
}
