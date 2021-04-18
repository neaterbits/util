package org.jutils.buffers;

/**
 * Base class for buffers, allow allocating data of a certain type
 * 
 */

public abstract class BaseBuffers<ARRAY, BUF> {

	protected ARRAY buffers;
	private final int maxBuffers;
	private final int bufferSize;
	
	private int numBuffers;
	
	protected BaseBuffers(ARRAY initial, int numInitialBuffers, int maxBuffers, int bufferSizeBits) {
		this.buffers = initial;
		this.numBuffers = numInitialBuffers;
		this.maxBuffers = maxBuffers;
		this.bufferSize = BitOperations.max(bufferSizeBits);
	}
	
	protected final void expandBuffers(int bufNo) {
		if (bufNo >= numBuffers) {
			// Increase buffers array
			if (bufNo >= maxBuffers) {
				// More than max buffers
				throw new IllegalStateException("bufNo >= MAX_BUFFERS");
			}
			
			final int numBuffers = Math.min(maxBuffers, this.numBuffers * 2);
			
			this.buffers = copy(buffers, numBuffers);
			this.numBuffers = numBuffers;
		}
		
		if (get(buffers, bufNo) == null) {
			
			final BUF buf = allocateBuffer(bufferSize);
			
			if (buf == null) {
				throw new IllegalStateException("buf == null");
			}
			
			set(buffers, bufNo, buf);
		}
	}
	
	protected abstract BUF get(ARRAY array, int bufNo);
	
	protected abstract void set(ARRAY array, int bufNo, BUF buf);

	protected abstract BUF allocateBuffer(int length);
	
	protected abstract ARRAY copy(ARRAY array, int newLength);

}
