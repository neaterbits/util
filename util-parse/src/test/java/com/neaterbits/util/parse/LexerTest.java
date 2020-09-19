package com.neaterbits.util.parse;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.neaterbits.util.io.buffers.StringBuffers;
import com.neaterbits.util.io.loadstream.SimpleLoadStream;
import com.neaterbits.util.io.strings.CharInput;

public class LexerTest {

	private Lexer<TestToken, CharInput> createLexer(CharInput data) {
	
		final Lexer<TestToken, CharInput> lexer = new Lexer<TestToken, CharInput>(
				data,
				TestToken.class,
				TestToken.NONE,
				TestToken.EOF,
				new TestToken [] { TestToken.WS },
				new TestToken [] { TestToken.C_COMMENT, TestToken.CPP_COMMENT });
		
		
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
	public void testParseSkipWSAndCComment() throws IOException {
		
		final StringBuffers buffer = new StringBuffers(new SimpleLoadStream(" /* xyz */zyx "));
		final Lexer<TestToken, CharInput> lexer = createLexer(buffer);
		
		assertThat(lexer.lexSkipWSAndComment(TestToken.KEYWORD_ZYX)).isEqualTo(TestToken.KEYWORD_ZYX);

		assertThat(lexer.get()).isEqualTo("zyx");
	}

    @Test
    public void testParseSkipWSAndCPPComment() throws IOException {

        final StringBuffers buffer = new StringBuffers(new SimpleLoadStream(" // xyz \nzyx "));
        final Lexer<TestToken, CharInput> lexer = createLexer(buffer);

        assertThat(lexer.lexSkipWSAndComment(TestToken.KEYWORD_ZYX)).isEqualTo(TestToken.KEYWORD_ZYX);

        assertThat(lexer.get()).isEqualTo("zyx");
    }

    @Test
    public void testParseLexNotFoundDoesNotChangeStreamEvenIfSameInitial() throws IOException {
        
        final StringBuffers buffer = new StringBuffers(new SimpleLoadStream("else  "));
        final Lexer<TestToken, CharInput> lexer = createLexer(buffer);

        assertThat(lexer.lexSkipWS(TestToken.KEYWORD_ELSE_IF)).isEqualTo(TestToken.NONE);

        assertThat(lexer.lexSkipWS(TestToken.KEYWORD_ELSE)).isEqualTo(TestToken.KEYWORD_ELSE);

        assertThat(lexer.get()).isEqualTo("else");
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

    @Test
    public void testParseFromToLiteralContents() throws IOException {
        
        final StringBuffers buffer = new StringBuffers(new SimpleLoadStream("/* xyz */"));
        final Lexer<TestToken, CharInput> lexer = createLexer(buffer);
        
        assertThat(lexer.lexSkipWS(TestToken.C_COMMENT)).isEqualTo(TestToken.C_COMMENT);

        assertThat(lexer.get()).isEqualTo("/* xyz */");
        assertThat(buffer.getString(lexer.getStringRef())).isEqualTo("/* xyz */");
    }

    @Test
    public void testParseFromToCharContents() throws IOException {
        
        final StringBuffers buffer = new StringBuffers(new SimpleLoadStream("\"xyz\""));
        final Lexer<TestToken, CharInput> lexer = createLexer(buffer);
        
        assertThat(lexer.lexSkipWS(TestToken.STRING_LITERAL)).isEqualTo(TestToken.STRING_LITERAL);

        assertThat(lexer.get()).isEqualTo("\"xyz\"");
        assertThat(buffer.getString(lexer.getStringRef())).isEqualTo("\"xyz\"");
    }
}
