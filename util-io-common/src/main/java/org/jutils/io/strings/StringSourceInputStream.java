package org.jutils.io.strings;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;

import org.jutils.IOUtils;

public class StringSourceInputStream extends InputStream implements StringSource {

	private final byte [] data;
	private final Charset charset;
	
	private int offset;

	public static StringSourceInputStream fromString(String string) {
		
		final Charset defaultCharset = Charset.defaultCharset();
		
		try {
			return new StringSourceInputStream(new ByteArrayInputStream(string.getBytes(defaultCharset)), defaultCharset);
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	public StringSourceInputStream(InputStream delegate, Charset charset) throws IOException {

		Objects.requireNonNull(charset);
		
		this.data = IOUtils.readAll(delegate);
		this.charset = charset;
	}

	@Override
	public int read() throws IOException {
		final int read;
		
		if (offset == data.length) {
			read = -1;
		}
		else {
			read = data[offset ++];
		}
		
		return read;
	}

	@Override
	public String asString(long stringRef) {
		return new String(
				data,
				OffsetLengthStringRef.decodeOffset(stringRef),
				OffsetLengthStringRef.decodeLength(stringRef),
				charset);
	}

    @Override
    public String asStringFromOffset(int startOffset, int endOffset) {
        return new String(data, startOffset, endOffset - startOffset + 1, charset);
    }
}
