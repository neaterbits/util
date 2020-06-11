package com.neaterbits.util.parse;

import java.util.function.Predicate;

public enum TestToken implements IToken {

	NONE(TokenType.NONE),
	EOF(TokenType.EOF),
	
	KEYWORD_XYZ("xyz"),
	KEYWORD_ZYX("zyx"),
	
	KEYWORD_ELSE("else"),
	KEYWORD_ELSE_IF("else if"),
	
	WS(new CharTypeWS()),
	COMMENT("/*", "*/");

	private final TokenType tokenType;
	private final String literal;
	private final String toLiteral;
	private final CharType charType;
	
	private TestToken(TokenType tokenType) {
		this(tokenType, null, null, null);
	}
	
	private TestToken(String literal) {
		this(TokenType.CI_LITERAL, literal, null, null);
	}
	
	private TestToken(String fromLiteral, String toLiteral) {
		this(TokenType.FROM_STRING_TO_STRING, fromLiteral, toLiteral, null);
	}
	
	private TestToken(CharType charType) {
		this(TokenType.CHARTYPE, null, null, charType);
	}
	
	private TestToken(TokenType tokenType, String literal, String toLiteral, CharType charType) {
		this.tokenType = tokenType;
		this.literal = literal;
		this.toLiteral = toLiteral;
		this.charType = charType;
	}

	@Override
	public TokenType getTokenType() {
		return tokenType;
	}

	@Override
	public char getCharacter() {
		throw new UnsupportedOperationException();
	}

	@Override
	public char getFromCharacter() {
		throw new UnsupportedOperationException();
	}

	@Override
	public char getToCharacter() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLiteral() {
		return literal;
	}

	@Override
	public String getFromLiteral() {
		return literal;
	}

	@Override
	public String getToLiteral() {
		return toLiteral;
	}

	@Override
	public CharType getCharType() {
		return charType;
	}

    @Override
    public Predicate<CharSequence> getCustom() {
        throw new UnsupportedOperationException();
    }
}

