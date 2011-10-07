/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.utils;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jubula.tools.constants.SwtAUTHierarchyConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created Apr 16, 2008
 */
public final class DialogUtils {

    /** SWT modal constant */
    private static final int MODAL = 
        (SWT.PRIMARY_MODAL | SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL);

    /** private constructor */
    private DialogUtils() {
        // utility class
    }
    
    /**
     * Sets the technical widget name for the given dialog in the corresponding 
     * parent shell, if that shell exists and is modal.
     * Otherwise nothing is done.
     * NOTE: This method must be called after create() was called on the dialog.
     *       The widget name cannot be set in the dialog itself.
     * @param dialog the modal dialog for that the wigdet name has to be set in
     *               the corresponding parent shell
     */
    public static void setWidgetNameForModalDialog(Dialog dialog) {
        final Shell parentShell = dialog.getShell();
        
        if ((parentShell != null) && ((parentShell.getStyle() & MODAL) > 0)) {
            setWidgetName(parentShell, getShortClassName(dialog.getClass()));
        }
    }
    
    /**
     * Sets the technical widget name for the given widget. If the widget 
     * already has a technical name, it will be overwritten. 
     * 
     * @param widget The widget for which to set the name. If <code>null</code>
     *               or disposed, no name will be set.
     * @param name The technical name to use.
     */
    public static void setWidgetName(Widget widget, String name) {
        if (widget != null && !widget.isDisposed()) {
            widget.setData(
                    SwtAUTHierarchyConstants.WIDGET_NAME, name);
        }
    }

    /**
     * Returns the short class name (that is everything after the last '.').
     * @param classObj the class for that the short name has to be returned
     * @return the short class name
     */
    private static String getShortClassName(Class classObj) {
        final String longClassName = String.valueOf(classObj);
        final int indexOfLastDot = longClassName.lastIndexOf('.');
        final String shortClassName;
        
        if (indexOfLastDot >= 0) {
            shortClassName = longClassName.substring(indexOfLastDot + 1);
        } else {
            shortClassName = longClassName;
        }
        
        return shortClassName;
    }
    
    /**
     * @param shell
     *            the shell which size to adjust
     * @param relWidth
     *            the relative width factor; must be 0 <= relWidth <= 1
     * @param relHeight
     *            the relative height factor; must be 0 <= relWidth <= 1
     */
    public static void adjustShellSizeRelativeToClientSize(Shell shell,
            float relWidth, float relHeight) {
        adjustShellSizeRelativeToRectangleSize(shell, relWidth, relHeight,
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
                    .getBounds());
    }
    
    /**
     * @param shell
     *            the shell which size to adjust
     * @param relWidth
     *            the relative width factor; must be 0 <= relWidth <= 1
     * @param relHeight
     *            the relative height factor; must be 0 <= relWidth <= 1
     */
    public static void adjustShellSizeRelativeToDisplaySize(Shell shell,
            float relWidth, float relHeight) {
        Display display = Display.getCurrent();
        Rectangle r = display.getClientArea();
        adjustShellSizeRelativeToRectangleSize(shell, relWidth, relHeight, r);
    }
    
    /**
     * @param shell
     *            the shell which size to adjust
     * @param relWidth
     *            the relative width factor; must be 0 <= relWidth <= 1
     * @param relHeight
     *            the relative height factor; must be 0 <= relWidth <= 1
     * @param relativeTo
     *            the rectangle the size should be computed relative to
     */
    private static void adjustShellSizeRelativeToRectangleSize(Shell shell,
            float relWidth, float relHeight, Rectangle relativeTo) {
        int dWidth = relativeTo.width;
        int dHeight = relativeTo.height;

        int newShellWidth = Math.round(relWidth * dWidth);
        int newShellHeight = Math.round(relHeight * dHeight);

        shell.setSize(newShellWidth, newShellHeight);
        shell.setLocation(((dWidth - shell.getSize().x) / 2) + relativeTo.x,
                ((dHeight - shell.getSize().y) / 2) + relativeTo.y);
    }
}
