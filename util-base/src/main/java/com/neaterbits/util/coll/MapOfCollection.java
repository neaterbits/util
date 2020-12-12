package com.neaterbits.util.coll;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public abstract class MapOfCollection<K, V, C extends Collection<V>> {

	private final Map<K, C> map;

	protected abstract C create();
	
	protected abstract C create(Collection<V> values);

	protected MapOfCollection() {
		this.map = new HashMap<K, C>();
	}

	protected MapOfCollection(int size) {
		this.map = new HashMap<K, C>(size);
	}

	protected MapOfCollection(MapOfCollection<K, V, C> toCopy) {
		this(toCopy.map.size());

		addAll(toCopy);
	}

	public final void addAll(MapOfCollection<K, V, C> other) {
		for (Map.Entry<K, C> e : other.map.entrySet()) {
			add(e.getKey(), e.getValue());
		}
	}
	
	public final Set<Map.Entry<K, C>> entrySet() {
	    return map.entrySet();
	}
	
	public final Stream<V> valuesStream() {
	    return map.values().stream().flatMap(c -> c.stream());
	}

	protected final void add(K key, Collection<V> values) {
		final C existing = map.get(key);
		
		if (existing == null) {
			map.put(key, create(values));
		}
		else {
			existing.addAll(values);
		}
	}

	public final int keyCount() {
		return map.size();
	}
	
	public final boolean isEmpty() {
		return map.isEmpty();
	}
	
	public final void add(K key, V value) {
		C coll = map.get(key);

		if (coll == null) {
			coll = create();

			map.put(key, coll);
		}
		coll.add(value);
	}

	public final C get(K key) {
		return map.get(key);
	}

	public final void forEach(Consumer<V> consumer) {
		for (C c : map.values()) {
			for (V v : c) {
				consumer.accept(v);
			}
		}
	}

	public final Set<K> keys() {
		return map.keySet();
	}
	
	public final boolean containsKey(K key) {
		return map.containsKey(key);
	}

	public final boolean removeValueByIdentity(K key, V value) {
		
		boolean removed;
		
		final C c = map.get(key);
		
		if (c == null) {
			removed = false;
		}
		else {
			removed = false;
			
			final Iterator<V> iter = c.iterator();
			
			while (iter.hasNext()) {
				final V v = iter.next();
				
				if (v == value) {
					iter.remove();
					removed = true;
					break;
				}
			}
		}

		return removed;
	}
	
	
	protected static abstract class MapOfCollectionCollector<K, V, C extends Collection<V>, 
			MOC extends MapOfCollection<K, V, C>, T>
		implements Collector<T, MOC, MOC> {

		@Override
		public Set<Characteristics> characteristics() {
			return EnumSet.of(Characteristics.UNORDERED);
		}

		@Override
		public BinaryOperator<MOC> combiner() {
			return new BinaryOperator<MOC>() {
				@Override
				public MOC apply(MOC left, MOC right) {
					left.addAll(right);
					
					return left;
				}
			};
		}

		@Override
		public Function<MOC, MOC> finisher() {

			return new Function<MOC, MOC>() {

				@Override
				public MOC apply(MOC t) {
					return t;
				}
			};
		}
	}
	
	protected static abstract class KeyOnlyMapOfCollectionCollector<K, T, C extends Collection<T>, 
			MOC extends MapOfCollection<K, T, C>>
			
			extends MapOfCollectionCollector<K, T, C, MOC, T>  {
		
		private final Function<T, K> valueToKey;

		public KeyOnlyMapOfCollectionCollector(Function<T, K> valueToKey) {
			this.valueToKey = valueToKey;
		}

		@Override
		public BiConsumer<MOC, T> accumulator() {
			
			return new BiConsumer<MOC, T>() {

				@Override
				public void accept(MOC ml, T t) {
					ml.add(valueToKey.apply(t), t);
				}
			};
		}
	}
	
	protected static abstract class KeyValueMapOfCollectionCollector<K, V, C extends Collection<V>, 
			MOC extends MapOfCollection<K, V, C>, T> 
		
			extends MapOfCollectionCollector<K, V, C, MOC, T>  {
	
		private final Function<T, K> valueToKey;
		private final Function<T, V> valueToValue;

		public KeyValueMapOfCollectionCollector(Function<T, K> valueToKey, Function<T, V> valueToValue) {
			this.valueToKey = valueToKey;
			this.valueToValue = valueToValue;
		}

		@Override
		public BiConsumer<MOC, T> accumulator() {
			return new BiConsumer<MOC, T>() {
				@Override
				public void accept(MOC ml, T t) {
					ml.add(valueToKey.apply(t), valueToValue.apply(t));	
				}
			};
		}

	}

	@Override
	public String toString() {
		return map.toString();
	}
}
