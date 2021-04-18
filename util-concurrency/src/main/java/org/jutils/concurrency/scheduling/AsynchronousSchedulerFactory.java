package org.jutils.concurrency.scheduling;

import org.jutils.concurrency.scheduling.cpu.CPUScheduler;
import org.jutils.concurrency.scheduling.cpu.ThreadCPUScheduler;
import org.jutils.concurrency.scheduling.io.ThreadIOSchedulerImpl;
import org.jutils.threads.ForwardResultToCaller;

public class AsynchronousSchedulerFactory implements SchedulerFactory {

	private final ThreadIOSchedulerImpl ioScheduler;
	private final CPUScheduler cpuScheduler;
	
	public AsynchronousSchedulerFactory(ForwardResultToCaller forwardToCaller) {
		this.ioScheduler = new ThreadIOSchedulerImpl(forwardToCaller);
		this.cpuScheduler = new ThreadCPUScheduler(forwardToCaller);
	}
	
	@Override
	public Scheduler createScheduler() {
		return new AsynchronousSchedulerImpl(ioScheduler, cpuScheduler);
	}
}
