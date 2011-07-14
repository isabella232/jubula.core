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
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.osgi.util.NLS;


/**
 * Property tester for persistent model objects.
 *
 * @author BREDEX GmbH
 * @created Mar 5, 2009
 */
public class PersistentObjectPropertyTester extends PropertyTester {

    /** the id of the "isInCurrentProject" property */
    public static final String IS_IN_CUR_PROJECT = "isInCurrentProject"; //$NON-NLS-1$

    /** the logger */
    private static final Log LOG = 
        LogFactory.getLog(PersistentObjectPropertyTester.class);

    /**
     * 
     * {@inheritDoc}
     */
    public boolean test(Object receiver, String property, Object[] args,
            Object expectedValue) {

        if (receiver instanceof IPersistentObject) {
            IPersistentObject po = 
                (IPersistentObject)receiver;
            if (property.equals(IS_IN_CUR_PROJECT)) {
                boolean isInCurrentProject = testIsInCurrentProject(po);
                boolean expectedBoolean = expectedValue instanceof Boolean 
                    ? ((Boolean)expectedValue).booleanValue() : true;
                return isInCurrentProject == expectedBoolean;
            }

            LOG.warn(NLS.bind(Messages.PropertyTesterPropertyNotSupported,
                    property));
            return false;
        }

        String receiverClass = 
            receiver != null ? receiver.getClass().getName() : "null"; //$NON-NLS-1$
        LOG.warn(NLS.bind(Messages.PropertyTesterTypeNotSupported,
                receiverClass));
        return false;
    }

    /**
     * 
     * @param po The persistent model object to test.
     * @return <code>true</code> if the given object is in the currently 
     *         open project. Otherwise <code>false</code>.
     */
    private boolean testIsInCurrentProject(IPersistentObject po) {

        IProjectPO currentProject = GeneralStorage.getInstance().getProject();
        if (currentProject != null && po.getParentProjectId() != null) {
            return po.getParentProjectId().equals(currentProject.getId());
        }
        
        return false;
    }

}
