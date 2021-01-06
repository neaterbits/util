package com.neaterbits.ide.util.swt;

import org.eclipse.jface.viewers.ITreeContentProvider;

public abstract class TreeContentAdapter implements ITreeContentProvider {

	@Override
	public void dispose() {
		
	}
	
	@Override
	public boolean hasChildren(Object element) {
		
		final Object [] elements = getElements(element);

		return elements != null && elements.length != 0;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return getElements(parentElement);
	}
}
