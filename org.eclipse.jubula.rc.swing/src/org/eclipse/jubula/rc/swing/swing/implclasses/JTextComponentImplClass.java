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
package org.eclipse.jubula.rc.swing.swing.implclasses;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.common.implclasses.Verifier;
import org.eclipse.jubula.rc.swing.swing.interfaces.IJTextComponentImplClass;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;


/**
 * Implementation class for <code>JTextComponent</code> and subclasses.
 *
 * @author BREDEX GmbH
 * @created 05.08.2005
 */
public class JTextComponentImplClass extends AbstractSwingImplClass 
    implements IJTextComponentImplClass {
    /**
     * The <code>JTextComponent</code> instance.
     */
    private JTextComponent m_textComponent;

    /**
     * @return The <code>JTextComponent</code> instance.
     */
    protected JTextComponent getTextComponent() {
        return (JTextComponent)getComponent();
    }
    
    /**
     * {@inheritDoc}
     */
    public JComponent getComponent() {
        return m_textComponent;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        m_textComponent = (JTextComponent)graphicsComponent;
    }


    /**
     * Sets the caret at the position <code>index</code>.
     *
     * @param index The caret position
     */
    private void setCaretPosition(final int index) {
        if (index < 0) {
            throw new StepExecutionException("Invalid position: " + index, //$NON-NLS-1$
                EventFactory.createActionError());
        }
        getEventThreadQueuer().invokeAndWait("setCaretPosition", //$NON-NLS-1$
            new IRunnable() {
                public Object run() {
                    int index2 = 0;
                    String text = getTextComponent().getText();
                    if (text != null) {
                        index2 = index > text.length() ? text.length() : index;
                    }
                    getTextComponent().setCaretPosition(index2);
                    return null;
                }
            });
    }


    /**
     * @see org.eclipse.jubula.rc.swing.swing.implclasses.AbstractSwingImplClass#getText()
     * @return {@link JTextComponent#getText()} value
     */
    protected String getText() {
        String actual = (String)getEventThreadQueuer().invokeAndWait(
            "getText", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    return getTextComponent().getText();
                }
            });
        return actual;
    }

    /**
     * Action to read the value of a JTextComponent to store it in a variable
     * in the Client
     * @param variable the name of the variable
     * @return the text value.
     */
    public String gdReadValue(String variable) {
        return getText();
    }

    /**
     * @param text The text to insert at the current caret position
     */
    private void insertText(String text) {
        // Scroll it to visible first to ensure that the typing
        // performs correctly.
        getRobot().scrollToVisible(getTextComponent(), null);
        getRobot().type(getTextComponent(), text);
    }

    /**
     * Verifies if the textfield shows the passed text.
     *
     * @param text The text to verify.
     * @param operator The operator used to verify
     */
    public void gdVerifyText(String text, String operator) {
        Verifier.match(getText(), text, operator);
    }
    /**
     * Verifies if the textfield shows the passed text.
     *
     * @param text The text to verify.
     */
    public void gdVerifyText(String text) {
        gdVerifyText(text, MatchUtil.DEFAULT_OPERATOR);
    }
    /**
     * Types <code>text</code> into the component. This replaces the shown
     * content.
     *
     * @param text the text to type in
     */
    public void gdReplaceText(String text) {
        gdSelect();
        if (StringUtils.EMPTY.equals(text)) {
            getRobot().keyStroke("DELETE"); //$NON-NLS-1$
        }
        insertText(text);
    }

    /**
     * Types <code>text</code> into the component.
     *
     * @param text the text to type in
     */
    public void gdInputText(String text) {
        if (!hasFocus()) {
            gdClick(1);
        }
        insertText(text);
    }


    /**
     * Inserts <code>text</code> at the position <code>index</code>.
     *
     * @param text The text to insert
     * @param index The position for insertion
     */
    public void gdInsertText(String text, int index) {
        gdClick(1);
        setCaretPosition(index);
        insertText(text);
    }
    /**
     * Inserts <code>text</code> before or after the first appearance of
     * <code>pattern</code>.
     *
     * @param text The text to insert
     * @param pattern The pattern to find the position for insertion
     * @param operator Operator to select Matching Algorithm
     * @param after
     *            If <code>true</code>, the text will be inserted after the
     *            pattern, otherwise before the pattern.
     * @throws StepExecutionException
     *             If the pattern is invalid or cannot be found
     */
    public void gdInsertText(String text, String pattern,
            String operator, boolean after)
        throws StepExecutionException {

        if (text == null) {
            throw new StepExecutionException(
                "The text to be inserted must not be null", EventFactory //$NON-NLS-1$
                    .createActionError());
        }
        final MatchUtil.FindResult matchedText = MatchUtil.getInstance().
            find(getText(), pattern, operator);
        if ((matchedText == null) || (matchedText.getStr() == null)
                || (matchedText.getStr().length() == 0)) {
            throw new StepExecutionException("The pattern '" + pattern //$NON-NLS-1$
                    + "' could not be found", //$NON-NLS-1$
                    EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
        }
        final int index = matchedText.getPos();
        int insertPos = after ? index + matchedText.getStr().length() : index;
        gdInsertText(text, insertPos);
    }
    /**
     * Verifies the editable property.
     *
     * @param editable The editable property to verify.
     */
    public void gdVerifyEditable(boolean editable) {
        verify(editable, "isEditable", new IRunnable() { //$NON-NLS-1$
            public Object run() {
                return getTextComponent().isEditable() 
                    && getTextComponent().isEnabled()
                        ? Boolean.TRUE : Boolean.FALSE; // see findBugs
            }
        });
    }

    /**
     * select the whole text of the textfield by pressing ctrl + A; in case this
     * won't work the whole text is selected programmatically
     */
    public void gdSelect() {
        gdClick(1);
        getRobot().keyStroke(getRobot().getSystemModifierSpec() + " A"); //$NON-NLS-1$

        if (!getText().equals(getSelectionText())) {
            getEventThreadQueuer().invokeAndWait(
                    "selectAll", new IRunnable() { //$NON-NLS-1$
                        public Object run() {
                            getTextComponent().selectAll();
                            return null;
                        }
                    });
        }
    }
    
    /**
     * 
     * @return the selectes text
     */
    protected String getSelectionText() {
        String actual = (String)getEventThreadQueuer().invokeAndWait(
            "getSelectionText", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    return getTextComponent().getSelectedText();
                }
            });
        return actual;
    }
    
    /**
     * Selects the first (not)appearance of <code>pattern</code> in the text
     * component's content.
     *
     * @param pattern The pattern to select
     * @param operator operator
     * @throws StepExecutionException
     *             If the pattern is invalid or cannot be found
     */
    public void gdSelect(final String pattern, String operator)
        throws StepExecutionException {
        gdClick(1);
        final MatchUtil.FindResult matchedText = MatchUtil.getInstance().
            find(getText(), pattern, operator);
        if ((matchedText == null) || (matchedText.getStr().length() == 0)) {
            throw new StepExecutionException("Invalid pattern for insertion", //$NON-NLS-1$
                EventFactory.createActionError());
        }
        final int index = matchedText.getPos();
        if (operator.startsWith("not")) { //$NON-NLS-1$
            getEventThreadQueuer().invokeAndWait("setSelection", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        if (pattern.equals(getText())) {
                            String msg = "The pattern '" + pattern //$NON-NLS-1$
                                + "' is equal to current text"; //$NON-NLS-1$
                            throw new StepExecutionException(msg, EventFactory
                                .createActionError(TestErrorEvent
                                    .EXECUTION_ERROR, new String[] {msg}));
                        } else if (index > 0) {
                            // select part before pattern
                            getTextComponent().setSelectionStart(0);
                            getTextComponent().setSelectionEnd(index);
                        } else {
                            // select part after pattern
                            getTextComponent().setSelectionStart(
                                matchedText.getStr().length());
                            getTextComponent()
                                 .setSelectionEnd(getText().length());
                        }
                        return null;
                    }
                });
        } else {
            getEventThreadQueuer().invokeAndWait("setSelection", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        getTextComponent().setSelectionStart(index);
                        getTextComponent().setSelectionEnd(index
                            + matchedText.getStr().length());
                        return null;
                    }
                });
        }
    }
    /**
     * performs a <code>count</code> -click with the left button on the
     * textfield. The click will be performed at the first caret position.
     *
     * @param count
     *            the number of clicks
     */
    public void gdClick(int count) {
        gdClick(count, 1);
    }
    /**
     * performs a <code>count</code> -click on the textfield. The click will
     * be performed at the left beginning of the textfield plus 3 pixels
     * to be sure that the click is really within the component.
     *
     * @param count
     *            the number of clicks
     * @param button
     *            the button that was clicked
     */
    public void gdClick(int count, int button) {
        getRobot().click(getTextComponent(), null,
                ClickOptions.create().setClickCount(count)
                    .setMouseButton(button), 
                3, true, 50, false);
    }

    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return null;
    }
}
