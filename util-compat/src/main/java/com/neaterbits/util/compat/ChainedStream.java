package com.neaterbits.util.compat;

abstract class ChainedStream<I, T> extends BaseStream<T> {
	final BaseStream<I> input;

	public ChainedStream(BaseStream<I> input) {
		this.input = input;
	}
}
