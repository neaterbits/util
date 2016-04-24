package com.neaterbits.gui.util.compat;

import com.neaterbits.gui.util.compat.function.CConsumer;

public interface ForEachable<T> {

	void forEach(CConsumer<T> coll);
	
}
