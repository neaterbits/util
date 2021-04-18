package org.jutils.concurrency.scheduling;

@FunctionalInterface
public interface ScheduleFunction<T, R> {

	R perform(T parameter);
	
}
