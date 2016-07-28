package com.neaterbits.gui.util.compat;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

public class StringUtilsTest {

	@Test(groups={"unit"})
	public void testTrimHead() {

		assertThat(StringUtils.trimHead(new String [] { null, "", "abc", "", null }))
					.isEqualTo(new String [] { "abc", "", null });

	}
	
	@Test(groups={"unit"})
	public void testTrimTail() {

		assertThat(StringUtils.trimTail(new String [] { null, "", "abc", "", null }))
					.isEqualTo(new String [] { null, "", "abc" });

	}
	
	@Test(groups={"unit"})
	public void testTrim() {

		assertThat(StringUtils.trim(new String [] { null, "", "abc", "", null }))
					.isEqualTo(new String [] { "abc" });

	}

	@Test(groups={"unit"})
	public void testSplit() {
		checkSplit("123", '_', "123");
		checkSplit("/a/b/c", '/', "", "a", "b", "c");
		checkSplit("/a/b/c/", '/', "", "a", "b", "c", "");
		checkSplit("/a//c/", '/', "", "a", "", "c", "");
		
		checkSplit("entityId1-fieldId1-1", '-', "entityId1", "fieldId1", "1");
	}
	
	private void checkSplit(String str, char c, String ... expected) {
		assertThat(StringUtils.split(str, c)).isEqualTo(expected);
	}

	@Test(groups={"unit"})
	public void testToUpperFirst() {
		assertThat(StringUtils.toUpperFirst("upperCase")).isEqualTo("UpperCase");
		assertThat(StringUtils.toUpperFirst("UpperCase")).isEqualTo("UpperCase");
	}

	@Test(groups={"unit"})
	public void testToLowerFirst() {
		assertThat(StringUtils.toLowerFirst("LowerCase")).isEqualTo("lowerCase");
		assertThat(StringUtils.toLowerFirst("lowerCase")).isEqualTo("lowerCase");
	}
}

