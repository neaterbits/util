package com.neaterbits.util.compat;

import com.neaterbits.util.compat.function.CFunction;
import com.neaterbits.util.compat.function.CPredicate;

class FilterStream<T> extends ChainedStream<T, T> {
	
	private final CPredicate<T> predicate;

	public FilterStream(BaseStream<T> input, CPredicate<T> predicate) {
		super(input);
		this.predicate = predicate;
	}

	@Override
	public <Q> Q forEach(final CFunction<T, Q> function) {
		final CFunction<T, Q> predicateFunction = new CFunction<T, Q>() {
			@Override
			public Q apply(T s) {
				final Q ret;
				if (predicate.test(s)) {
					ret = function.apply(s);
				}
				else {
					ret = null;
				}
				
				return ret;
			}
		};

		return input.forEach(predicateFunction);
	}
}
