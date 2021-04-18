package org.jutils.io.loadstream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

public class SimpleLoadStream extends LoadStream {
	private final Reader reader;
	private boolean isBlocked;
	
	public SimpleLoadStream(String data) {
		this.reader = new StringReader(data);
	}
	
	public SimpleLoadStream(byte [] data) {
		this.reader = new InputStreamReader(new ByteArrayInputStream(data));
	}
	
	public SimpleLoadStream(InputStream inputStream) {
		this.reader = new InputStreamReader(inputStream);
	}

	public boolean isBlocked() {
		return isBlocked;
	}

	public void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	@Override
	public long read(char[] buffer, int offset, int length) throws IOException {

		final int charsRead = reader.read(buffer, offset, length);
		
		final long status;
		
		if (charsRead < 0) {
			status = StreamStatus.eof();
		}
		else {
			status = StreamStatus.of(charsRead, isBlocked);
		}
		
		return status;
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}
}
