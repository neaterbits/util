package com.neaterbits.gui.util.compat;

public interface COptional<T> {
	T get();

	T orElse(T other);
}
