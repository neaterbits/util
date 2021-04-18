package org.jutils.buffers;

public class BitOperations {
	public static int max(int numBits) {
		return (1 << numBits) - 1;
	}

	public static int maskToInt(long l, int shiftBits, int numBits) {
		return (int) ((l >> shiftBits) & ((1 << numBits) - 1));
	}
	
	public static long maskToLong(long l, int shiftBits, int numBits) {
		return  ((l >> shiftBits) & ((1 << numBits) - 1));
	}
	
	protected static long unsignedIntToLong(int integer) {
		long l;
		
		if (integer < 0) {
			// clear bit 31 and set in long
			final int i2 = (integer & ~(1 << 31));
			
			l = i2;
			
			l |= 1L << 31;
			
			//System.out.format("converted negative int %08x to %08x %d %016x\n", integer, i2, i2, l);
		}
		else {
			l = integer;
		}
		
		if (l < 0) {
			throw new IllegalStateException("l < 0: " + integer + "/" + l);
		}
		return l;
	}
}
