package com.neaterbits.util.io.strings;

import com.neaterbits.util.IEnum;
import com.neaterbits.util.buffers.MapStringStorageBuffer;

/**
 * Marker interface for tokenizer that allows to read tokens. All strings are longs
 * @author nhl
 * 
 *
 */

public interface Tokenizer extends StringSource {
	
	<E extends Enum<E> & IEnum> E asEnum(Class<E> enumClass, long stringRef, boolean caseSensitive);
	
	boolean equalsIgnoreCase(String s, long stringRef);
	
	int addToBuffer(MapStringStorageBuffer buffer, long stringRef);
	
	String asString(long startOffset, long endOffset);
}
