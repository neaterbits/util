package org.jutils;

public class Indent {
	
	private static final String INDENT = "  ";
	
	public static final String indent(int indent) {
		
		final StringBuilder sb = new StringBuilder(INDENT.length() * indent);
		
		for (int i = 0; i < indent; ++ i) {
			sb.append(INDENT);
		}
		
		return sb.toString();
	}
	
}
