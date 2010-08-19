package org.activequant.data.util;

/**
 * 
 * Container for a data triple<br>
 * <br>
 * <b>History:</b><br>
 *  - [27.11.2007] Created (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 */
public class Triple<T,U,W> {

	private T object1; 
	private U object2; 
	private W object3; 
	
	public Triple(){
	}
	
	public Triple(T o1, U o2, W o3){
		this.object1 = o1;
		this.object2 = o2; 
		this.object3 = o3;
	}

	public T getObject1() {
		return object1;
	}

	public void setObject1(T object1) {
		this.object1 = object1;
	}

	public U getObject2() {
		return object2;
	}

	public void setObject2(U object2) {
		this.object2 = object2;
	}

	public W getObject3() {
		return object3;
	}

	public void setObject3(W object3) {
		this.object3 = object3;
	}
	
}
