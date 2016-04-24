package com.neaterbits.gui.util.compat.stream;

import java.util.Collection;

import com.neaterbits.gui.util.compat.function.CBiConsumer;
import com.neaterbits.gui.util.compat.function.CBinaryOperator;
import com.neaterbits.gui.util.compat.function.CFunction;

abstract class CollCollector<T, C extends Collection<T>> implements CCollector<T, C, C> {
	@Override
	public final CBiConsumer<C, T> accumulator() {
		return new CBiConsumer<C, T>() {

			@Override
			public void accept(C s, T t) {
				s.add(t);
			}
		};
	}


	@Override
	public final CBinaryOperator<C> combiner() {
		return new CBinaryOperator<C>() {

			@Override
			public C apply(C left, C right) {
				left.addAll(right);

				return left;
			}
		};
	}

	@Override
	public final CFunction<C, C> finisher() {
		return new CFunction<C, C>() {

			@Override
			public C apply(C t) {
				return t;
			}
		};
	}

}
