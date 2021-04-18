package org.jutils.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;

public class SWTDialogs {

    public static void displayError(Control control, String title, Exception ex) {
        
        final MessageBox messageBox = new MessageBox(control.getShell(), SWT.NONE);
        
        messageBox.setText(title);
        
        messageBox.setMessage(ex.getMessage());
        
        messageBox.open();
    }
}
