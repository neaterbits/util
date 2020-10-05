package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

public interface NoTargetTargetBuildSpecPrerequisitesBuilder {

	default void withNamedPrerequisite(String ... prerequisite) {
		withNamedPrerequisites(prerequisite);
	}

	void withNamedPrerequisites(String ... prerequisites);
	
}
