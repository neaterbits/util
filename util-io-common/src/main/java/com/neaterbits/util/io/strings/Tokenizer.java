package com.neaterbits.util.io.strings;

import com.neaterbits.util.IEnum;
import com.neaterbits.util.buffers.MapStringStorageBuffer;

/**
 * Marker interface for tokenizer that allows to read tokens. All strings are longs
 *
 */

public interface Tokenizer extends StringSource {
	
	<E extends Enum<E> & IEnum> E asEnum(Class<E> enumClass, long stringRef, boolean caseSensitive);
	
	boolean equalsIgnoreCase(String s, long stringRef);
	
	int addToBuffer(MapStringStorageBuffer buffer, long stringRef);
	
	/**
	 * String between two buffer references
	 * 
	 * @param startStringRef opaque start reference
	 * @param endStringRef opaque end reference
	 * 
	 * @return the resulting {@link String}
	 */
	String asString(long startStringRef, long endStringRef);
	
	/**
	 * As a String counted as offset from the beginning of the stream
	 * 
	 * @param startOffset index from beginning of stream
	 * @param endOffset index from end of stream, 
	 * 
	 * @return the resulting {@link String}
	 */
	String asStringFromOffset(int startOffset, int endOffset);
	
	boolean equals(long stringRef1, long stringRef2);
}
