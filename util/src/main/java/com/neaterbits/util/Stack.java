package com.neaterbits.util;

import java.util.ArrayList;

public class Stack<T> {

	private final ArrayList<T> stack;
	
	
	public Stack() {
		this.stack = new ArrayList<T>();
	}
	
	public Stack(int size) {
		this.stack = new ArrayList<T>(size);
	}

	public final void push(T item) {
		if (item == null) {
			throw new IllegalArgumentException("item == null");
		}

		stack.add(item);
	}
	
	public final T pop() {
		final T ret = stack.remove(stack.size() - 1);
				
		return ret;
	}
	
	public final int size() {
		return stack.size();
	}

	public final T top() {
		return stack.get(stack.size() - 1);
	}

	public final T get(int idx) {
		return stack.get(idx);
	}
}
