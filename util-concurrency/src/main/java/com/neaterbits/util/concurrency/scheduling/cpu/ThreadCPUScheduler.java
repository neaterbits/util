package com.neaterbits.util.concurrency.scheduling.cpu;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.neaterbits.util.concurrency.scheduling.ForwardResultToCaller;

public final class ThreadCPUScheduler implements CPUScheduler {

	private final ForwardResultToCaller forwardToCaller;
	
	private final ThreadPoolExecutor executor;
	
	public ThreadCPUScheduler(ForwardResultToCaller forwardToCaller) {
		this.forwardToCaller = forwardToCaller;
		this.executor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
	}
	
	@Override
	public <T, R> void schedule(T parameter, Function<T, R> callable, BiConsumer<T, R> completion) {

		executor.execute(() -> {
			R result;
			try {
				result = callable.apply(parameter);

				forwardToCaller.forward(() -> completion.accept(parameter, result));
			} catch (Exception ex) {
				System.err.println("Caught exception in scheduler " + ex);
				ex.printStackTrace();
			}
		});
		
	}
}
