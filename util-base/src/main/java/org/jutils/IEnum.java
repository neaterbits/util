package org.jutils;

public interface IEnum {

	String getName();

	public static <E extends Enum<E> & IEnum> E asEnum(Class<E> enumClass, String s, boolean caseSensitive) {
		final E [] values = enumClass.getEnumConstants();
		
		for (E e : values) {
			if (caseSensitive) {
				if (e.getName().equals(s)) {
					return e;
				}
			}
			else {
				if (e.getName().equalsIgnoreCase(s)) {
					return e;
				}
			}
		}
		
		return null;
	}
	
}
