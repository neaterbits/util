package org.jutils.concurrency.scheduling;

import java.util.Objects;

import org.jutils.concurrency.scheduling.cpu.CPUScheduler;
import org.jutils.concurrency.scheduling.io.ThreadIOSchedulerImpl;

final class AsynchronousSchedulerImpl implements Scheduler {

	private final ThreadIOSchedulerImpl ioScheduler;
	private final CPUScheduler cpuScheduler;
	
	AsynchronousSchedulerImpl(ThreadIOSchedulerImpl ioScheduler, CPUScheduler cpuScheduler) {
		this.ioScheduler = ioScheduler;
		this.cpuScheduler = cpuScheduler;
	}

	@Override
	public <T, R> void schedule(Constraint constraint, T parameter, ScheduleFunction<T, R> function, ScheduleListener<T, R> listener) {

		Objects.requireNonNull(constraint);
		Objects.requireNonNull(function);
		Objects.requireNonNull(listener);

		switch (constraint) {
		case IO:
			ioScheduler.schedule(parameter, null, function, null, listener);
			break;
			
		case CPU:
			cpuScheduler.schedule(parameter, param -> function.perform(param), (param, result) -> listener.onScheduleResult(param, result));
			break;
			
		default:
			throw new UnsupportedOperationException();
		}
	}
}
