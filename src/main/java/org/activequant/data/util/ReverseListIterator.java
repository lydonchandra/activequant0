package org.activequant.data.util;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Iterator that is built on top of a List and iterates in reverse list order.
 * Iterator does not implement {@link #remove()} method.
 * <br>
 * <b>History:</b><br>
 *  - [03.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class ReverseListIterator<T> implements Iterator<T> {

	private final Logger log = Logger.getLogger(getClass());
	private final List<T> series;
	private int index;
	
	
	public ReverseListIterator(List<T> s) {
		series = s;
		index = series.size();
		log.info("reverse iterator initialized with series size=" + index);
	}

	public boolean hasNext() {
		return index > 0;
	}

	public T next() {
		if(hasNext()) {
			return series.get(--index);
		}
		return null;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}