package org.jutils.parse;

public enum LexerMatch {
	FIRST_MATCH, // return as soon as we match something, even if other tokens might match a longer string
	LONGEST_MATCH; // loop over tokens till we find the one that matches the largest amout of characters
}
