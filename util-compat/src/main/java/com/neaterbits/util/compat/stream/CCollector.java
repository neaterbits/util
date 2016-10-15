package com.neaterbits.util.compat.stream;

import java.util.Set;

import com.neaterbits.util.compat.function.CBiConsumer;
import com.neaterbits.util.compat.function.CBinaryOperator;
import com.neaterbits.util.compat.function.CFunction;
import com.neaterbits.util.compat.function.CSupplier;

public interface CCollector<T, A, R> {
	
	public enum Characteristics {
		CONCURRENT,
		IDENTITY_FINISH,
		UNORDERED
	};
	
	CBiConsumer<A,T> accumulator();
	
	Set<Characteristics> 	characteristics();

	CBinaryOperator<A> 	combiner();

	CFunction<A,R> 	finisher();
	
	CSupplier<A> supplier();
}
