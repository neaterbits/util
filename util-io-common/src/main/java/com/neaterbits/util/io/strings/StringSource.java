package com.neaterbits.util.io.strings;

import java.math.BigDecimal;

public interface StringSource extends StringRef {

	String asString(long stringRef);

    default Integer asInteger(long stringRef) {

        return Integer.parseInt(asString(stringRef));
    }
    
    default int asInt(long stringRef) {
        
        return Integer.parseInt(asString(stringRef));
    }

    default long asLong(long stringRef) {
        
        return Long.parseLong(asString(stringRef));
    }

    default long asLong(long stringRef, int startPos, int endSkip) {
        
        final String string = asString(stringRef);

        return Long.parseLong(string, startPos, string.length() - endSkip, 10);
    }

    default long asHexLong(long stringRef, int startPos, int endSkip) {
        
        final String string = asString(stringRef);

        return Long.parseLong(string, startPos, string.length() - endSkip, 16);
    }

    default long asOctalLong(long stringRef, int startPos, int endSkip) {
        
        final String string = asString(stringRef);

        return Long.parseLong(string, startPos, string.length() - endSkip, 8);
    }

    default BigDecimal asBigDecimal(long stringRef) {

        return new BigDecimal(asString(stringRef));
    }
}
