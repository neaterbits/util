package com.neaterbits.util.compat;

import com.neaterbits.util.compat.function.CBiFunction;

public interface StackForeachable<T> {
	<S> S forEachStack(CBiFunction<T, S, S> function, S s);
}
