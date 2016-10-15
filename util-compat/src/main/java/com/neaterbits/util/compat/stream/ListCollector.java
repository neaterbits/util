package com.neaterbits.util.compat.stream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.neaterbits.util.compat.function.CSupplier;

public final class ListCollector<T> extends CollCollector<T, List<T>> {

	@Override
	public Set<CCollector.Characteristics> characteristics() {
		return new HashSet<CCollector.Characteristics>();
	}
	
	@Override
	public CSupplier<List<T>> supplier() {
		return new CSupplier<List<T>>() {

			@Override
			public List<T> get() {
				return new ArrayList<T>();
			}
		};
	}
}
