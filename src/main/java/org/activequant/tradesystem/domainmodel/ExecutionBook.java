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
import java.util.Iterator;
import java.util.List;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.util.tools.Arrays;



/**
 * 
 * The execution book gathers all executions for a given broker. <br>
 * <br>
 * <b>History:</b><br>
 *  - [08.06.2007] Created (Ulrich Staudinger)<br>
 *  - [02.07.2007] Adding execution iterator(Ulrich Staudinger)<br>
 *  - [14.07.2007] Added persistence (Erik Nijkamp)<br>
 *  - [29.09.2007] Removed annotations (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 */
public class ExecutionBook {
	
	private Long id;

	private List<Execution> executions = new ArrayList<Execution>();

	public ExecutionBook() {
		
	}
	
	public ExecutionBook(Execution... executions) {
		setExecutions(executions);
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

	public Execution[] getExecutions() {
		return executions.toArray(new Execution[]{});
	}

	public void setExecutions(List<Execution> executions) {
		this.executions = executions;
	}
	
	public void setExecutions(Execution... executions) {
		this.executions = Arrays.asList(executions);
	}
	
	public void addExecution(Execution exec){
		executions.add(exec);
	}
	
	public Iterator<Execution> getExecutionIterator(){
		return executions.iterator();
	}
	
	/**
	 * method to fetch a list of executions by contract specification
	 * @param spec
	 * @return
	 */
	public Execution[] getExecutionsBySpecification(InstrumentSpecification spec) {
		List<Execution> ret = new ArrayList<Execution>();

		for (Execution exec : executions) {
			if (exec.getInstrumentSpecification().equals(spec))
				ret.add(exec);
		}

		return ret.toArray(new Execution[] {});
	}

	/**
	 * method to fetch a list of executions by contract specification
	 * @param spec
	 * @return
	 */
	public Execution[] getExecutionsByOrderId(String orderId){
		List<Execution> ret = new ArrayList<Execution>();
		
		for (Execution exec : executions) {
			if (exec.getOrder().getBrokerAssignedId().equals(orderId))
				ret.add(exec);
		}
		
		return ret.toArray(new Execution[] {}); 
	}

}