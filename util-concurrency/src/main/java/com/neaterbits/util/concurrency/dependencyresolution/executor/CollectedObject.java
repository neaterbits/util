package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.List;

public interface CollectedObject {

	String getName();
	
	List<String> getCollected();
	
}
