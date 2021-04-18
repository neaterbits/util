package org.jutils.concurrency.dependencyresolution.executor;

import java.util.List;

public interface CollectedObject {

	String getName();
	
	List<String> getCollected();
	
}
