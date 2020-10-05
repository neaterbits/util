package com.neaterbits.structuredlog.binary.logging;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

final class ListWrapper<S> extends CollectionWrapper<S, List<S>> implements List<S> {

	ListWrapper(List<S> delegate) {
		super(delegate);
	}

	@Override
	public void add(int arg0, S arg1) {
		delegate.add(arg0, arg1);
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends S> arg1) {
		return delegate.addAll(arg0, arg1);
	}

	@Override
	public S get(int arg0) {
		return delegate.get(arg0);
	}

	@Override
	public int indexOf(Object arg0) {
		return delegate.indexOf(arg0);
	}

	@Override
	public int lastIndexOf(Object arg0) {
		return delegate.lastIndexOf(arg0);
	}

	@Override
	public ListIterator<S> listIterator() {
		return delegate.listIterator();
	}

	@Override
	public ListIterator<S> listIterator(int arg0) {
		return delegate.listIterator(arg0);
	}

	@Override
	public S remove(int arg0) {
		return delegate.remove(arg0);
	}

	@Override
	public S set(int arg0, S arg1) {
		return delegate.set(arg0, arg1);
	}

	@Override
	public List<S> subList(int arg0, int arg1) {
		return delegate.subList(arg0, arg1);
	}

	@Override
	public String toString() {
		return delegate.toString();
	}
}
