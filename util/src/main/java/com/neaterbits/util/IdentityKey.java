package com.neaterbits.util;

public class IdentityKey<T> {

	private final T obj;

	public IdentityKey(T obj) {
		
		if (obj == null) {
			throw new IllegalArgumentException("obj == null");
		}
		
		this.obj = obj;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(obj);
	}

	@Override
	public boolean equals(Object obj) {
		return this.obj == obj;
	}
}
