package com.neaterbits.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

public class ArrayUtilsTest {

	@Test(groups={"unit"})
	public void testMerge() {
		final Integer [] ret = ArrayUtils.merge(
				new Integer [] { 9, 8, 7 },
				new Integer [] { 5, 6 },
				new Integer [] { 3, 2, 1, 4 });
		
		
		assertThat(ret).isEqualTo(new Integer [] {
				9, 8, 7, 5, 6, 3, 2, 1, 4
		});
	}

	@Test(groups={"unit"})
	public void testSubArray() {
		
		final Integer [] input = new Integer [] { 3, 2, 1 };
		
		assertThat(ArrayUtils.subArray(input, 0)).isEqualTo(input);
		assertThat(ArrayUtils.subArray(input, 1)).isEqualTo(new Integer [] { 2, 1 });
		assertThat(ArrayUtils.subArray(input, 2)).isEqualTo(new Integer [] { 1 });
		assertThat(ArrayUtils.subArray(input, 3)).isEqualTo(new Integer [] {  });
	}
}
