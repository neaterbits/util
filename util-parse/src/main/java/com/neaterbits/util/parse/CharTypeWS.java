package com.neaterbits.util.parse;

public class CharTypeWS extends CharType {
	
	public static final CharTypeWS INSTANCE = new CharTypeWS();

	@Override
	protected boolean isOfType(char c) {
		return Character.isWhitespace(c);
	}
}
