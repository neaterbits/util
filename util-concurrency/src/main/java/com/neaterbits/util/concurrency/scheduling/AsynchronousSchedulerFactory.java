package com.neaterbits.util.concurrency.scheduling;

import com.neaterbits.util.concurrency.scheduling.cpu.CPUScheduler;
import com.neaterbits.util.concurrency.scheduling.cpu.ThreadCPUScheduler;
import com.neaterbits.util.concurrency.scheduling.io.ThreadIOSchedulerImpl;

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
