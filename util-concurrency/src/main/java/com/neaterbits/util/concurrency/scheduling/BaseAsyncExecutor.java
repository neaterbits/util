package com.neaterbits.util.concurrency.scheduling;

import java.util.function.Function;

import com.neaterbits.util.threads.ForwardResultToCaller;

public abstract class BaseAsyncExecutor<QUEUE> implements AsyncExecutor {

	private int scheduledJobs;
	private final Scheduler scheduler;
	final QUEUE queue;

	BaseAsyncExecutor(QUEUE queue, Function<QUEUE, ForwardResultToCaller> forwardResultToCallerFactory, boolean scheduleAsynchronously) {
		this(forwardResultToCallerFactory.apply(queue), scheduleAsynchronously, queue);
	}

	protected BaseAsyncExecutor(ForwardResultToCaller forwardResultToCaller, boolean scheduleAsynchronously) {
		this(forwardResultToCaller, scheduleAsynchronously, null);
	}

	private BaseAsyncExecutor(ForwardResultToCaller forwardResultToCaller, boolean scheduleAsynchronously, QUEUE queue) {
		
		final SchedulerFactory schedulerFactory =
				scheduleAsynchronously
					? new AsynchronousSchedulerFactory(forwardResultToCaller)
					: new SynchronousSchedulerFactory(forwardResultToCaller);

		this.scheduler = schedulerFactory.createScheduler();
		this.queue = queue;
	}
	
	QUEUE getQueue() {
		return queue;
	}

	@Override
	public <T, R> void schedule(Constraint constraint, T parameter, ScheduleFunction<T, R> function,
			ScheduleListener<T, R> listener) {

		final long threadNo = Thread.currentThread().getId();
		
		scheduler.schedule(
				constraint,
				parameter,
				param -> {
					try {
						return function.perform(param);
					}
					catch (Throwable ex) {
						System.out.println("Exception in scheduled function");
						ex.printStackTrace();
						throw ex;
					}
				},
				(param, result) -> {
			
			if (threadNo != Thread.currentThread().getId()) {
				throw new IllegalStateException();
			}
			
			-- scheduledJobs;
			
			listener.onScheduleResult(parameter, result);
		});
		
		++ scheduledJobs;
	}

	public int getNumScheduledJobs() {
		return scheduledJobs;
	}

	public void runQueuedResultRunnables() {
		
	}
}
