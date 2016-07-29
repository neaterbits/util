package com.neaterbits.util.compat;

import com.neaterbits.util.compat.function.CFunction;

final class MapStream<T, R> extends ChainedStream<T, R> {

	private final CFunction<T, R> mapFunction;

	MapStream(BaseStream<T> input, CFunction<T, R> function) {
		super(input);
		
		this.mapFunction = function;
	}

	@Override
	public <Q> Q forEach(final CFunction<R, Q> function) {
		final CFunction<T, Q> mapConsumer = new CFunction<T, Q>() {
			@Override
			public Q apply(T s) {
				return function.apply(mapFunction.apply(s));
			}
		};
		
		return input.forEach(mapConsumer);
	}
}
