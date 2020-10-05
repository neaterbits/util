package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.Objects;

final class CollectedTargetObject extends Collected {

	private final Object subTargetObject;

	CollectedTargetObject(Object subTargetObject) {

		Objects.requireNonNull(subTargetObject);

		if (subTargetObject instanceof Collected) {
			throw new IllegalArgumentException();
		}

		this.subTargetObject = subTargetObject;
	}

	Object getSubTargetObject() {
		return subTargetObject;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subTargetObject == null) ? 0 : subTargetObject.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CollectedTargetObject other = (CollectedTargetObject) obj;
		if (subTargetObject == null) {
			if (other.subTargetObject != null)
				return false;
		} else if (!subTargetObject.equals(other.subTargetObject))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return subTargetObject.toString();
	}
}
