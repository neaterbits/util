package com.neaterbits.util.compat.function;

public class CPredicates {

	public static <T> CPredicate<T> alwaysTrue() {
		return new CPredicate<T>() {
			@Override
			public boolean test(T t) {
				return true;
			}
		};
	}
}
