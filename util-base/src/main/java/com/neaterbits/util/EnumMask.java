package com.neaterbits.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public abstract class EnumMask<T extends Enum<T>> {

	private static final int MAX_INDEX = 31;
	
	private final Class<T> type;
	private final int mask;
	
	public EnumMask(Class<T> type, @SuppressWarnings("unchecked") T ... values) {
		this(type, Arrays.asList(values));
	}

	public EnumMask(Class<T> type, Collection<T> values) {
		
		int mask = 0;
		
		for (T value : values) {
			if (value.ordinal() > MAX_INDEX) {
				throw new IllegalArgumentException();
			}
			
			mask |= (1 << value.ordinal());
		}
		
		this.type = type;
		this.mask = mask;
	}
	
	public final boolean isSet(T value) {
		Objects.requireNonNull(value);

		return (mask & (1 << value.ordinal())) != 0;
	}

	
	@SafeVarargs
	public final boolean isSetOnly(T ... values) {
		Objects.requireNonNull(values);
		
		if (values.length == 0) {
			throw new IllegalArgumentException();
		}

		int expected = 0;
		
		for (T value : values) {
			expected |= 1 << value.ordinal();
		}
		
		return (mask & expected) == expected;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mask;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EnumMask<?> other = (EnumMask<?>) obj;
		if (mask != other.mask)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {

		final StringBuilder sb = new StringBuilder();
		
		boolean found = false;
		
		for (int i = 0; i < MAX_INDEX; ++ i) {
			
			if ((mask & (1 << i)) != 0) {

				if (found) {
					sb.append('|');
				}
				else {
					found = true;
				}
				
				sb.append(type.getEnumConstants()[i].name());
			}
		}

		return sb.toString();
	}
}
