package org.jutils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class IOUtils {

    public static void applyAll(InputStream inputStream, OutputStream outputStream) throws IOException {
        
        final byte [] tmp = new byte[10000];
        
        for (;;) {
            int read = inputStream.read(tmp, 0, tmp.length);
            
            if (read >= 0) {
                outputStream.write(tmp, 0, read);
            }
            else {
                break;
            }
        }
    }

    public static byte [] readAll(File file) throws IOException {
        
        final byte [] data;
        
        try (FileInputStream inputStream = new FileInputStream(file)) {
            data = readAll(inputStream);
        }
        
        return data;
    }

	public static byte [] readAll(InputStream inputStream) throws IOException {

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
		applyAll(inputStream, baos);
	
		return baos.toByteArray();
	}

	public static byte [] readAllAndClose(InputStream inputStream) throws IOException {

		final byte [] ret;
		
		try {
			ret = readAll(inputStream);
		}
		finally {
			inputStream.close();
		}

		return ret;
	}

    public static void write(File file, InputStream inputStream) throws IOException {

        try (FileOutputStream outputStream = new FileOutputStream(file)) {

            applyAll(inputStream, outputStream);
        }
    }

    public static void write(File file, String data) throws IOException {
        
        write(file, data, Charset.forName("UTF-8"));
    }
	
	public static void write(File file, String data, Charset charset) throws IOException {
	    
	    write(file, data.getBytes(charset));
	}

	public static void write(File file, byte [] data) throws IOException {
        
	    try (FileOutputStream outputStream = new FileOutputStream(file)) {
	        outputStream.write(data);
	    }
    }
}
