package org.jutils.io.strings;

import java.math.BigDecimal;

import org.jutils.StringUtils;

public interface StringSource extends StringRef {

	String asString(long stringRef);

    /**
     * As a String counted as offset from the beginning of the stream
     *
     * @param startOffset index from beginning of stream
     * @param endOffset index from end of stream,
     *
     * @return the resulting {@link String}
     */
    String asStringFromOffset(int startOffset, int endOffset);
	
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

    default long asBinaryLong(long stringRef, int startPos, int endSkip) {
        
        final String string = asString(stringRef);

        return Long.parseLong(string, startPos, string.length() - endSkip, 2);
    }
    
    default String getStringWithoutSeparator(long stringRef, char separator) {
        
        return StringUtils.remove(asString(stringRef), separator);
    }

    default long asLongWithSeparator(long stringRef, int startPos, int endSkip, char separator) {
        
        final String string = getStringWithoutSeparator(stringRef, separator);

        return Long.parseLong(string, startPos, string.length() - endSkip, 10);
    }

    default long asHexLongWithSeparator(long stringRef, int startPos, int endSkip, char separator) {
        
        final String string = getStringWithoutSeparator(stringRef, separator);

        return Long.parseLong(string, startPos, string.length() - endSkip, 16);
    }

    default long asOctalLongWithSeparator(long stringRef, int startPos, int endSkip, char separator) {
        
        final String string = getStringWithoutSeparator(stringRef, separator);

        return Long.parseLong(string, startPos, string.length() - endSkip, 8);
    }

    default long asBinaryLongWithSeparator(long stringRef, int startPos, int endSkip, char separator) {
        
        final String string = getStringWithoutSeparator(stringRef, separator);

        return Long.parseLong(string, startPos, string.length() - endSkip, 2);
    }

    default BigDecimal asBigDecimal(long stringRef) {

        return new BigDecimal(asString(stringRef));
    }
}
