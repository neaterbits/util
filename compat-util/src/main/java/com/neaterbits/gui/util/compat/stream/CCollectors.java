package com.neaterbits.gui.util.compat.stream;

import java.util.List;
import java.util.Set;

public class CCollectors {
	public static <T> CCollector<T, ?, List<T>> toList() {
		return new ListCollector<T>(); 
	}

	public static <T> CCollector<T, ?, Set<T>> toSet() {
		return new SetCollector<T>(); 
	}
}
