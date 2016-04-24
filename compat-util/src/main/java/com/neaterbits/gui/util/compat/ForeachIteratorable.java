package com.neaterbits.gui.util.compat;

import java.util.ListIterator;

import com.neaterbits.gui.util.compat.function.CBiConsumer;

public interface ForeachIteratorable<T> {
	
	void forEachIterator(CBiConsumer<ListIterator<T>, T> consumer);

}
