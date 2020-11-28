package com.neaterbits.util;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Stream;

public class ArrayStack<T> implements Stack<T>, StackView<T> {

	private final ArrayList<T> list;

	public ArrayStack() {
		this.list = new ArrayList<>();
	}

	@Override
    public final void push(T element) {
		if (element == null) {
			throw new IllegalArgumentException("element == null");
		}

		list.add(element);
	}

	@Override
    public final T pop() {
		return list.remove(list.size() - 1);
	}

	@Override
	public final T get() {
		return list.get(list.size() - 1);
	}

	@Override
	public final T get(int index) {
		return list.get(index);
	}

	public final Stream<T> stream() {
		return list.stream();
	}

	@Override
	public final T getFromTop(int count) {
		return list.get(list.size() - count - 1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <E extends T> E getFromTop(Class<E> cl) {
		for (int i = list.size() - 1; i >= 0; -- i) {
			final T element = list.get(i);

			if (element.getClass().equals(cl)) {
				return (E)element;
			}
		}

		return null;
	}

	@Override
	public final boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public final int size() {
		return list.size();
	}

	@Override
	public String toString() {
		return list.toString();
	}

    @Override
    public String toString(Function<T, String> entryToString) {

        final StringBuilder sb = new StringBuilder('[');
        
        for (int i = 0; i < list.size(); ++ i) {
            if (i > 0) {
                sb.append(',');
            }
            
            sb.append(entryToString.apply(list.get(i)));
        }
        
        sb.append(']');
        
        return sb.toString();
    }
}
