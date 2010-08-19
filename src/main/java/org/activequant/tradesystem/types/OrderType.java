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
package org.activequant.tradesystem.types;

/**
 * Enumeration that holds a time frame. <br>
 * <br>
 * <b>History:</b><br>
 *  - [03.05.2006] Created (Ulrich Staudinger)<br>
 *  - [11.06.2007] Consts according to FixMl (Erik Nijkamp)<br>
 *  - [02.11.2007] Adding trailing stop order type.<br>
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public enum OrderType {

	MARKET, 
    LIMIT, 
    STOP, 
    STOP_LIMIT, 
    TRAILING_STOP;
	
}

