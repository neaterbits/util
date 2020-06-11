package com.neaterbits.util.parse;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.neaterbits.util.io.loadstream.SimpleLoadStream;
import com.neaterbits.util.io.strings.CharInput;
import com.test.util.io.buffers.StringBuffers;

public class LexerTest {

	private Lexer<TestToken, CharInput> createLexer(CharInput data) {
	
		final Lexer<TestToken, CharInput> lexer = new Lexer<TestToken, CharInput>(
				data,
				TestToken.class,
				TestToken.NONE,
				TestToken.EOF,
				new TestToken [] { TestToken.WS },
				new TestToken [] { TestToken.COMMENT });
		
		
		return lexer;
	}
	
	@Test
	public void testParseSkipWS() throws IOException {
		
		final StringBuffers buffer = new StringBuffers(new SimpleLoadStream(" xyz  "));
		final Lexer<TestToken, CharInput> lexer = createLexer(buffer);
		
		assertThat(lexer.lexSkipWS(TestToken.KEYWORD_XYZ)).isEqualTo(TestToken.KEYWORD_XYZ);

		assertThat(lexer.get()).isEqualTo("xyz");
	}

	@Test
	public void testParseSkipWSAndComment() throws IOException {
		
		final StringBuffers buffer = new StringBuffers(new SimpleLoadStream(" /* xyz */zyx "));
		final Lexer<TestToken, CharInput> lexer = createLexer(buffer);
		
		assertThat(lexer.lexSkipWSAndComment(TestToken.KEYWORD_ZYX)).isEqualTo(TestToken.KEYWORD_ZYX);

		assertThat(lexer.get()).isEqualTo("zyx");
	}

	@Test
	public void testGettingElseAndElseIfReturnsElseWithoutTrailingSpace() throws IOException {
	    
	    // Make sure that 'else {' returns 'else'
	    // even if also matching middle space of 'else if' 

        final StringBuffers buffer = new StringBuffers(new SimpleLoadStream("else {")); 
        final Lexer<TestToken, CharInput> lexer = createLexer(buffer);

        final TestToken [] tokens = new TestToken[] {
                TestToken.KEYWORD_ELSE,
                TestToken.KEYWORD_ELSE_IF
        };
        
        final TestToken token = lexer.lexSkipWSAndComment(tokens);
        
        assertThat(token).isEqualTo(TestToken.KEYWORD_ELSE);
        
        assertThat(buffer.getString(lexer.getStringRef())).isEqualTo("else");
        assertThat(lexer.get()).isEqualTo("else");
	}
}
