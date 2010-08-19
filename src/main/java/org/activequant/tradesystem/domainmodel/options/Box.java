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

/**
 * 
 * Option box.<br>
 * <br>
 * <b>History:</b><br>
 *  - [20.09.2007] Created (Ulrich Staudinger)<br>
 *  - [22.09.2007] Switched to Map<..> and IllegalArgumentException (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 */
public class Box {
	
	private boolean longBox = true;
	private InstrumentSpecification lowerCall, lowerPut,upperCall, upperPut;
	
	
	public Box(){}
	
	public Box(boolean longBox, InstrumentSpecification lowerCall,
			InstrumentSpecification lowerPut,
			InstrumentSpecification upperCall, InstrumentSpecification upperPut) {
		this.lowerCall = lowerCall;
		this.lowerPut = lowerPut;
		this.upperCall = upperCall;
		this.upperPut = upperPut;
		this.longBox = longBox;
	}
	
	public String toString(){
		return (longBox ? "LONG " : "SHORT ") + "[" + lowerCall.toString()
				+ "+" + lowerPut.toString() + "+" + upperCall.toString() + "+"
				+ upperPut.toString() + "]";
	}

	public boolean isLongBox() {
		return longBox;
	}
	
	public void setLongBox(boolean longBox) {
		this.longBox = longBox;
	}
	
	public InstrumentSpecification getLowerCall() {
		return lowerCall;
	}
	
	public void setLowerCall(InstrumentSpecification option1) {
		this.lowerCall = option1;
	}
	
	public InstrumentSpecification getLowerPut() {
		return lowerPut;
	}
	
	public void setLowerPut(InstrumentSpecification option2) {
		this.lowerPut = option2;
	}
	
	public InstrumentSpecification getUpperCall() {
		return upperCall;
	}
	
	public void setUpperCall(InstrumentSpecification option3) {
		this.upperCall = option3;
	}
	
	public InstrumentSpecification getUpperPut() {
		return upperPut;
	}
	public void setUpperPut(InstrumentSpecification option4) {
		this.upperPut = option4;
	} 
	
}
