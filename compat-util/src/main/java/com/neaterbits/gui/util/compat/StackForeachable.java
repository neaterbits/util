package com.neaterbits.gui.util.compat;

import com.neaterbits.gui.util.compat.function.CBiFunction;

public interface StackForeachable<T> {
	<S> S forEachStack(CBiFunction<T, S, S> function, S s);
}
