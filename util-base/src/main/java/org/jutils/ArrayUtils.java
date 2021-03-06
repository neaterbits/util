package org.jutils;

import java.util.Arrays;
import java.util.Objects;

public class ArrayUtils {

	@SafeVarargs
	public static <T> T[] merge(T [] ... arrays) {
		
		if (arrays.length == 0) {
			throw new IllegalArgumentException("no arrays");
		}
		
		if (arrays.length == 1) {
			throw new IllegalArgumentException("only one array");
		}
		
		int totalLength = 0;
		
		for (T [] ar : arrays) {
			totalLength += ar.length;
		}
		
		final T [] ret = Arrays.copyOf(arrays[0], totalLength);
		
		int pos = arrays[0].length;
		
		for (int i = 1; i < arrays.length; ++ i) {
			
			final T [] src = arrays[i];
			
			System.arraycopy(src, 0, ret, pos, src.length);
			
			pos += src.length;
		}
		
		return ret;
	}
	
	public static <T> String toString(T [] array, int numElements) {
	    
	    return Arrays.toString(Arrays.copyOf(array, numElements));
	}


	public static <T> T [] subArray(T [] array, int startIdx) {
		return Arrays.copyOfRange(array, startIdx, array.length);
	}
	
	public static <T> boolean contains(T [] array, T object) {
		Objects.requireNonNull(array);
		
		for (int i = 0; i < array.length; ++ i) {
			if (Objects.equals(array[i], object)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static <T> boolean startsWith(T [] array, T [] start) {
	    
	    boolean startsWith;
	    
	    if (start.length > array.length) {
	        startsWith = false;
	    }
	    else {
	        startsWith = true;
	        
	        for (int i = 0; i < start.length; ++ i) {
	            if (!array[i].equals(start[i])) {
	                startsWith = false;
	                break;
	            }
	        }
	    }

	    return startsWith;
	}
}
