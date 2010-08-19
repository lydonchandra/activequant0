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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Locates a resource which can be located within the jar or in a<br>
 * directory.<br>
 * <br>
 * <b>History:</b><br>
 *  - [16.05.2006] Created (Erik N.)<br>
 * 
 *  @author Erik Nijkamp
 */
public class ResourceLocator {
	
	/**
	 * Find resource in jar or directory.
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 */
    public static URL find(String fileName) throws Exception
    {        
        // filesystem?
       	File file = new File(fileName);
       	if (file.exists())
       		return file.toURI().toURL();

        // classpath?
        URL location = null;
        location = ResourceLocator.class.getClassLoader().getResource(fileName);
        if(location != null)
            return location;
        
        location = ResourceLocator.class.getClassLoader().getResource('/' + fileName);
        if(location != null)
            return location;

        // not found
        throw new FileNotFoundException("Cannot find file '" + fileName + "'.");
    }
    
    /**
     * Open resource as stream.
     * @param fileName
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream openStream(String fileName) throws Exception {    	
        // filesystem?
       	File file = new File(fileName);
       	if (file.exists()) {
       		System.out.println("Opening file '" + fileName + "' as FileInputStream.");
       		return new FileInputStream(file);
       	}

        // classpath?
        InputStream stream = ResourceLocator.class.getResourceAsStream(fileName);
        if(stream != null) {
        	System.out.println("Opening file '" + fileName + "' as ResourceStream.");
            return stream;
        }
        
        stream = ResourceLocator.class.getResourceAsStream('/' + fileName);
        if(stream != null) {
        	System.out.println("Opening file '" + '/' + fileName + "' as ResourceStream.");
            return stream;
        }

        // not found
        throw new FileNotFoundException("Cannot open file '" + fileName + "' as stream.");
    }
    
    public static Properties loadProperties(String fileName) throws Exception {
    	Properties props = new Properties();
    	props.load(openStream(fileName));
    	return props;
    }
    
    public static Properties loadProperties(File file) throws Exception {
    	return loadProperties(file.getPath());
    }
}
