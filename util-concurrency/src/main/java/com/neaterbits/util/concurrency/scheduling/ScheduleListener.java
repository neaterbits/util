package com.neaterbits.util.concurrency.scheduling;

@FunctionalInterface
public interface ScheduleListener<T, R> {

	void onScheduleResult(T parameter, R result);
	
}
