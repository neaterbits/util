package org.jutils.parse;

import java.util.Arrays;

import org.jutils.io.strings.CharInput;

public abstract class BaseParser<TOKEN extends Enum<TOKEN> & IToken, INPUT extends CharInput> {

	private final Lexer<TOKEN, CharInput> lexer;

	protected BaseParser(Lexer<TOKEN, CharInput> lexer) {

		if (lexer == null) {
			throw new IllegalArgumentException("lexer == null");
		}

		this.lexer = lexer;
	}

	protected final Lexer<TOKEN, CharInput> getLexer() {
		return lexer;
	}

	@SafeVarargs
	protected static <T extends IToken> T [] tokens(T ... tokens) {
		
		if (tokens.length <= 1) {
			throw new IllegalArgumentException();
		}
		
		return Arrays.copyOf(tokens, tokens.length);
	}
}
