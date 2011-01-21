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
package org.eclipse.jubula.client.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ISpecObjContPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.core.persistence.ISpecPersistable;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.controllers.TreeIterator;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.model.CapGUI;
import org.eclipse.jubula.client.ui.model.CategoryGUI;
import org.eclipse.jubula.client.ui.model.EventExecTestCaseGUI;
import org.eclipse.jubula.client.ui.model.ExecTestCaseGUI;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.model.ProjectGUI;
import org.eclipse.jubula.client.ui.model.RefTestSuiteGUI;
import org.eclipse.jubula.client.ui.model.ReusedProjectGUI;
import org.eclipse.jubula.client.ui.model.SpecTestCaseGUI;
import org.eclipse.jubula.client.ui.model.TestCaseBrowserRootGUI;
import org.eclipse.jubula.client.ui.model.TestJobGUI;
import org.eclipse.jubula.client.ui.model.TestJobRootGUI;
import org.eclipse.jubula.client.ui.model.TestSuiteBrowserRootGUI;
import org.eclipse.jubula.client.ui.model.TestSuiteGUI;
import org.eclipse.jubula.client.ui.model.TestSuiteRootGUI;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.JBException;
import org.eclipse.jubula.tools.exception.JBFatalException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;


/**
 * @author BREDEX GmbH
 * @created 19.10.2004
 * 
 * 
 * This class builds new GUI-Trees for the TestSpecification- and
 * TestExecution-View when a saved file was opened.
 *  
 */
public class TreeBuilder {
    /** the name of the Test Job category */
    public static final String TJ_CAT_NAME = Messages.TSBCategoryTJ;
    
    /** the name of the Test Suite category */
    public static final String TS_CAT_NAME = Messages.TSBCategoryTS;

    /**
     * Constructor.
     */
    private TreeBuilder() {
        // private constructor
    }
    
    /**
     * Builds a GUI representation of the TestSpecificationTree. 
     * @param specTestCases a List of SpecTestCases.
     * @param reusedProjects a List of reused projects.
     * @return a SpecTestsuiteGUI
     */
    public static TestCaseBrowserRootGUI buildTestCaseBrowserTree(
        List<ISpecPersistable> specTestCases, 
        List<IReusedProjectPO> reusedProjects) {
        boolean childNodes = Plugin.getDefault().getPreferenceStore()
            .getBoolean(Constants.SHOW_TRANSIENT_CHILDREN_KEY);
        TestCaseBrowserRootGUI dummy = new TestCaseBrowserRootGUI("TestSpecTv_root"); //$NON-NLS-1$
        TestCaseBrowserRootGUI specTsGUI = 
            new TestCaseBrowserRootGUI(Messages.TreeBuilderTestCases);
        dummy.addNode(specTsGUI);
        Plugin.getDefault().setTestCaseBrowserRootGUI(dummy);
        // Add TCs to root:
        Iterator<ISpecPersistable> specObjIter = specTestCases.iterator();
        while (specObjIter.hasNext()) {
            ISpecPersistable nodePO = specObjIter.next();
            Class nodePOClass = Hibernator.getClass(nodePO);
            if (Hibernator.isPoClassSubclass(
                    nodePOClass, ISpecTestCasePO.class)) {
                SpecTestCaseGUI specTcGUI = new SpecTestCaseGUI(
                    nodePO.getName(), specTsGUI, nodePO);
                addEventHandlerToGuiModel(specTcGUI, childNodes);
            } else if (Hibernator.isPoClassSubclass(
                    nodePOClass, ICategoryPO.class)) {
                new CategoryGUI(nodePO.getName(), specTsGUI, nodePO);
            }
        }
        // Add all children to the root-TestCases:
        Iterator<GuiNode> childIter = specTsGUI.getChildren().iterator();
        while (childIter.hasNext()) {
            GuiNode nodeGUI = childIter.next();
            buildChildTree(nodeGUI.getContent(), nodeGUI, childNodes, null);
        }
        // Add reused project Categories/TCs
        for (IReusedProjectPO reused : reusedProjects) {
            buildSubTree(reused, specTsGUI, childNodes, null);
        }
        
        return dummy;
    }

