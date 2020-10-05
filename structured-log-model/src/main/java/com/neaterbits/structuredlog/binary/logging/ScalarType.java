package com.neaterbits.structuredlog.binary.logging;

public enum ScalarType {

	BYTE(byte.class, Byte.class),
	SHORT(short.class, Short.class),
	INT(int.class, Integer.class),
	LONG(long.class, Long.class),
	FLOAT(float.class, Float.class),
	DOUBLE(double.class, Double.class),
	
	CHAR(char.class, Character.class),
	
	STRING(null, String.class);
	
	private final Class<?> primitiveType;
	private final Class<?> objectType;
	
	
	private ScalarType(Class<?> primitiveType, Class<?> objectType) {
		this.primitiveType = primitiveType;
		this.objectType = objectType;
	}

	public Class<?> getPrimitiveType() {
		return primitiveType;
	}

	public Class<?> getObjectType() {
		return objectType;
	}
}
