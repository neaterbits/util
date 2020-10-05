package com.neaterbits.util.concurrency.scheduling;

@FunctionalInterface
public interface ScheduleFunction<T, R> {

	R perform(T parameter);
	
}
