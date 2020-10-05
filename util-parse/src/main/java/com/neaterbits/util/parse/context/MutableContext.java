package com.neaterbits.util.parse.context;

public final class MutableContext implements Context {

    private int startOffset;
    private int endOffset;
    
    public MutableContext() {

    }
    
    public MutableContext(Context context) {
        this(context.getStartOffset(), context.getEndOffset());
    }

    public MutableContext(int startOffset, int endOffset) {

        init(startOffset, endOffset);
    }

    public void init(Context context) {
        init(context.getStartOffset(), context.getEndOffset());
    }

    public void init(int startOffset, int endOffset) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }
    
    @Override
    public final int getStartOffset() {
        return startOffset;
    }

    @Override
    public final int getEndOffset() {
        return endOffset;
    }
}
