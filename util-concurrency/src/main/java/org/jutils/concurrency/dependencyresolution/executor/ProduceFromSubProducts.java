package org.jutils.concurrency.dependencyresolution.executor;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public final class ProduceFromSubProducts<TARGET> {

	private final Class<?> productClass;
	private final BiFunction<TARGET, List<?>, ?> collect;

	public ProduceFromSubProducts(Class<?> productClass, BiFunction<TARGET, List<?>, ?> collect) {

		Objects.requireNonNull(productClass);
		Objects.requireNonNull(collect);
		
		this.productClass = productClass;
		this.collect = collect;
	}

	CollectedProduct collect(Object targetObject, CollectedProducts subProducts) {

		Objects.requireNonNull(targetObject);
		Objects.requireNonNull(subProducts);
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		final BiFunction<Object, List<Object>, Object> function = (BiFunction)collect;
		
		final Object productObject =  function.apply(targetObject, subProducts.getCollectedObjects());
		
		if (productObject == null) {
			throw new IllegalStateException();
		}
		
		if (!productObject.getClass().equals(productClass)) {
			throw new IllegalStateException();
		}
		
		return new CollectedProduct(productClass, productObject);
	}
}
