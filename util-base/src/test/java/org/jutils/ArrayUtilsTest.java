package org.jutils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ArrayUtilsTest {

	@Test
	public void testMerge() {
		final Integer [] ret = ArrayUtils.merge(
				new Integer [] { 9, 8, 7 },
				new Integer [] { 5, 6 },
				new Integer [] { 3, 2, 1, 4 });
		
		
		assertThat(ret).isEqualTo(new Integer [] {
				9, 8, 7, 5, 6, 3, 2, 1, 4
		});
	}

	@Test
	public void testSubArray() {
		
		final Integer [] input = new Integer [] { 3, 2, 1 };
		
		assertThat(ArrayUtils.subArray(input, 0)).isEqualTo(input);
		assertThat(ArrayUtils.subArray(input, 1)).isEqualTo(new Integer [] { 2, 1 });
		assertThat(ArrayUtils.subArray(input, 2)).isEqualTo(new Integer [] { 1 });
		assertThat(ArrayUtils.subArray(input, 3)).isEqualTo(new Integer [] {  });
	}
	
	@Test
	public void testStartsWith() {

	    final Integer [] array = new Integer [] { 1, 2, 3 };

        assertThat(ArrayUtils.startsWith(new Integer[] { }, new Integer [] { })).isTrue();
        
        assertThat(ArrayUtils.startsWith(array, new Integer [] { })).isTrue();
        assertThat(ArrayUtils.startsWith(array, new Integer [] { 1 })).isTrue();
        assertThat(ArrayUtils.startsWith(array, new Integer [] { 1, 2 })).isTrue();
	    assertThat(ArrayUtils.startsWith(array, new Integer [] { 1, 2, 3 })).isTrue();
        assertThat(ArrayUtils.startsWith(array, new Integer [] { 1, 2, 3, 4 })).isFalse();
	    
        assertThat(ArrayUtils.startsWith(array, new Integer [] { 3 })).isFalse();
        assertThat(ArrayUtils.startsWith(array, new Integer [] { 3, 2 })).isFalse();
        assertThat(ArrayUtils.startsWith(array, new Integer [] { 3, 2, 1 })).isFalse();
	}
}
