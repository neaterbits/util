package com.neaterbits.util.parse;

public class CharTypeHexDigit extends CharType {

	public static final CharTypeHexDigit INSTANCE = new CharTypeHexDigit();
	
	@Override
	protected boolean isOfType(char c) {
		return Character.isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
	}
}
