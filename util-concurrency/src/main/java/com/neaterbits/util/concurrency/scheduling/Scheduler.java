package com.neaterbits.util.concurrency.scheduling;

public interface Scheduler {

	<T, R> void schedule(Constraint constraint, T parameter, ScheduleFunction<T, R> function, ScheduleListener<T, R> listener);

}
