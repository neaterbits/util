package org.jutils.parse;

import org.jutils.parse.CharType;
import org.jutils.parse.CharTypeWS;
import org.jutils.parse.IToken;
import org.jutils.parse.TokenType;

public enum TestToken implements IToken {

	NONE(TokenType.NONE),
	EOF(TokenType.EOF),
	
	KEYWORD_XYZ("xyz"),
	KEYWORD_ZYX("zyx"),
	
	KEYWORD_ELSE("else"),
	KEYWORD_ELSE_IF("else if"),
	
	STRING_LITERAL('"', '"'),
	
	WS(new CharTypeWS()),
	C_COMMENT("/*", "*/"),
	CPP_COMMENT("//", TokenType.FROM_STRING_TO_EOL);

	private final TokenType tokenType;
	private final String literal;
	private final String toLiteral;
	private final char fromCharacter;
	private final char toCharacter; 
	private final CharType charType;
	
	private TestToken(TokenType tokenType) {
		this(tokenType, null, null, (char)0, (char)0, null);
	}
	
	private TestToken(String literal) {
		this(TokenType.CI_LITERAL, literal, null, (char)0, (char)0, null);
	}
	
	private TestToken(String fromLiteral, String toLiteral) {
		this(TokenType.FROM_STRING_TO_STRING, fromLiteral, toLiteral, (char)0, (char)0, null);
	}
	
	private TestToken(char fromCharacter, char toCharacter) {
	    this(TokenType.FROM_CHAR_TO_CHAR, null, null, fromCharacter, toCharacter, null);
	}
	
	private TestToken(CharType charType) {
		this(TokenType.CHARTYPE, null, null, (char)0, (char)0, charType);
	}
	
	private TestToken(String fromLiteral, TokenType tokenType) {
	    this.tokenType = tokenType;
	    this.literal = fromLiteral;
	    this.toLiteral = null;
        this.fromCharacter = 0;
        this.toCharacter = 0;
	    this.charType = null;
	}
	
	private TestToken(
	        TokenType tokenType,
	        String literal, String toLiteral,
	        char fromCharacter, char toCharacter,
	        CharType charType) {

		this.tokenType = tokenType;
		this.literal = literal;
		this.toLiteral = toLiteral;
		this.fromCharacter = fromCharacter;
		this.toCharacter = toCharacter;
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
	    return fromCharacter;
	}

	@Override
	public char getToCharacter() {
	    return toCharacter;
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
    public CustomMatcher getCustom() {
        throw new UnsupportedOperationException();
    }
}

