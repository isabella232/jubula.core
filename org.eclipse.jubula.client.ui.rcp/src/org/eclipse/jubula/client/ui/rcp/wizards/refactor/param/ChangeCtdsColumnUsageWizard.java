/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.wizards.refactor.param;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * This wizard is used for replacing parameter names in Test Cases.
 * @author BREDEX GmbH
 */
public class ChangeCtdsColumnUsageWizard extends Wizard {

    /** The wizard page for selecting the old parameter name. */
    private ParameterNames m_paramNames;

    /**
     * @param testCases The set of execution Test Cases the change of
     *                  parameter name usage should operate on.
     */
    public ChangeCtdsColumnUsageWizard(Set<ITestCasePO> testCases) {
        setWindowTitle(Messages.ChangeParameterUsageActionDialog);
        // create for each parameter name a corresponding set of execution Test Cases
        m_paramNames = new ParameterNames(testCases);
        addPage(new SelectOldAndNewParameterNamesPage(m_paramNames));
    }

    /**
     * {@inheritDoc}
     */
    public boolean performFinish() {
        try {
            IProgressService ps = PlatformUI.getWorkbench()
                    .getProgressService();
            ps.run(true, false, new ChangeCtdsColumnUsageOperation(
                    m_paramNames));
        } catch (InvocationTargetException e) {
            //Already handled;
        } catch (InterruptedException e) {
            //Already handled
        }
        return true;
    }

}
