package com.neaterbits.util;

import java.util.function.Function;

public interface StackView<T> {

	T get();
	
	T get(int index);
	
	T getFromTop(int count);
	
	<E extends T> E getFromTop(Class<E> cl);
	
	boolean isEmpty();
	
	int size();

	String toString(Function<T, String> entryToString);
}
