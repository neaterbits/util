package org.jutils.concurrency.scheduling.cpu;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface CPUScheduler {

	<T, R> void schedule(T parameter, Function<T, R> callable, BiConsumer<T, R> completion);
	
}
