package org.jutils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void testTrimHead() {

		assertThat(StringUtils.trimHead(new String [] { null, "", "abc", "", null }))
					.isEqualTo(new String [] { "abc", "", null });

	}
	
	@Test
	public void testTrimTail() {

		assertThat(StringUtils.trimTail(new String [] { null, "", "abc", "", null }))
					.isEqualTo(new String [] { null, "", "abc" });

	}
	
	@Test
	public void testTrim() {

		assertThat(StringUtils.trim(new String [] { null, "", "abc", "", null }))
					.isEqualTo(new String [] { "abc" });

	}

	@Test
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

	@Test
	public void testToUpperFirst() {
		assertThat(StringUtils.toUpperFirst("upperCase")).isEqualTo("UpperCase");
		assertThat(StringUtils.toUpperFirst("UpperCase")).isEqualTo("UpperCase");
	}

	@Test
	public void testToLowerFirst() {
		assertThat(StringUtils.toLowerFirst("LowerCase")).isEqualTo("lowerCase");
		assertThat(StringUtils.toLowerFirst("lowerCase")).isEqualTo("lowerCase");
	}

	@Test
	public void testIsAlphaNumeric() {
		assertThat(StringUtils.isAlphaNumeric("1234")).isEqualTo(true);
		assertThat(StringUtils.isAlphaNumeric("abcd")).isEqualTo(true);
		assertThat(StringUtils.isAlphaNumeric("1a2b3c4d")).isEqualTo(true);

		assertThat(StringUtils.isAlphaNumeric(" 1234")).isEqualTo(false);
		assertThat(StringUtils.isAlphaNumeric("abcd ")).isEqualTo(false);
		assertThat(StringUtils.isAlphaNumeric("1a2b 3c4d")).isEqualTo(false);

		assertThat(StringUtils.isAlphaNumeric("1234_")).isEqualTo(false);
		assertThat(StringUtils.isAlphaNumeric("_abcd")).isEqualTo(false);
		assertThat(StringUtils.isAlphaNumeric("1a2b_3c4d")).isEqualTo(false);
		
	}

	@Test
	public void testIsRemoveBlanks() {
		assertThat(StringUtils.removeBlanks("")).isEqualTo("");
		assertThat(StringUtils.removeBlanks("1234")).isEqualTo("1234");
		assertThat(StringUtils.removeBlanks("1234 ")).isEqualTo("1234");
		assertThat(StringUtils.removeBlanks("12 34 ")).isEqualTo("1234");
		assertThat(StringUtils.removeBlanks(" 1234 ")).isEqualTo("1234");
		assertThat(StringUtils.removeBlanks("  ")).isEqualTo("");
	}
	
	@Test
	public void testTokenize() {
	    
	    assertThat(StringUtils.tokenize("")).isEqualTo(new String[0]);
        assertThat(StringUtils.tokenize("abc")).isEqualTo(new String[] { "abc" });
        assertThat(StringUtils.tokenize(" abc")).isEqualTo(new String[] { "abc" });
        assertThat(StringUtils.tokenize("abc ")).isEqualTo(new String[] { "abc" });
        assertThat(StringUtils.tokenize(" abc ")).isEqualTo(new String[] { "abc" });
        assertThat(StringUtils.tokenize("abc def")).isEqualTo(new String[] { "abc", "def" });
        assertThat(StringUtils.tokenize("abc  def")).isEqualTo(new String[] { "abc", "def" });
        assertThat(StringUtils.tokenize("abc   def")).isEqualTo(new String[] { "abc", "def" });
        assertThat(StringUtils.tokenize(" abc   def ")).isEqualTo(new String[] { "abc", "def" });
        assertThat(StringUtils.tokenize("  abc   def  ")).isEqualTo(new String[] { "abc", "def" });
	    
	}
}

