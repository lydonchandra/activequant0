package org.activequant.data.retrieval.filtering;

/**
 * Simple filter that decides whether to accept data instance or not.
 * <br>
 * <b>History:</b><br>
 *  - [04.12.2007] Created (Mike Kroutikov)<br>
 *
 *  @author Mike Kroutikov
 */
public interface IDataFilter<T> {
	
	/**
	 * Implementation should evaluate the incoming data object and
	 * decide whether to accept it or reject. Note that object state should not
	 * be altered.
	 * 
	 * @param data object to evaluate.
	 * @return true if accepted, false if rejected.
	 */
	boolean evaluate(T data);

}
