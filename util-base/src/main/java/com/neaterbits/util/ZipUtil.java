package com.neaterbits.util;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {

	public static void listZip(ZipInputStream zipStream) throws IOException {
    	for (;;) {
    		final ZipEntry ze = zipStream.getNextEntry();
    		
    		if (ze == null) {
    			break;
    		}
    		
    		System.out.println("## ze: " + ze.getName() + "/" + ze.getSize());
    		
    		final byte [] buf = new byte[1000];
    		
    		while (0 <= zipStream.read(buf)) {
    			;
    		}

    		zipStream.closeEntry();
    	}
    }
}
