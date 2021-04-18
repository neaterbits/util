package org.jutils.coll;

import java.util.ListIterator;
import java.util.function.BiConsumer;

public interface ForeachIteratorable<T> {
	
	void forEachIterator(BiConsumer<ListIterator<T>, T> consumer);

}
