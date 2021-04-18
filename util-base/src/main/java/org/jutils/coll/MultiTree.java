package org.jutils.coll;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MultiTree<K, T> {

	private final Map<K, T> entityById;
	private final MapOfList<K, T> entitiesByParent;
	private final MapOfList<K, T> parentsByEntity;
	
	public MultiTree(Iterable<T> values, Function<T, K> keyFunction, Function<T, Stream<K>> recursor) {
		int size = makeEntitiesHierarchial(values, keyFunction, recursor);
		
		this.entityById = new HashMap<K, T>(size);
		this.entitiesByParent = new MapOfList<K, T>(size);
		this.parentsByEntity = new MapOfList<K, T>(size);
	}
	
	private int makeEntitiesHierarchial(Iterable<T> entities,
			final Function<T, K> keyFunction,
			final Function<T, Stream<K>> recursor) {
		
		int size = 0;
		
		for (T t : entities) {
			final K key = keyFunction.apply(t);
			final T existing = entityById.put(key, t);
			
			if (existing != null) {
				throw new IllegalStateException("Multiple entities for key " + key);
			}
			
			++ size;
		}
		
				
		for (T entity : entities) {
			final T e = entity;
			final Stream<K> sub = recursor.apply(entity);
			
			final Consumer<K> s = new Consumer<K>() {
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

		return size;
	}

	public Collection<T> getRoots() {
		// Find all entries that do not have parents
		final Set<K> allKeys = new HashSet<K>(entityById.keySet());

		allKeys.removeAll(parentsByEntity.keys());

		final Function<K, T> mapper = entityById::get;
		
		return allKeys.stream()
				.map(mapper)
				.collect(Collectors.toList());
	}
}
