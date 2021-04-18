package org.jutils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.Test;

public class ValueMapTest {

	// @Test
	public void testAllocationSize() {
		
		assertThat(ValueMap.getAllocationSize(1, 100)).isEqualTo(2);
	}

	// @Test
	public void testValueMapOneBit() {
		
		final ValueMap valueMap = new ValueMap(1, 100);
		
		valueMap.storeValue(25, 1);
		
		assertThat(valueMap.getValue(0)).isEqualTo(0);
		assertThat(valueMap.getValue(25)).isEqualTo(1);
		assertThat(valueMap.getValue(75)).isEqualTo(0);
		
		try {
			valueMap.storeValue(100, 1);
			
			fail("Expected exception");
		}
		catch (IllegalArgumentException ex) {
			
		}
		
		try {
			valueMap.storeValue(-1, 1);
			
			fail("Expected exception");
		}
		catch (IllegalArgumentException ex) {
			
		}
		
		try {
			valueMap.storeValue(30, 2);
			
			fail("Expected exception");
		}
		catch (IllegalArgumentException ex) {
			
		}
	}
	
	// @Test
	public void testValueMapOneTwoBits() {
		
		final ValueMap valueMap = new ValueMap(2, 150);
		
		valueMap.storeValue(25, 3);

		checkRange(valueMap, 0, 24, 0);
		
		assertThat(valueMap.getValue(25)).isEqualTo(3);

		checkRange(valueMap, 26, 149, 0);

		valueMap.storeValue(25, 2);

		checkRange(valueMap, 0, 24, 0);

		assertThat(valueMap.getValue(25)).isEqualTo(2);

		checkRange(valueMap, 26, 149, 0);
	}

	//@Test
	public void testValueMapOneThreeBitsLongBoundary() {
		checkStoreAndRestore(3, 150, 0, 4);
		
		// in first 64 bits
		checkStoreAndRestore(3, 150, 15, 4);
		
		// last bits in first long
		checkStoreAndRestore(3, 150, 20, 0);
		checkStoreAndRestore(3, 150, 20, 1);
		checkStoreAndRestore(3, 150, 20, 2);
		checkStoreAndRestore(3, 150, 20, 3);
		checkStoreAndRestore(3, 150, 20, 4);
		checkStoreAndRestore(3, 150, 20, 5);
		checkStoreAndRestore(3, 150, 20, 6);
		checkStoreAndRestore(3, 150, 20, 7);

		// 2 bit overlap to next since 63 bits stored before (offset 63)
		checkStoreAndRestore(3, 150, 21, 0);
		checkStoreAndRestore(3, 150, 21, 1);
		checkStoreAndRestore(3, 150, 21, 2);
		checkStoreAndRestore(3, 150, 21, 3);
		checkStoreAndRestore(3, 150, 21, 4);
		checkStoreAndRestore(3, 150, 21, 5);
		checkStoreAndRestore(3, 150, 21, 6);
		checkStoreAndRestore(3, 150, 21, 7);

		checkStoreAndRestore(3, 150, 21, 7, 0);
		checkStoreAndRestore(3, 150, 21, 7, 1);
		checkStoreAndRestore(3, 150, 21, 7, 2);
		checkStoreAndRestore(3, 150, 21, 7, 3);
		checkStoreAndRestore(3, 150, 21, 7, 4);
		checkStoreAndRestore(3, 150, 21, 7, 5);
		checkStoreAndRestore(3, 150, 21, 7, 6);
		checkStoreAndRestore(3, 150, 21, 7, 7);

		checkStoreAndRestore(3, 150, 21, 7, 0);
		checkStoreAndRestore(3, 150, 21, 6, 1);
		checkStoreAndRestore(3, 150, 21, 5, 2);
		checkStoreAndRestore(3, 150, 21, 4, 3);
		checkStoreAndRestore(3, 150, 21, 3, 4);
		checkStoreAndRestore(3, 150, 21, 2, 5);
		checkStoreAndRestore(3, 150, 21, 1, 6);
		checkStoreAndRestore(3, 150, 21, 0, 7);

		checkStoreAndRestore(3, 150, 21, 0, 7);
		checkStoreAndRestore(3, 150, 21, 1, 6);
		checkStoreAndRestore(3, 150, 21, 2, 5);
		checkStoreAndRestore(3, 150, 21, 3, 4);
		checkStoreAndRestore(3, 150, 21, 4, 3);
		checkStoreAndRestore(3, 150, 21, 5, 2);
		checkStoreAndRestore(3, 150, 21, 6, 1);
		checkStoreAndRestore(3, 150, 21, 7, 0);

		// in next long
		checkStoreAndRestore(3, 150, 22, 0);
		checkStoreAndRestore(3, 150, 22, 1);
		checkStoreAndRestore(3, 150, 22, 2);
		checkStoreAndRestore(3, 150, 22, 3);
		checkStoreAndRestore(3, 150, 22, 4);
		checkStoreAndRestore(3, 150, 22, 5);
		checkStoreAndRestore(3, 150, 22, 6);
		checkStoreAndRestore(3, 150, 22, 7);
		
		// Set all bits to 1 initially
		final ValueMap valueMap = new ValueMap(3, 150, 0xFFFFFFFFFFFFFFFFL);

		checkStoreAndRestore(valueMap, 3, 150, 21, 0, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 1, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 2, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 3, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 4, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 5, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 6, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 7, 7);

		checkStoreAndRestore(valueMap, 3, 150, 21, 7, 0, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 7, 1, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 7, 2, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 7, 3, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 7, 4, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 7, 5, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 7, 6, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 7, 7, 7);

		checkStoreAndRestore(valueMap, 3, 150, 21, 7, 0, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 6, 1, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 5, 2, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 4, 3, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 3, 4, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 2, 5, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 1, 6, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 0, 7, 7);

		checkStoreAndRestore(valueMap, 3, 150, 21, 0, 7, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 1, 6, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 2, 5, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 3, 4, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 4, 3, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 5, 2, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 6, 1, 7);
		checkStoreAndRestore(valueMap, 3, 150, 21, 7, 0, 7);
	}
	
