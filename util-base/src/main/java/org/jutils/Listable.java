package org.jutils;

public interface Listable<T> extends Enumerable<T> {

	T getAt(int idx);
	
}
