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

import java.io.File;

import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.ApplicationContext;
import org.springframework.context.access.ContextBeanFactoryReference;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


/**
 * Locates and provides all available application services.<br>
 * <br>
 * <b>History:</b><br>
 *  - [25.02.2007] Created (Erik Nijkamp)<br>
 *  - [25.10.2007] Added classpath/filesystem switch (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 *  
 */
public class ServiceLocator {
	
    
    /**
	 * The default bean reference factory location.
	 */
    private static final String DEFAULT_BEAN_REFERENCE_LOCATION = "factory.xml";
    
    /**
     * The default bean reference factory ID.
     */
    private static final String DEFAULT_BEAN_REFERENCE_ID = "beanRefFactory";
    
    /**
     * The bean factory reference instance.
     */
    private BeanFactoryReference beanFactoryReference;
    
    /**
     * The bean factory reference location.
     */
    private String beanFactoryReferenceLocation;
    
    /**
     * The bean factory reference id.
     */
    private String beanRefFactoryReferenceId;
    
	/**
     * The shared instance of this ServiceLocator.
     */
    protected static ServiceLocator instance = new ServiceLocator();
    
	
    protected ServiceLocator() {
        // shouldn't be instantiated
    }


    /**
     * Gets the shared instance of this Class
     *
     * @return the shared service locator instance.
     */
    public static final ServiceLocator instance() {
        return instance;
    }
    
    /**
     * Gets the shared instance of this Class
     *
     * @return the shared service locator instance.
     */
    public static final ServiceLocator instance(final String beanFactoryReferenceLocation) {
    	instance.init(beanFactoryReferenceLocation);
        return instance;
    }

    /**
     * Initializes the Spring application context from
     * the given <code>beanFactoryReferenceLocation</code>.  If <code>null</code>
     * is specified for the <code>beanFactoryReferenceLocation</code>
     * then the default application context will be used.
     *
     * @param beanFactoryReferenceLocation the location of the beanRefFactory reference.
     */
    public synchronized void init(final String beanFactoryReferenceLocation,
			final String beanRefFactoryReferenceId) {
		this.beanFactoryReferenceLocation = beanFactoryReferenceLocation;
		this.beanRefFactoryReferenceId = beanRefFactoryReferenceId;
		this.beanFactoryReference = null;
	}

    /**
	 * Initializes the Spring application context from the given
	 * <code>beanFactoryReferenceLocation</code>. If <code>null</code> is
	 * specified for the <code>beanFactoryReferenceLocation</code> then the
	 * default application context will be used.
	 * 
	 * @param beanFactoryReferenceLocation
	 *            the location of the beanRefFactory reference.
	 */
    public synchronized void init(final String beanFactoryReferenceLocation) {
		this.beanFactoryReferenceLocation = beanFactoryReferenceLocation;
		this.beanFactoryReference = null;
	}
    
    /**
	 * Shuts down the ServiceLocator and releases any used resources.
	 */
    public synchronized void shutdown() {
		if (this.beanFactoryReference != null) {
			this.beanFactoryReference.release();
			this.beanFactoryReference = null;
		}
	}

    /**
     * Gets the Spring ApplicationContext.
     */
    public synchronized ApplicationContext getContext() {
		if (this.beanFactoryReference == null) {
			// init defaults
			if (this.beanFactoryReferenceLocation == null) {
				this.beanFactoryReferenceLocation = DEFAULT_BEAN_REFERENCE_LOCATION;
			}
			if (this.beanRefFactoryReferenceId == null) {
				this.beanRefFactoryReferenceId = DEFAULT_BEAN_REFERENCE_ID;
			}
			// init context
			ApplicationContext context = resolveApplicationContext(beanFactoryReferenceLocation);
			
			// detect factory
	    	if(context.containsBean(DEFAULT_BEAN_REFERENCE_ID)) {
	    		// use bean factory (factory bean defined)
	    		beanFactoryReference = new ContextBeanFactoryReference((ApplicationContext) context.getBean(DEFAULT_BEAN_REFERENCE_ID));
	    	} else {
	    		// use context directly (no factory bean defined)
	    		beanFactoryReference = new ContextBeanFactoryReference(context);
	    	}
		}
		return (ApplicationContext) this.beanFactoryReference.getFactory();
	}
    
    private ApplicationContext resolveApplicationContext(String path) {    	
    	// filesystem
    	if(new File(path).exists()) {
    		return new FileSystemXmlApplicationContext(path);
    	}
    	
    	// classpath
    	if(ServiceLocator.class.getClassLoader().getResource(path) != null) {
    		return new ClassPathXmlApplicationContext(path);
    	}
    	
    	// error
    	throw new IllegalArgumentException("Cannot locate spring context file '"+path+"'");
    } 
    
    /**
     * Gets an instance of the given service.
     */
    @SuppressWarnings("unchecked")
	public final <T> T getServiceByName(String serviceName) {
		return (T) getContext().getBean(serviceName);
	}
    
    /**
     * Gets an instance of the given service.
     */
    @SuppressWarnings("unchecked")
	public final <T> T getService(Class<T> clazz) {
    	return (T) getContext().getBeansOfType(clazz).values().iterator().next();
	}
}