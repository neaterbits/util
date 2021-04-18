package org.jutils.coll;

import java.util.function.Consumer;

public interface ForEachable<T> {

	void forEach(Consumer<T> coll);
	
}
