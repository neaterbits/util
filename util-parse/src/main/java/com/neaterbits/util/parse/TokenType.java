package com.neaterbits.util.parse;

public enum TokenType {
	
	NONE,
	EOF,
	
	CHARACTER, // A single character
	CS_LITERAL, // A literal string
	CI_LITERAL, // A case insensitive literal string
	CHARTYPE,
	FROM_CHAR_TO_CHAR, // From a character to a character, eg. quoted string
	FROM_CHAR_UPTO_CHAR, // From a character to a character, but not including
	FROM_STRING_TO_STRING, // From a string to another, eg. HTML comment
	
	FROM_STRING_TO_EOL,
	
	EXCLUDING_CHAR, // up to but excluding char
	INCLUDING_CHAR, // up to and including char
	
	CUSTOM
	;
}