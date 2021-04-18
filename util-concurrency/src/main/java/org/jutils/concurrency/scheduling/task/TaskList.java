package org.jutils.concurrency.scheduling.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class TaskList<T, V, R> extends TaskSequence<T> {

	@FunctionalInterface
	public interface ProcessResult<DATA, VALUE, RESULT> {
		void onResult(DATA data, VALUE value, RESULT result);
	}
	
	private final List<Task<?>> tasks;

	public TaskList(T data, List<Task<?>> tasks) {
		super(data);
		
		Objects.requireNonNull(tasks);
		
		this.tasks = tasks;
	}
	
	public TaskList(
			T data,
			Function<T, Collection<V>> toCollection,
			BiFunction<T, V, R> performTask,
			ProcessResult<T, V, R> processResult) {
		
		super(data);
		
		final Collection<V> values = toCollection.apply(data);
		
		this.tasks = new ArrayList<>(values.size());
		
		for (V value : values) {
			tasks.add(new ResultTask<V, R>(value) {
				@Override
				R perform(V v) {
					return performTask.apply(data, v);
				}
			});
		}
	}

	@Override
	protected final Collection<Task<?>> makeTasks(T data) {
		return tasks;
	}
}
