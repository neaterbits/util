package com.neaterbits.util.parse;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

import com.neaterbits.util.ArrayUtils;
import com.neaterbits.util.Value;
import com.neaterbits.util.io.strings.CharInput;

public final class Lexer<TOKEN extends Enum<TOKEN> & IToken, INPUT extends CharInput> {

	private static final Integer DEBUG_LEVEL = 0;

	private static final String PREFIX = "Lexer";

	private final INPUT input;
	private final TOKEN tokNone;
	private final TOKEN tokEOF;

	
	private final StringBuilder cur;

	// Scratch array for single token
	private final TOKEN [] singleToken;
	
	private	final TOKEN [] wsTokens;
	private final int numWSTokens;
	
	private final TOKEN [] wsAndCommentsToken;
	private final int numWSAndCommentTokens;

	
	// Scratch array for maintaining number of matching tokens at any given type
	private final TOKEN [] possiblyMatchingTokens;
	private final boolean [] exactMatches;
	private final TokenMatch tokenMatch;
	
	private int lineNo;
	private long lineStartOffset;
	
	private long tokenizerPos;
	
	// For debug
	private TOKEN lastToken;
	
	public Lexer(INPUT input, Class<TOKEN> tokenClass, TOKEN tokNone, TOKEN tokEOF, TOKEN [] wsTokens, TOKEN [] commentTokens) {
		
		if (input == null) {
			throw new IllegalArgumentException("inputStream == null");
		}
		
		if (tokNone == null) {
			throw new IllegalArgumentException("tokNone == null");
		}
		
		if (wsTokens != null && ArrayUtils.contains(wsTokens, null)) {
			throw new IllegalArgumentException();
		}
		
		if (commentTokens != null && ArrayUtils.contains(commentTokens, null)) {
			throw new IllegalArgumentException();
		}
		
		this.input = input;
		
		this.singleToken = createTokenArray(tokenClass, 1);

		if (wsTokens != null) {
			this.wsTokens = createTokenArray(tokenClass);
			this.numWSTokens = wsTokens.length;

			System.arraycopy(wsTokens, 0, this.wsTokens, 0, wsTokens.length);
			
			if (commentTokens != null) {
				this.wsAndCommentsToken = createTokenArray(tokenClass);
				this.numWSAndCommentTokens = wsTokens.length + commentTokens.length;

				System.arraycopy(wsTokens, 0, this.wsAndCommentsToken, 0, wsTokens.length);
				System.arraycopy(commentTokens, 0, this.wsAndCommentsToken, wsTokens.length, commentTokens.length);
			}
			else {
				this.wsAndCommentsToken = null;
				this.numWSAndCommentTokens = 0;
			}
		}
		else {
			this.wsTokens = null;
			this.numWSTokens = 0;
			this.wsAndCommentsToken = null;
			this.numWSAndCommentTokens = 0;
		}

		this.possiblyMatchingTokens = createTokenArray(tokenClass);
		
		
		this.exactMatches = new boolean[tokenClass.getEnumConstants().length];
		
		this.tokenMatch = new TokenMatch();
		
		this.tokNone = tokNone;
		this.tokEOF = tokEOF;
		this.cur = new StringBuilder();
		this.lineNo = 1;
	}

	private void mark() {
		this.tokenizerPos = input.getReadPos();
	}
	
	public long getTokenStartPos() {
		return tokenizerPos;
	}
	
	public int getStartPosInLine() {
		return (int)(getTokenStartPos() - lineStartOffset);
	}
	
	public long getStringRef() {
        return input.getStringRef(tokenizerPos, input.getReadPos(), 0, 0);
	}

	public long getStringRef(int startPos, int endSkip) {
		return input.getStringRef(tokenizerPos, input.getReadPos(), startPos, endSkip);
	}

	private TOKEN [] createTokenArray(Class<TOKEN> tokenClass) {
		return createTokenArray(tokenClass, tokenClass.getEnumConstants().length);
	}

