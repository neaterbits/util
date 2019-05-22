package com.neaterbits.util.parse;

// A character type, eg letter or whitespace

public abstract class CharType {

	protected boolean isOfType(char c) {
		return false;
	}
	
	public boolean matches(String s) {
		
		final int len = s.length();
		
		boolean matches = true;
		
		for (int i = 0; i < len; ++ i) {
			if (!isOfType(s.charAt(i))) {
				matches = false;
				break;
			}
		}

		return matches;
	}
	
	protected static boolean isASCIILetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'); 
	}
	
}
