package com.neaterbits.util;

import java.io.File;

public class PathUtil {

	public static String removeDirectoryFromPath(File directory, File file) {
		
		String path = file.getPath().substring(directory.getPath().length());
	
		if (path.startsWith("/")) {
			path = path.substring(1);
		}

		return path;
	}
}