	// @Test
	public void testOffsetInRange() {

		final ValueMap valueMap = new ValueMap(32, 150);
		
		try {
			valueMap.storeValue(0, 25, 30, 123);
		}
		catch (IllegalArgumentException ex) {
			fail("Expected exception");
		}
	}

	@Test
	public void testMultipleNumbersInOneBlock() {

		final ValueMap valueMap = new ValueMap(68, 150);

		for (int i = 0; i < 150; ++ i) {

			valueMap.storeValue(i, 0, 64, i + 123);
			valueMap.storeValue(i, 64, 4, i % 16);
			
			assertThat(valueMap.getValue(i, 0, 64)).isEqualTo(i + 123);
			assertThat(valueMap.getValue(i, 64, 4)).isEqualTo(i % 16);
		}
	}

	private void checkStoreAndRestore(int numBits, int count, int index, int value) {
		
		final ValueMap valueMap = new ValueMap(numBits, count);
	
		checkStoreAndRestore(valueMap, numBits, count, index, value, value, 0);
	}

	private void checkStoreAndRestore(int numBits, int count, int index, int value1, int value2) {
		final ValueMap valueMap = new ValueMap(numBits, count);

		checkStoreAndRestore(valueMap, numBits, count, index, value1, value2, 0);
	}

	private void checkStoreAndRestore(ValueMap valueMap, int numBits, int count, int index, int value, int rangeCheck) {
		checkStoreAndRestore(valueMap, numBits, count, index, value, value, rangeCheck);
	}

	
	private void checkStoreAndRestore(ValueMap valueMap, int numBits, int count, int index, int value1, int value2, int rangeCheck) {
		
		valueMap.storeValue(index, value1);

		if (index > 0) {
			checkRange(valueMap, 0, index - 1, rangeCheck);
		}
		
		assertThat(valueMap.getValue(index)).isEqualTo(value1);
		
		if (index < count - 1) {
			checkRange(valueMap, index + 1, count - 1, rangeCheck);
		}

		valueMap.storeValue(index, value2);

		if (index > 0) {
			checkRange(valueMap, 0, index - 1, rangeCheck);
		}
		
		assertThat(valueMap.getValue(index)).isEqualTo(value2);

		if (index < count - 1) {
			checkRange(valueMap, index + 1, count - 1, rangeCheck);
		}
	}

	private void checkRange(ValueMap valueMap, int first, int last, int expected) {
		
		for (int i = first; i <= last; ++ i) {
			assertThat(valueMap.getValue(i)).isEqualTo(expected);
		}
	}
}
