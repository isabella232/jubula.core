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
package org.eclipse.jubula.client.ui.propertytester;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.ui.IEditorPart;


/**
 * @author BREDEX GmbH
 * @created 30.07.2009
 */
public class EditorPartPropertyTester extends PropertyTester {

    /** the id of the "isInCurrentProject" property */
    public static final String IS_DIRTY = "isDirty"; //$NON-NLS-1$

    /** the logger */
    private static final Log LOG = LogFactory
            .getLog(EditorPartPropertyTester.class);

    /**
     * {@inheritDoc}
     */
    public boolean test(Object receiver, String property, Object[] args,
            Object expectedValue) {

        if (receiver instanceof IEditorPart) {
            IEditorPart ep = (IEditorPart)receiver;
            if (property.equals(IS_DIRTY)) {
                boolean hasAUT = ep.isDirty() ? true : false;
                boolean expectedBoolean = expectedValue 
                    instanceof Boolean ? ((Boolean)expectedValue)
                        .booleanValue()
                        : true;
                return hasAUT == expectedBoolean;
            }

            LOG.warn(I18n.getString("PropertyTester.PropertyNotSupported", //$NON-NLS-1$
                    new String[] { property }));
            return false;
        }

        String receiverClass = receiver != null ? receiver.getClass().getName()
                : "null"; //$NON-NLS-1$
        LOG.warn(I18n.getString("PropertyTester.TypeNotSupported", //$NON-NLS-1$
                new String[] { receiverClass }));
        return false;
    }
}