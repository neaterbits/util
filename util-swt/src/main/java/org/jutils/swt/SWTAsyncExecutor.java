package org.jutils.swt;

import org.eclipse.swt.widgets.Display;
import org.jutils.concurrency.scheduling.BaseAsyncExecutor;

public final class SWTAsyncExecutor extends BaseAsyncExecutor<Void> {

	public SWTAsyncExecutor(Display display) {
		super(
				runnable -> display.asyncExec(() -> {
					
					try {
						runnable.run();
					}
					catch (Throwable ex) {
						ex.printStackTrace();
					}
				}),
				true);
	}
}
