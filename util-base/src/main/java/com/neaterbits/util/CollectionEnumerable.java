package com.neaterbits.util;

import java.util.Collection;
import java.util.Iterator;

public class CollectionEnumerable<T> implements Enumerable<T> {

	final Collection<T> collection;

	public CollectionEnumerable(Collection<T> collection) {
		
		if (collection == null) {
			throw new IllegalArgumentException("collection == null");
		}

		this.collection = collection;
	}

	@Override
	public final Iterator<T> iterator() {
		return collection.iterator();
	}

	@Override
	public final boolean isEmpty() {
		return collection.isEmpty();
	}

	@Override
	public final int size() {
		return collection.size();
	}
}
