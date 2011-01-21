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

import java.util.Locale;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.ui.businessprocess.WorkingLanguageBP;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.constants.Layout;
import org.eclipse.jubula.client.ui.controllers.dnd.LocalSelectionClipboardTransfer;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.model.CapGUI;
import org.eclipse.jubula.client.ui.model.ExecTestCaseGUI;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.model.SpecTestCaseGUI;
import org.eclipse.jubula.client.ui.model.TestCaseBrowserRootGUI;
import org.eclipse.jubula.client.ui.model.TestSuiteGUI;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;


/**
 * Base class for all label probider
 *
 * @author BREDEX GmbH
 * @created May 11, 2010
 */
public class GeneralLabelProvider extends ColumnLabelProvider 
    implements IColorProvider, ILabelProvider {
    /** close bracked */
    public static final String CLOSE_BRACKED = "]"; //$NON-NLS-1$

    /** open bracked */
    public static final String OPEN_BRACKED = " ["; //$NON-NLS-1$
    
    /** <code>SEPARATOR</code> */
    public static final String SEPARATOR = "; "; //$NON-NLS-1$
    
    /**
     * <code>UNNAMED_NODE</code>
     */
    private static final String UNNAMED_NODE = 
            Messages.GeneralGDLabelProviderUnnamedNode;
    /**
     * <code>COMMENT_PREFIX</code>
     */
    private static final String COMMENT_PREFIX = 
        Messages.AbstractGuiNodePropertySourceComment + StringConstants.COLON
        + StringConstants.SPACE;
    
    /** The color for disabled elements */
    private static final Color DISABLED_COLOR = Layout.GRAY_COLOR;
    /** The color for reusedProjects */
    private static final Color REUSED_PROJECTS_COLOR = Display.getDefault()
            .getSystemColor(SWT.COLOR_BLUE);
    
    /** clipboard */
    private static Clipboard clipboard = new Clipboard(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay());

    /** {@inheritDoc} */
    public void dispose() {
        // empty
    }

    /** {@inheritDoc} */
    public String getText(Object element) {
        return getGDText(element);
    }

    /** {@inheritDoc} */
    public Image getImage(Object element) {
        return getGDImage(element);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getToolTipText(Object element) {
        if (element instanceof GuiNode) {
            GuiNode gnode = (GuiNode)element;
            INodePO node = gnode.getContent();
            if (node != null) {
                StringBuilder toolTip = new StringBuilder();
                String comment = node.getComment();
                if (!StringUtils.isBlank(comment)) {
                    toolTip.append(COMMENT_PREFIX);
                    toolTip.append(ObjectUtils.toString(comment));
                    return toolTip.toString();
                }
            }
        }
        return super.getToolTipText(element);
    }
    

    /** {@inheritDoc} */
    public Point getToolTipShift(Object object) {
        return new Point(5, 5);
    }

    /** {@inheritDoc} */
    public int getToolTipDisplayDelayTime(Object object) {
        return 50;
    }

    /** {@inheritDoc} */
    public int getToolTipTimeDisplayed(Object object) {
        return 5000;
    }
    
    /** {@inheritDoc} */
    public Image getToolTipImage(Object object) {
        String tooltipText = getToolTipText(object);
        if (tooltipText != null) {
            if (tooltipText.startsWith(COMMENT_PREFIX)) {
                return IconConstants.INFO_IMAGE;
            }
            return IconConstants.ERROR_IMAGE;
        }
        return super.getToolTipImage(object);
    }
    
    /**
     * @param element the element to get the text for 
     * @return a descriptive text for the given element
     */
    public static String getGDText (Object element) {
        if (element instanceof GuiNode) {
            if (((GuiNode)element).getName() == null) {
                return UNNAMED_NODE;
            }
            GuiNode nodeGUI = (GuiNode)element;
            if (nodeGUI instanceof TestCaseBrowserRootGUI) {
                // because content is current project
                return nodeGUI.getName();
            }
            INodePO node = ((GuiNode)element).getContent();
            if (node != null) {
                StringBuilder builder = new StringBuilder(nodeGUI.getName());
                nodeGUI.getInfoString(builder);
                return builder.toString();
            }
            return nodeGUI.getName();
        }
        return element == null ? StringConstants.EMPTY : element.toString();
    }
    
    /**
     * @param element the element to get the image for 
     * @return an image for the given element
     */
    public static Image getGDImage(Object element) {
        if (element instanceof TestSuiteGUI) {
            TestSuiteGUI tsGUI = (TestSuiteGUI)element;
            ITestSuitePO ts = (ITestSuitePO)tsGUI.getContent();
            if (ts != null) {
                Locale workLang = WorkingLanguageBP.getInstance()
                    .getWorkingLanguage();
                if (ts.getAut() != null 
                    && !WorkingLanguageBP.getInstance().isTestSuiteLanguage(
                        workLang, ts)) {
                    return tsGUI.getDisabledImage();
                }
            }
        }
        if (element instanceof GuiNode) {
            GuiNode guiNode = (GuiNode)element;
            Object cbContents = clipboard
                    .getContents(LocalSelectionClipboardTransfer.getInstance());
            if (cbContents instanceof IStructuredSelection) {
                IStructuredSelection sel = (IStructuredSelection)cbContents;
                for (Object selObject : sel.toArray()) {
                    if (element == selObject) {
                        return guiNode.getCutImage();
                    }
                }
            }
            if (guiNode.getContent() != null
                    && guiNode.getContent().isGenerated()) {
                return guiNode.getGeneratedImage();
            }
            return guiNode.getImage();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Color getBackground(Object element) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Color getForeground(Object element) {
        if (element instanceof ExecTestCaseGUI || element instanceof CapGUI) {
            return DISABLED_COLOR;
        }
        if (element instanceof GuiNode && !((GuiNode)element).isEditable()) {
            if (element instanceof SpecTestCaseGUI) {
                return null;
            }
            if (element instanceof ExecTestCaseGUI 
                    || element instanceof CapGUI) {
                return DISABLED_COLOR;
            }
            return REUSED_PROJECTS_COLOR;
        }
        return null;
    }
}
