package com.neaterbits.util;

import java.util.Arrays;

/*
 * For storing entries of numbers of up to 64 bit size
 * 
 * For saving storage space.
 * 
 * Has bitsPerValue where bitsPerValue can be > 64 (for storing multiple numbers, each <= 64 bits)
 * and count which are the number of such blocks.
 * 
 */
public final class ValueMap {

	private static final Boolean DEBUG = false;
	
	private final int bitsPerValue;
	private final int valueCount;
	
	private final long [] values;

	static int getAllocationSize(int bitsPerValue, int valueCount) {
		
		final int numBitsTotal = bitsPerValue * valueCount;
		
		return ((numBitsTotal - 1) / 64) + 1;
	}
	
	public ValueMap(int bitsPerValue, int valueCount) {
		this.bitsPerValue = bitsPerValue;
		this.valueCount = valueCount;
		
		final int numLongs = getAllocationSize(bitsPerValue, valueCount);
		
		this.values = new long[numLongs];
	}
	
	ValueMap(int bitsPerValue, int valueCount, long setValue) {
		this(bitsPerValue, valueCount);
		
		Arrays.fill(values, setValue);
	}
	
	public long getValue(int index) {
		
		if (bitsPerValue > 64) {
			throw new IllegalStateException();
		}
	
		return getValue(index, 0, bitsPerValue);
	}
	
	public long getValue(int index, int offset, int numBits) {
		
		if (numBits > 64) {
			throw new IllegalArgumentException();
		}
		
		if (index >= valueCount) {
			throw new IllegalArgumentException();
		}
		
		if (index < 0) {
			throw new IllegalArgumentException();
		}
		
		final int bitOffset = index * bitsPerValue + offset;

		final int arrayIndex = bitOffset / 64;
		final int bitOffsetInLong = bitOffset % 64;
		
		final int spaceInLong = 64 - bitOffsetInLong;
	
		long result;

		if (DEBUG) {
			System.out.println("## get at offset " + offset + " with numbits " + numBits 
				+ " spaceInLong " + spaceInLong + " bitOffset " + bitOffsetInLong + " arrayIndex " + arrayIndex);
		}

		if (numBits <= spaceInLong) {

			final long getBitsMask = Bits.maskForNumBits(numBits);
			final long getBits = (values[arrayIndex] >> bitOffsetInLong);
		
			if (DEBUG) {
				System.out.format("## space for numbits with mask 0x%016x, get bits 0x%016x\n", getBitsMask, getBits);
			}
			
			result = (getBits & getBitsMask);
		}
		else {

			final int numBits0 = spaceInLong;
			final int numBits1 = numBits - spaceInLong;

			final long getBitsMask0 = Bits.mask(numBits0, bitOffsetInLong);
			final long getBitsMask1 = Bits.maskForNumBits(numBits1);


			final long getBits0 = (values[arrayIndex] & getBitsMask0) >>> bitOffsetInLong;
			final long getBits1 = (values[arrayIndex + 1] & getBitsMask1);
			
			result = getBits0;
			result |= getBits1 << numBits0;

			if (DEBUG) {
				System.out.format("## not enough space for numbits get [0] mask 0x%016x, get bits 0x%016x, [1] mask 0x%016x, get bits 0x%016x %d/%d\n",
					getBitsMask0, getBits0,
					getBitsMask1, getBits1,
					numBits0, numBits1);
			}
		}

		return result;
	}
	
	public void storeValue(int index, long value) {
		
		if (bitsPerValue > 64) {
			throw new IllegalStateException();
		}

		storeValue(index, 0, bitsPerValue, value);
	}

	public void storeValue(int index, int offset, int numBits, long value) {

		
		if (numBits > 64) {
			throw new IllegalArgumentException();
		}
		
		if (index < 0) {
			throw new IllegalArgumentException();
		}
		
		if (index >= valueCount) {
			throw new IllegalArgumentException();
		}
		
		if (numBits < 64 && value > Bits.maskForNumBits(numBits)) {
			throw new IllegalArgumentException("value " + value + " > mask " + Bits.maskForNumBits(numBits) + " for " + numBits);
		}
		
		final int bitOffset = index * bitsPerValue + offset;

		final int arrayIndex = bitOffset / 64;
		final int bitOffsetInLong = bitOffset % 64;
		
		final int spaceInLong = 64 - bitOffsetInLong;

		if (DEBUG) {
			System.out.println("## store at " + offset + " with numbits " + numBits + " for value " + value
				+ " spaceInLong " + spaceInLong + " bitOffset " + bitOffsetInLong);
		}
			
		if (numBits <= spaceInLong) {
			
			final long clearBitsMask = Bits.mask(numBits, bitOffsetInLong);
			final long setBits = value << bitOffsetInLong;
		
			if (DEBUG) {
				System.out.format("## space for numbits clear with mask 0x%016x, set bits 0x%016x\n", clearBitsMask, setBits);
			}
			
			values[arrayIndex] &= ~clearBitsMask;
			values[arrayIndex] |= setBits;
		}
		else {

			final int numBits0 = spaceInLong;
			
			final long clearBitsMask0 = Bits.mask(numBits0, bitOffsetInLong);
			final long setBits0 = (value & Bits.maskForNumBits(numBits0)) << bitOffsetInLong;
			
			final int numBits1 = numBits - spaceInLong;
			
			final long clearBitsMask1 = Bits.maskForNumBits(numBits1);
			final long setBits1 = value >>> numBits0;
			
			
			if (DEBUG) {
				System.out.format("## not enough space for numbits clear [0] mask 0x%016x, set bits 0x%016x, [1] mask 0x%016x, set bits 0x%016x %d/%d\n",
						clearBitsMask0, setBits0,
						clearBitsMask1, setBits1,
						numBits0, numBits1);
			}

			values[arrayIndex] &= ~clearBitsMask0;
			values[arrayIndex] |= setBits0;
			
			values[arrayIndex + 1] &= ~clearBitsMask1;
			values[arrayIndex + 1] |= setBits1;
		}
	}
}
