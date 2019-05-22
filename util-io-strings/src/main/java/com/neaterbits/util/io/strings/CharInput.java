package com.neaterbits.util.io.strings;

import java.io.IOException;

public interface CharInput {

	int readNext() throws IOException;

	// Current read pos, may be encoded so cannot do arithmetics on this
	long getReadPos();
	
	long getStringRef(long startPos, long endPos, int startOffset, int endSkip);
	
	// when reading one char extra in lexer so that tokenizer position in buffer reflects
	// where the lexer is at, so that the next mark() is correct
	// TODO fix this in better way? lexer keeps token in StringBuilder anyway, might as well get string from there?
	// TODO or perhaps lexer should use tokenizer to avoid copying, and remove its own StringBuilder?
	// TODO though for debugging, there should be a mode for the lexer to cache even more characters so can easily see context
	
	// val param is just to verify that matches with last param
	void rewindOneCharacter(int val);
	
	void rewind(int numCharacters);
	
	// For debug, peek the number of characters, or whatever is left in the stream
	String peek(int num) throws IOException;
	
	int peek() throws IOException;
}
