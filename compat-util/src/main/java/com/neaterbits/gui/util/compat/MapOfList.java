package com.neaterbits.gui.util.compat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.neaterbits.gui.util.compat.function.CFunction;
import com.neaterbits.gui.util.compat.function.CSupplier;
import com.neaterbits.gui.util.compat.stream.CCollector;

public class MapOfList<K, V> extends MapOfCollection<K, V, List<V>> {

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

	public static <K, T> CCollector<T, MapOfList<K, T>, MapOfList<K, T>> keyCollector(CFunction<T, K> keyFunc) {
		return new KeyOnlyMapOfCollectionCollector<K, T, List<T>, MapOfList<K, T>>(keyFunc) {

			@Override
			public CSupplier<MapOfList<K, T>> supplier() {
				
				return new CSupplier<MapOfList<K,T>>() {
					@Override
					public MapOfList<K, T> get() {
						return new MapOfList<K, T>();
					}
				};
			}
		};
	}

	public static <K, V, T> CCollector<T, MapOfList<K, V>, MapOfList<K, V>> 
		keyValueCollector(CFunction<T, K> keyFunc, CFunction<T, V> valueFunc) {
		
		return new KeyValueMapOfCollectionCollector<K, V, List<V>, MapOfList<K, V>, T>(keyFunc, valueFunc) {

			@Override
			public CSupplier<MapOfList<K, V>> supplier() {
				return new CSupplier<MapOfList<K,V>>() {

					@Override
					public MapOfList<K, V> get() {
						return new MapOfList<K, V>();
					}
				};
			}
			
		};
	}

}