	@SuppressWarnings("unchecked")
	private TOKEN [] createTokenArray(Class<TOKEN> tokenClass, int length) {
		return (TOKEN[])Array.newInstance(tokenClass, length);
	}
	
	// Helper class to return multiple values
	private static class TokenMatch {
		private boolean matchesExactly;
		private boolean mightMatch;
	}

	private static final LexerMatch DEFAULT_MATCH = LexerMatch.LONGEST_MATCH;

	public TOKEN peek(@SuppressWarnings("unchecked") TOKEN ... inputTokens) throws IOException {
        return peek(DEFAULT_MATCH, null, inputTokens);
    }

	public TOKEN peek(Value<String> value, @SuppressWarnings("unchecked") TOKEN ... inputTokens) throws IOException {
		return peek(DEFAULT_MATCH, value, inputTokens);
	}

	public TOKEN peek(LexerMatch matchMethod, Value<String> value, @SuppressWarnings("unchecked") TOKEN ... inputTokens) throws IOException {
		
		final String existing = cur.toString();
		
		// will reset cur
		final TOKEN lexed = lex(matchMethod, inputTokens);
		
		// must revert chars back to stream
		input.rewind(cur.length());
		
		if (value != null) {
			value.set(cur.toString());
		}
	
		cur.setLength(0);
		cur.append(existing);
		
		return lexed;
	}

	public TOKEN lex(TOKEN inputToken) throws IOException {
		
		this.singleToken[0] = inputToken;
		
		return lex(DEFAULT_MATCH, singleToken);
	}

	public TOKEN lex(TOKEN [] inputTokens) throws IOException {
		return lex(DEFAULT_MATCH, inputTokens);
	}

	private boolean isWSToken(TOKEN token) {
		Objects.requireNonNull(token);
		
		for (int i = 0; i < numWSTokens; ++ i) {
			if (token == wsTokens[i]) {
				return true;
			}
		}
		
		return false;
	}

	private boolean isWSOrCommentToken(TOKEN token) {
		Objects.requireNonNull(token);
		
		for (int i = 0; i < numWSAndCommentTokens; ++ i) {
			if (token == wsAndCommentsToken[i]) {
				return true;
			}
		}
		
		return false;
	}

	
	public final TOKEN lexSkipWS(TOKEN toFind) throws IOException {
		
		Objects.requireNonNull(toFind);
		
		wsTokens[numWSTokens] = toFind;
		
		TOKEN found = lex(DEFAULT_MATCH, wsTokens, numWSTokens + 1);

		if (isWSToken(found)) {
			found = lex(toFind);
		}
		
		return found;
	}

	public final TOKEN lexSkipWS(TOKEN [] tokens) throws IOException {

		System.arraycopy(tokens, 0, wsTokens, numWSTokens, tokens.length);
		
		TOKEN token = lex(DEFAULT_MATCH, wsTokens, numWSTokens + tokens.length);

		if (isWSToken(token)) {
			token = lex(tokens);
		}
		
		return token;
	}

	
	public final TOKEN lexSkipWSAndComment(TOKEN toFind) throws IOException {

		TOKEN token;

		wsAndCommentsToken[numWSAndCommentTokens] = toFind;
		
		for (;;) {
			token = lex(DEFAULT_MATCH, wsAndCommentsToken, numWSAndCommentTokens + 1);

			if (!isWSOrCommentToken(token)) {
				break;
			}
		}
		
		return token;
	}

	public final TOKEN lexSkipWSAndComment(TOKEN [] tokens) throws IOException {

		TOKEN token;

		System.arraycopy(tokens, 0, wsAndCommentsToken, numWSAndCommentTokens, tokens.length);
		
		for (;;) {
			token = lex(DEFAULT_MATCH, wsAndCommentsToken, numWSAndCommentTokens + tokens.length);

			if (!isWSOrCommentToken(token)) {
				break;
			}
		}
		
		return token;
	}


