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

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.SeriesSpecification;

/**
 * @TODO desc<br>
 * <br>
 * <b>History:</b><br>
 *  - [30.09.2007] Created (Erik Nijkamp)<br>
 *
 *
 *  @author Erik Nijkamp
 */
public class DirUtils {
	
	public static String check(String dir) {
		mkdir(dir);
		return appendSlash(dir);
	}
	
	public static void mkdir(String dir) {
		if(!new File(dir).exists()) {
			new File(dir).mkdirs();
		}
	}
	
	public static String appendSlash(String dir) {
		return dir.endsWith(File.separator) ? dir : dir + File.separator;
	}

	private static final String SPEC_SEPARATOR = "_";
    public static String asFileName(InstrumentSpecification spec) {
    	String out = "";

    	if(spec.getSymbol() != null) {
    		out += spec.getSymbol().toString();
    	}
    	if(spec.getExchange() != null) {
    		out += SPEC_SEPARATOR + spec.getExchange();
    	}
    	if(spec.getVendor() != null) {
    		out += SPEC_SEPARATOR + spec.getVendor();
    	}
    	if(spec.getCurrency() != null) {
    		out += SPEC_SEPARATOR + spec.getCurrency();
    	}
    	if(spec.getSecurityType() != null) {
    		out += SPEC_SEPARATOR + spec.getSecurityType();
    	}
    	if(spec.getExpiry() != null) {
    		out += SPEC_SEPARATOR + spec.getExpiry().toString();
    	}
    	if(spec.getContractRight() != null) {
    		out += SPEC_SEPARATOR + spec.getContractRight();
    	}
    	if(spec.getStrike() > 0.0) {
    		out += SPEC_SEPARATOR + spec.getStrike();
    	}

    	return out.replace("/", "").replace("\\", "").replace(":", "");
	}

    private final static DateFormat format = new SimpleDateFormat("yyyyMMdd.HHmm");
    public static String asFileName(SeriesSpecification spec) {
    	String out = asFileName(spec.getInstrumentSpecification());

    	if(spec.getTimeFrame() != null) {
    		out += SPEC_SEPARATOR + spec.getTimeFrame().toString();
    	}
    	if(spec.getStartTimeStamp() != null) {
    		out += SPEC_SEPARATOR + format.format(spec.getStartTimeStamp());
    	}
    	if(spec.getEndTimeStamp() != null) {
    		out += SPEC_SEPARATOR + format.format(spec.getEndTimeStamp());
    	}

    	return out.replace("/", "").replace("\\", "").replace(":", "");
	}
}
