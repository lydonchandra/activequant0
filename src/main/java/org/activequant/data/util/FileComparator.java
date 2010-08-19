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

import java.io.File;
import java.text.Collator;
import java.util.Comparator;

/**
 * 
 * compares the NAME and TYPE of two files, not their content.<br>
 * <br>
 * <b>History:</b><br>
 *  - [01.01.2006] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class FileComparator implements Comparator<File> {
	private Collator c = Collator.getInstance();

	public int compare(File f1, File f2) {

		if (f1.isDirectory() && f2.isFile())
			return -1;
		if (f1.isFile() && f2.isDirectory())
			return 1;

		return c.compare(f1.getName(), f2.getName());
	}
}