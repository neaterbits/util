package org.jutils.concurrency.scheduling.task;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class MapReduceTaskList<T, V, R, S> extends TaskList<T, V, R> {

	public MapReduceTaskList(
			T data,
			S list,
			Function<T, Collection<V>> toCollection,
			BiFunction<T, V, R> performTask,
			BiConsumer<S, R> accumulateResult) {
		
		super(data, toCollection, performTask, (t, v, r) -> accumulateResult.accept(list, r));
	}
}
