package com.neaterbits.util.io.strings;

public interface OffsetLengthStringRef extends StringRef {

	public static long encode(int offset, int length) {
		return ((long)offset) << 32 | (long)length;
	}

	public static int decodeOffset(long stringRef) {
		return (int)(stringRef >>> 32);
	}

	public static int decodeLength(long stringRef) {
		return (int)(stringRef & 0xFFFFFFFF);
	}
	
	public static long substring(long ref, int start, int newLength) {
		
		final int offset = decodeOffset(ref);
		final int length = decodeLength(ref);
		
		if (start + newLength > length) {
			throw new IllegalArgumentException();
		}

		return encode(offset + start, newLength);
	}
}
