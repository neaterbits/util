package com.neaterbits.util.concurrency.scheduling.io;

import com.neaterbits.util.concurrency.scheduling.ForwardResultToCaller;
import com.neaterbits.util.concurrency.scheduling.ScheduleFunction;

public class ThreadIOSchedulerFactory implements IOSchedulerFactory {

	@Override
	public <T, R> IOScheduler<T, R> makeScheduler(
			ScheduleFunction<T, R> ioFunction,
			ScheduleFunction<T, R> getCached,
			ForwardResultToCaller forwardToCaller) {

		return new ThreadIOScheduler<>(ioFunction, getCached, forwardToCaller);
	}
}
