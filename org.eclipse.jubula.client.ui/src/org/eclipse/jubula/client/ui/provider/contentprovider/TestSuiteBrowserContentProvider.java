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
package org.eclipse.jubula.client.ui.provider.contentprovider;
import java.util.Locale;

import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.ui.businessprocess.WorkingLanguageBP;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.model.TestSuiteGUI;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.Assert;


/**
 * @author BREDEX GmbH
 * @created 15.10.2004
 */
public class TestSuiteBrowserContentProvider 
    extends AbstractTreeViewContentProvider {

    /**
     * @param parentElement Object
     * @return object array
     */
    public Object[] getChildren(Object parentElement) {
        Assert.verify(parentElement instanceof GuiNode,
                Messages.WrongTypeOfElement + StringConstants.EXCLAMATION_MARK);
        GuiNode data = (GuiNode)parentElement;
        if (data instanceof TestSuiteGUI) {
            TestSuiteGUI tsGUI = (TestSuiteGUI)data;
            ITestSuitePO ts = (ITestSuitePO)tsGUI.getContent();
            if (ts != null) {
                Locale workLang = WorkingLanguageBP.getInstance()
                        .getWorkingLanguage();
                if (ts.getAut() != null
                        && !WorkingLanguageBP.getInstance()
                                .isTestSuiteLanguage(workLang, ts)) {
                    return new Object[0];
                }
            }
        }
        if (parentElement instanceof GuiNode) {
            return data.getChildren().toArray();
        }
        return new Object[0];
    }
}