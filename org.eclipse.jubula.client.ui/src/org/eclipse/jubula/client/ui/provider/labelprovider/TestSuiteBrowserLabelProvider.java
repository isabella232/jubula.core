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
package org.eclipse.jubula.client.ui.provider.labelprovider;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.ui.businessprocess.WorkingLanguageBP;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.model.RefTestSuiteGUI;
import org.eclipse.jubula.client.ui.model.TestJobGUI;
import org.eclipse.jubula.client.ui.model.TestSuiteGUI;
import org.eclipse.jubula.tools.i18n.I18n;



/**
 * A label provider implementation which, by default, 
 * uses an element's toString value for its text and null for its image.
 * @author BREDEX GmbH
 * @created 06.07.2004
 */
public class TestSuiteBrowserLabelProvider extends GeneralGDLabelProvider {
    /** {@inheritDoc} */
    public String getToolTipText(Object element) {
        if (element instanceof GuiNode) {
            return createToolTipText((GuiNode)element);
        }
        return super.getToolTipText(element);
    }
    

    /**
     * @param gnode
     *            the node to check
     * @return whether the given node or one of it's parent is active or not
     */
    protected boolean isNodeActive(GuiNode gnode) {
        INodePO node = gnode.getContent();
        if (node == null || !node.isActive()) {
            return false;
        }
        GuiNode parentGNode = gnode.getParentNode();
        while (parentGNode != null) {
            node = parentGNode.getContent();
            if (node != null) {
                if (!node.isActive()) {
                    return false;
                }
            }
            parentGNode = parentGNode.getParentNode();
        }
        return true;
    }
    
    /**
     * creates the ToolTip Text
     * @param element
     *      GuiNode
     * @return
     *      String
     */
    private String createToolTipText(GuiNode element) {
        StringBuilder toolTip = new StringBuilder();
        INodePO node = element.getContent();

        final WorkingLanguageBP workLangBP = WorkingLanguageBP.getInstance();
        Locale locale = workLangBP.getWorkingLanguage();
        ITestSuitePO testSuite = (ITestSuitePO)
            (TestSuiteGUI.getTestSuiteForNode(element))
                .getContent();
        if (node != null && isNodeActive(element)) {
            if (testSuite != null) {
                IAUTMainPO aut = testSuite.getAut();
                if (element instanceof TestSuiteGUI) {
                    TestSuiteGUI execTs = (TestSuiteGUI)element;
                    if (testSuite.getAut() != null 
                            && !workLangBP
                                .isTestSuiteLanguage(locale, testSuite)) {
                        toolTip.append(Constants.BULLET).append(
                                I18n.getString("TestDataDecorator.unsupportedAUTLanguage")); //$NON-NLS-1$
                    } else {
                        checkNode(execTs, aut, locale, toolTip);
                    }
                } else if (node instanceof IExecTestCasePO) {
                    checkNode((IExecTestCasePO)node, aut, locale, toolTip);
                } else if (node instanceof ICapPO) {
                    ICapPO cap = (ICapPO)node;
                    IExecTestCasePO execTC = (IExecTestCasePO)(element).
                    getParentNode().getParentNode().getContent();
                    boolean overWrittenName = false;
                    for (ICompNamesPairPO pair : execTC.getCompNamesPairs()) {
                        if (pair.getFirstName().equals(cap.getComponentName())
                                && pair.getSecondName() != null 
                                && !pair.getSecondName().equals(
                                        cap.getComponentName())) {
                            overWrittenName = true;
                            break;
                        }
                    }
                    checkNode(aut, locale, cap, toolTip, overWrittenName);
                }
            } 
            if (element instanceof TestJobGUI) {
                if (!isTestJobGuiValid((TestJobGUI)element)) {
                    addMessage(toolTip, I18n
                            .getString("TestDataDecorator.TestJobIncompl")); //$NON-NLS-1$
                }
            } else if (element instanceof RefTestSuiteGUI) {
                if (!isRefTestSuiteGuiValid((RefTestSuiteGUI)element)) {
                    addMessage(toolTip, I18n
                            .getString("TestDataDecorator.RefTsIncompl")); //$NON-NLS-1$
                }
            }
            if (toolTip.length() == 0) {
                return super.getToolTipText(element);
            }
        }
        return toolTip.length() > 0 ? toolTip.toString() : null;
    }

    /**
     * Checks an TestSuiteGUI for decoration 
     * @param execTs the Node
     * @param aut the aut
     * @param locale the local
     * @param toolTip the tool tip
     * @return the changed tooltip
     */
    private StringBuilder checkNode(TestSuiteGUI execTs, IAUTMainPO aut,
        Locale locale, StringBuilder toolTip) {
        if (!execTs.getContent().getSumOMFlag(aut) && aut != null) {
            addMessage(toolTip, 
                I18n.getString("TestDataDecorator.OMTSIncompl")); //$NON-NLS-1$
        }
        if (aut == null) {
            addMessage(toolTip, 
                I18n.getString("TestDataDecorator.TestSuiteWithoutAUT")); //$NON-NLS-1$
        }
        if (!execTs.getContent().getSumTdFlag(locale)) {
            addMessage(toolTip, 
                I18n.getString("TestDataDecorator.TDChildrenIncompl")); //$NON-NLS-1$
        }
        if (!execTs.getContent().getSumSpecTcFlag()) {
            addMessage(toolTip, 
                I18n.getString("TestDataDecorator.SpecTcChildrenMissing")); //$NON-NLS-1$
        }
        return toolTip;
    }
    
