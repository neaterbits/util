package org.jutils.coll;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class MapOfList<K, V> extends MapOfCollection<K, V, List<V>> {

	public MapOfList() {
		super();
	}

	public MapOfList(int size) {
		super(size);
	}

	@Override
	protected List<V> create() {
		return new ArrayList<V>();
	}
	
	@Override
	protected List<V> create(Collection<V> values) {
		return new ArrayList<V>(values);
	}

	public static <K, T> Collector<T, MapOfList<K, T>, MapOfList<K, T>> keyCollector(Function<T, K> keyFunc) {
		return new KeyOnlyMapOfCollectionCollector<K, T, List<T>, MapOfList<K, T>>(keyFunc) {

			@Override
			public Supplier<MapOfList<K, T>> supplier() {
				
				return new Supplier<MapOfList<K,T>>() {
					@Override
					public MapOfList<K, T> get() {
						return new MapOfList<K, T>();
					}
				};
			}
		};
	}

	public static <K, V, T> Collector<T, MapOfList<K, V>, MapOfList<K, V>> 
		keyValueCollector(Function<T, K> keyFunc, Function<T, V> valueFunc) {
		
		return new KeyValueMapOfCollectionCollector<K, V, List<V>, MapOfList<K, V>, T>(keyFunc, valueFunc) {

			@Override
			public Supplier<MapOfList<K, V>> supplier() {
				return new Supplier<MapOfList<K,V>>() {

					@Override
					public MapOfList<K, V> get() {
						return new MapOfList<K, V>();
					}
				};
			}
			
		};
	}

}
