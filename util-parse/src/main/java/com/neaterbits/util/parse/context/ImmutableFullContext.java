package com.neaterbits.util.parse.context;

public final class ImmutableFullContext
        extends ImmutableContext
        implements FullContext {

	private final String file;
	private final int startLine;
	private final int startPosInLine;
	private final int endLine;
	private final int endPosInLine;
	private final String text;
	
	public ImmutableFullContext(String file, int startLine, int startPosInLine, int startOffset, int endLine, int endPos, int endOffset, String text) {
	    super(startOffset, endOffset);
		
		this.file = file;
		this.startLine = startLine;
		this.startPosInLine = startPosInLine;
		this.endLine = endLine;
		this.endPosInLine = endPos;
		this.text = text;
	}
	
	public ImmutableFullContext(FullContext context) {
	    this(
	            context.getFile(),
	            context.getStartLine(),
	            context.getStartPosInLine(),
	            context.getStartOffset(),
	            context.getEndLine(),
	            context.getEndPosInLine(),
	            context.getEndOffset(),
	            context.getText());
	}

	@Override
	public String getFile() {
		return file;
	}

	@Override
	public int getStartLine() {
		return startLine;
	}

	@Override
	public int getStartPosInLine() {
		return startPosInLine;
	}
	
	@Override
	public int getEndLine() {
		return endLine;
	}

	@Override
	public int getEndPosInLine() {
		return endPosInLine;
	}
	
	@Override
	public int getLength() {
		return getEndOffset() - getStartOffset() + 1;
	}
	
	@Override
	public String getText() {
		return text;
	}

    @Override
	public String toString() {
		return "ImmutableContext [file=" + file + ", startLine=" + startLine + ", startPosInLine=" + startPosInLine + ", endLine=" + endLine + ", endPosInLine=" + endPosInLine + "]";
	}
}
