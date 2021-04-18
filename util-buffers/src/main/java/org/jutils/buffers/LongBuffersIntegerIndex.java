package org.jutils.buffers;

import static org.jutils.buffers.BitOperations.max;

public class LongBuffersIntegerIndex extends LongBuffers {

	private static final boolean DEBUG = false;
	
	private static final int OFFSET_BITS = 16;
	private static final int BUFNO_BITS = 16;
	
	private static final int BUFFER_SIZE = max(OFFSET_BITS);
	private static final int MAX_BUFFERS = max(BUFNO_BITS);
	
	private static final int bufNo(int ref) {
		return ref >> 16;
	}
	
	private static final int bufOffset(int ref) {
		return ref & 0x0000FFFF;
	}
	
	protected static final int INITIAL_ELEMENT = 1;
	
	private int writePos;
	
	protected LongBuffersIntegerIndex() {
		super(100, MAX_BUFFERS, OFFSET_BITS);
		this.writePos = INITIAL_ELEMENT;
	}
	
	protected final long [] buf(int ref) {
		return buffers[bufNo(ref)];
	}

	protected final int offset(int ref) {
		return bufOffset(ref);
	}
	
	protected final int getBufferSize() {
		return BUFFER_SIZE;
	}
	
	protected final int getWritePos() {
		return writePos;
	}
	
	
	/**
	 * Allocate a chunk of given size
	 * 
	 * @param size size of chunk to allocate
	 * 
	 * @return encoded index to start of block
	 */
	protected final int allocate(int size, String name) {
		
		if (size > BUFFER_SIZE) {
			throw new IllegalArgumentException("size > BUFFER_SIZE");
		}
		
		
		int offset = bufOffset(writePos);
		int bufNo = bufNo(writePos);

		if (DEBUG) {
			System.out.println("allocate " + name + " of size " + size + " from " + offset);
		}
		
		
		if (offset + size > BUFFER_SIZE) {
			// No room in current buffer, must allocate a new one
			++ bufNo;
			offset = 0;
			
			expandBuffers(bufNo);
		}
		else if (buffers[bufNo] == null) {
			buffers[bufNo] = allocateBuffer(BUFFER_SIZE);
		}
		
		final int ret = bufNo << BUFNO_BITS | offset;
		
		this.writePos = ret + size;
		
		// Verify that 0s
		for (int i = 0; i < size; ++ i) {
			if (buffers[bufNo][offset + i] != 0L) {
				throw new IllegalStateException("Buffer not 0s at " + (offset + i));
			}
		}
		
		if (DEBUG) {
			System.out.println("allocate returning for " + name + " at " + offset(ret));
		}
		
		return ret;
	}
}
