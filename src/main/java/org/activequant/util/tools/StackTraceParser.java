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
package org.activequant.util.tools;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Converts an array of stacktrace elements to a string. 
 * Based on ccapi2 code by Ulrich Staudinger. <br>
 * <br>
 * <b>History:</b><br>
 *  - [24.04.2006] Created (Erik Nijkamp)<br>
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public class StackTraceParser {
	
	/**
	 * Converts an array of stacktrace elements to a string.
	 * @param elements
	 * @return a string representation of a stack trace. 
	 */
    public static String getStackTraceMessage(StackTraceElement[] elements) {
        // Create message
        String separator = System.getProperty("line.separator");
        String message = "";
        for(int i = 0; i < elements.length; i++) {
            message += elements[i].toString() + separator;
        }
        return message;        
    }  
    
    public static String getStackTrace(Throwable ex){
		StringWriter sw = new StringWriter();
		PrintWriter s = new PrintWriter(sw);
		ex.printStackTrace(s);
		return sw.getBuffer().toString();
    }
}