    /**
     * Returns the GuiNode of the given content of the given tree root
     * @param treeRoot the root of the tree to search in
     * @param content the content whose GuiNode to search
     * @return the GuiNode or null if nothing found.
     */
    public static GuiNode getGuiNodeByContent(GuiNode treeRoot, 
        INodePO content) {
        
        TreeIterator treeIter = new TreeIterator(treeRoot);
        while (treeIter.hasNext()) {
            GuiNode nodeGUI = treeIter.next();
            if (nodeGUI != null) {
                if (content.equals(nodeGUI.getContent())) {
                    return nodeGUI;
                }
            }
        }
        return null;
    }

    /**
     * Builds a GUI representation of the TestExecutionTree. <br>
     * <b>Note: First call <code>buildTestSpecificationTree</code>!</b>
     * 
     * @param project
     *            The actual project.
     * @return a TestSuiteGUI
     */
    public static TestSuiteGUI buildTestSuiteBrowserTree(IProjectPO project) {
        TestSuiteGUI execTsGUIRoot = new TestSuiteBrowserRootGUI("Root"); //$NON-NLS-1$
        Plugin.getDefault().setTestSuiteBrowserRootGUI(execTsGUIRoot);
        buildChildTree(project, execTsGUIRoot, true, null);
        if (project.getTestSuiteCont().getTestSuiteList().size() > 0) {
            TestSuiteBrowser testExeView = (TestSuiteBrowser)Plugin
                    .getView(Constants.TS_BROWSER_ID);
            if (testExeView != null) {
                testExeView.setSelection(new StructuredSelection(execTsGUIRoot
                        .getChildren().get(0)));
            }
        }
        return execTsGUIRoot;
    }

    /**
     * Builds a GUI-Tree of the given INodePO-Root. <br>
     * <b>Note:</b> Every GuiNode in the GUI-Tree is unique!
     * @param root the root of the Model-Tree to be represented with GuiNodes
     * @param guiParent the parent of the GuiNode.
     * @param childNodes true to build more than one child node level
     * @param pos the position to  insert, if null, child will be added 
     * as last element.
     * @return GuiNode the assambled GuiNode
     */
    private static GuiNode buildChildTree(INodePO root, GuiNode guiParent, 
        boolean childNodes, Integer pos) {
        return buildChildTree(root, guiParent, childNodes, pos, true);
    }

