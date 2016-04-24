package com.neaterbits.gui.util.compat;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.neaterbits.gui.util.compat.function.CBiConsumer;
import com.neaterbits.gui.util.compat.function.CBinaryOperator;
import com.neaterbits.gui.util.compat.function.CConsumer;
import com.neaterbits.gui.util.compat.function.CFunction;
import com.neaterbits.gui.util.compat.stream.CCollector;

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

	protected final void add(K key, Collection<V> values) {
		final C existing = map.get(key);
		
		if (existing == null) {
			map.put(key, create(values));
		}
		else {
			existing.addAll(values);
		}
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

	public final void forEach(CConsumer<V> consumer) {
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

	
	protected static abstract class MapOfCollectionCollector<K, V, C extends Collection<V>, 
			MOC extends MapOfCollection<K, V, C>, T>
		implements CCollector<T, MOC, MOC> {

		@Override
		public Set<Characteristics> characteristics() {
			return EnumSet.of(Characteristics.UNORDERED);
		}

		@Override
		public CBinaryOperator<MOC> combiner() {
			return new CBinaryOperator<MOC>() {
				@Override
				public MOC apply(MOC left, MOC right) {
					left.addAll(right);
					
					return left;
				}
			};
		}

		@Override
		public CFunction<MOC, MOC> finisher() {

			return new CFunction<MOC, MOC>() {

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
		
		private final CFunction<T, K> valueToKey;

		public KeyOnlyMapOfCollectionCollector(CFunction<T, K> valueToKey) {
			super();
			this.valueToKey = valueToKey;
		}

		@Override
		public CBiConsumer<MOC, T> accumulator() {
			
			return new CBiConsumer<MOC, T>() {

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
	
		private final CFunction<T, K> valueToKey;
		private final CFunction<T, V> valueToValue;

		public KeyValueMapOfCollectionCollector(CFunction<T, K> valueToKey, CFunction<T, V> valueToValue) {
			this.valueToKey = valueToKey;
			this.valueToValue = valueToValue;
		}

		@Override
		public CBiConsumer<MOC, T> accumulator() {
			return new CBiConsumer<MOC, T>() {
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
