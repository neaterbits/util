package org.jutils.io.loadstream;

public class StreamStatus {
	private static final int EOF = 1; // EOF reached
	
	// Blocked on a dependency so can read data but cannot process
	// this occurs eg if computing layout in the parsing flow and we have to wait for a stylesheet,
	// we can continue to load into buffers but parsing must wait
	private static final int BLOCKED_ON_DEPENDENCY = 2;


	public static long of(int elementsRead, boolean blocked) {
		long s = elementsRead;
		
		if (blocked) {
			s |= ((long)BLOCKED_ON_DEPENDENCY) << 32;
		}
		
		return s;
	}

	public static boolean isEOF(long status) {
		return status >>> 32 == EOF;
	}

	public static boolean isBlocked(long status) {
		return status >>> 32 == BLOCKED_ON_DEPENDENCY;
	}

	public static long eof() {
		return ((long)EOF) << 32;
	}
	
	public static int getElementsRead(long status) {
		return (int)(status & 0x00000000FFFFFFFFL);
	}
}
