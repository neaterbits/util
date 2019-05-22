package com.neaterbits.util.buffers;

public class BufferUtil extends BitOperations {
	
	protected static class Flag {
		private final int numBits;
		private final Class<? extends Enum<?>> enumClass;
		
		public Flag(int numBits, Class<? extends Enum<?>> enumClass) {
			this.numBits = numBits;
			this.enumClass = enumClass;
		}
	}
	
	protected static void verifyFlags(Flag [] flags, int maxBits) {
		int sumBits = 0; 
		for (Flag flag : flags) {
			if (1 << flag.numBits < flag.enumClass.getEnumConstants().length) {
				throw new IllegalStateException("Not enough room for all bits in " + flag.enumClass);
			}
			
			sumBits += flag.numBits;
		}
		
		if (sumBits > maxBits) {
			throw new IllegalStateException("more than " + maxBits + " bits in flags");
		}
	}

	protected static <E extends Enum<E>> long setEnumBits(long cur, E enumValue, int shift, int bits) {
		long updated = cur & ~((1 << (shift + bits)) - 1);
		updated |= ((long)enumValue.ordinal()) << shift;
		
		//System.out.format("Set %s(%d) to %016x from %016x\n", enumValue.name(), enumValue.ordinal(), updated, cur);

		return updated;
	}

	protected static <E extends Enum<E>> E getEnum(long cur, Class<E> enumClass, int shift, int bits) {
		
		final long mask = (1 << bits) - 1;
		long ordinal = (cur >>> shift) & mask;
		
		return enumClass.getEnumConstants()[(int)ordinal];
	}
}
