package com.neaterbits.util.concurrency.scheduling.io;

import com.neaterbits.util.concurrency.scheduling.ScheduleListener;

public interface IOScheduler<T, R> {

	void schedule(T parameter, String debugName, ScheduleListener<T, R> listener);
	
}
