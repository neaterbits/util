package com.neaterbits.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.neaterbits.util.compat.function.CFunction;

public class StringUtils {
	
	private static final String EMPTY = "";

	private static final String [] EMPTY_ARRAY = new String[0];

	public static String join(String [] strings, char c) {
		final StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < strings.length; i ++) {
			if (i > 0) {
				sb.append(c);
			}

			sb.append(strings[i]);
		}

		return sb.toString();
	}
	
	public static String [] trimHead(String [] strings) {
		final int firstNonEmpty = firstNonEmpty(strings);
		
		return firstNonEmpty != -1 ? Arrays.copyOfRange(strings, firstNonEmpty, strings.length) : EMPTY_ARRAY;
	}
	
	public static String [] trimTail(String [] strings) {
		final int lastNonEmpty = lastNonEmpty(strings);
		
		return lastNonEmpty != -1 ? Arrays.copyOfRange(strings, 0, lastNonEmpty + 1) : EMPTY_ARRAY;
	}

	private static int firstNonEmpty(String [] strings) {
		for (int i = 0; i < strings.length; ++ i) {
			if (strings[i] != null && !strings[i].isEmpty()) {
				return i;
			}
		}

		return -1;
	}

	private static int lastNonEmpty(String [] strings) {
		for (int i = strings.length - 1; i >= 0; -- i) {
			if (strings[i] != null && !strings[i].isEmpty()) {
				return i;
			}
		}
		
		return -1;
	}

	public static String [] trim(String [] strings) {

		final int firstNonEmpty = firstNonEmpty(strings);
		final int lastNonEmpty = lastNonEmpty(strings);

		return Arrays.copyOfRange(strings,
				firstNonEmpty != -1 ? firstNonEmpty : 0,
				lastNonEmpty != -1 ? lastNonEmpty + 1 : strings.length);
	}
	
	
	public static String [] split(String s, char c) {
		final int length = s.length();

		final List<String> strings = new ArrayList<String>(100);

		int last = -1;
		
		for (int i = 0; i < length; ++ i) {
			if (s.charAt(i) == c) {
				
				final String found;

				if (i == 0) {
					found = EMPTY;
				}
				else if (i - last == 1) {
					found = EMPTY;
				}
				else {
					found = s.substring(last + 1, i);
				}

				last = i;

				if (found == null) {
					throw new IllegalStateException("should have found entry");
				}
				strings.add(found);
			}
		}

		strings.add(s.substring(last + 1, length));

		return strings.toArray(new String[strings.size()]);
	}

	private static final CFunction<Character, Character> toUpper = new CFunction<Character, Character>() {

		@Override
		public Character apply(Character t) {
			return Character.toUpperCase(t);
		}
	};
	
	private static final CFunction<Character, Character> toLower = new CFunction<Character, Character>() {

		@Override
		public Character apply(Character t) {
			return Character.toLowerCase(t);
		}
	};

	public static String toUpperFirst(String s) {
		return changeFirst(s, toUpper);
	}
	
	public static String toLowerFirst(String s) {
		return changeFirst(s, toLower);
	}

	private static String changeFirst(String s, CFunction<Character, Character> func) {
		String ret;
		
		if (s == null || s.isEmpty()) {
			ret = s;
		}
		else  {
			ret = "" + func.apply(s.charAt(0));

			if (s.length() > 1) {
				ret += s.substring(1, s.length());
			}
		}

		return ret;
	}

	public static boolean isBlank(String s) {
		return s == null || s.isEmpty() || s.trim().isEmpty();
	}
	
	public static boolean isAlphaNumeric(String s) {

		final int len = s.length();
		
		for (int i = 0; i < len; ++ i) {
			
			final int codePoint = s.codePointAt(i);
			
			if (!Character.isAlphabetic(codePoint) && !Character.isDigit(codePoint)) {
				return false;
			}
		}
		
		return true;
	}

	public static String removeBlanks(String s) {

		final int len = s.length();
		
		final StringBuilder sb = new StringBuilder(len);
		
		for (int i = 0; i < len; ++ i) {
			final char c = s.charAt(i);

			if (!Character.isWhitespace(c)) {
				sb.append(c);
			}
		}

		return sb.toString();
	}
	
}
