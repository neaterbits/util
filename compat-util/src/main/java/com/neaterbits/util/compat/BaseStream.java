package com.neaterbits.util.compat;

import com.neaterbits.util.compat.function.CBiConsumer;
import com.neaterbits.util.compat.function.CConsumer;
import com.neaterbits.util.compat.function.CFunction;
import com.neaterbits.util.compat.function.CPredicate;
import com.neaterbits.util.compat.stream.CCollector;
import com.neaterbits.util.compat.stream.CStream;

abstract class BaseStream<T> implements CStream<T> {

	private static final class OptionalImpl<T> implements COptional<T> {

		private final T value;
		
		OptionalImpl(T value) {
			this.value = value;
		}

		@Override
		public T get() {
			if (value == null) {
				throw new IllegalStateException("value == null");
			}

			return value;
		}

		@Override
		public T orElse(T other) {
			return value != null ? value : other;
		}
	}
	
	abstract <Q> Q forEach(CFunction<T, Q> exitIfNonNull);

	@Override
	public final CStream<T> filter(CPredicate<T> predicate) {
		return new FilterStream<T>(this, predicate);
	}

	@Override
	public <R> CStream<R> map(CFunction<	T, R> mapper) {
		return new MapStream<T, R>(this, mapper);
	}

	@Override
	public final <R> CStream<R> flatMap(CFunction<T, CStream<R>> mapper) {
		return new FlatMapStream<T, R>(this, mapper);
	}

	@Override
	public final <R, A> R collect(CCollector<? super T, A, R> collector) {
		final A acc = collector.supplier().get();

		final CBiConsumer<A, ? super T> accumulator = collector.accumulator();
		
		final CConsumer<T> consumer = new CConsumer<T>() {
			@Override
			public void accept(T s) {
				accumulator.accept(acc, s);
			}
		};

		forEach(consumer);

		return collector.finisher().apply(acc);
	}

	@Override
	public final COptional<T> findFirst() {
		
		final CFunction<T, T> testFunc = new CFunction<T, T>() {
			@Override
			public T apply(T t) {
				return t;
			}
		};
		
		return new OptionalImpl<T>(forEach(testFunc));
	}

	public final void forEach(final CConsumer<T> consumer) {
		final CFunction<T, Void> func = new CFunction<T, Void>() {
			@Override
			public Void apply(T t) {
				consumer.accept(t);

				return null;
			}
		};

		forEach(func);
	}
}