	public final void skipAnyWS() throws IOException {
		lex(LexerMatch.LONGEST_MATCH, wsTokens, numWSTokens);
	}

	public TOKEN lex(LexerMatch matchMethod, TOKEN [] inputTokens) throws IOException {
		return lex(matchMethod, inputTokens, inputTokens.length);
	}
		
	public TOKEN lex(LexerMatch matchMethod, TOKEN [] inputTokens, int numInputTokens) throws IOException {
		if (hasDebugLevel(1)) {
			debug("----");
			debug("lex(\"" + input.peek(20) + "\": " + Arrays.toString(inputTokens) + ")");
		}
		
		if (inputTokens.length > possiblyMatchingTokens.length) {
			throw new IllegalArgumentException("tokens.length > matchingTokens.length");
		}
		
		cur.setLength(0);
		
			// Mark so that tokenizer may know starting point of string

		mark();
		
		// Scan all tokens for input from reader and check whether any tokens match 
		
		TOKEN found = null;
		
		TOKEN longestFoundSoFar = null;
		int lengthOfLongestFoundSoFar = 0;
		
		// Start out with scanning all input tokens, we will switch to scan only those tokens left matching
		TOKEN [] tokens = inputTokens;
		int numTokens = numInputTokens;
		
		// Read input until finds matching token
		// TODO: exit if cannot find matching token of any length
		do {
			final int val = read();
			
			if (val < 0) {
				// If found a matching token, return that
				if (longestFoundSoFar != null) {
					found = longestFoundSoFar;
				}
				else {
					if (tokEOF != null) {
						for (TOKEN token : tokens) {
							if (token == tokEOF) {
								found = tokEOF;
								break;
							}
						}
					}
				}
				
				if (found == null) {
					throw new EOFException("Reached EOF");
				}
				else {
					// Just break out of loop since we already found a token
					break;
				}
			}
			
			final char c = (char)val;
			
			// for showing lineno in case of error
			if (c == '\n') {
				++ lineNo;
				lineStartOffset = tokenizerPos + cur.length() + 1;
			}
			
			cur.append(c);
			
			int numPossibleMatch = 0;
			
			// Loop through all tokens to see if any match. We will return the longest-matching token so we have to keep track of that
			TOKEN firstMatchThisIteration = null;

			if (DEBUG_LEVEL  > 1) {
				debug("matching tokens  to " + tokensToString(tokens, numTokens) + " to  buf = \"" + curForDebug());
			}
			
			for (int i = 0; i < numTokens; ++ i) {
				// Check whether tokens matching
				final TOKEN token = tokens[i];
				
				matchToken(token, c, tokenMatch);

				final boolean match = tokenMatch.matchesExactly;
				if (match && matchMethod == LexerMatch.FIRST_MATCH) {
					found = token;
					break;
				}
				
				// If a token went from matching to non matching, we should remove it from the array of tokens
				// eg for a C style comment, we can continue to have a possible match against */ after found one, but we already have a match and should remove it from the list
				// since it already stopped matching
				final boolean wentFromMatchingToNonMatching = ! match && this.exactMatches[token.ordinal()];
				
				// If might match later or matches now, add to array for next iteration
				if ((tokenMatch.mightMatch || match) && ! wentFromMatchingToNonMatching) {
					this.possiblyMatchingTokens[numPossibleMatch] = token;
					++ numPossibleMatch;
				}
				
				this.exactMatches[token.ordinal()] = match;

				if (hasDebugLevel(3)) {
					debug(PREFIX + " match to " + token + "\", match=" + match +", numPossibleMatch=" + numPossibleMatch+ ", buf = \"" + curForDebug());
				}
				
				if (match) {
					if (firstMatchThisIteration == null) {
						firstMatchThisIteration = token;
					}
				}
			}
			
			if (found == null) { // found may be set in case of FIRST_MATCH matching method
				if (firstMatchThisIteration != null) {
					// found a match this iteration, set as longest so far
					longestFoundSoFar = firstMatchThisIteration;
					lengthOfLongestFoundSoFar = cur.length();
				}
	
				// No possible matches and none found, return tokNone unless have found an earlier match
				if (numPossibleMatch == 0) {
	
					// If read a character, buffer it for next iteration and remove from buffer
					bufferCharacter(val);
	
					if (cur.charAt(cur.length() - 1) != c) {
						throw new IllegalStateException("Mismatch of last char: " + c);
					}
					
					cur.setLength(cur.length() - 1);
	
					if (longestFoundSoFar == null) {
					
						if (hasDebugLevel(2)) {
							debug("No possible matches, returning");
						}
						
						// No possible matches, return not-found token
						found = tokNone; // triggers to break out of loop
					}
					else {
						// found a token, return that
						found = longestFoundSoFar;

						// Reset back to length of last found
						final int beyondLongest = cur.length() - lengthOfLongestFoundSoFar;
				
						if (beyondLongest > 0) {
						    cur.setLength(lengthOfLongestFoundSoFar);
						    input.rewind(beyondLongest);
						}
					}
				}
				else {
					// Continue iterating, switch to iterating over only matching tokens
					tokens = this.possiblyMatchingTokens;
					numTokens = numPossibleMatch;
				}
			}
		} while (found == null);
		
		if (hasDebugLevel(1)) {
					
			debug("returned token " + found + " at " + lineNo + ", buf=\"" + cur + "\", next=\""+ getNext() + "\"");
		}
		
		this.lastToken = found;
		
		return found;
	}
	
