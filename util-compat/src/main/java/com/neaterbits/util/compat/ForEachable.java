package com.neaterbits.util.compat;

import com.neaterbits.util.compat.function.CConsumer;

public interface ForEachable<T> {

	void forEach(CConsumer<T> coll);
	
}