    /**
     * Builds a GUI-Tree of the given INodePO-Root. <br>
     * <b>Note:</b> Every GuiNode in the GUI-Tree is unique!
     * @param root the root of the Model-Tree to be represented with GuiNodes
     * @param guiParent the parent of the GuiNode.
     * @param childNodes true to build more than one child node level
     * @param pos the position to  insert, if null, child will be added 
     * as last element.
     * @param isEditable whether or not this subtree is editable
     * @return GuiNode the assambled GuiNode
     */
    private static GuiNode buildChildTree(INodePO root, GuiNode guiParent, 
        boolean childNodes, Integer pos, boolean isEditable) {
        Iterator iter = root.getNodeListIterator();
        GuiNode parentGUI = guiParent;
        GuiNode tsParentGUI = null;
        GuiNode tjParentGUI = null;
        if (Hibernator.isPoSubclass(root, IProjectPO.class)) {
            IProjectPO project = (IProjectPO)root;
            parentGUI = new ProjectGUI(project.getName(), parentGUI, root);
            tsParentGUI = new TestSuiteRootGUI(TS_CAT_NAME, parentGUI, null); //$NON-NLS-1$
            tjParentGUI = new TestJobRootGUI(TJ_CAT_NAME, parentGUI, null); //$NON-NLS-1$
            List<INodePO> ltstj = new ArrayList<INodePO>();
            ltstj.addAll(project.getTestSuiteCont().getTestSuiteList());
            ltstj.addAll(project.getTestJobCont().getTestJobList());
            iter = ltstj.iterator();
        }
        if (Hibernator.isPoSubclass(root, IExecTestCasePO.class)) {
            return parentGUI;
        }
        Integer position = pos;
        while (iter.hasNext()) {
            INodePO nodePO = (INodePO)iter.next();
            Class nodePOClass = Hibernator.getClass(nodePO);
            if (Hibernator.isPoClassSubclass(nodePOClass, ICapPO.class)) {
                buildSubTree((ICapPO)nodePO, parentGUI, isEditable);
            } else if (Hibernator.isPoClassSubclass(nodePOClass,
                    ISpecTestCasePO.class)) {
                buildSubTree((ISpecTestCasePO)nodePO, parentGUI, childNodes,
                        position, isEditable);
            } else if (Hibernator.isPoClassSubclass(nodePOClass,
                    IExecTestCasePO.class)) {
                buildSubTree((IExecTestCasePO)nodePO, parentGUI, childNodes,
                        position, isEditable);
            } else if (Hibernator.isPoClassSubclass(nodePOClass,
                    ITestSuitePO.class)) {
                buildSubTree((ITestSuitePO)nodePO, tsParentGUI, childNodes,
                        position);
            } else if (Hibernator.isPoClassSubclass(nodePOClass,
                    ITestJobPO.class)) {
                buildSubTree((ITestJobPO)nodePO, tjParentGUI, childNodes,
                        position);
            } else if (Hibernator.isPoClassSubclass(nodePOClass,
                    IRefTestSuitePO.class)) {
                buildSubTree((IRefTestSuitePO)nodePO, parentGUI, childNodes,
                        position);
            } else if (Hibernator.isPoClassSubclass(nodePOClass,
                    ICategoryPO.class)) {
                buildSubTree((ICategoryPO)nodePO, parentGUI, childNodes,
                        position, isEditable);
            }

            // use position only in first iteration
            position = null;
        }
        return parentGUI;
    }

    /**
     * Builds a GUI representation of a subtree which starts with 
     * a RefTestSuitePO
     * @param nodePO the ref test suite po
     * @param tjParentGUI the parent test job gui node
     * @param childNodes true to build more than one child node level
     * @param position the position
     * @return the Guinode.
     */
    private static GuiNode buildSubTree(IRefTestSuitePO nodePO,
            GuiNode tjParentGUI, boolean childNodes, Integer position) {
        return buildSubTree(nodePO, tjParentGUI, childNodes, position, true);
    }


    /**
     * Builds a GUI representation of a subtree which starts with 
     * a TestSuitePO.
     * @param testSuite the TestSuitePO.
     * @param parentGUI parentGUI the parent of the GUI subtree. Could be null in some cases!
     * @param childNodes true to build more than one child node level
     * @param pos pos the position to insert. Has no effect here, should be null.
     * @return the Guinode.
     */
    private static GuiNode buildSubTree(ITestSuitePO testSuite,
            GuiNode parentGUI, boolean childNodes, Integer pos) {
        return buildSubTree(testSuite, parentGUI, childNodes, pos, true);
    }
    
    /**
     * Builds a GUI representation of a subtree which starts with 
     * a TestJobPO.
     * @param testJob the TestJobPO.
     * @param parentGUI parentGUI the parent of the GUI subtree. Could be null in some cases!
     * @param childNodes true to build more than one child node level
     * @param pos pos the position to insert. Has no effect here, should be null.
     * @return the Guinode.
     */
    private static GuiNode buildSubTree(ITestJobPO testJob, GuiNode parentGUI,
            boolean childNodes, Integer pos) {
        return buildSubTree(testJob, parentGUI, childNodes, pos, true);
    }

