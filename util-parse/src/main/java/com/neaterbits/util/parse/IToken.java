package com.neaterbits.util.parse;

import java.util.function.Predicate;

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
	
	Predicate<CharSequence> getCustom();
}
