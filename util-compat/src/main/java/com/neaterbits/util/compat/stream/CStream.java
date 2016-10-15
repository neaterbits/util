package com.neaterbits.util.compat.stream;

import com.neaterbits.util.compat.COptional;
import com.neaterbits.util.compat.function.CConsumer;
import com.neaterbits.util.compat.function.CFunction;
import com.neaterbits.util.compat.function.CPredicate;

public interface CStream<T> {

	void forEach(CConsumer<T> consumer);
	
	CStream<T> filter(CPredicate<T> predicate);
	
	<R> CStream<R> map(CFunction<T, R> mapper);
	
	<R,A> R collect(CCollector<? super T,A,R> collector);
	
	<R> CStream<R> flatMap(CFunction<T, CStream<R>> mapper);

	COptional<T> findFirst();
}
