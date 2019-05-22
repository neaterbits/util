package com.neaterbits.util.parse;

public interface IToken {

	TokenType getTokenType();
	
	// Single-character token
	char getCharacter();
	
	// From-to character
	char getFromCharacter();
	char getToCharacter();
	
	String getLiteral();

	// From-to String
	String getFromLiteral();
	String getToLiteral();
	
	// Chartype token
	CharType getCharType();
}