    /**
     * Builds a GUI representation of a subtree which starts with 
     * a TestSuitePO.
     * @param testSuite the TestSuitePO.
     * @param parentGUI parentGUI the parent of the GUI subtree. Could be null in some cases!
     * @param childNodes true to build more than one child node level
     * @param pos pos the position to insert. Has no effect here, should be null.
     * @param isEditable whether or not this subtree is editable
     * @return the Guinode.
     */
    private static GuiNode buildSubTree(ITestSuitePO testSuite,
            GuiNode parentGUI, boolean childNodes, Integer pos,
            boolean isEditable) {
        TestSuiteGUI execTsGUI = null;
        if (parentGUI == null) {
            execTsGUI = new TestSuiteGUI(testSuite.getName(), testSuite,
                    isEditable);
        } else {
            execTsGUI = new TestSuiteGUI(testSuite.getName(), parentGUI,
                    testSuite, isEditable);
        }
        buildChildTree(testSuite, execTsGUI, childNodes, pos);
        return execTsGUI;
    }

    /**
     * Builds a GUI representation of a subtree which starts with 
     * a RefTestSuitePO.
     * @param reftestSuite the TestSuitePO.
     * @param parentGUI parentGUI the parent of the GUI subtree.
     * @param childNodes true to build more than one child node level
     * @param pos pos the position to insert. Has no effect here, should be null.
     * @param isEditable whether or not this subtree is editable
     * @return the Guinode.
     */
    private static GuiNode buildSubTree(IRefTestSuitePO reftestSuite,
            GuiNode parentGUI, boolean childNodes, Integer pos,
            boolean isEditable) {
        RefTestSuiteGUI refTsGUI = new RefTestSuiteGUI(reftestSuite.getName(),
                parentGUI, reftestSuite, isEditable);
        buildChildTree(reftestSuite, refTsGUI, childNodes, pos);
        return refTsGUI;
    }
    
    /**
     * Builds a GUI representation of a subtree which starts with 
     * a TestJobPO.
     * @param testJob the TestJobPO.
     * @param parentGUI parentGUI the parent of the GUI subtree. Could be null in some cases!
     * @param childNodes true to build more than one child node level
     * @param pos pos the position to insert. Has no effect here, should be null.
     * @param isEditable whether or not this subtree is editable
     * @return the Guinode.
     */
    private static GuiNode buildSubTree(ITestJobPO testJob, GuiNode parentGUI,
            boolean childNodes, Integer pos, boolean isEditable) {
        TestJobGUI execTsGUI = null;
        if (parentGUI == null) {
            execTsGUI = new TestJobGUI(testJob.getName(), testJob, isEditable);
        } else {
            execTsGUI = new TestJobGUI(testJob.getName(), parentGUI, testJob,
                    isEditable);
        }
        buildChildTree(testJob, execTsGUI, childNodes, pos);
        return execTsGUI;
    }
    
    /**
     * Builds a GUI representation of a subtree which starts with 
     * an CapPO.
     * @param capPO the CapPO
     * @param parentGUI the parent of the GUI subtree.
     * @param monitor The progress monitor for this potentially long-running 
     *                operation.
     * @return the Guinode.
     */
    private static GuiNode buildSubTree(
        ICapPO capPO, GuiNode parentGUI, IProgressMonitor monitor) {
        
        return buildSubTree(capPO, parentGUI, true);
    }

    /**
     * Builds a GUI representation of a subtree which starts with 
     * an CapPO.
     * @param capPO the CapPO
     * @param parentGUI the parent of the GUI subtree.
     * @param isEditable whether or not this subtree is editable
     * @return the Guinode.
     */
    private static GuiNode buildSubTree(
        ICapPO capPO, GuiNode parentGUI, boolean isEditable) {
        
        return new CapGUI(capPO.getName(), 
            (SpecTestCaseGUI)parentGUI, capPO, isEditable);
    }

    /**
     * Builds a GUI representation of a subtree which starts with 
     * an ExecTestCasePO
     * @param execTestCase the ExecTestCasePO.
     * @param parentGUI the parent of the GUI subtree.
     * @param childNodes true to build more than one child node level
     * @param pos the position to add the Exec Node into the parent.
     * @return an ExecTestCaseGUI node.
     */
    private static GuiNode buildSubTree(IExecTestCasePO execTestCase,
            GuiNode parentGUI, boolean childNodes, Integer pos) {
        return buildSubTree(execTestCase, parentGUI, childNodes, pos, true);
    }
    
