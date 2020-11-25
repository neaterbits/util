package com.neaterbits.util.coll;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

final class ReverseIterable<T> implements Iterable<T> {
	
	private static class ReverseIterator<T> implements Iterator<T> {
		private final ListIterator<T> it;
		private final boolean readOnly;

		ReverseIterator(ListIterator<T> it, boolean readOnly) {
			this.it = it;
			this.readOnly = readOnly;
		}

		public boolean hasNext() {
			return it.hasPrevious();
		}

		public T next() {
			return it.previous();
		}

		public void remove() {
			if (readOnly) {
				throw new UnsupportedOperationException("Read only operator");
			}
			it.remove();
		}
	}

	private final List<T> l;
	private final boolean readonly;

	ReverseIterable(List<T> l, boolean readOnly) {
		this.l = l;
		this.readonly = readOnly;
	}

	@Override
	public Iterator<T> iterator() {
		return new ReverseIterator<T>(l.listIterator(l.size()), readonly);
	}
}