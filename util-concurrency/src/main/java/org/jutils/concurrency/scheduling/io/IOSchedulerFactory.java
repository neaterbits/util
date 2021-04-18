package org.jutils.concurrency.scheduling.io;

import org.jutils.concurrency.scheduling.ScheduleFunction;
import org.jutils.threads.ForwardResultToCaller;

public interface IOSchedulerFactory {

	<T, R> IOScheduler<T, R> makeScheduler(
			ScheduleFunction<T, R> ioFunction,
			ScheduleFunction<T, R> getCached,
			ForwardResultToCaller forwardToCaller);
	
}
