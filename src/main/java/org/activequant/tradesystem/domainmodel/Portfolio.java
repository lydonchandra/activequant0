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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.util.exceptions.ValueNotFoundException;



/**
 * 
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [09.06.2007] Created (Ulrich Staudinger)<br>
 *  - [02.07.2007] Adding remove portfolio position (Ulrich Staudinger)<br>
 *  - [14.07.2007] Added persistence (Erik Nijkamp)<br>
 *  - [29.09.2007] Removed annotations (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 */
public class Portfolio {
	
	private Long id;

	private List<Position> positions = new ArrayList<Position>();

	public Portfolio() {
		
	}
	
	public Portfolio(Position...positions) {
		setPositions(positions);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public boolean hasId() {
		return id != null;
	}

	public Position[] getPositions() {
		return positions.toArray(new Position[] {});
	}

	public void setPositions(List<Position> positions) {
		this.positions = positions;
	}

	public void setPositions(Position... positions) {
		this.positions = Arrays.asList(positions);
	}
	
	public boolean hasPosition(InstrumentSpecification instrumentSpecification) {
		for (Position p : positions) {
			if (p.getInstrumentSpecification().equals(instrumentSpecification)) {
				return true;
			}
		}	
		return false;
	}

	public Position getPosition(InstrumentSpecification instrumentSpecification) throws ValueNotFoundException {
		for (Position p : positions) {
			if (p.getInstrumentSpecification().equals(instrumentSpecification)) {
				return p;
			}
		}
		throw new ValueNotFoundException("Cannot find position '" + instrumentSpecification + "'.");
	}

	public void addPosition(Position position){
		this.positions.add(position);
	}
	
	public void removePosition(Position position){
		this.positions.remove(position);
	}
	
	public Iterator<Position> getPositionIterator() {
		return positions.iterator();
	}
}