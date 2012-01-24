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
package org.eclipse.jubula.client.ui.widgets;

import java.util.List;

import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * @author BREDEX GmbH
 * @created 14.02.2006
 */
public abstract class UIComponentHelper {

    /** text to append to each label that corresponds to a text input */
    private static final String LABEL_TERMINATOR = ":"; //$NON-NLS-1$
    
    /**
     * hide construcotr for utility class
     */
    private UIComponentHelper() {
        // hide construcotr for utility class
    }

    /**
     * Create a simple separator which spans the supplied number of columns
     * @param parent parent composite
     * @param hSpan number of columns the sepator should span
     * @return a Label representing the separator
     */
    public static Label createSeparator(Composite parent, int hSpan) {
        Label sep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData sepData = new GridData();
        sepData.horizontalAlignment = GridData.FILL;
        sepData.horizontalSpan = hSpan;
        sep.setLayoutData(sepData);
        return sep;
    }
    /**
     * Create a Label with a text derived from the supplied I18N key
     * @param parent parent composite
     * @param i18nKey Key to be used when calling I18n methods
     * @return a Label with a text set according to the supplied key
     */
    public static Label createLabel(Composite parent, String i18nKey) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(I18n.getString(i18nKey, true));
        GridData labelGrid = new GridData(GridData.BEGINNING, GridData.CENTER, 
            false , false, 1, 1);
        label.setLayoutData(labelGrid);
        return label;        
    }
    
    /**
     * Create a Textfield which spans the supplied number of columns
     * @param parent parent composite
     * @param hSpan number of columns the sepator should span
     * @return a Text
     */
    public static Text createTextField(Composite parent, int hSpan) {
        final Text textField = new Text(parent, SWT.BORDER);
        GridData textGrid = new GridData(GridData.FILL, GridData.CENTER, 
            true , false, hSpan, 1);
        LayoutUtil.addToolTipAndMaxWidth(textGrid, textField); // FIXME al
        textField.setLayoutData(textGrid);
        return textField;
    }
    /**
     * Creates a composite with a predefined layout. This layout works well
     * with i.e. Buttons.
     * @param parent The parent composite.
     * @return A composite with a defined layout
     */
    public static Composite createLayoutComposite(Composite parent) {
        return createLayoutComposite(parent, 1);
    }

    /**
     * Creates a composite with a predefined layout. This layout works well
     * with i.e. Buttons.
     * @param parent The parent composite.
     * @param numColumns The number of columns in the layout.
     * @return A composite with a defined layout
     */
    public static Composite createLayoutComposite(Composite parent, 
        int numColumns) {
        
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = numColumns;
        compositeLayout.marginHeight = 0;
        compositeLayout.marginWidth = 0;
        composite.setLayout(compositeLayout);
        GridData data = new GridData();
        data.verticalAlignment = GridData.BEGINNING;
        data.horizontalAlignment = GridData.FILL;
        composite.setLayoutData(data);
        return composite;
    }

    /**
     * Creates a toggle button which spans the supplied number of columns
     * @param parent The composite.
     * @param hSpan The horizontal span of the Button.
     * @return a new toggle button
     */
    public static Button createToggleButton(Composite parent, int hSpan) {
        Button toggle = new Button(parent, SWT.CHECK);
        GridData labelGrid = new GridData(GridData.BEGINNING, GridData.CENTER, 
            false , false, hSpan, 1);
        toggle.setLayoutData(labelGrid);
        return toggle;
    }

    /**
     * @param parent The composite.
     * @param hSpan The horizontal span of the combo.
     * @param comboObjects the comboObjects
     * @param comboDispObjects the displayed string list
     * @param nullSelectionIsAllowed true or false
     * @return a new combo
     */
    public static DirectCombo<String> createCombo(Composite parent, int hSpan,
            List<String> comboObjects, List<String> comboDispObjects, 
            boolean nullSelectionIsAllowed) {
        
        final DirectCombo<String> combo = new DirectCombo<String>(parent, 
                SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY, comboObjects, 
                comboDispObjects, nullSelectionIsAllowed, false);
        GridData comboGrid = new GridData(GridData.FILL, GridData.CENTER, 
            true , false, hSpan, 1);
        LayoutUtil.addToolTipAndMaxWidth(comboGrid, combo);
        combo.setLayoutData(comboGrid);
        return combo;
    }

    /**
     * @param <E> This methods works for instances of Enum E
     * @param parent The composite.
     * @param hSpan The horizontal span of the combo.
     * @param baseKey i18n base key
     * @param enumClass which enum shall be used
     * @return a new combo
     */
    public static <E extends Enum> I18nEnumCombo<E> createEnumCombo(
        Composite parent, int hSpan,
        String baseKey, Class<E> enumClass) {
        final I18nEnumCombo<E> combo = new I18nEnumCombo<E>(parent, SWT.BORDER
            | SWT.READ_ONLY, baseKey, enumClass, false, false); 
        GridData comboGrid = new GridData(GridData.FILL, GridData.CENTER, true,
            false, hSpan, 1);
        LayoutUtil.addToolTipAndMaxWidth(comboGrid, combo);
        combo.setLayoutData(comboGrid);
        return combo;
    }

    /**
     * set the maximum number of characters this Text may hold and install
     * a listener which shows a warning dialog if the number of characters is
     * reached.
     * @param textField The Text for which the limit should be set
     * @param limit The limit, any number greater than one and less than
     * Text.LIMIT;
     */
    public static void setMaxTextChars(Text textField, final int limit) {
        if (textField == null) {
            return;
        }
        textField.setTextLimit(limit);
        textField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (((Text)e.widget).getCharCount() == limit) {
                    ErrorHandlingUtil.createMessageDialog(
                            MessageIDs.W_MAX_CHAR, 
                            new Object[] {limit}, null);  
                }
            }
        });

    }

    /**
     * Creates a label with appropriate text in the given composite.
     * 
     * @param parent The parent composite for the label.
     * @param fieldName The internationalized name of the text input field for 
     *                  which to create a label.
     * @param style the SWT style for the label.
     */
    public static void createLabel(
            Composite parent, String fieldName, int style) {
        Label label = new Label(parent, style);
        label.setText(fieldName + LABEL_TERMINATOR);
    }
    
    
}
