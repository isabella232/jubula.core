/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.toolkit.ios.provider.ui;

import java.util.Map;

import org.eclipse.jubula.client.ui.widgets.UIComponentHelper;
import org.eclipse.jubula.toolkit.mobile.provider.i18n.Messages;
import org.eclipse.jubula.toolkit.mobile.provider.ui.MobileAutConfigComponent;
import org.eclipse.jubula.tools.internal.constants.SwtAUTHierarchyConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Class to provide the iOS toolkit component
 * 
 * @author BREDEX GmbH
 */
public class IOSAutConfigComponent extends MobileAutConfigComponent {

    /** the technical name prefix */
    private static final String TECHNICAL_NAME_PREFIX = "org.eclipse.jubula.toolkit.mobile.provider.ios.ui.iOSAutConfigComponent."; //$NON-NLS-1$
    /** the platform name */
    private static final String PLATFORM = "iOS";

    /**
     * Constructor
     * 
     * @param parent
     *            the parent
     * @param style
     *            the style
     * @param autConfig
     *            the AUT configuration
     * @param autName
     *            the AUTs name
     */
    public IOSAutConfigComponent(Composite parent, int style,
            Map<String, String> autConfig, String autName) {
        super(parent, style, autConfig, autName, PLATFORM);
    }

    /** {@inheritDoc} */
    // FIXME soeren: cannot be moved to super class, because the super
    // constructors
    // this method, TECHNICAL_NAME_PREFIX and PLATFORM cannot be initialized
    // before calling this method
    protected void createBasicArea(Composite basicAreaComposite) {
        super.createBasicArea(basicAreaComposite);

        UIComponentHelper.createSeparator(basicAreaComposite, NUM_COLUMNS);

        // AUT hostname property
        Label autHostnameLabel = UIComponentHelper.createLabelWithText(
                basicAreaComposite, PLATFORM + " " + Messages.autHostnameLabel);
        autHostnameLabel.setData(SwtAUTHierarchyConstants.WIDGET_NAME,
                TECHNICAL_NAME_PREFIX + "autHostnameLabel"); //$NON-NLS-1$

        Text autHostTextField = UIComponentHelper.createTextField(
                basicAreaComposite, 2);
        autHostTextField.setData(SwtAUTHierarchyConstants.WIDGET_NAME,
                TECHNICAL_NAME_PREFIX + "autHostnameTextField"); //$NON-NLS-1$
        setAutHostTextField(autHostTextField);

        // AUT port property
        Label autPortLabel = UIComponentHelper.createLabelWithText(
                basicAreaComposite, PLATFORM + " " + Messages.autPortLabel); //$NON-NLS-1$
        autPortLabel.setData(SwtAUTHierarchyConstants.WIDGET_NAME,
                TECHNICAL_NAME_PREFIX + "autPortLabel"); //$NON-NLS-1$

        Text autPortTextField = UIComponentHelper.createTextField(
                basicAreaComposite, 2);
        autPortTextField.setData(SwtAUTHierarchyConstants.WIDGET_NAME,
                TECHNICAL_NAME_PREFIX + "autPortTextField"); //$NON-NLS-1$
        setAutPortTextField(autPortTextField);
    }
}
