package com.neaterbits.gui.util.compat;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.neaterbits.gui.util.compat.function.CConsumer;
import com.neaterbits.gui.util.compat.function.CFunction;
import com.neaterbits.gui.util.compat.stream.CCollectors;
import com.neaterbits.gui.util.compat.stream.CStream;

public class MultiTree<K, T> {

	private final Map<K, T> entityById;
	private final MapOfList<K, T> entitiesByParent;
	private final MapOfList<K, T> parentsByEntity;
	
	public MultiTree(Collection<T> values, CFunction<T, K> keyFunction, CFunction<T, CStream<K>> recursor) {
		makeEntitiesHierarchial(values, keyFunction, recursor);
		
		final int size = values.size();
		this.entityById = new HashMap<K, T>(size);
		this.entitiesByParent = new MapOfList<K, T>(size);
		this.parentsByEntity = new MapOfList<K, T>(size);
	}
	
	private void makeEntitiesHierarchial(Collection<T> entities,
			final CFunction<T, K> keyFunction,
			final CFunction<T, CStream<K>> recursor) {
		
		for (T t : entities) {
			final K key = keyFunction.apply(t);
			final T existing = entityById.put(key, t);
			
			if (existing != null) {
				throw new IllegalStateException("Multiple entities for key " + key);
			}
		}
		
				
		for (T entity : entities) {
			final T e = entity;
			final CStream<K> sub = recursor.apply(entity);
			
			final CConsumer<K> s = new CConsumer<K>() {
				@Override
				public void accept(K k) {
					final T subEntity = entityById.get(k);

					if (subEntity == null) {
						throw new IllegalStateException("No sub entity for ID " + k);
					}

					entitiesByParent.add(keyFunction.apply(e), subEntity);
					parentsByEntity.add(keyFunction.apply(subEntity), e);
				}
			};
			
			if (sub != null) {
				sub.forEach(s);
			}
		}
	}

	public Collection<T> getRoots() {
		// Find all entries that do not have parents
		final Set<K> allKeys = new HashSet<K>(entityById.keySet());

		allKeys.removeAll(parentsByEntity.keys());

		final CFunction<K, T> mapper = new CFunction<K, T>() {

			@Override
			public T apply(K k) {
				return entityById.get(k);
			}
		};
		
		return Coll.stream(allKeys)
				.map(mapper)
				.collect(CCollectors.<T>toList());
	}
}
