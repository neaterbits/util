package com.neaterbits.util.parse;

import java.io.IOException;
import java.util.Arrays;

import com.neaterbits.util.io.strings.CharInput;

public abstract class BaseParser<TOKEN extends Enum<TOKEN> & IToken, INPUT extends CharInput> {

	private final Lexer<TOKEN, CharInput> lexer;
	private final TOKEN wsToken;

	protected BaseParser(Lexer<TOKEN, CharInput> lexer, TOKEN wsToken) {

		if (lexer == null) {
			throw new IllegalArgumentException("lexer == null");
		}

		if (wsToken == null) {
			throw new IllegalArgumentException("wsToken == null");
		}

		this.lexer = lexer;
		this.wsToken = wsToken;
	}

	protected final Lexer<TOKEN, CharInput> getLexer() {
		return lexer;
	}

	@SafeVarargs
	protected final TOKEN lexSkipWS(TOKEN ... tokens) throws IOException {

		TOKEN token = lexer.lex(TokenMergeHelper.merge(wsToken, tokens));

		if (token == wsToken) {
			token = lexer.lex(tokens);
		}
		
		return token;
	}
	
	@SafeVarargs
	protected static <T extends IToken> T [] tokens(T ... tokens) {
		return Arrays.copyOf(tokens, tokens.length);
	}
}
