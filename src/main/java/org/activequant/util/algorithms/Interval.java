package org.activequant.util.algorithms;

/**
 * Generic close-open interval. Left boundary (<code>from</code>) is closed (i.e. inclusive),
 * right boundary (<code>to</code>) is opened (exclusive).
 * <br>
 * <b>History:</b><br>
 *  - [05.11.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public class Interval<T extends Comparable<T>> implements Comparable<Interval<T>> {
	
	public final T from;
	public final T to;
	
	public Interval(T f, T t) {
		if(f.compareTo(t) >= 0) {
			throw new IllegalArgumentException("bad interval: high bound must be greater than low bound: " + f + ", " + t);
		}
		from = f;
		to = t;
	}

	public int compareTo(Interval<T> o) {
		// lower bound determines ordering
		return from.compareTo(o.from);
	}
	
	public boolean isIntersectableWith(Interval<T> other) {
		return this.from.compareTo(other.to) < 0 && this.to.compareTo(other.from) > 0;
	}

	public Interval<T> intersectWith(Interval<T> other) {
		if(!isIntersectableWith(other)) {
			return null;
		}
		
		T from = this.from.compareTo(other.from) < 0 ? other.from : this.from;
		T to   = this.to.compareTo(other.to) < 0 ? this.to : other.to;

		return new Interval<T>(from, to);
	}

	public boolean isMergeableWith(Interval<T> other) {
		return this.from.compareTo(other.to) <= 0 && this.to.compareTo(other.from) >= 0;
	}

	public Interval<T> mergeWith(Interval<T> other) {
		if(!isMergeableWith(other)) {
			throw new IllegalArgumentException("intervals not mergeable");
		}
		
		T from = this.from.compareTo(other.from) < 0 ? this.from : other.from;
		T to   = this.to.compareTo(other.to) < 0 ? other.to : this.to;

		return new Interval<T>(from, to);
	}
	
	public String toString() {
		return "[" + from + " .. " + to + ")";
	}
}
