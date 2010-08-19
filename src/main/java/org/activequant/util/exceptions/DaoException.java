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
package org.activequant.util.exceptions;

/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [16.07.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public class DaoException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 320890611139231032L;
	
	public DaoException(Exception e) {
		super(e);
	}
	
	public DaoException() {
		super();
	}
	
	public DaoException(String message) {
		super(message);
	}

}
