package com.neaterbits.util.compat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import com.neaterbits.util.compat.function.CBiConsumer;
import com.neaterbits.util.compat.function.CBiFunction;
import com.neaterbits.util.compat.function.CConsumer;
import com.neaterbits.util.compat.stream.CStream;

public class Coll {

	public static <T> Iterable<T> reverse(List<T> list, boolean readOnly) {
		return new ReverseIterable<T>(list, readOnly);
	}

	public static <T, S extends T> Collection<T> downConvert(Collection<S> coll) {
		final List<T> ret = new ArrayList<T>(coll.size()); 

		for (S s : coll) {
			ret.add(s);
		}
		
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static <T, S extends T> Iterable<T> downConvertToIterable(Collection<S> coll) {
		return ((Collection<T>)coll);
	}
	
	public static <T, S extends T> List<T> downConvertList(List<S> coll) { 
		final List<T> ret = new ArrayList<T>(coll.size()); 

		for (S s : coll) {
			ret.add(s);
		}
		
		return ret;
	}
	
	public static <T extends ForEachable<T>> void safeForEach(Collection<T> coll, CConsumer<T> consumer) {
		if (coll != null) {
			for (T widget : coll) {
				widget.forEach(consumer);
			}
		}
	}

	public static <S, T extends StackForeachable<T>> void safeForEachStack(Collection<? extends StackForeachable<T>> coll, CBiFunction<T, S, S> function, S s) {
		if (coll != null) {
			for (StackForeachable<T> widget : coll) {
				widget.forEachStack(function, s);
			}
		}
	}

	
	public static <T extends ForeachIteratorable<T>> void foreEachIterator(List<T> list, CBiConsumer<ListIterator<T>, T> consumer) {
		if (list != null) {
			ListIterator<T> iter = list.listIterator();
			
			while (iter.hasNext()) {
				final T widget = iter.next();
				
				consumer.accept(iter, widget);
				
				widget.forEachIterator(consumer);
			}
		}
	}

	public static <T> CStream<T> stream(Collection<T> coll) {
		return null;
	}
	
	public static <T> T [] arrayOfNonNulls(T ... args) {
		
		int nonNulls = 0;
		
		for (int i = 0; i < args.length; ++ i) {
			if (args[i] != null) {
				++ nonNulls;
			}
		}
		
		final T[] ret = Arrays.copyOf(args, nonNulls);

		int dst = 0;
		for (int i = 0; i < args.length; ++ i) {
			if (args[i] != null) {
				ret[dst ++] = args[i];
			}
		}
		
		return ret;
	}
}
