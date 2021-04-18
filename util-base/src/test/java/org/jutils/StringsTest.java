package org.jutils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class StringsTest {

	@Test
	public void testCountOccurencesOf() {
		
		assertThat(Strings.countOccurencesOf("[] ", "[]")).isEqualTo(1);
		assertThat(Strings.countOccurencesOf("[] [ ", "[]")).isEqualTo(1);
		assertThat(Strings.countOccurencesOf("[] []", "[]")).isEqualTo(2);
		assertThat(Strings.countOccurencesOf(" []xyz[] zyx[]", "[]")).isEqualTo(3);
	}
	
	@Test
	public void testHexString() {

		assertThat(Strings.toHexString(0, 4, true)).isEqualTo("0000");
		assertThat(Strings.toHexString(0, 4, false)).isEqualTo("0");

		assertThat(Strings.toHexString(127, 4, true)).isEqualTo("007F");
		assertThat(Strings.toHexString(127, 4, false)).isEqualTo("7F");

		assertThat(Strings.toHexString(127, 4, true)).isEqualTo("007F");
		assertThat(Strings.toHexString(127, 4, false)).isEqualTo("7F");

		assertThat(Strings.toHexString(535, 4, true)).isEqualTo("0217");
		assertThat(Strings.toHexString(535, 4, false)).isEqualTo("217");

	}
}