    /**
     * Builds a GUI representation of a subtree which starts with 
     * an ExecTestCasePO
     * @param execTestCase the ExecTestCasePO.
     * @param parentGUI the parent of the GUI subtree.
     * @param childNodes true to build more than one child node level
     * @param pos the position to add the Exec Node into the parent.
     * @param isEditable whether or not this subtree is editable
     * @return an ExecTestCaseGUI node.
     */
    private static GuiNode buildSubTree(
        IExecTestCasePO execTestCase, GuiNode parentGUI, boolean childNodes,
        Integer pos, boolean isEditable) {
        
        ExecTestCaseGUI execTcGUI = null;
        if (Hibernator.isPoSubclass(execTestCase, IEventExecTestCasePO.class)) {
            execTcGUI = new EventExecTestCaseGUI(execTestCase.getName(), 
                (SpecTestCaseGUI)parentGUI, (IEventExecTestCasePO) 
                execTestCase, isEditable);
        } else if (pos == null) {
            execTcGUI = new ExecTestCaseGUI(
                execTestCase.getName(), parentGUI, null, 
                execTestCase, isEditable);
        } else {
            execTcGUI = new ExecTestCaseGUI(
                execTestCase.getName(), parentGUI, null, execTestCase, 
                pos, isEditable);
        }
        ISpecTestCasePO specTcPO = execTestCase.getSpecTestCase();
        if (specTcPO != null && childNodes) {
            SpecTestCaseGUI specTcGUI;
            if (specTcPO.getParentProjectId().equals(
                GeneralStorage.getInstance().getProject().getId())) {
                
                specTcGUI = (SpecTestCaseGUI)buildSubTree(
                    specTcPO, execTcGUI, childNodes, null, isEditable); 
            } else {
                specTcGUI = (SpecTestCaseGUI)buildSubTree(
                    specTcPO, execTcGUI, childNodes, null, false);
            }
            execTcGUI.setSpecTestCase(specTcGUI);
        } else {
            execTcGUI.setSpecTestCase(null);
        }
        
        return execTcGUI;
    }
   
    
    /**
     * Builds a GUI representation of a subtree which starts with 
     * a SpecTestCasePO
     * @param specTestCase the SpecTestCasePO.
     * @param parentGUI the parent of the GUI subtree.
     * @param childNodes true to build more than one child node level
     * @param pos the position to insert, can be null.
     * @param monitor The progress monitor for this potentially long-running 
     *                operation.
     * @return the SpecTestCaseGUI.
     */
    private static GuiNode buildSubTree(
        ISpecTestCasePO specTestCase, GuiNode parentGUI, boolean childNodes,
        Integer pos, IProgressMonitor monitor) {
        return buildSubTree(specTestCase, parentGUI, childNodes, pos, true);
    }
    
    /**
     * Builds a GUI representation of a subtree which starts with 
     * a SpecTestCasePO
     * @param specTestCase the SpecTestCasePO.
     * @param parentGUI the parent of the GUI subtree.
     * @param childNodes true to build more than one child node level
     * @param pos the position to insert, can be null.
     * @param isEditable whether or not this subtree is editable
     * @return the SpecTestCaseGUI.
     */
    private static GuiNode buildSubTree(ISpecTestCasePO specTestCase, 
        GuiNode parentGUI, boolean childNodes, Integer pos, 
        boolean isEditable) {
        SpecTestCaseGUI specTcGUI = new SpecTestCaseGUI(
                specTestCase.getName(), 
                parentGUI, specTestCase, pos, isEditable);
        GuiNode specTestCaseGUI;

        // build children of specTestCase
        specTestCaseGUI = buildChildTree(specTestCase, specTcGUI, childNodes, 
                null, isEditable);
        addEventHandlerToGuiModel(
                (SpecTestCaseGUI)specTestCaseGUI, childNodes);

        return specTestCaseGUI;
    }
    
