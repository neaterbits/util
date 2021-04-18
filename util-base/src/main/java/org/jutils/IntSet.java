package org.jutils;

import org.jutils.Hash.GetCompareValue;

public final class IntSet {

	private static final long HASH_UNDEF = 0xFFFFFFFFFFFFFFFFL;

	private long [] hash;

	private static final GetCompareValue GET_COMPARE_VALUE = new GetCompareValue() {

		@Override
		public long makeMapValue(long key, long value) {
			return key << 32 | value;
		}

		@Override
		public long getValue(long mapValue) {
			return mapValue & 0xFFFFFFFFFL;
		}

		@Override
		public long getKey(long mapValue) {
			return mapValue >> 32;
		}

		@Override
		public long getDefaultValue() {
			return HASH_UNDEF;
		}
	};

	public IntSet(int size) {

		this.hash = Hash.makeHashMap(size, HASH_UNDEF);
	}

	public boolean contains(int key) {
		return Hash.hashGet(hash, key, HASH_UNDEF, GET_COMPARE_VALUE) != HASH_UNDEF;
	}

	public void add(int value) {
		this.hash = Hash.hashStore(hash, value, value, HASH_UNDEF, GET_COMPARE_VALUE);
	}

	public int size() {
	    return Hash.hashSize(hash);
	}

	public void clear() {
		Hash.hashClear(hash, HASH_UNDEF);
	}

}
