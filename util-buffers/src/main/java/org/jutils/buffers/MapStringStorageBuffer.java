package org.jutils.buffers;

import java.util.HashMap;
import java.util.Map;

/**
 * Buffer for keeping Strings that used often, eg element id or class.
 * May use some time for building, most of time is spent reading, but for most
 * element processing, the generated integer IDs will be utilized so no need to access the strings themselves.
 *  
 * References are start/length, encoded as integers
 *  - can retrieve original String
 *  - can use integers for lookup
 *  - only a single buffer
 *  - finds already added strings and during addition and returns same ID
 *  
 *   
 *  Only add strings since faster
 *  
 * @author nhl
 *
 */

public class MapStringStorageBuffer extends BaseStringStorageBuffer {

	// Detect repeated strings and return same value
	private final Map<String, Integer> map;
	
	public MapStringStorageBuffer() {
		this.map = new HashMap<String, Integer>();
	}
	
	@Override
	public int add(String s) {
		
		if (s == null) {
			throw new IllegalArgumentException("s == 0");
		}
		
		final Integer idx = map.get(s);
		final int ret;
		
		if (idx == null) {
			ret = super.add(s);
		}
		else {
			ret = idx;
		}
		
		return ret;
	}
	
	public int add(char [] chars, int offset, int length) {
		return add(new String(chars, offset, length));
	}
}
