package org.jutils.parse.context;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

public interface Context {

	int getStartOffset();
	
	int getEndOffset();
	
	Context makeImmutable();
	
	default int getLength() {
	    return getEndOffset() - getStartOffset() + 1;
	}

    public static <T> Context merge(Collection<T> elements, Function<T, Context> getContext) {
        
        if (elements.size() <= 1) {
            throw new IllegalArgumentException();
        }
        
        Context lower = null;
        Context upper = null;
        
        final Iterator<T> iter = elements.iterator();
        
        while (iter.hasNext()) {
            final Context context = getContext.apply(iter.next());
        
            if (lower == null) {
                lower = context;
            }
            
            upper = context;
        }
        
        final Context result;
        
        if (lower != null && upper != null) {
            
            /* TODO add this back
            if (lower.getEndOffset() > upper.getStartOffset()) {
                throw new IllegalArgumentException();
            }
            
            if (lower.getStartOffset() > upper.getStartOffset()) {
                throw new IllegalArgumentException();
            }
            */
            
            result = new ImmutableContext(lower.getStartOffset(), upper.getEndOffset());
        }
        else {
            result = null;
        }

        return result;
    }

}