    /**
     * Adds the EventHandler of an SpecTestCaseGUI to the GUI-Model.<BR> 
     * <b>Note:</b> In the GUI-Model the EventHandler are children of the SpecTestCaseGUI.
     * @param specTcGUI the parent to add the EventHandler.
     * @param childNodes true to build more than one child node level
     */
    private static void addEventHandlerToGuiModel(SpecTestCaseGUI specTcGUI,
        boolean childNodes) {
        ISpecTestCasePO specTcPO = (ISpecTestCasePO)specTcGUI.getContent();
        for (Object obj : specTcPO.getEventExecTcMap().values()) {
            IEventExecTestCasePO evExecTcPO = (IEventExecTestCasePO)obj;
            buildSubTree(evExecTcPO, specTcGUI, childNodes, null);
        }
    }
     
    /**
     * Builds a GUI representation of a subtree which starts with a CategoryPO
     * @param category the CategoryPO
     * @param parentGUI the parent of the GUI subtree.
     * @param childNodes true to build more than one child node level
     * @param pos pos the position to insert. Has no effect here, should be null.
     * @return the CategoryGUI.
     */
    private static GuiNode buildSubTree(ICategoryPO category,
            GuiNode parentGUI, boolean childNodes, Integer pos) {
        return buildSubTree(category, parentGUI, childNodes, pos, true);
    }
 
    /**
     * Builds a GUI representation of a subtree which starts with a CategoryPO
     * @param category the CategoryPO
     * @param parentGUI the parent of the GUI subtree.
     * @param childNodes true to build more than one child node level
     * @param pos pos the position to insert. Has no effect here, should be null.
     * @param isEditable whether or not this subtree is editable
     * @return the CategoryGUI.
     */
    private static GuiNode buildSubTree(ICategoryPO category,
            GuiNode parentGUI, boolean childNodes, Integer pos,
            boolean isEditable) {
        CategoryGUI catGUI = new CategoryGUI(category.getName(), parentGUI,
                category, isEditable);
        GuiNode categoryGUI = buildChildTree(category, catGUI, childNodes, pos,
                isEditable);
        return categoryGUI;
    }
    
    /**
     * Builds a GUI representation of a subtree which starts with a 
     * ReusedProjectPO.
     * 
     * @param reused the ReusedProjectPO
     * @param parentGUI the parent of the GUI subtree.
     * @param childNodes true to build more than one child node level
     * @param pos pos the position to insert. Has no effect here, should be null.
     * @return the CategoryGUI.
     */
    private static GuiNode buildSubTree(IReusedProjectPO reused,
            GuiNode parentGUI, boolean childNodes, Integer pos) {
        IProjectPO reusedProject;
        try {
            reusedProject = ProjectPM.loadReusedProjectInMasterSession(reused);

            ReusedProjectGUI reusedGUI = new ReusedProjectGUI(reused
                    .getProjectGuid(), parentGUI, reusedProject, reused
                    .getVersionString());
            ISpecObjContPO specObjCont = reusedProject == null ? null
                    : reusedProject.getSpecObjCont();
            GuiNode reusedProjectGUI = buildChildTree(specObjCont, reusedGUI,
                    childNodes, pos, false);
            return reusedProjectGUI;

        } catch (JBException e) {
            Utils.createMessageDialog(e, null, null);
            return new ReusedProjectGUI(reused.getProjectGuid(), parentGUI,
                    null, reused.getVersionString());
        }
    }

