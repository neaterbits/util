package org.jutils.concurrency.dependencyresolution.executor;

import java.util.Objects;

public final class CollectedProduct extends Collected {

	private final Class<?> productType;
	private final Object productObject;

	CollectedProduct(Class<?> productType, Object productObject) {

		Objects.requireNonNull(productObject);
		
		if (!productType.equals(productObject.getClass())) {
			throw new IllegalArgumentException();
		}
		
		if (productObject instanceof Collected) {
			throw new IllegalArgumentException();
		}

		this.productType = productType;
		this.productObject = productObject;
	}

	Class<?> getProductType() {
		return productType;
	}

	public Object getProductObject() {
		return productObject;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((productObject == null) ? 0 : productObject.hashCode());
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
		CollectedProduct other = (CollectedProduct) obj;
		if (productObject == null) {
			if (other.productObject != null)
				return false;
		} else if (!productObject.equals(other.productObject))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CollectedProduct [productType=" + productType + ", productObject=" + productObject + "]";
	}
}
