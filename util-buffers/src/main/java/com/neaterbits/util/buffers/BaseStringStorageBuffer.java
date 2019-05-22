package com.neaterbits.util.buffers;

import java.util.Arrays;

public abstract class BaseStringStorageBuffer {

	public static final int NONE = 0;

	// Stores everything in one buffer
	private char [] buffer;
	
	private int writePos;

	BaseStringStorageBuffer() {
		this.buffer = new char[10000];
		
		// Start at 1 so not same as NONE
		this.writePos = 1;
	}

	public int add(String s) {
		// Not already added, add to buffer
		
		final int len = s.length();
		
		if (len == 0) {
			throw new IllegalArgumentException("s.length() == 0");
		}

		final int size = writePos + len + 1;
		
		if (size > Integer.MAX_VALUE) {
			throw new IllegalStateException("No more room in array");
		}
		
		if (size > buffer.length) {
			expandBuffer(size);
		}
	
		s.getChars(0, len, buffer, writePos);
	
		final int ret = writePos;
		
		writePos += len;
		
		buffer[writePos] = '\0';
		
		++ writePos;
	
		return ret;
	}

	private void expandBuffer(int size) {
		final int newSize = Math.max(size, buffer.length * 2);
		
		final char [] newBuffer = Arrays.copyOf(buffer, newSize);
		
		this.buffer = newBuffer;
	}
	
	public final String getString(int ref) {
		if (ref > writePos) {
			throw new IllegalArgumentException("ref > writePos");
		}
		
		if (ref == NONE) {
			throw new IllegalArgumentException("ref == NONE");
		}
		
		for (int i = ref; ; ++ i) {
			final char c = buffer[i];
			
			if (c == '\0') {
				// 0-termination
				if (i == 0) {
					throw new IllegalStateException("empty string for ref " + ref);
				}
				
				return new String(buffer, ref, i - ref);
			}
		}
	}

}
