package org.jutils.parse.context;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

public interface FullContext extends Context {

    public static Context makeTestContext() {
        return new ImmutableFullContext("", 0, 0, 0, 0, 0, 0, "");
    }

    public static <T> FullContext merge(Collection<T> elements, Function<T, FullContext> getContext) {
        
        if (elements.size() <= 1) {
            throw new IllegalArgumentException();
        }
        
        FullContext lower = null;
        FullContext upper = null;
        
        final StringBuilder sb = new StringBuilder();
        
        final Iterator<T> iter = elements.iterator();
        
        while (iter.hasNext()) {
            final FullContext context = getContext.apply(iter.next());
        
            if (lower == null) {
                lower = context;
            }
            else {
                sb.append(' ');
                
                if (!lower.getFile().equals(context.getFile())) {
                    throw new IllegalArgumentException();
                }
            }
            
            upper = context;

            if (context != null) {
                sb.append(context.getText());
            }
        }
        
        final FullContext result;
        
        if (lower != null && upper != null) {
            
            /* TODO add this back
            if (lower.getEndOffset() > upper.getStartOffset()) {
                throw new IllegalArgumentException();
            }
            */
            
            result = new ImmutableFullContext(
                    lower.getFile(),
                    lower.getStartLine(), lower.getStartPosInLine(), lower.getStartOffset(), 
                    upper.getEndLine(), upper.getEndPosInLine(), upper.getEndOffset(),
                    sb.toString());
        }
        else {
            result = null;
        }

        return result;
    }
    
    String getFile();
    
    int getStartLine();
    
    int getStartPosInLine();

    int getEndLine();
    
    int getEndPosInLine();

    int getLength();
    
    String getText();
}
