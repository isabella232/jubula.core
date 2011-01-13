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
package org.eclipse.jubula.client.ui.handlers.existing.testcase;

import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.model.SpecTestCaseGUI;
import org.eclipse.jubula.client.ui.model.TestSuiteGUI;


/**
 * @author BREDEX GmbH
 * @created 18.02.2009
 */
public class AddExistingTestCase extends AbstractReferenceExistingTestCase {
    /**
     * {@inheritDoc}
     */
    protected Integer getPositionToInsert(INodePO workVersion, 
            GuiNode selectedNodeGUI) {
        
        int positionToAdd = selectedNodeGUI.getPositionInParent() + 1;
        if (selectedNodeGUI instanceof SpecTestCaseGUI
                || selectedNodeGUI instanceof TestSuiteGUI)  {
            positionToAdd = 0;
        }
        if (Plugin.getDefault().getPreferenceStore().getBoolean(
                Constants.NODE_INSERT_KEY)) {
            positionToAdd = workVersion.getUnmodifiableNodeList().size() + 1;
        }
        return positionToAdd;
    }
}
