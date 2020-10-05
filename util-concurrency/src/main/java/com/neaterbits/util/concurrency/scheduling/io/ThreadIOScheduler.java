package com.neaterbits.util.concurrency.scheduling.io;

import java.util.Objects;

import com.neaterbits.util.concurrency.scheduling.ForwardResultToCaller;
import com.neaterbits.util.concurrency.scheduling.ScheduleFunction;
import com.neaterbits.util.concurrency.scheduling.ScheduleListener;


final class ThreadIOScheduler<T, R> implements IOScheduler<T, R> {

	private final ScheduleFunction<T, R> ioFunction;
	private final ScheduleFunction<T, R> getCached;


	private final ThreadIOSchedulerImpl schedulerImpl;
	
	
	ThreadIOScheduler(
			ScheduleFunction<T, R> ioFunction,
			ScheduleFunction<T, R> getCached,
			ForwardResultToCaller forwardToCaller) {
		
		Objects.requireNonNull(ioFunction);
		Objects.requireNonNull(forwardToCaller);

		this.ioFunction = ioFunction;
		this.getCached = getCached;

		this.schedulerImpl = new ThreadIOSchedulerImpl(forwardToCaller);
	}

	@Override
	public void schedule(T parameter, String debugName, ScheduleListener<T, R> listener) {
		schedulerImpl.schedule(parameter, debugName, ioFunction, getCached, listener);
	}
}
