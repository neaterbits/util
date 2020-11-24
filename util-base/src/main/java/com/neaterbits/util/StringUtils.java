package com.neaterbits.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class StringUtils {

	private static final String EMPTY = "";

	private static final String [] EMPTY_ARRAY = new String[0];

	public static boolean equals(String s1, String s2) {
		final boolean equals;

		if (s1 == null && s2 == null) {
			equals = true;
		}
		else if (s1 == null || s2 == null) {
			equals = false;
		}
		else {
			equals = s1.equals(s2);
		}

		return equals;
	}

	public static String trimToNull(String s) {

		final String trimmed;

		if (s == null) {
			trimmed = null;
		}
		else if (s.isEmpty()) {
			trimmed = null;
		}
		else if ( ! Character.isWhitespace(s.charAt(0)) && ! Character.isWhitespace(s.charAt(s.length() - 1)) ) {
			trimmed = s;
		}
		else {
			final String s2 = s.trim();

			trimmed = s2.isEmpty() ? null : s2;
		}

		return trimmed;
	}

	public static Integer asIntegerOrNull(String value) {
		Integer integer;

		final String trimmed = value.trim();

		if (trimmed.isEmpty()) {
			integer = null;
		}
		else {
			try {
				integer = Integer.parseInt(value);
			}
			catch (NumberFormatException ex) {
				integer = null;
			}
		}

		return integer;
	}

    public static String join(Collection<String> strings, char separator) {
        return StringUtils.join(strings.toArray(new String[strings.size()]), separator);
    }

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

    public static <T> String join(Collection<T> strings, char separator, Function<T, String> map) {
        return join(strings, separator, strings.size(), map);
    }

    public static String join(Collection<String> strings, char separator, int count) {
        return join(strings.toArray(new String[strings.size()]), separator, count);
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

    public static boolean startsWith(List<String> strings, String [] parts) {

        Objects.requireNonNull(strings);
        Objects.requireNonNull(parts);

        final boolean startsWith;

        if (parts.length > strings.size()) {
            startsWith = false;
        }
        else {

            boolean matches = true;

            for (int i = 0; i < parts.length; ++ i) {
                if (!parts[i].equals(strings.get(i))) {
                    matches = false;
                    break;
                }
            }

            startsWith = matches;
        }

        return startsWith;
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

	public static String capitalize(String s) {
	    return toUpperFirst(s);
	}

	public static String toUpperFirst(String s) {
		return changeFirst(s, Character::isUpperCase, Character::isLowerCase, Character::toUpperCase);
	}

	public static String toLowerFirst(String s) {
		return changeFirst(s, Character::isLowerCase, Character::isUpperCase, Character::toLowerCase);
	}
	
	@FunctionalInterface
	interface TestIsCase {
	    
	    boolean test(char c);
	}

	@FunctionalInterface
	interface ChangeCase {
	    
	    char change(char c);
	    
	}

	private static String changeFirst(
	        String string,
	        TestIsCase testIsCase,
	        TestIsCase testIsOppositeCase,
	        ChangeCase changeCase) {

	    final String result;
        
        if (string == null || string.isEmpty()) {
            result = string;
        }
        else {
            final char initial = string.charAt(0);
            
            if (testIsCase.test(initial)) {
                result = string;
            }
            else if (testIsOppositeCase.test(initial)) {
                
                final String upperCase = String.valueOf(changeCase.change(initial));
                
                if (string.length() == 1) {
                    result = upperCase;
                }
                else {
                    result = upperCase + string.substring(1);
                }
            }
            else {
                throw new IllegalArgumentException();
            }
        }

		return result;
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

		return remove(s, Character::isWhitespace);
	}

	@FunctionalInterface
	private interface CharTest {

	    boolean match(char c);
	}

	public static String remove(String s, char toRemove) {

	    return remove(s, c -> c == toRemove);
	}

	private static String remove(String s, CharTest matcher) {

        final int len = s.length();

        final StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; ++ i) {
            final char c = s.charAt(i);

            if (!matcher.match(c)) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

	public static boolean isHexDigit(char c) {

	    return
	       (c >= '0' && c <= '9')
        || (c >= 'a' && c <= 'f')
        || (c >= 'A' && c <= 'F');
	}

    public static boolean isOctalDigit(char c) {

        return c >= '0' && c <= '8';
    }
}
