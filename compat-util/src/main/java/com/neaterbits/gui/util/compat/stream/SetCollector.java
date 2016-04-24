package com.neaterbits.gui.util.compat.stream;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import com.neaterbits.gui.util.compat.function.CSupplier;

public final class SetCollector<T> extends CollCollector<T, Set<T>> {

	@Override
	public Set<CCollector.Characteristics> characteristics() {
		return EnumSet.of(Characteristics.UNORDERED);
	}

	@Override
	public CSupplier<Set<T>> supplier() {
		return new CSupplier<Set<T>>() {
			@Override
			public Set<T> get() {
				return new HashSet<T>();
			}
		};
	}
}
