package com.neaterbits.gui.util.compat;

import com.neaterbits.gui.util.compat.function.CFunction;
import com.neaterbits.gui.util.compat.stream.CStream;

class FlatMapStream<T, R> extends ChainedStream<T, R> {
	
	private final CFunction<T, CStream<R>> mapper;

	public FlatMapStream(BaseStream<T> input, CFunction<T, CStream<R>> mapper) {
		super(input);

		this.mapper = mapper;
	}

	@Override
	<Q> Q forEach(final CFunction<R, Q> function) {
		final CFunction<T, Q> flatMapFunction = new CFunction<T, Q>() {
			@Override
			public Q apply(T s) {
				final CStream<R> stream = mapper.apply(s);

				// TODO: Not safe for third party streams, must pass in original operation and continue that
				return ((BaseStream<R>)stream).forEach(function);
			}
		};

		return input.forEach(flatMapFunction);
	}
}
