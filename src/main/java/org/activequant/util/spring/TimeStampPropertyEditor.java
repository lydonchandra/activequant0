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
package org.activequant.util.spring;

import java.beans.PropertyEditorSupport;
import java.util.TimeZone;

import org.activequant.core.util.TimeStampFormat;

/**
 * Spring helper to conveniently instantiate TimeStamp from a date/time string.
 * <b>History:</b><br>
 *  - [03.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class TimeStampPropertyEditor extends PropertyEditorSupport {
	
	private final String DEFAULT_ZONE = "UTC";
	private final String DEFAULT_FORMAT = "yyyy/MM/dd HH:mm:ss.SSSnnnnnn";
	
	private String zone = DEFAULT_ZONE;
	
	private TimeStampFormat format = new TimeStampFormat(DEFAULT_FORMAT);
	
	public String getFormat() {
		return format.toString();
	}
	
	public void setFormat(String val) {
		format = new TimeStampFormat(val);
		format.setTimeZone(TimeZone.getTimeZone(zone));
	}
	
	public String getTimeZone() {
		return zone;
	}
	
	public void setTimeZone(String val) {
		zone = val;
		format.setTimeZone(TimeZone.getTimeZone(zone));
	}

    public void setAsText(String text) {
        setValue(format.parse(text));
    }
}