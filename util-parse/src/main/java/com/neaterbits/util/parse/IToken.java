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
	
	default boolean isEager() {
	    return false;
	}
	
	enum CustomMatchResult {
	    
	    // Matches
	    MATCH,
	    
	    // Does not match but might match with more input
	    POSSIBLE_MATCH,
	    
	    // No match and more input will not change it
	    NO_MATCH
	}
	
	@FunctionalInterface
	interface CustomMatcher {
	    
	    CustomMatchResult test(CharSequence string);
	}
	
	CustomMatcher getCustom();
}
