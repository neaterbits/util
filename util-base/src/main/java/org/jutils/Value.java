package org.jutils;

public class Value<T> {

	private T v;
	
	public Value() {
		
	}

	public Value(T v) {
		this.v = v;
	}

	public T get() {
		return v;
	}
	
	public void set(T v) {
		this.v = v;
	}
	
	public void clear() {
		this.v = null;
	}
}
