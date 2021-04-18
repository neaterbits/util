package org.jutils.threads;

public interface ForwardResultToCaller {

	void forward(Runnable runnable);
	
}
