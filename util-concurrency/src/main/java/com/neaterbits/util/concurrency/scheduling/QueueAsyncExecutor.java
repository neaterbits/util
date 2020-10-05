package com.neaterbits.util.concurrency.scheduling;

public final class QueueAsyncExecutor extends BaseAsyncExecutor<ThreadsafeQueue<Runnable>> {
	
	private int scheduledJobs;
	
	public QueueAsyncExecutor(boolean scheduleAsynchronously) {
		super(new ThreadsafeQueue<Runnable>(), queue -> runnable -> queue.add(runnable), scheduleAsynchronously);
	}

	// Invoke result runnables on calling thread
	// so that caller can be threadsafe without synchronization (similar to UI thread)
	
	@Override
	public <T, R> void schedule(Constraint constraint, T parameter, ScheduleFunction<T, R> function,
			ScheduleListener<T, R> listener) {

		super.schedule(constraint, parameter, function, listener);
		
		++ scheduledJobs;
	}

	@Override
	public int getNumScheduledJobs() {
		return scheduledJobs;
	}

	@Override
	public void runQueuedResultRunnables() {

		Runnable runnable;

		while (scheduledJobs > 0) {

			while (null != (runnable = queue.take())) {
				
				-- scheduledJobs;
				
				runnable.run();
				
				if (scheduledJobs == 0) {
					
					if (!queue.isEmpty()) {
						throw new IllegalStateException();
					}
					break;
				}
			}
		}
	}
}
