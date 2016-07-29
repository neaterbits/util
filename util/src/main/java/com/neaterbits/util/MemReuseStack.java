package com.neaterbits.util;

import java.util.ArrayList;

public abstract class MemReuseStack<T> {
	private final ArrayList<T> stack;
	
	protected MemReuseStack() {
		this.stack = new ArrayList<T>();
	}
	
	private int length;

	protected abstract T create();
	
	public final T push() {
		
		final T ret;
		
		final int size = stack.size();
		
		if (length < size) {
			ret = stack.get(length);
		}
		else if (length == size) {
			// Add to stack
			ret = create();
			
			stack.add(ret);
		}
		else {
			// length > size
			throw new IllegalStateException("length > size");
		}

		++ length;
		
		return ret;
	}
	
	public final T pop() {
		if (length == 0) {
			throw new IllegalArgumentException("length == 0");
		}
		-- length;

		return stack.get(length);
	}

	public final T getAt(int idx) {
		return stack.get(idx);
	}
	
	public final T get() {
		return stack.get(length - 1);
	}
	
	public final boolean isEmpty() {
		return length == 0;
	}

	public final int length() {
		return length;
	}
}
