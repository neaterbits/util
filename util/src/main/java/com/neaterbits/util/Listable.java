package com.neaterbits.util;

public interface Listable<T> extends Enumerable<T> {

	T getAt(int idx);
	
}
