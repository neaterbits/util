package org.jutils.parse;

import java.io.IOException;

import org.jutils.io.strings.CharInput;
import org.jutils.io.strings.Tokenizer;

@FunctionalInterface
public interface IParse<DOCUMENT> {
	DOCUMENT parse(CharInput charInput, Tokenizer tokenizer) throws IOException, ParserException;
}
