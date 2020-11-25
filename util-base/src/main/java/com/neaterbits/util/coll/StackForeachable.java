package com.neaterbits.util.coll;

import java.util.function.BiFunction;

public interface StackForeachable<T> {
	<S> S forEachStack(BiFunction<T, S, S> function, S s);
}
