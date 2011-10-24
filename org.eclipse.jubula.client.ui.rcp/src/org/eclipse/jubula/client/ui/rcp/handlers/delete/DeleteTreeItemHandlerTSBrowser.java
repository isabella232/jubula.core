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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.AbstractCmdHandle;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.DeleteCatHandle;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.DeleteExecHandle;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.rcp.editors.TestJobEditor;
import org.eclipse.jubula.client.ui.rcp.editors.TestSuiteEditor;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.ui.IEditorReference;

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
        IEditorReference[] editors = Plugin.getActivePage()
                .getEditorReferences();
        List<AbstractCmdHandle> cmds = new ArrayList<AbstractCmdHandle>(
                nodesToDelete.size());
        for (INodePO node : nodesToDelete) {
            AbstractCmdHandle cmd = null;
            if (node instanceof ITestSuitePO) {
                ITestSuitePO testSuite = (ITestSuitePO) node;
                List<IRefTestSuitePO> refTs = NodePM.getInternalRefTestSuites(
                        testSuite.getGuid(), testSuite.getParentProjectId());
                if (refTs.size() > 0) {
                    ErrorHandlingUtil
                            .createMessageDialog(MessageIDs.I_REUSED_TS);
                    return ListUtils.EMPTY_LIST;
                }

                closeEditors(testSuite, editors);
                cmd = new DeleteExecHandle(testSuite);
            } else if (node instanceof ITestJobPO) {
                ITestJobPO testjob = (ITestJobPO) node;
                closeEditors(testjob, editors);
                cmd = new DeleteExecHandle(testjob);
            } else if (node instanceof ICategoryPO) {
                ICategoryPO category = (ICategoryPO) node;
                cmd = new DeleteCatHandle(category);
            }
            cmds.add(cmd);
        }
        return cmds;
    }

    /** {@inheritDoc} */
    protected void collectNodesToDelete(List<INodePO> nodesToDelete,
            INodePO node) {
        nodesToDelete.add(node);
        if (node instanceof ICategoryPO) {
            Iterator iter = node.getNodeListIterator();
            while (iter.hasNext()) {
                collectNodesToDelete(nodesToDelete, (INodePO) iter.next());
            }
        }
    }

    /**
     * Closes all given IEditorReferences
     * 
     * @param testjob
     *            the Test Job
     * @param editors
     *            the IEditorReferences
     */
    private void closeEditors(ITestJobPO testjob, IEditorReference[] editors) {
        for (IEditorReference editor : editors) {
            if (editor.getPart(true) instanceof TestJobEditor) {
                TestJobEditor tjEditor = (TestJobEditor) editor.getPart(true);
                if (tjEditor.getEditorInput().getName()
                        .endsWith(testjob.getName())) {
                    tjEditor.getEditorSite().getPage()
                            .closeEditor(tjEditor, true);
                }
            }
        }
    }

    /**
     * Closes all given IEditorReferences
     * 
     * @param execTS
     *            the TestSuite
     * @param editors
     *            the IEditorReferences
     */
    private void closeEditors(ITestSuitePO execTS, IEditorReference[] editors) {
        for (IEditorReference editor : editors) {
            if (editor.getPart(true) instanceof TestSuiteEditor) {
                TestSuiteEditor tsEditor = (TestSuiteEditor) editor
                        .getPart(true);
                if (tsEditor.getEditorInput().getName()
                        .endsWith(execTS.getName())) {

                    tsEditor.getEditorSite().getPage()
                            .closeEditor(tsEditor, true);
                }
            }
            if (editor.getPart(true) instanceof ObjectMappingMultiPageEditor) {
                int autCounter = 0;
                ObjectMappingMultiPageEditor omEditor = 
                        (ObjectMappingMultiPageEditor) editor
                            .getPart(true);
                IAUTMainPO omAut = omEditor.getAut();
                List<ITestSuitePO> tsList = TestSuiteBP.getListOfTestSuites();
                for (ITestSuitePO ts : tsList) {
                    if (ts.getAut() != null && ts.getAut().equals(omAut)) {
                        autCounter++;
                    }
                }
                if (execTS.getAut() != null && execTS.getAut().equals(omAut)) {
                    autCounter--;
                }
                if (autCounter == 0) {
                    omEditor.getEditorSite().getPage()
                            .closeEditor(omEditor, true);
                }
            }
        }
    }

}
