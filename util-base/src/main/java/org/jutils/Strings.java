package org.jutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Strings {

	private static final String EMPTY = "";

	public static String[] split(String s, char c) {
		final int length = s.length();

		final List<String> strings = new ArrayList<String>(100);

		int last = -1;

		for (int i = 0; i < length; ++i) {
			if (s.charAt(i) == c) {

				final String found;

				if (i == 0) {
					found = EMPTY;
				} else if (i - last == 1) {
					found = EMPTY;
				} else {
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

	public static boolean startsWithToFindLowerCase(String toSearch, String toFind) {

		boolean startsWith;

		final int toSearchLength = toSearch.length();
		final int toFindLength = toFind.length();

		if (toSearchLength < toFindLength) {
			startsWith = false;
		}
		else {
			startsWith = true;

			for (int i = 0; i < toFindLength; ++ i) {
				if (Character.toLowerCase(toSearch.charAt(i)) != toFind.charAt(i)) {
					startsWith = false;
					break;
				}
			}
		}

		return startsWith;
	}

	public static String join(String[] strings, char separator) {
		return join(strings, separator, strings.length);
	}

	public static String join(Collection<String> strings, char separator) {
		return join(strings, separator, strings.size());
	}

	public static <T> String join(Collection<T> strings, char separator, Function<T, String> map) {
		return join(strings, separator, strings.size(), map);
	}

	public static String join(Collection<String> strings, char separator, int count) {
		return Strings.join(strings.toArray(new String[strings.size()]), separator, count);
	}

	public static String join(String[] strings, char separator, int count) {
		return join(Arrays.asList(strings), separator, count, string -> string);
	}

	public static <T> String join(Collection<T> strings, char separator, int count, Function<T, String> map) {
		return join(strings, separator, 0, count, map);
	}

	public static <T> String join(Collection<T> strings, char separator, int start, int count, Function<T, String> map) {

		final StringBuilder sb = new StringBuilder();

		final Iterator<T> iter = strings.iterator();

		for (int i = 0; i < start; ++ i) {
			iter.next();
		}

		for (int i = 0; i < count; ++ i) {
			if (i > 0) {
				sb.append(separator);
			}

			sb.append(map.apply(iter.next()));
		}

		return sb.toString();
	}


	public static boolean startsWith(String [] strings, String [] parts) {
		return startsWith(Arrays.asList(strings), parts);
	}

	public static boolean startsWith(List<String> strings, String [] parts) {

	    return StringUtils.startsWith(strings, parts);
	}

	public static String replaceTextRange(String text, int start, int replaceLength, String toAdd) {

		return
				  text.substring(0, start)
				+ toAdd
				+ text.substring(start + replaceLength);
	}


	public static String [] lastOf(String [] parts, int num) {
		final String [] updated = new String[num];

		System.arraycopy(parts, parts.length - num, updated, 0, updated.length);

		return updated;
	}

	public static int countOccurencesOf(String s, String toFind) {

		int occurences = 0;
		int nextIndex = 0;

		if (!s.isEmpty()) {

			for (;;) {

				int index = s.indexOf(toFind, nextIndex);

				if (index < 0) {
					break;
				}

				++ occurences;

				nextIndex += index + toFind.length();

				if (nextIndex + toFind.length() > s.length()) {
					break;
				}
			}
		}

		return occurences;
	}

	public static final <T, V> void outputList(List<V> list, String separator, Function<V, String> convert, Consumer<String> append) {
		outputList(null, list, separator, convert, (state, string) -> append.accept(string));
	}

	public static final <T, V> void outputList(T state, List<V> list, String separator, Function<V, String> convert, BiConsumer<T, String> append) {

		for (int i = 0; i < list.size(); ++ i) {
			if (i > 0) {
				append.accept(state, separator);
			}

			append.accept(state, convert.apply(list.get(i)));
		}
	}

	public static String indent(int indent) {
		return concat("  ", indent);
	}

	public static String spaces(int times) {
		return concat(" ", times);
	}

	public static String concat(String string, int times) {
		final StringBuilder sb = new StringBuilder(string.length() * times);

		for (int i = 0; i < times; ++ i) {
			sb.append(string);
		}

		return sb.toString();
	}

	private static char hex(int hex) {

		final char c;

		switch (hex) {
		case 0: c = '0'; break;
		case 1: c = '1'; break;
		case 2: c = '2'; break;
		case 3: c = '3'; break;
		case 4: c = '4'; break;
		case 5: c = '5'; break;
		case 6: c = '6'; break;
		case 7: c = '7'; break;
		case 8: c = '8'; break;
		case 9: c = '9'; break;
		case 10: c = 'A'; break;
		case 11: c = 'B'; break;
		case 12: c = 'C'; break;
		case 13: c = 'D'; break;
		case 14: c = 'E'; break;
		case 15: c = 'F'; break;

		default:
			throw new UnsupportedOperationException("Unknown hex digit: " + hex);
		}

		return c;
	}

	public static String toHexString(int number, int chars, boolean pad) {
		final double maxNum = Math.pow(16, chars);

		if (number > maxNum) {
			throw new IllegalArgumentException("Not room for " + number + " in " + chars + " chars");
		}

		final StringBuilder sb = new StringBuilder(chars);

		do {
			final int digit = number % 16;

			number /= 16;

			sb.append(hex(digit));
		} while(number != 0);

		if (pad) {
			final int num = chars - sb.length();

			for (int i = 0; i < num; ++ i) {
				sb.append('0');
			}
		}

		return sb.reverse().toString();
	}
}
