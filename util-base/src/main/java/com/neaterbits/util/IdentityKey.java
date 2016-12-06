package com.neaterbits.util;

public final class IdentityKey<T> {

	private final T obj;

	public IdentityKey(T obj) {
		
		if (obj == null) {
			throw new IllegalArgumentException("obj == null");
		}
		
		this.obj = obj;
	}
	
	public final T get() {
		return obj;
	}

	@Override
	public final int hashCode() {
		return System.identityHashCode(obj);
	}

	@Override
	public final boolean equals(Object obj) {
		return this.obj == obj;
	}
}
