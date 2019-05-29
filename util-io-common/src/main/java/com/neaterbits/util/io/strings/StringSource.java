package com.neaterbits.util.io.strings;

import java.math.BigDecimal;

public interface StringSource extends StringRef {

	String asString(long stringRef);

	Integer asInteger(long stringRef);

	// int asDecimalSize(long stringRef);

	BigDecimal asBigDecimal(long stringRef);

}
