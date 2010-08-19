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
package org.activequant.data.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.activequant.core.domainmodel.Symbol;
import org.activequant.util.exceptions.ValueNotFoundException;



/**
 *  Symbols.<br>
 * <br>
 * <b>History:</b><br>
 *  - [27.04.2006] Created(Erik Nijkamp)<br>
 *
 *
 *  @author Erik Nijkamp
 */
public class SymbolMap {
    
    private Map<String, Symbol> symbols = new HashMap<String, Symbol>();
    
    public SymbolMap() {
    	
    }
    
    public SymbolMap(String[][] entries) {
    	addAssociatedSymbols(entries);
    }
    
	/**
	 * Associates a provider-specific id with a generic one.
	 * This mechanism allows us to write generic handler code
	 * because we are not forced to use provider-specific symbol names.
	 * 
	 * @param symbol generic symbol e.g. "DAX", "BUNDFUTURE"
	 * @param nativeName specific provider-dependent symbol name e.g. "71", "forex2"
	 */
	public void associateSymbol(Symbol symbol, String nativeName) {
		if (!symbols.containsKey(nativeName)) {
			symbols.put(nativeName, symbol);
		}
	}

	/**
	 * Allows to add the whole list of mapped Symbols. 
	 * @param entries
	 */
	public void addAssociatedSymbols(Set<Entry<Symbol, String>> entries) {
		for (Entry<Symbol, String> entry : entries)
			associateSymbol(entry.getKey(), entry.getValue());
	}

	/**
	 * Allows to add the whole list of mapped Symbols. 
	 * @param entries
	 */
	public void addAssociatedSymbols(String[][] entries) {
		for (String[] entry : entries)
			associateSymbol(new Symbol(entry[0]), entry[1]);
	}
	
	public String getNativeSymbolName(Symbol symbol)
			throws ValueNotFoundException {
		if (!symbols.containsValue(symbol)) {
			throw new ValueNotFoundException();
		}
		for (Entry<String, Symbol> entry : symbols.entrySet()) {
			if (entry.getValue().equals(symbol)) {
				return entry.getKey();
			}
		}
		throw new ValueNotFoundException();
	}

	/**
	 * 
	 * @param nativeName
	 * @return
	 */
	public boolean hasSymbol(String nativeName) {
		return symbols.containsKey(nativeName);
	}

	/**
	 * 
	 * @param genericName
	 * @return
	 */
	public boolean hasNativeSymbolName(Symbol symbol) {
		return symbols.containsValue(symbol);
	}
	
	/**
	 * 
	 * @param nativeName
	 * @return
	 */
	public Symbol getSymbol(String nativeName) {
		return symbols.get(nativeName);
	}
}