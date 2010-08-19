package org.activequant.util.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Generic close-open interval set. The set is always normalized, meaning that
 * there are no intersecting or mergeable intervals there.
 * <br>
 * <b>History:</b><br>
 *  - [05.11.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class IntervalSet<T extends Comparable<T>> implements Iterable<Interval<T>> {
	
	private final List<Interval<T>> list = new ArrayList<Interval<T>>();

	private final Comparator<Interval<T>> headComparator = new Comparator<Interval<T>>() {
		public int compare(Interval<T> o1, Interval<T> o2) {
			return o1.to.compareTo(o2.from);
		}
	};

	/**
	 * Merges interval into the current set. This is analogous to <code>add()</code>
	 * operation, but takes care of necessary interval merging that guarantees that
	 * the result is normalized.
	 *  
	 * @param arg interval to merge in.
	 */
	public void mergeIn(Interval<T> arg) {
		
		int index = Collections.binarySearch(list, arg, headComparator);
		if(index < 0) {
			index = - (index + 1);
		}
		
		while(index < list.size()) {
			Interval<T> curr = list.get(index);
			if(arg.isMergeableWith(curr)) {
				arg = arg.mergeWith(list.remove(index));
			} else {
				break;
			}
		}
		list.add(index, arg);
	}
	
	/**
	 * Intersects this set with the given interval and returns the result of
	 * intersection. Current set is not changed.
	 * 
	 * @param arg interval to intersect with.
	 * @return intersection set (may be empty).
	 */
	public IntervalSet<T> intersect(Interval<T> arg) {

		int index = Collections.binarySearch(list, arg, headComparator);
		if(index < 0) {
			index = - (index + 1);
		}
		
		IntervalSet<T> out = new IntervalSet<T>();
		while(index < list.size()) {
			Interval<T> curr = list.get(index);
			if(arg.isIntersectableWith(curr)) {
				out.list.add(arg.intersectWith(curr));
				index++;
			} else {
				break;
			}
		}
		
		return out;
	}

	public Iterator<Interval<T>> iterator() {
		return list.iterator();
	}
	
	public String toString() {
		return list.toString();
	}
}
