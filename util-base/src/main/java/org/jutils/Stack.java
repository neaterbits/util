package org.jutils;

public interface Stack<T> extends StackView<T> {

	void push(T element);
	
	T pop();
	
}
