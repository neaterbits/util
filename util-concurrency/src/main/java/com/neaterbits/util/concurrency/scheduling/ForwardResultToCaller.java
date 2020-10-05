package com.neaterbits.util.concurrency.scheduling;

public interface ForwardResultToCaller {

	void forward(Runnable runnable);
	
}
