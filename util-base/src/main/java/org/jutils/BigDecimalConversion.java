package org.jutils;

import java.math.BigDecimal;

// TODO take decimal comma in locale into account?
public class BigDecimalConversion {

	public static String fromBigDecimal(BigDecimal value) {
		return value.toPlainString();
	}

	public static BigDecimal fromString(String s) {
		return new BigDecimal(s);
	}
}
