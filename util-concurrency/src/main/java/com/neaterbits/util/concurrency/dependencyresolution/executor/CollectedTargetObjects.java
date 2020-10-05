package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class CollectedTargetObjects {

	private final Set<CollectedTargetObject> collected;

	CollectedTargetObjects(Set<CollectedTargetObject> collected) {
	
		Objects.requireNonNull(collected);
		
		this.collected = collected;
	}

	public CollectedTargetObjects mergeWith(CollectedTargetObjects other) {
		
		Objects.requireNonNull(other);
		
		if (this == other) {
			throw new IllegalArgumentException();
		}
		
		final Set<CollectedTargetObject> set = new HashSet<>(collected.size() + other.collected.size());
		
		set.addAll(collected);
		set.addAll(other.collected);
		
		return new CollectedTargetObjects(set);
	}
	
	public List<Object> getCollectedObjects() {
		return collected.stream()
				.map(CollectedTargetObject::getSubTargetObject)
				.collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return collected.toString();
	}
}
