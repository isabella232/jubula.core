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
package org.eclipse.jubula.client.ui.views.dataset;

import java.util.List;
import java.util.Locale;

import org.eclipse.jubula.client.core.businessprocess.CompletenessGuard;
import org.eclipse.jubula.client.core.businessprocess.TestCaseParamBP;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.editors.NodeEditorInput;
import org.eclipse.jubula.tools.exception.Assert;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;


/**
 * @author BREDEX GmbH
 * @created Jul 12, 2010
 */
public class ParamNodeDataSetPage extends AbstractDataSetPage {

    /** Constructor */
    public ParamNodeDataSetPage() {
        super(new TestCaseParamBP());
    }

    /**
     * @param paramObj
     *            IParamNodePO
     * @return the param node if the param interface obj is instanceof
     *         IParamNodePO; otherwise throws exception
     */
    private IParamNodePO getParamNodePO(IParameterInterfacePO paramObj) {
        if (paramObj instanceof IParamNodePO) {
            IParamNodePO paramNode = ((IParamNodePO)paramObj);
            return paramNode;
        }
        Assert.notReached();
        return null;
    }
    
    /** {@inheritDoc} */
    protected boolean isNodeValid(IParameterInterfacePO paramObj) {
        return getParamNodePO(paramObj).isValid();
    }

    /**
     *
     * {@inheritDoc} 
     */    
    protected void setIsEntrySetComplete(IParameterInterfacePO paramNode, 
             Locale locale) {
        IParamNodePO node = getParamNodePO(paramNode);
        boolean isComplete = node.isTestDataComplete(locale);
        CompletenessGuard.setCompFlagForTD(node, locale, isComplete);
    }

    /** {@inheritDoc} */
    protected boolean isEditorOpen(IParameterInterfacePO paramObj) {
        if (paramObj != null) {
            IParamNodePO paramNode = getParamNodePO(paramObj);
            INodePO node = paramNode;
            if (paramNode instanceof ICapPO 
                    || paramNode instanceof IExecTestCasePO) {
                node = paramNode.getParentNode();
            }
            List<IEditorReference> editors = Plugin.getAllEditors();
            for (IEditorReference reference : editors) {
                try {
                    if (reference.getEditorInput() instanceof NodeEditorInput) {
                        INodePO editorInputNode = 
                            ((NodeEditorInput)reference.getEditorInput())
                            .getNode();
                        if (editorInputNode != null 
                                && editorInputNode.equals(node)) {
                            return true;
                        }
                    }
                } catch (PartInitException e) {
                    // should not happpen. If it happens, it does not matter here.
                }
            }
        }
        return false;
    }

}
