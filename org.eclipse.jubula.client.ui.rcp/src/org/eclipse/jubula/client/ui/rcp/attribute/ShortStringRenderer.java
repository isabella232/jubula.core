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
package org.eclipse.jubula.client.ui.rcp.attribute;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * Renders and manages a short (one-line) text field with a corresponding
 * value. 
 *
 * @author BREDEX GmbH
 * @created 16.05.2008
 */
public class ShortStringRenderer extends AbstractAttributeRenderer {
    
    /**
     * {@inheritDoc}
     */
    public void renderAttribute(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        new Label(composite, SWT.NONE).setText(
                I18n.getString(getDescription().getLabelKey())
                + StringConstants.COLON + StringConstants.SPACE);
        Text text = new Text(composite, SWT.BORDER);
        text.setEditable(true);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        text.setText(
                StringUtils.defaultString(getAttribute().getValue()));
        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                String newValue = ((Text)e.getSource()).getText();
                if (getDescription().isValueValid(newValue)) {
                    getAttribute().setValue(newValue);
                }
                // FIXME zeb Else show error
            }
        });
    }
}
