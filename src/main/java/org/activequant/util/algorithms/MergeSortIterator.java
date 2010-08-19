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
package org.activequant.util.algorithms;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Sorting utility: takes input as a set of Iterators (they are supposed to
 * output data in the ascending order, according to the comparator).
 * Merges these data streams together into one Iterator that also outputs the
 * data in the ascending order.
 * 
 *       <b>History:</b><br>
 * - [09.11.2007] Created (Mike Kroutikov)<br>
 * 
 * @author Mike Kroutikov
 */
public class MergeSortIterator<T> implements Iterator<T> {
	
	private final Comparator<T> comparator;
	private final TreeSet<Stream> sortBucket = new TreeSet<Stream>();
	private long  serialNumber = 0L;
	
	public MergeSortIterator(Comparator<T> comparator) {
		this.comparator = comparator;
	}

	private class Stream implements Comparable<Stream> {
		private final Iterator<T> iterator; // where from take the events
		private T top; // top element
		private long serialNo; // for sort stability
		
		Stream(Iterator<T> i, long s) {
			iterator = i;
			serialNo = s;
		}
		
		public int compareTo(Stream other) {
			int rc = comparator.compare(this.top, other.top);
			if(rc != 0) return rc;
			if(this.serialNo > other.serialNo) {
				return 1;
			} else if(this.serialNo < other.serialNo) {
				return -1;
			}
			return 0;
		}
	}

	public void addIterator(Iterator<T> iterator) {
		if(iterator.hasNext()) {
			Stream stream = new Stream(iterator, serialNumber++);
			stream.top = stream.iterator.next();
			sortBucket.add(stream);
		}
	}

	public boolean hasNext() {
		return !sortBucket.isEmpty();
	}

	public T next() {
		if(!hasNext()) return null;
			
		Stream stream = sortBucket.first();
		sortBucket.remove(stream);
		T top = stream.top;
		if(stream.iterator.hasNext()) {
			stream.top = stream.iterator.next();
			stream.serialNo = serialNumber++;
			// check sanity: input stream must be correctly ordered
			if(comparator.compare(stream.top, top) < 0) {
				throw new IllegalStateException("input iterator is out of order: previous entity: " + top + ", next entity: " + stream.top);
			}
			sortBucket.add(stream);
		}
			
		return top;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}
