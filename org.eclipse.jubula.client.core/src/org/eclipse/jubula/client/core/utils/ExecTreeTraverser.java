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
package org.eclipse.jubula.client.core.utils;

import java.util.Iterator;

import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.IExecPersistable;


/**
 * @author BREDEX GmbH
 * @created 08.09.2006
 */
public class ExecTreeTraverser extends TreeTraverser {

    /**
     * Creates a traverser traversing over all 
     * Exec-Nodes (ExecTestCases and CAPs).
     * 
     * @param rootNode the root node (The Project)
     * @param operation the operation to execute.
     */
    public ExecTreeTraverser(INodePO rootNode, 
            ITreeNodeOperation<INodePO> operation) {
        super(rootNode, operation);
    }

    /**
     * Creates a traverser traversing over all 
     * Exec-Nodes (ExecTestCases and CAPs).
     * 
     * @param rootNode the root node (The Project)
     */
    public ExecTreeTraverser(INodePO rootNode) {
        super(rootNode);
    }
    /**
     * 
     * {@inheritDoc}
     */
    protected void traverseImpl(ITreeTraverserContext<INodePO> context, 
            INodePO parent, INodePO node) {
        context.append(node);
        if (context.isContinue()) {
            for (ITreeNodeOperation<INodePO> operation : getOperations()) {
                operation.operate(context, parent, node, false);
            }
            if (node instanceof IProjectPO) {
                IProjectPO project = (IProjectPO)node;
                for (IExecPersistable exec : project.getExecObjCont()
                        .getExecObjList()) {

                    traverseImpl(context, project, exec);
                }
            } else if (node instanceof ICategoryPO) {
                ICategoryPO category = (ICategoryPO)node;
                Iterator<INodePO> iter =  category.getNodeListIterator();
                while (iter.hasNext()) {
                    INodePO next = iter.next();
                    traverseImpl(context, parent, next);
                }
            } else if (node instanceof ITestSuitePO) {
                ITestSuitePO suite = (ITestSuitePO)node;
                Iterator<INodePO> iter =  suite.getNodeListIterator();
                while (iter.hasNext()) {
                    INodePO next = iter.next();
                    traverseImpl(context, parent, next);
                }
            } else if (node instanceof IExecTestCasePO) {
                IExecTestCasePO execTC = (IExecTestCasePO)node;
                Iterator<INodePO> iter = execTC.getNodeListIterator();
                while (iter.hasNext()) {
                    traverseImpl(context, parent, iter.next());
                }
            }
            for (ITreeNodeOperation<INodePO> operation : getOperations()) {
                operation.postOperate(context, parent, node, false);
            }
        }
        context.removeLast();
    } 
}