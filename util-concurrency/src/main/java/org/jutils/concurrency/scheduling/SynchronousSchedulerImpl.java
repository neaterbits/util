package org.jutils.concurrency.scheduling;

import java.util.Objects;

import org.jutils.threads.ForwardResultToCaller;

final class SynchronousSchedulerImpl implements Scheduler {

	private final ForwardResultToCaller forwardToCaller;
	
	SynchronousSchedulerImpl(ForwardResultToCaller forwardToCaller) {
		
		Objects.requireNonNull(forwardToCaller);
		
		this.forwardToCaller = forwardToCaller;
	}

	@Override
	public <T, R> void schedule(
			Constraint constraint,
			T parameter,
			ScheduleFunction<T, R> function,
			ScheduleListener<T, R> listener) {

		final R result = function.perform(parameter);

		forwardToCaller.forward(() -> {
			listener.onScheduleResult(parameter, result);
		});
	}
}
