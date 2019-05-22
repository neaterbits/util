package com.neaterbits.util.parse;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

public class TokenMergeHelper {
	
	public static <TOKEN extends Enum<TOKEN> & IToken> boolean has(TOKEN [] tokens, TOKEN token) {
		for (TOKEN t : tokens) {
			if (t == token) {
				return true;
			}
		}
		
		return false;
	}
	
	@SafeVarargs
	public static <TOKEN> TOKEN [] merge(TOKEN token, TOKEN ... otherTokens) {
		final TOKEN [] copy = Arrays.copyOf(otherTokens, otherTokens.length + 1);

		// token first since is most likely to find?
		copy[otherTokens.length] = token;
		
		return copy;
	}

	@SafeVarargs
	public static <TOKEN> TOKEN [] merge(TOKEN [] tokens, TOKEN ... otherTokens) {
		final TOKEN [] copy = Arrays.copyOf(tokens, tokens.length + otherTokens.length);

		// token first since is most likely to find?
		System.arraycopy(otherTokens, 0, copy, tokens.length, otherTokens.length);

		return copy;
	}

	@SafeVarargs
	public static <TOKEN> TOKEN [] merge(Collection<TOKEN []> tokens, TOKEN ... otherTokens) {

		int num = 0;
		
		for (TOKEN [] tokArray : tokens) {
			num += tokArray.length;
		}
		
		if (num == 0) {
			throw new IllegalStateException("no tokens");
		}
		
		num += otherTokens.length;
		
		// Just make copy of intial array
		final TOKEN[] copy = Arrays.copyOf(tokens.iterator().next(), num);
		
		int dstIdx = 0;
		
		for (TOKEN [] tokArray : tokens) {
			for (TOKEN t : tokArray) {
				copy[dstIdx ++] = t;
			}
		}
		
		for (TOKEN t : otherTokens) {
			copy[dstIdx ++] = t;
		}
		
		return copy;
	}


	@SafeVarargs
	public static <TOKEN extends Enum<TOKEN>& IToken> TOKEN [] copyTokens(Class<TOKEN> tokenClass, Predicate<TOKEN> test, TOKEN ... extra) {
		
		final TOKEN [] copy;
		
		int numTokens = 0;
		
		final TOKEN [] enumConstants = tokenClass.getEnumConstants();
		
		for (TOKEN token : enumConstants) {
			
			if (test.test(token)) {
				++ numTokens;
			}
		}
		
		numTokens += extra.length;
		
		copy = createTokenArray(tokenClass, numTokens);
		
		int idx = 0;
		for (TOKEN token : enumConstants) {
			if (test.test(token)) {
				copy[idx ++] = token;
			}
		}

		// May be WS or bracket end as well
		for (int i = 0; i < extra.length; ++ i) {
			copy[idx ++] = extra[i];
		}
		
		return copy;
	}
	
	@SuppressWarnings("unchecked")
	private static <TOKEN extends Enum<TOKEN>& IToken> TOKEN [] createTokenArray(Class<TOKEN> tokenClass, int numTokens) {
		return (TOKEN[])Array.newInstance(tokenClass, numTokens);
	}
}