	private String getNext() throws IOException {
		final int peekLength = 20;
		String next = input.peek(peekLength);
		if (next.length() == peekLength) {
			next += "...";
		}

		return next;
	}

	private String tokensToString(TOKEN [] tokens, int numTokens) {
		final StringBuilder sb = new StringBuilder("[ ");

		for (int i = 0; i < numTokens; ++ i) {
			if (i > 0) {
				sb.append(", ");
			}
			
			sb.append(tokens[i].name());
		}
		
		sb.append(" ]");
		
		return sb.toString();
	}
	
	private String curForDebug() {
		return cur.toString().replaceAll("\n", "<newline>");
	}

	private void matchToken(TOKEN token, char c, TokenMatch tokenMatch) throws IOException {
		
		final boolean match;
		final boolean possibleMatch;
		
		switch (token.getTokenType()) {
		case CHARACTER:
			if (cur.length() == 1) {
				match = c == token.getCharacter();

				possibleMatch = match;
			}
			else {
				match = false;
				possibleMatch = false;
			}
			break;
			
		case FROM_CHAR_TO_CHAR:
			if (cur.charAt(0) != token.getFromCharacter()) {
				match = false;
				possibleMatch = false;
			}
			else if (cur.length() >= 2 && c == token.getToCharacter() ){
				match = true;
				possibleMatch = true;
			}
			else {
				possibleMatch = true;
				match = false;
			}
			break;

		case FROM_CHAR_UPTO_CHAR: {
			if (cur.charAt(0) != token.getFromCharacter()) {
				match = false;
				possibleMatch = false;
			}
			else if (cur.length() >= 1 && ((char)input.peek()) == token.getToCharacter() ){
				match = true;
				possibleMatch = true;
			}
			else {
				possibleMatch = true;
				match = false;
			}
			break;
		}
			
		case FROM_STRING_TO_STRING:
			if (cur.length() <= token.getFromLiteral().length()) {
				match = false;
				possibleMatch = token.getFromLiteral().startsWith(cur.toString());
			}
			else if (cur.length() >= (token.getFromLiteral().length() + token.getToLiteral().length())) {
				final String s = cur.toString();
				
				if (s.startsWith(token.getFromLiteral())) {
					if (s.endsWith(token.getToLiteral())) {
						match = true;
						possibleMatch = true;
					}
					else {
						possibleMatch = true;
						match = false;
					}
				}
				else {
					possibleMatch = false;
					match = false;
				}
			}
			else {
				// cur length is > from literal length but less than sum
				final String s = cur.toString();
				
				match = false;
				possibleMatch = s.startsWith(token.getFromLiteral());
			}
			break;
			
		case INCLUDING_CHAR:
			if (c == token.getToCharacter()) {
				match = true;
				possibleMatch = true;
			}
			else {
				match = false;
				possibleMatch = true;
			}
			break;
			
		case EXCLUDING_CHAR: {
			final char next = (char)input.peek();
			
			if (next == token.getToCharacter()) {
				match = true;
				possibleMatch = true;
			}
			else {
				match = false;
				possibleMatch = true;
			}
			break;
		}
			
		case CS_LITERAL:
			if (cur.length() < token.getLiteral().length()) {
				match = false;
				possibleMatch = token.getLiteral().startsWith(cur.toString());
			}
			else if (cur.length() == token.getLiteral().length()) {
				match = cur.toString().equals(token.getLiteral());
				possibleMatch = match;
			}
			else {
				match = false;
				possibleMatch = false;
			}
			break;
			
		case CI_LITERAL:
			if (cur.length() < token.getLiteral().length()) {
				match = false;
				possibleMatch = token.getLiteral().substring(0, cur.length()).equalsIgnoreCase(cur.toString());
			}
			else if (cur.length() == token.getLiteral().length()) {
				match = cur.toString().equalsIgnoreCase(token.getLiteral());
				possibleMatch = match;
			}
			else {
				match = false;
				possibleMatch = false;
			}
			break;

		case CHARTYPE: {
			final boolean matches = token.getCharType().matches(cur.toString());
			if (matches) {
				// Matches but we should read all characters from stream
				match = true;
				possibleMatch = true;
			}
			else {
				match = false;
				possibleMatch = false;
			}
			break;
		}
			
		case CUSTOM: {
            final boolean matches = token.getCustom().test(cur);
            if (matches) {
                // Matches but we should read all characters from stream
                match = true;
                possibleMatch = true;
            }
            else {
                match = false;
                possibleMatch = false;
            }
		    break;
		}
			
		case EOF:
			// skip
			match = false;
			possibleMatch = false;
			break;
				
			
		default:
			throw new IllegalArgumentException("Unknown token type " + token.getTokenType() + " for token " + token);
		}

		tokenMatch.matchesExactly = match;
		tokenMatch.mightMatch = possibleMatch;
	}
	
	private final int read() throws IOException {
		final int ret;
		
		ret = input.readNext();

		return ret;
	}
	
	private void bufferCharacter(int val) {
		
		input.rewindOneCharacter(val);
		
		if ((char) val == '\n') {
			// if buffering newline, subtract from previously increased lineNo
			-- lineNo;
		}
	}
	
	public final String get() {
		return cur.toString();
	}
	
	public final int getStartLineNo() {
		return lineNo;
	}
	
	public final int getEndLineNo() {
		return lineNo;
	}

	public final int getTokenLength() {
		return cur.length();
	}
	
	public final ParserException unexpectedToken() {
		
		String next;
		
		try {
			next = getNext();
		} catch (IOException e) {
			next = "<exception>";
		}
		
		return new ParserException("Unexpected token for \"" + cur.toString() + "\" at " + lineNo + ": " + lastToken+ "\", next=\""+ next + "\"");
	}
	
	public final int getEndSkip() {
		return 0;
	}
  
	public final long getInputReadPos() {
		return input.getReadPos();
	}

	private void debug(String s) {
		System.out.println(PREFIX + " " + lineNo + ": " + s);
	}
	
	private static boolean hasDebugLevel(int level) {
		return DEBUG_LEVEL>= level;
	}
 }
