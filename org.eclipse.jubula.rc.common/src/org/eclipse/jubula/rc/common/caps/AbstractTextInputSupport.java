/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.caps;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;

/**
 * This class represents the general implmentation for components,
 *  which have text input support.
 * 
 * @author BREDEX GmbH
 */
public abstract class AbstractTextInputSupport extends AbstractTextVerifiable {

    /**
     * Verifies the editable property of the current component.<br>
     * If it is a complex component, it is always the selected object.
     * @param editable The editable property to verify.
     */
    public abstract void gdVerifyEditable(boolean editable);
 // FIXME this is only a pattern because only Tables uses this at the moment
    /**
     * Types <code>text</code> into the component. This replaces the shown
     * content.<br>
     * If it is a complex component, it is always the selected object.
     * @param text the text to type in
     * @throws StepExecutionException
     *  If there is no selected cell, or if the cell is not editable,
     *  or if the table cell editor permits the text to be written.
     */
    public abstract void gdReplaceText(String text) 
        throws StepExecutionException;
 // FIXME this is only a pattern because only Tables uses this at the moment
    /**
     * Writes the passed text into the currently component.<br>
     * If it is a complex component, it is always the selected object.
     * @param text The text.
     * @throws StepExecutionException
     *             If there is no selected cell, or if the cell is not editable,
     *             or if the table cell editor permits the text to be written.
     */
    public abstract void gdInputText(final String text) 
        throws StepExecutionException;
 // FIXME this is only a pattern because only Tables uses this at the moment
}
