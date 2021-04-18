package org.jutils;

import java.util.List;

public class ListListable<T> extends CollectionEnumerable<T> implements Listable<T> {

	
	public ListListable(List<T> collection) {
		super(collection);
	}

	@Override
	public T getAt(int idx) {
		return ((List<T>)collection).get(idx);
	}
}
