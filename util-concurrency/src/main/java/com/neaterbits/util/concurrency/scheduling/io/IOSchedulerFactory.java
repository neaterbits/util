package com.neaterbits.util.concurrency.scheduling.io;

import com.neaterbits.util.concurrency.scheduling.ForwardResultToCaller;
import com.neaterbits.util.concurrency.scheduling.ScheduleFunction;

public interface IOSchedulerFactory {

	<T, R> IOScheduler<T, R> makeScheduler(
			ScheduleFunction<T, R> ioFunction,
			ScheduleFunction<T, R> getCached,
			ForwardResultToCaller forwardToCaller);
	
}