    /**
     * Builds a GUI-Tree of the given ISpecObjectContPO-Root. <br>
     * <b>Note:</b> Every GuiNode in the GUI-Tree is unique!
     * @param root the root of the Model-Tree to be represented with GuiNodes
     * @param guiParent the parent of the GuiNode.
     * @param childNodes true to build more than one child node level
     * @param pos the position to  insert, if null, child will be added 
     * as last element.
     * @param isEditable whether or not this subtree is editable
     * @return GuiNode the assembled GuiNode
     */
    private static GuiNode buildChildTree(ISpecObjContPO root,
            ReusedProjectGUI guiParent, boolean childNodes, Integer pos,
            boolean isEditable) {
        if (root == null) {
            return guiParent;
        }
        Iterator<ISpecPersistable> iter = root.getSpecObjList().iterator();
        GuiNode parentGUI = guiParent;
        Integer position = pos;
        while (iter.hasNext()) {
            INodePO nodePO = iter.next();
            Class nodePOClass = Hibernator.getClass(nodePO);
            if (Hibernator.isPoClassSubclass(
                    nodePOClass, ICapPO.class)) {
                buildSubTree((ICapPO)nodePO, parentGUI, isEditable);
            } else if (Hibernator.isPoClassSubclass(
                    nodePOClass, ISpecTestCasePO.class)) {
                buildSubTree((ISpecTestCasePO)nodePO, parentGUI, childNodes, 
                        position, isEditable);
            } else if (Hibernator.isPoClassSubclass(
                    nodePOClass, IExecTestCasePO.class)) {
                buildSubTree((IExecTestCasePO)nodePO, parentGUI, childNodes, 
                        position, isEditable);
            } else if (Hibernator.isPoClassSubclass(
                    nodePOClass, ITestSuitePO.class)) {
                buildSubTree((ITestSuitePO)nodePO, parentGUI, childNodes, 
                        position, isEditable);
            } else if (Hibernator.isPoClassSubclass(
                    nodePOClass, ICategoryPO.class)) {
                buildSubTree((ICategoryPO)nodePO, parentGUI, childNodes, 
                        position, isEditable);
            }
            // use position only in first iteration
            position = null;
        }
        return parentGUI;
    }


    /**
     * Builds a subtree of GuiNodes beginning with the given root as content
     * and the given GuiNode as parent of the root and the given position to 
     * insert the root into the parent.
     * @param root the root of the InodePO tree.
     * @param parentGUI the parent for the subtree to build.
     * @param childNodes true to build more than one child node level
     * @param pos the beginning position of the subtree in the parent.
     * @param monitor The progress monitor for this potentially long-running 
     *                operation.
     * @return The GuiNode subtree beginning with the GuiNode of the given root.
     */
    public static GuiNode buildSubTree(INodePO root, GuiNode parentGUI,
            boolean childNodes, Integer pos, IProgressMonitor monitor) {
        Class rootClass = Hibernator.getClass(root);
        if (Hibernator.isPoClassSubclass(rootClass, ICapPO.class)) {
            return buildSubTree((ICapPO)root, parentGUI, monitor);
        } else if (Hibernator.isPoClassSubclass(rootClass, ICategoryPO.class)) {
            return buildSubTree((ICategoryPO)root, parentGUI, childNodes, pos);
        } else if (Hibernator.isPoClassSubclass(rootClass,
                IExecTestCasePO.class)) {
            return buildSubTree((IExecTestCasePO)root, parentGUI, childNodes,
                    pos);
        } else if (Hibernator.isPoClassSubclass(rootClass,
                ISpecTestCasePO.class)) {
            return buildSubTree((ISpecTestCasePO)root, parentGUI, childNodes,
                    pos, monitor);
        } else if (Hibernator
                .isPoClassSubclass(rootClass, ITestSuitePO.class)) {
            return buildSubTree((ITestSuitePO)root, parentGUI, childNodes, pos);
        } else if (Hibernator.isPoClassSubclass(rootClass, ITestJobPO.class)) {
            return buildSubTree((ITestJobPO)root, parentGUI, childNodes, pos);
        } else if (Hibernator.isPoClassSubclass(rootClass,
                IRefTestSuitePO.class)) {
            return buildSubTree((IRefTestSuitePO)root, parentGUI, childNodes,
                    pos);
        }
        throw new JBFatalException(Messages.UnsupportedBuildSubTree 
                + StringConstants.COLON + StringConstants.DOT
                + root.getClass().getName(), MessageIDs.E_UNSUPPORTED_NODE);
    }
    
    
    /**
     * Builds a GUI representation of a subtree which starts with 
     * a TestSuitePO.
     * @param testSuite the TestSuitePO.
     * @param rootGUI parentGUI the parent of the GUI subtree. Could be null in some cases!
     * @return the Guinode.
     */
    public static GuiNode buildTestSuiteEditorTree (
        ITestSuitePO testSuite, GuiNode rootGUI) {

        // build test suite
        TestSuiteGUI execTsGUI = null;
        if (rootGUI == null) {
            execTsGUI = 
                new TestSuiteGUI(testSuite.getName(), testSuite);
        } else {
            execTsGUI = 
                new TestSuiteGUI(testSuite.getName(), rootGUI, testSuite);
        }
        
        // build children
        Iterator iter = testSuite.getNodeListIterator();
        while (iter.hasNext()) {
            INodePO nodePO = (INodePO)iter.next();
            if (nodePO instanceof IExecTestCasePO) {
                IExecTestCasePO execTestCase = (IExecTestCasePO)nodePO;
                new ExecTestCaseGUI(
                    execTestCase.getName(), execTsGUI, null, execTestCase);
            }
        }
        return  execTsGUI;
    }