    /**
     * Adds a newline at the end of the string, if the string is not currently 
     * empty.
     * 
     * @param toolTip The string to manipulate.
     */
    private void addWhitespace(StringBuilder toolTip) {
        if (toolTip.length() > 0) {
            toolTip.append("\n"); //$NON-NLS-1$
        }
    }

    /**
     * @param execTC the "real" model node with parameters
     * @param aut the AUT
     * @param locale the Locale
     * @param toolTip the tooltip
     * @return the changed tooltip
     */
    @SuppressWarnings("unchecked")
    private StringBuilder checkNode(IExecTestCasePO execTC, IAUTMainPO aut,
        Locale locale, StringBuilder toolTip) {  
        
        if (execTC == null) {
            return toolTip;
        }
        if (!execTC.getCompleteSpecTcFlag()) {
            addMessage(toolTip, I18n.getString("TestDataDecorator.SpecTcMissing")); //$NON-NLS-1$
        } else {
            IParamNodePO realParamNode = execTC;
            if (execTC.getHasReferencedTD()) {
                realParamNode = execTC.getSpecTestCase();
            }
            if ((aut != null) && !execTC.getSumOMFlag(aut)) {
                addMessage(toolTip, I18n.getString("TestDataDecorator.OMChildrenIncompl"));  //$NON-NLS-1$
            } 
            if (execTC.getDataFile() == null
                || execTC.getDataFile().length() == 0) {
                
                boolean isAlreadySet = false;
                if (!realParamNode.getCompleteTdFlag(locale)) {
                    addMessage(toolTip, I18n.getString("TestDataDecorator.TdInTestCaseIncompl")); //$NON-NLS-1$
                    isAlreadySet = true;
                } 
                if (!isAlreadySet 
                        && !execTC.getCompleteTdFlag(locale)) {
                    addMessage(toolTip, I18n.getString("TestDataDecorator.TdInTestCaseIncompl")); //$NON-NLS-1$
                }
            }
            if (!execTC.getSumTdFlag(locale) 
                && execTC.getCompleteTdFlag(locale)) {
                
                addMessage(toolTip, I18n.getString("TestDataDecorator.TDChildrenIncompl")); //$NON-NLS-1$
            } else if (!execTC.getSumTdFlag(locale) 
                && !execTC.getCompleteTdFlag(locale)) {
                Iterator<IParamNodePO> it = execTC.getNodeListIterator();
                boolean tmpFlag = true;
                while (it.hasNext() && tmpFlag) {
                    IParamNodePO child = it.next();
                    if (child instanceof IExecTestCasePO) {
                        tmpFlag = tmpFlag && child.getSumTdFlag(locale);
                    } else {
                        tmpFlag = tmpFlag 
                            && child.getCompleteTdFlag(locale);
                    }
                }
                if (!tmpFlag) {
                    addMessage(toolTip, I18n.getString("TestDataDecorator.TDChildrenIncompl")); //$NON-NLS-1$
                }               
            }
            boolean atLeastOneEvData = false;

            if (!execTC.getSumSpecTcFlag()) {
                addMessage(toolTip, I18n.getString("TestDataDecorator.SpecTcChildrenMissing")); //$NON-NLS-1$
            }
            
            for (Object obj : execTC.getSpecTestCase()
                .getAllEventEventExecTC()) {

                IEventExecTestCasePO ev = (IEventExecTestCasePO)obj;
                if (!ev.getSumTdFlag(locale) && !atLeastOneEvData) {
                    addMessage(toolTip, I18n.getString("TestDataDecorator.TDEvIncompl")); //$NON-NLS-1$
                    atLeastOneEvData = true;
                }
            }
        }
        return toolTip;
    }

    /**
     * Appends whitespace (if needed), a bullet, and the given message to the
     * given StringBuilder.
     * 
     * @param toolTip The StringBuilder to which message will be appended.
     * @param message The message to append.
     */
    private void addMessage(StringBuilder toolTip, String message) {
        addWhitespace(toolTip);
        toolTip.append(Constants.BULLET).append(message);
    }
    
    /**
     * @param aut the AUT
     * @param locale the Loocale
     * @param cap the "real" model node
     * @param toolTip the tooltip
     * @param overWrittenName is component name orginal
     * @return the changed tooltip
     */
    private StringBuilder checkNode(IAUTMainPO aut, Locale locale, ICapPO cap,
        StringBuilder toolTip, boolean overWrittenName) {
        
        if (!overWrittenName
            && !cap.getCompleteOMFlag(aut)) {
            addMessage(toolTip, 
                I18n.getString("TestDataDecorator.OMInStepIncompl")); //$NON-NLS-1$
        }
        if (!cap.getCompleteTdFlag(locale)) {
            addMessage(toolTip, 
                I18n.getString("TestDataDecorator.TDInStepIncompl")); //$NON-NLS-1$
        }
        return toolTip; 
    }
    
    /**
     * @param tj
     *            the test job gui to check
     * @return whether invalid decoration is necessary
     */
    protected boolean isTestJobGuiValid(TestJobGUI tj) {
        // if aut does not contain children, do not decorate
        List<IRefTestSuitePO> refTsList = tj.getContent()
                .getUnmodifiableNodeList();
        for (IRefTestSuitePO refTs : refTsList) {
            if (TestExecution.isAutNameSet(refTs.getTestSuiteAutID())) {
                return false;
            }
        }
        return true;
    }
    

    /**
     * @param element
     *            the referenced test suite gui node
     * @return whether invalid decoration is necessary
     */
    protected boolean isRefTestSuiteGuiValid(RefTestSuiteGUI element) {
        IRefTestSuitePO refTs = (IRefTestSuitePO)element.getContent();
        if (TestExecution.isAutNameSet(refTs.getTestSuiteAutID())) {
            return false;
        }
        return true;
    }
}