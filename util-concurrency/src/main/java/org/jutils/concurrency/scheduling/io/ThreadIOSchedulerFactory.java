package org.jutils.concurrency.scheduling.io;

import org.jutils.concurrency.scheduling.ScheduleFunction;
import org.jutils.threads.ForwardResultToCaller;

public class ThreadIOSchedulerFactory implements IOSchedulerFactory {

	@Override
	public <T, R> IOScheduler<T, R> makeScheduler(
			ScheduleFunction<T, R> ioFunction,
			ScheduleFunction<T, R> getCached,
			ForwardResultToCaller forwardToCaller) {

		return new ThreadIOScheduler<>(ioFunction, getCached, forwardToCaller);
	}
}