    /**
     * @param testJob
     *            the test job
     * @param rootGUI
     *            rootGUI parentGUI the parent of the GUI subtree.
     * @return the Guinode.
     */
    public static GuiNode buildTestJobEditorTree(ITestJobPO testJob,
            GuiNode rootGUI) {
        // build test job
        TestJobGUI tj = new TestJobGUI(testJob.getName(), rootGUI, testJob);
        // build children
        Iterator iter = testJob.getNodeListIterator();
        while (iter.hasNext()) {
            INodePO nodePO = (INodePO)iter.next();
            if (nodePO instanceof IRefTestSuitePO) {
                IRefTestSuitePO rts = (IRefTestSuitePO)nodePO;
                new RefTestSuiteGUI(rts.getName(), tj, rts);
            }
        }
        return tj;
    }
    
    /**
     * Builds a GUI representation of a subtree which starts with 
     * a ISpecTestCasePO.
     * @param specTestCase the ISpecTestCasePO
     * @param rootGUI parentGUI the parent of the GUI subtree. Could be null in some cases!
     * @return the Guinode.
     */
    public static GuiNode buildTestCaseEditorTopTree (
        ISpecTestCasePO specTestCase, GuiNode rootGUI) {

        // build SpecTestCase
        SpecTestCaseGUI specTcGUI = new SpecTestCaseGUI(
                specTestCase.getName(), rootGUI, specTestCase, null);
        
        // build children
        Iterator iter = specTestCase.getNodeListIterator();
        while (iter.hasNext()) {
            INodePO nodePO = (INodePO)iter.next();
            if (nodePO instanceof IExecTestCasePO) {
                IExecTestCasePO execTestCase = (IExecTestCasePO)nodePO;
                new ExecTestCaseGUI(
                    execTestCase.getName(), specTcGUI, null, execTestCase);
            }
            if (nodePO instanceof ICapPO) {
                ICapPO capPO = (ICapPO)nodePO;
                new CapGUI(capPO.getName(), specTcGUI, capPO);
            }
        }
        return  specTcGUI;
    }

    /**
     * Builds a GUI representation of a subtree which starts with 
     * a ISpecTestCasePO.
     * @param specTestCase the ISpecTestCasePO
     * @param rootGUI parentGUI the parent of the GUI subtree. Could be null in some cases!
     * @return the Guinode.
     */
    public static GuiNode buildTestCaseEditorBottomTree (
        ISpecTestCasePO specTestCase, GuiNode rootGUI) {

        // build SpecTestCase
        SpecTestCaseGUI specTcGUI = new SpecTestCaseGUI(
                specTestCase.getName(), rootGUI, specTestCase, null);

        // build children
        Iterator iter = specTestCase.getAllEventEventExecTC().iterator();
        while (iter.hasNext()) {
            INodePO nodePO = (INodePO)iter.next();
            if (nodePO instanceof IEventExecTestCasePO) {
                IEventExecTestCasePO execTestCase = 
                    (IEventExecTestCasePO)nodePO;
                new EventExecTestCaseGUI(execTestCase.getName(), 
                    specTcGUI, execTestCase);
            }
        }
        return  specTcGUI;
    }

}
