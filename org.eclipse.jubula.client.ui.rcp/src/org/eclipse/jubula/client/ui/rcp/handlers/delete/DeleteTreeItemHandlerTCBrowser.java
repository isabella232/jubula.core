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
package org.eclipse.jubula.client.ui.rcp.handlers.delete;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.ListUtils;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.AbstractCmdHandle;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.DeleteCatHandle;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.DeleteEvHandle;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.DeleteTCHandle;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;


/**
 * @author BREDEX GmbH
 * @created 28.02.2006
 */
public class DeleteTreeItemHandlerTCBrowser 
        extends AbstractDeleteBrowserTreeItemHandler {
    /**
     * Creates a String with the locations of use of the given ISpecTestCasePO.
     * @param specTcPO a SpecTestCasePO
     * @param reusesSet
     *      List <IExecTestCasePO>
     * @param  nodesToDelete
     *      List<INodePO>
     * @return a String
     */
    private static Object[] createLocOfUseArray(ISpecTestCasePO specTcPO,
        List <IExecTestCasePO> reusesSet, List<INodePO> nodesToDelete) {
        Set < String > locations = new TreeSet < String > ();
        for (IExecTestCasePO node : reusesSet) {
            INodePO parent = null;
            if (node instanceof IEventExecTestCasePO) {
                parent = ((IEventExecTestCasePO) node).getParentNode();
            } else {
                parent = node.getParentNode();
            }
            if (parent != null && !nodesToDelete.contains(parent)) {
                locations.add(Constants.BULLET + parent.getName() 
                        + StringConstants.NEWLINE);
            }
        }
        String list = StringConstants.EMPTY;
        for (String string : locations) {
            list += string;       
        }
        return new Object[] {specTcPO.getName(), locations.size(), list};
    }
    
    /**
     * @param nodesToDelete the nodes to delete
     * @return a list of abstract cmd handles for node deletion
     */
    protected List<AbstractCmdHandle> getDeleteCommands(
            List<INodePO> nodesToDelete) {
        List<AbstractCmdHandle> cmds = new ArrayList<AbstractCmdHandle>(
                nodesToDelete.size());
        // check for all ISpecTestCases if they were reused somewhere, 
        // outside of the selected nodes, if not
        // create command for deletion
        ParamNameBPDecorator dec = 
            new ParamNameBPDecorator(ParamNameBP.getInstance());
        for (INodePO node : nodesToDelete) {
            closeOpenEditor(node);
            if (node instanceof ISpecTestCasePO) {
                ISpecTestCasePO specTcPO = (ISpecTestCasePO)node;
                List<IExecTestCasePO> execTestCases;
                execTestCases = NodePM.getInternalExecTestCases(
                    specTcPO.getGuid(), specTcPO.getParentProjectId());
                if (!MultipleNodePM.allExecsFromList(
                        nodesToDelete, execTestCases)) {
                    ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.I_REUSED_SPEC_TCS, 
                        createLocOfUseArray(specTcPO, execTestCases,
                            nodesToDelete), null);
                    return ListUtils.EMPTY_LIST;
                }
                dec.clearAllNames();
                cmds.add(new DeleteTCHandle(specTcPO, dec));
            }
            if (node instanceof IEventExecTestCasePO) {
                cmds.add(new DeleteEvHandle((IEventExecTestCasePO)node));
            }
            if (node instanceof ICategoryPO) {
                cmds.add(new DeleteCatHandle((ICategoryPO)node));
            }
        }
        return cmds;
    }
}