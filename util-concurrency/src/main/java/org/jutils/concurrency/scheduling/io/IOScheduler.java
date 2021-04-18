package org.jutils.concurrency.scheduling.io;

import org.jutils.concurrency.scheduling.ScheduleListener;

public interface IOScheduler<T, R> {

	void schedule(T parameter, String debugName, ScheduleListener<T, R> listener);
	
}
