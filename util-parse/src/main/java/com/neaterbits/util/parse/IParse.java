package com.neaterbits.util.parse;

import java.io.IOException;

import com.neaterbits.util.io.strings.CharInput;
import com.neaterbits.util.io.strings.Tokenizer;

@FunctionalInterface
public interface IParse<DOCUMENT> {
	DOCUMENT parse(CharInput charInput, Tokenizer tokenizer) throws IOException, ParserException;
}
