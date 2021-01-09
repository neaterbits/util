package com.neaterbits.util.threads;

public interface ForwardResultToCaller {

	void forward(Runnable runnable);
	
}
