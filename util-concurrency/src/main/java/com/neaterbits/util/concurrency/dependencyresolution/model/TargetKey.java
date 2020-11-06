package com.neaterbits.util.concurrency.dependencyresolution.model;

import java.util.Objects;

public class TargetKey<TARGET> {

	private final Class<TARGET> type;
	private final TARGET targetObject;

	public TargetKey(Class<TARGET> type, TARGET targetObject) {
		
		if (targetObject != null) {
			Objects.requireNonNull(type);
			
			if (!type.isAssignableFrom(targetObject.getClass())) {
				throw new IllegalArgumentException("Type mismatch " + type + "/" + targetObject.getClass());
			}
		}
		
		this.type = type;
		this.targetObject = targetObject;
	}

	public final Class<TARGET> getType() {
		return type;
	}
	
	public final TARGET getTargetObject() {
		return targetObject;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [type=" + (type != null ? type.getSimpleName() : null) + ", targetObject=" + targetObject + "]";
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((targetObject == null) ? 0 : targetObject.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final TargetKey<?> other = (TargetKey<?>) obj;
		if (targetObject == null) {
			if (other.targetObject != null)
				return false;
		} else if (!targetObject.equals(other.targetObject))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
