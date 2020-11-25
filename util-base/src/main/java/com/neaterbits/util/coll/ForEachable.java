package com.neaterbits.util.coll;

import java.util.function.Consumer;

public interface ForEachable<T> {

	void forEach(Consumer<T> coll);
	
}
