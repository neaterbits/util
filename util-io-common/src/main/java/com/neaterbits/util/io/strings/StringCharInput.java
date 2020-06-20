package com.neaterbits.util.io.strings;

import java.io.IOException;

// Simple CharInput that operates on a single String
public class StringCharInput implements CharInput {

	private final String string;
	private int pos;
	
	public StringCharInput(String string) {
		this.string = string;
		this.pos = 0;
	}

	@Override
	public int readNext() throws IOException {
		final int next;
		
		if (pos == string.length()) {
			next = -1;
		}
		else {
			 next = string.charAt(pos);
			 ++ pos;
		}

		return next;
	}

	
	@Override
	public long getReadPos() {
		return pos;
	}

	@Override
    public int getOffsetFromStart(long stringRef) {
        return (int)(pos >> 32);
    }

    @Override
	public long getStringRef(long startPos, long endPos, int startOffset, int endSkip) {
		
		if (startPos > endPos) {
			throw new IllegalArgumentException("startPos > endPos");
		}
		
		final long length = endPos - startPos - startOffset - endSkip;
		
		if (length < 0L) {
			throw new IllegalStateException("length < 0");
		}
		
		final long pos = startPos + startOffset;
		
		if (pos + length > this.pos) {
			throw new IllegalStateException("pos + length > this.pos");
		}

		return pos << 32 | length;
	}

	@Override
	public void rewindOneCharacter(int val) {
		if (pos == 0) {
			throw new IllegalStateException("already at start");
		}
		
		if (string.charAt(pos - 1) != (char)val) {
			throw new IllegalStateException("chars do not match");
		}
		
		-- pos;
	}
	
	@Override
	public void rewind(int numCharacters) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String peek(int num) throws IOException {
		return string.substring(pos, Math.min(string.length(), pos + num));
	}

	@Override
	public int peek() throws IOException {
		return pos >= string.length() ? -1 : string.charAt(pos + 1);
	}
}
