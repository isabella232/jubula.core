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

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.businessprocess.ProjectNameBP;
import org.eclipse.jubula.client.core.businessprocess.TestCaseParamBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.IncompatibleTypeException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.handlers.AbstractEditParametersHandler;
import org.eclipse.jubula.client.ui.rcp.handlers.project.RefreshProjectHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.osgi.util.NLS;

/**
 * Operation for replacing the parameter names.
 * @author BREDEX GmbH
 */
public class ChangeCtdsColumnUsageOperation
        extends AbstractEditParametersHandler
        implements IRunnableWithProgress {

    /** The selected parameter names. */
    private final ParameterNames m_paramNames;

    /**
     * @param paramNames The selected parameter names.
     */
    public ChangeCtdsColumnUsageOperation(
            ParameterNames paramNames) {
        m_paramNames = paramNames;
    }

    /**
     * {@inheritDoc}
     */
    public void run(IProgressMonitor monitor) {
        monitor.beginTask(
                NLS.bind(Messages.ChangeParameterUsageOperation,
                        m_paramNames.getOldParamDescription().getName(),
                        m_paramNames.getNewParamDescription().getName()),
                m_paramNames.getSelectedTestCases().size());
        TestCaseParamBP testCaseParamBP = new TestCaseParamBP();
        EditSupport es = null;
        boolean isOk = true;
        for (ITestCasePO testCase : m_paramNames.getSelectedTestCases()) {
            if (testCase instanceof IExecTestCasePO) {
                IExecTestCasePO exec = (IExecTestCasePO) testCase;
                testCase = exec.getSpecTestCase();
            }
            try {
                isOk = false;
                boolean isModified = false;
                ISpecTestCasePO spec = (ISpecTestCasePO) testCase;
                es = new EditSupport(spec,
                        new ParamNameBPDecorator(
                                ParamNameBP.getInstance(), spec));
                es.lockWorkVersion();
                ProjectNameBP.getInstance().clearCache();
                isModified = editParameters(
                        spec,
                        m_paramNames.getNewParametersFromSpecTestCase(spec),
                        false,
                        es.getParamMapper(),
                        testCaseParamBP);
                if (isModified) {
                    es.saveWorkVersion();
                }
                isOk = true;
            } catch (ProjectDeletedException e) {
                ErrorHandlingUtil.createMessageDialog(e, null, null);
            } catch (IncompatibleTypeException e) {
                ErrorHandlingUtil.createMessageDialog(e, null, null);
            } catch (PMException e) {
                ErrorHandlingUtil.createMessageDialog(e, null, null);
            } catch (RuntimeException e) {
                e.printStackTrace();
            } finally {
                if (es != null) {
                    es.close();
                }
            }
            monitor.worked(1);
            if (!isOk) {
                break; // stop loop in case of errors
            }
        }
        monitor.done();
        if (refreshProject()) {
            DataEventDispatcher.getInstance().fireParamChangedListener();
            DataEventDispatcher.getInstance().firePropertyChanged(false);
        }
    }

    /**
     * Refresh the project by executing the same refresh project handler,
     * which is called, if the user pressed F5.
     * @return True, if the project has been refreshed, otherwise false on errors.
     */
    private boolean refreshProject() {
        final AtomicReference<IStatus> statusOfRefresh =
                new AtomicReference<IStatus>();
        Plugin.getDisplay().syncExec(new Runnable() {
            public void run() {
                RefreshProjectHandler rph =
                    new RefreshProjectHandler();
                statusOfRefresh.set((IStatus)rph.executeImpl(null));
            }
        });
        return statusOfRefresh.get() != null && statusOfRefresh.get().isOK();
    }

    /**
     * {@inheritDoc}
     */
    protected Object executeImpl(ExecutionEvent event) {
        return null;
    }

}
