package org.jutils.concurrency.scheduling;

public interface AsyncExecutor extends Scheduler {

	int getNumScheduledJobs();

	void runQueuedResultRunnables();

}
