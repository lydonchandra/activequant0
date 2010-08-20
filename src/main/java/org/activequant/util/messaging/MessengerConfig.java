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
package org.activequant.util.messaging;

/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 * 21.12.2006 Created (en)
 *
 *  @author Erik Nijkamp
 */
public class MessengerConfig {
    public String accountName, accountPassword, implementationClass;
    
    public MessengerConfig(String accountName, String accountPassword, String implementationClass) {
       this.accountName = accountName;
       this.accountPassword = accountPassword;
       this.implementationClass = implementationClass;
    }
}
