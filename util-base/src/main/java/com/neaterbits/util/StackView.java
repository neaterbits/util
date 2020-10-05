package com.neaterbits.util;

public interface StackView<T> {

	T get();
	
	T get(int index);
	
	T getFromTop(int count);
	
	<E extends T> E getFromTop(Class<E> cl);
	
	boolean isEmpty();
	
	int size();
	
}
