package com.neaterbits.structuredlog.binary.logging;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

class CollectionWrapper<S, C extends Collection<S>> implements Collection<S> {

	final C delegate;
	
	CollectionWrapper(C delegate) {
	
		Objects.requireNonNull(delegate);
		
		this.delegate = delegate;
	}

	@Override
	public final boolean add(S arg0) {
		return delegate.add(arg0);
	}

	@Override
	public final boolean addAll(Collection<? extends S> arg0) {
		return delegate.addAll(arg0);
	}

	@Override
	public final void clear() {
		delegate.clear();
	}

	@Override
	public final boolean contains(Object arg0) {
		return delegate.contains(arg0);
	}

	@Override
	public final boolean containsAll(Collection<?> arg0) {
		return delegate.containsAll(arg0);
	}

	@Override
	public boolean equals(Object arg0) {
		return delegate.equals(arg0);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	@Override
	public final boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public final Iterator<S> iterator() {
		return delegate.iterator();
	}

	@Override
	public final boolean remove(Object arg0) {
		return delegate.remove(arg0);
	}

	@Override
	public final boolean removeAll(Collection<?> arg0) {
		return delegate.removeAll(arg0);
	}

	@Override
	public final boolean retainAll(Collection<?> arg0) {
		return delegate.retainAll(arg0);
	}

	@Override
	public final int size() {
		return delegate.size();
	}

	@Override
	public final Object[] toArray() {
		return delegate.toArray();
	}

	@Override
	public final <T> T[] toArray(T[] arg0) {
		return delegate.toArray(arg0);
	}

	@Override
	public String toString() {
		return delegate.toString();
	}
}
