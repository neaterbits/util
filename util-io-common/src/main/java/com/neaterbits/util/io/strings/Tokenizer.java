package com.neaterbits.util.io.strings;

import com.neaterbits.util.IEnum;

/**
 * Marker interface for tokenizer that allows to read tokens. All strings are longs
 *
 */

public interface Tokenizer extends StringSource, StringBufferAdder {

	default <E extends Enum<E> & IEnum> E asEnum(Class<E> enumClass, long stringRef, boolean caseSensitive) {

	    final E [] values = enumClass.getEnumConstants();

        if (caseSensitive) {
            for (E e : values) {
                if (e.getName().equals(asString(stringRef))) {
                    return e;
                }
            }
        }
        else {
            for (E e : values) {
                if (e.getName().equalsIgnoreCase(asString(stringRef))) {
                    return e;
                }
            }
        }

        return null;
	}

	default boolean equalsIgnoreCase(String s, long stringRef) {

	    return s.equalsIgnoreCase(asString(stringRef));
	}

	/**
	 * String between two buffer references
	 *
	 * @param startStringRef opaque start reference
	 * @param endStringRef opaque end reference
	 *
	 * @return the resulting {@link String}
	 */
	String asString(long startStringRef, long endStringRef);

	default boolean equals(long stringRef1, long stringRef2) {

	    return asString(stringRef1).equals(asString(stringRef2));
	}
}
