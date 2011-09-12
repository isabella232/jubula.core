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
package org.eclipse.jubula.client.ui.provider.labelprovider.decorators;

import java.util.Locale;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jubula.client.core.businessprocess.problems.IProblem;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.ui.businessprocess.UINodeBP;
import org.eclipse.jubula.client.ui.businessprocess.WorkingLanguageBP;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.constants.Layout;
import org.eclipse.jubula.client.ui.provider.labelprovider.TestSuiteBrowserLabelProvider;
import org.eclipse.swt.graphics.Image;


/**
 * @author BREDEX GmbH
 * @created 10.05.2005
 */
public class CompletenessDecorator extends AbstractLightweightLabelDecorator {
    /** indicator that there's no decoration */
    private static final int NO_DECORATION = -1;

    /** {@inheritDoc} */
    public void decorate(Object element, IDecoration decoration) {
        decoration.setForegroundColor(Layout.DEFAULT_OS_COLOR);
        final INodePO node = (INodePO)element;
        final ITestSuitePO owningTestsuite = UINodeBP.getOwningTestSuite(node);
        if (shouldNotDecorate(node, decoration, owningTestsuite)) {
            return;
        }
        boolean flag = false;
        if (TestSuiteBrowserLabelProvider.isNodeActive(node)) {
            if (owningTestsuite != null) {
                final WorkingLanguageBP workLangBP = WorkingLanguageBP
                    .getInstance();
                Locale locale = workLangBP.getWorkingLanguage();
                IAUTMainPO aut = owningTestsuite.getAut();
                if (element instanceof ITestSuitePO) {
                    ITestSuitePO execTs = (ITestSuitePO)element;
                    if (aut != null && !workLangBP
                                .isTestSuiteLanguage(locale, owningTestsuite)) {
                        decoration.setForegroundColor(Layout.GRAY_COLOR);
                    } else {
                        decoration.setForegroundColor(Layout.DEFAULT_OS_COLOR);
                        flag = node.getSumTdFlag(locale) && node
                             .getSumOMFlag(aut) && node.getSumSpecTcFlag();
                    }
                    if (execTs.getNodeListSize() == 0) {
                        flag = true;
                    }
                    if (aut == null) {
                        flag = false;
                    }
                } else if (node instanceof IExecTestCasePO) {
                    flag = node.getSumTdFlag(locale) && node.getSumOMFlag(aut)
                        && node.getSumSpecTcFlag();
                } else if (node instanceof ICapPO) {
                    ICapPO cap = (ICapPO)node;
                    INodePO grandParent = node.getParentNode().getParentNode();
                    boolean overWrittenName = false;
                    if (grandParent instanceof IExecTestCasePO) {
                        IExecTestCasePO execTC = (IExecTestCasePO)grandParent;
                        for (ICompNamesPairPO pair 
                                : execTC.getCompNamesPairs()) {
                            if (pair.getFirstName().equals(
                                        cap.getComponentName())
                                    && pair.getSecondName() != null
                                        && !pair.getSecondName().equals(
                                                cap.getComponentName())) {
                                
                                overWrittenName = true;
                                break;
                            }
                        }
                        flag = cap.getCompleteTdFlag(locale);
                        if (!overWrittenName) {
                            flag = flag && cap.getCompleteOMFlag(aut);
                        }
                    } else {
                        flag = true;
                    }
                }
            } else {
                if (element instanceof ITestJobPO) {
                    flag = TestSuiteBrowserLabelProvider
                            .isTestJobGuiValid((ITestJobPO)element);
                } else if (element instanceof IRefTestSuitePO) {
                    flag = TestSuiteBrowserLabelProvider
                            .isRefTestSuiteGuiValid((IRefTestSuitePO)element);
                } else {
                    flag = true;
                }
            }
        } else {
            flag = true;
        }
        setIcon(decoration, getStatus(node, flag));
    }

    /**
     * @param node
     *            the node
     * @param decoration
     *            the decoration
     * @param owningTestsuite the owning test suite of the node
     * @return wheter decoration should continue for this element or not
     */
    private boolean shouldNotDecorate(INodePO node, IDecoration decoration,
            ITestSuitePO owningTestsuite) {
        return node == null
                || node.getParentNode() == null
                || owningTestsuite == null
                || decoration.getDecorationContext() 
                    instanceof NonDecorationContext
                || node instanceof IProjectPO;
    }

    /** 
     * returns the status from the view of the external problems of the 
     * object
     * 
     * @param flag Flag that is calculated in the method before
     * @param element element that will be checked for external problems
     * @return the status code. See {@link IStatus} for valid values.
     */
    private static int getStatus(INodePO element, boolean flag) {
        if (!flag) {
            return IStatus.ERROR; // if there's a flag, its an ERROR
        }
        int status = NO_DECORATION;
        for (IProblem problem : element.getProblems()) {
            if (problem.getSeverity() > status) {
                status = problem.getSeverity();
            }
        }
        return status;
    }

    /**
     * Adds an overlay to the given decoration based on the given status.
     * 
     * @param decoration The decoration that will be overlayed.
     * @param status Determines the icon that will be used.
     */
    private static void setIcon(IDecoration decoration, int status) {
        if (status == IStatus.ERROR) {
            decoration.addOverlay(
                    IconConstants.INCOMPLETE_DATA_IMAGE_DESCRIPTOR);
        } else if (status == IStatus.WARNING) {
            Image warning = IconConstants.WARNING_SMALL_IMAGE;
            decoration.addOverlay(ImageDescriptor.createFromImage(warning));
        }
    }
}