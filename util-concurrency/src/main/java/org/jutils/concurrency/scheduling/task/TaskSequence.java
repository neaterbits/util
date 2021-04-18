package org.jutils.concurrency.scheduling.task;

import java.util.Collection;

public abstract class TaskSequence<T> extends Task<T>{

	protected abstract Collection<Task<?>> makeTasks(T data);
	
	public TaskSequence(T data) {
		super(data);
	}
}
