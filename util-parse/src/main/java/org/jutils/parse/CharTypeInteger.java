package org.jutils.parse;

public class CharTypeInteger extends CharType {

	public static final CharTypeInteger INSTANCE = new CharTypeInteger();

	@Override
	protected boolean isOfType(char c) {
		return Character.isDigit(c);
	}
}
