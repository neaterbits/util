package com.neaterbits.util.compat;

import java.util.ListIterator;

import com.neaterbits.util.compat.function.CBiConsumer;

public interface ForeachIteratorable<T> {
	
	void forEachIterator(CBiConsumer<ListIterator<T>, T> consumer);

}
