package org.jutils;

import java.io.File;

public class PathUtil {

	public static String removeDirectoryFromPath(File directory, File file) {
		
		String path = file.getPath().substring(directory.getPath().length());
	
		if (path.startsWith(File.separator)) {
			path = path.substring(1);
		}

		return path;
	}
}
