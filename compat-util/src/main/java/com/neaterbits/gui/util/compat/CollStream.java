package com.neaterbits.gui.util.compat;

import java.util.Collection;

import com.neaterbits.gui.util.compat.function.CFunction;

final class CollStream<T> extends BaseStream<T> {
	
	private final Collection<T> coll;

	CollStream(Collection<T> coll) {
		
		if (coll == null) {
			throw new IllegalArgumentException("coll == null");
		}
		
		this.coll = coll;
	}
	
	
	@Override
	public <Q> Q forEach(CFunction<T, Q> function) {
		for (T t : coll) {
			final Q ret = function.apply(t);
			
			if (ret != null) {
				return ret;
			}
		}
		
		return null;
	}
}
