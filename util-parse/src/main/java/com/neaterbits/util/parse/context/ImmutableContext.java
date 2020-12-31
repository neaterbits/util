package com.neaterbits.util.parse.context;

public class ImmutableContext implements Context {

    private final int startOffset;
    private final int endOffset;
    
    public ImmutableContext(int startOffset, int endOffset) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }
    
    public ImmutableContext(Context context) {
        this(context.getStartOffset(), context.getEndOffset());
    }
    
    @Override
    public final int getStartOffset() {
        return startOffset;
    }

    @Override
    public final int getEndOffset() {
        return endOffset;
    }

    @Override
    public final Context makeImmutable() {
        return this;
    }

    @Override
    public String toString() {
        return "ImmutableContext [startOffset=" + startOffset + ", endOffset=" + endOffset + "]";
    }
}
