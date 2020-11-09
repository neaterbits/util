package com.neaterbits.util;

import java.io.File;
import java.util.Objects;
import java.util.function.Consumer;

public class Files {

    public static void deleteRecursively(File directory) {

        if (!directory.exists()) {
            throw new IllegalArgumentException("No such directory " + directory);
        }
        
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Not a directory " + directory);
        }

        for (File sub : directory.listFiles()) {
            
            if (sub.isDirectory()) {
                deleteRecursively(sub);
            }
        }

        directory.delete();
    }

	public static void recurseDirectories(File directory, Consumer<File> file) {
		
		Objects.requireNonNull(directory);
		Objects.requireNonNull(file);
		
		if (!directory.exists()) {
			throw new IllegalArgumentException("No such directory " + directory);
		}
		
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("Not a directory " + directory);
		}
		
		recurseDirectoriesSub(directory, file);
	}

	private static void recurseDirectoriesSub(File directory, Consumer<File> file) {

		for (File sub : directory.listFiles()) {
			if (sub.isFile()) {
				file.accept(sub);
			}
			else if (sub.isDirectory()) {
				recurseDirectories(sub, file);
			}
		}
	}
}
