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

****/package org.activequant.util.pattern.producer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.activequant.util.exceptions.ValueNotFoundException;
import org.activequant.util.pattern.producer.DynamicConstructor.Parameter;


/**
 * @TODO desc<br>
 * @TODO: is this class still requried ? 
 * <br>
 * <b>History:</b><br>
 *  - [27.10.2006] Created (Erik Nijkamp)<br>
 *
 *
 *  @author Erik Nijkamp
 *  @author Ulrich Staudinger
 */
public class DynamicProducer<Product> {
	
	private static final String STATIC_CONSTRUCTOR_METHOD = "getDefaultConstructor";
	
	private Class<? extends Product> defaultClass = null;
	
	public DynamicProducer() {		
	}
	
	public DynamicProducer(Class<? extends Product> defaultClass) {
		this.defaultClass = defaultClass;
	}

	@SuppressWarnings("unchecked")
	public synchronized Product create(String className) throws Exception {
		Class<? extends Product> clazz = (Class<? extends Product>) Class
				.forName(className);
		if(clazz == null)
			clazz = defaultClass;
		
		if (clazz == null)
			throw new Exception("Cannot find subsystem '" + className + "'.");

		Constructor<? extends Product> constructor = clazz
				.getConstructor(new Class[] {});

		Product instance = constructor.newInstance(new Object[] {});
		return instance;
	}
	
	public Product create(String className,
			String[][] configEntries) throws Exception {
		return create(className, convertArray(configEntries));
	}

	public <T1> Product create(String className, T1 t1,
			String[][] configEntries) throws Exception {
		return create(className, t1, convertArray(configEntries));
	}

	public <T1, T2> Product create(String className, T1 t1, T2 t2,
			String[][] configEntries) throws Exception {
		return create(className, t1, t2, convertArray(configEntries));
	}

	public <T1, T2, T3> Product create(String className, T1 t1,
			T2 t2, T3 t3, String[][] configEntries) throws Exception {
		return create(className, t1, t2, t3, convertArray(configEntries));
	}

	public <T1, T2, T3, T4> Product create(String className,
			T1 t1, T2 t2, T3 t3, T4 t4, String[][] configEntries)
			throws Exception {
		return create(className, t1, t2, t3, t4, convertArray(configEntries));
	}

	private Map<String, String> convertArray(String[][] configEntries) {
		Map<String, String> hash = new HashMap<String, String>();
		for (String[] entry : configEntries)
			hash.put(entry[0], entry[1]);
		return hash;
	}
	
	public Product create(String className,
			Properties configEntries) throws Exception {
		return create(className, convertProperties(configEntries));
	}

	public <T1> Product create(String className, T1 t1,
			Properties configEntries) throws Exception {
		return create(className, t1, convertProperties(configEntries));
	}

	public <T1, T2> Product create(String className, T1 t1, T2 t2,
			Properties configEntries) throws Exception {
		return create(className, t1, t2, convertProperties(configEntries));
	}

	public <T1, T2, T3> Product create(String className, T1 t1,
			T2 t2, T3 t3, Properties configEntries) throws Exception {
		return create(className, t1, t2, t3, convertProperties(configEntries));
	}

	public <T1, T2, T3, T4> Product create(String className,
			T1 t1, T2 t2, T3 t3, T4 t4, Properties configEntries)
			throws Exception {
		return create(className, t1, t2, t3, t4,
				convertProperties(configEntries));
	}

	private Map<String, String> convertProperties(Properties configEntries) {
		Map<String, String> hash = new HashMap<String, String>();
		for (Map.Entry<Object, Object> entry : configEntries.entrySet())
			hash.put((String) entry.getKey(), (String) entry.getValue());
		return hash;
	}
	
	public Product create(String className,
			Map<String, String> config) throws Exception {
		return create(className, new Object[] {}, config);
	}
	
	public <T1> Product create(String className, T1 t1,
			Map<String, String> config) throws Exception {
		return create(className, new Object[] { t1 }, config);
	}

	public <T1, T2> Product create(String className, T1 t1, T2 t2,
			Map<String, String> config) throws Exception {
		return create(className, new Object[] { t1, t2 }, config);
	}

	public <T1, T2, T3> Product create(String className, T1 t1,
			T2 t2, T3 t3, Map<String, String> config) throws Exception {
		return create(className, new Object[] { t1, t2, t3 }, config);
	}

	public <T1, T2, T3, T4> Product create(String className,
			T1 t1, T2 t2, T3 t3, T4 t4, Map<String, String> config)
			throws Exception {
		return create(className, new Object[] { t1, t2, t3, t4 }, config);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized Product create(String className, Object[] args,
			Map<String, String> config) throws Exception {
		// Get reflected class
		Class<? extends Product> clazz = (Class<? extends Product>) Class
				.forName(className);
		if (clazz == null)
			throw new Exception("Cannot find subsystem '" + className + "'.");

		// Get constructor signature
		DynamicConstructor<Product> constructor = callStaticConstructorMethod(clazz);
		
		// Get arguments
		Parameter[] params = constructor.getParams();

		// Check types
		for(int i = 0; i < args.length; i++) {
			if(params[i].getType() != args[i].getClass()) {
				throw new Exception("Constructor arg types '"+i
						+"' don't match for subsystem '"+ className + "'.");
			}
		}
		
		// Build constructor args
		Object[] constructorArgs = new Object[params.length];
		// Copy specified args
		for(int i = 0; i < args.length; i++) {
			constructorArgs[i] = args[i];
		}
		// Copy config args
		for(int i = args.length; i < params.length; i++) {
			Parameter param = params[i];
			String key = param.getName();
			if(!config.containsKey(key)) {
				throw new ValueNotFoundException(
						"Cannot find constructor param '" + param.getName()
								+ "' in config file for subsystem '"
								+ className + "'.");
			}
			String stringArg = config.get(key);
			Object concreteArg = convertType(stringArg, param);
			constructorArgs[i] = concreteArg;
		}

		// Create instance
		Product instance = constructor.newInstance(constructorArgs);
		return instance;
	}
	
	private Object convertType(String value, Parameter param) {
		Object returnObject = null; 
		
		// Get parameter type
		Class<?> type = param.getType();
		// Trim for array
		if(type.isArray()){
			value = value.trim();
		}
		
		// ## Primitive types
		if(type == int.class) {
			returnObject = Integer.valueOf(value);
		} else if(type == String.class) {
			returnObject = value;
		// ## Arrays
		} else if(type == String[].class) {
			returnObject = value.split(value, param.getArrayDelimiter());
		} else if(type == int[].class) {
			String[] stringArray = value.split(value, param.getArrayDelimiter());
			int[] intArray = new int[stringArray.length];
			for(int i = 0; i < intArray.length; i++) {
				intArray[i] = Integer.parseInt(stringArray[i]);
			}
			returnObject = intArray;			
		}
		return returnObject;
	}
	
	@SuppressWarnings("unchecked")
	private DynamicConstructor<Product> callStaticConstructorMethod(Class<? extends Product> clazz)
			throws Exception {
		// Get static method
		Method method = clazz.getMethod(STATIC_CONSTRUCTOR_METHOD);
        // Create concrete config
        return (DynamicConstructor) method.invoke(null, new Object[] {});
	}

}