package com.neaterbits.util.concurrency.scheduling;

public interface AsyncExecutor extends Scheduler {

	int getNumScheduledJobs();

	void runQueuedResultRunnables();

}
