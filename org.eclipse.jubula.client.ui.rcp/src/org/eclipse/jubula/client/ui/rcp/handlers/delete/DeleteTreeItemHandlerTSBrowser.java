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

import org.apache.commons.collections.ListUtils;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.AbstractCmdHandle;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.DeleteCatHandle;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.DeleteExecHandle;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;

/**
 * @author BREDEX GmbH
 * @created 28.02.2006
 */
public class DeleteTreeItemHandlerTSBrowser extends
        AbstractDeleteBrowserTreeItemHandler {

    /**
     * @param nodesToDelete
     *            the nodes to delete
     * @return a list of abstract cmd handles for node deletion
     */
    protected List<AbstractCmdHandle> getDeleteCommands(
            List<INodePO> nodesToDelete) {
        List<AbstractCmdHandle> cmds = new ArrayList<AbstractCmdHandle>(
                nodesToDelete.size());
        for (INodePO node : nodesToDelete) {
            AbstractCmdHandle cmd = null;
            if (node instanceof ITestSuitePO) {
                ITestSuitePO testSuite = (ITestSuitePO) node;
                List<IRefTestSuitePO> refTs = NodePM.getInternalRefTestSuites(
                        testSuite.getGuid(), testSuite.getParentProjectId());
                if (refTs.size() > 0) {
                    ErrorHandlingUtil.createMessageDialog(
                            MessageIDs.I_REUSED_TS);
                    return ListUtils.EMPTY_LIST;
                }
                closeOpenEditor(testSuite);
                cmd = new DeleteExecHandle(testSuite);
            } else if (node instanceof ITestJobPO) {
                ITestJobPO testjob = (ITestJobPO) node;
                closeOpenEditor(testjob);
                cmd = new DeleteExecHandle(testjob);
            } else if (node instanceof ICategoryPO) {
                ICategoryPO category = (ICategoryPO) node;
                cmd = new DeleteCatHandle(category);
            }
            cmds.add(cmd);
        }
        return cmds;
    }
}
