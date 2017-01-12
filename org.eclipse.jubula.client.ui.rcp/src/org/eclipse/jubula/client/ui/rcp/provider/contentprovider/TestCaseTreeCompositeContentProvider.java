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
package org.eclipse.jubula.client.ui.rcp.provider.contentprovider;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.exception.JBException;

/**
 * @author BREDEX GmbH
 * @created 08.10.2004
 */
public class TestCaseTreeCompositeContentProvider 
    extends AbstractTreeViewContentProvider {
    /**
     * {@inheritDoc}
     */
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof IProjectPO) {
            IProjectPO project = (IProjectPO)parentElement;
            List<Object> elements = new ArrayList<Object>();
            elements.addAll(project.getUnmodSpecList());
            elements.addAll(project.getUsedProjects());
            return elements.toArray();
        }
        
        if (parentElement instanceof ICategoryPO) {
            return ((ICategoryPO)parentElement)
                .getUnmodifiableNodeList().toArray();
        }
        
        if (parentElement instanceof IReusedProjectPO) {
            try {
                IProjectPO reusedProject = 
                    ProjectPM.loadReusedProjectInMasterSession(
                            (IReusedProjectPO)parentElement);

                if (reusedProject != null) {
                    return reusedProject.getUnmodSpecList().toArray();
                }

                return ArrayUtils.EMPTY_OBJECT_ARRAY;
            } catch (JBException e) {
                ErrorHandlingUtil.createMessageDialog(e, null, null);
                return ArrayUtils.EMPTY_OBJECT_ARRAY;
            }
        }
        
        return ArrayUtils.EMPTY_OBJECT_ARRAY;
    }  
}