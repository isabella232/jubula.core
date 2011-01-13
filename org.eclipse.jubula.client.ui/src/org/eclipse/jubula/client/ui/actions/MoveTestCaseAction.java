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
package org.eclipse.jubula.client.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.CapBP;
import org.eclipse.jubula.client.core.businessprocess.UsedToolkitBP;
import org.eclipse.jubula.client.core.businessprocess.treeoperations.CollectComponentNameUsersOp;
import org.eclipse.jubula.client.core.datastructure.CompNameUsageMap;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ISpecObjContPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.dialogs.ReusedProjectSelectionDialog;
import org.eclipse.jubula.client.ui.model.CategoryGUI;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.model.SpecTestCaseGUI;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.client.ui.views.TestCaseBrowser;
import org.eclipse.jubula.client.ui.views.TreeBuilder;
import org.eclipse.jubula.toolkit.common.businessprocess.ToolkitSupportBP;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.toolkit.common.utils.ToolkitUtils;
import org.eclipse.jubula.tools.exception.GDException;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.messagehandling.MessageInfo;
import org.eclipse.jubula.tools.xml.businessmodell.Component;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;


/**
 * @author BREDEX GmbH
 * @created Oct 15, 2007
 */
public class MoveTestCaseAction extends Action {

    /**
     * A problem with moving a node.
     *
     * @author BREDEX GmbH
     * @created Oct 17, 2007
     */
    private static class MoveProblem {
        
        /** referenced test case */
        private SpecTestCaseGUI m_refNode;
        
        /** indicated problem node */
        private GuiNode m_problemNode; 

        /**
         * Constructor
         * 
         * @param problemNode The node that is causing the 
         *                    problem.
         * @param refNode The referenced test case that is causing the problem.
         */
        public MoveProblem(GuiNode problemNode, 
            SpecTestCaseGUI refNode) {
            
            m_problemNode = problemNode;
            m_refNode = refNode;
        }

        /**
         * 
         * @return The node that iscausing the problem.
         */
        public GuiNode getCause() {
            return m_problemNode;
        }
        
        /**
         * 
         * @return The reference that is causing the problem.
         */
        public SpecTestCaseGUI getReference() {
            return m_refNode;
        }
    }
    
    /**
     * Represents problems with moving one or more Test Cases.
     *
     * @author BREDEX GmbH
     * @created Oct 17, 2007
     */
    private static class ProblemSet {
        
        /** valid problems */
        private List<MoveProblem> m_problems = new ArrayList<MoveProblem>();
        
        /** list of nodes that are being moved */
        private List<GuiNode> m_nodesToMove;
        
        /**
         * Constructor
         * 
         * @param nodesToMove The nodes that are to be moved.
         */
        public ProblemSet(List<GuiNode> nodesToMove) {
            m_nodesToMove = new ArrayList<GuiNode>();
            for (GuiNode node : nodesToMove) {
                addCatChildren(node, m_nodesToMove);
            }
        }
        
        /**
         * Adds a problem to the set, if it is recognized as a valid problem.
         * If the problem is determined to be invalid, it will not actually be
         * added to the set.
         * 
         * @param problemNode The node that is causing the 
         *                    problem.
         * @param refTestCase The referenced test case that is causing the 
         *                    problem.
         */
        public void addProblem(GuiNode problemNode, 
            SpecTestCaseGUI refTestCase) {
            
            if (!m_nodesToMove.contains(refTestCase)) {
                m_problems.add(new MoveProblem(problemNode.getParentNode(), 
                    refTestCase));
            }
        }
        
        /**
         * 
         * @return List of valid problems.
         */
        public List<MoveProblem> getProblems() {
            return m_problems;
        }
    }
    
    /**
     * Constructor
     *
     */
    public MoveTestCaseAction() {
        super(I18n.getString("MoveTestCaseAction.Move")); //$NON-NLS-1$
        setEnabled(false);
    }
    
    /**
     * {@inheritDoc}
     */
    public void run() {
        move();
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void setEnabled(boolean enabled) {
        IProjectPO currentProject = GeneralStorage.getInstance().getProject();
        boolean projectAvailable = currentProject == null ? false
            : !currentProject.getUsedProjects().isEmpty();
        
        super.setEnabled(enabled && projectAvailable);
    }
    
    /**
     * moves the selection of the TC Browser to another project.
     */
    @SuppressWarnings("unchecked")
    private void move() {
        // Gather selected nodes
        TestCaseBrowser tcb = getSpecView();
        if (!(tcb.getSelection() instanceof IStructuredSelection)) {
            return;
        }
        IStructuredSelection sel = (IStructuredSelection)tcb.getSelection();
        List<GuiNode> selectionList = sel.toList();

        if (!closeRelatedEditors(selectionList)) {
            return;
        }
        
        // Check if move is valid
        ProblemSet moveProblems = getMoveProblem(selectionList);

        if (moveProblems.getProblems().isEmpty()) {
            Set<IReusedProjectPO> reusedProjects = 
                GeneralStorage.getInstance().getProject().getUsedProjects();

            List<String> projectNamesList = new ArrayList<String>();
            for (IReusedProjectPO project : reusedProjects) {
                projectNamesList.add(project.getName());
            }

            String [] projectNames = 
                projectNamesList.toArray(new String [projectNamesList.size()]);

            ReusedProjectSelectionDialog dialog = 
                new ReusedProjectSelectionDialog(
                    Plugin.getShell(), projectNames, 
                    I18n.getString("MoveTestCaseDialog.title"), //$NON-NLS-1$
                    I18n.getString("MoveTestCaseDialog.message"), //$NON-NLS-1$
                    IconConstants.MOVE_TC_DIALOG_STRING, 
                    I18n.getString("MoveTestCaseDialog.shellTitle")); //$NON-NLS-1$

            dialog.setHelpAvailable(true);
            dialog.create();
            DialogUtils.setWidgetNameForModalDialog(dialog);
            Plugin.getHelpSystem().setHelp(dialog.getShell(), 
                ContextHelpIds.TESTCASE_MOVE_EXTERNAL);
            dialog.open();
            if (dialog.getReturnCode() == Window.OK) {
                // Check which project was selected
                String selectedName = dialog.getSelectedName();
                IReusedProjectPO selectedProject = null;
                for (IReusedProjectPO project : reusedProjects) {
                    if (selectedName.equals(project.getName())) {
                        selectedProject = project;
                        break;
                    }
                }

                doMove(tcb, selectionList, selectedProject);
            }
        } else {
            showProblems(moveProblems);
        }
    }

    /**
     * Closes all editors that are related to elements in the given list.
     * 
     * @param selectionList List of GuiNodes.
     * @return <code>true</code> if all editors were successfully closed. 
     *         Otherwise, <code>false</code>.
     */
    private boolean closeRelatedEditors(List<GuiNode> selectionList) {
        List<IEditorReference> editorsToClose = 
            new ArrayList<IEditorReference>(); 
        for (GuiNode guiNode : selectionList) {
            IEditorReference editor = 
                Utils.getEditorRefByPO(guiNode.getContent());
            if (editor != null) {
                editorsToClose.add(editor);
            }
        }

        return Plugin.getActivePage().closeEditors(
                editorsToClose.toArray(
                        new IEditorReference[editorsToClose.size()]), 
                true);
    }

    /**
     * Performs the moving.
     * @param tcb the TestCase-Browser.
     * @param selectionList the selected Nodes to move.
     * @param selectedProject the selected Project to move to.
     */
    private void doMove(TestCaseBrowser tcb, List<GuiNode> selectionList, 
        IReusedProjectPO selectedProject) {
        // Prepare modification to selected project
        EntityManager sess = null;
        try {
            IProjectPO extProject = ProjectPM.loadReusedProject(
                selectedProject);
            sess = Hibernator.instance().openSession();
            extProject = (IProjectPO)sess.find(
                    NodeMaker.getProjectPOClass(), extProject.getId());
            List<ICapPO> moveProblem = getMoveProblem(extProject, 
                selectionList);
            if (!moveProblem.isEmpty()) {
                Utils.createMessageDialog(
                    MessageIDs.E_MOVE_TO_EXT_PROJ_ERROR_TOOLKITLEVEL,
                    null, null);
                return;
            }
            ISpecObjContPO newParent = extProject.getSpecObjCont();

            List<MultipleNodePM.AbstractCmdHandle> commands = 
                createCommands(selectionList, newParent, extProject);

            // Perform move
            MessageInfo errorMessageInfo = 
                MultipleNodePM.getInstance().executeCommands(
                        commands, null, sess);

            if (errorMessageInfo == null) {
                GeneralStorage.getInstance().getMasterSession().refresh(
                        GeneralStorage.getInstance().getProject()
                            .getSpecObjCont());
                
                // Reparent GuiNodes
                GuiNode parentNode = TreeBuilder.getGuiNodeByContent(
                        tcb.getRootGuiNode(), extProject);
                reparentGuiNodes(selectionList, parentNode);
                tcb.getTreeViewer().refresh();
            } else {
                Utils.createMessageDialog(
                        errorMessageInfo.getMessageId(), 
                        errorMessageInfo.getParams(), 
                        null);
            }
        } catch (GDException e) {
            Utils.createMessageDialog(e, null, null);
        } catch (ToolkitPluginException tpie) {
            Utils.createMessageDialog(MessageIDs.E_GENERAL_TOOLKIT_ERROR);
        } finally {
            Hibernator.instance().dropSession(sess);
        }
    }

    /**
     * Checks if the toolkit of the given selectionLists is compatible with 
     * the given {@link IProjectPO}
     * @param extProject the {@link IProjectPO} to move the given selectionList to.
     * @param selectionList the selectionList to move to the given {@link IProjectPO}
     * @return A List of {@link ICapPO}s which are incompatible or an empty List
     * if everything is OK.
     * @throws ToolkitPluginException in case of a ToolkitPlugin error.
     */
    private List<ICapPO> getMoveProblem(IProjectPO extProject, 
        List<GuiNode> selectionList) throws ToolkitPluginException {
        
        final List<ICapPO> problemCaps = new ArrayList<ICapPO>();
        final String extToolkitId = extProject.getToolkit();
        final String extToolkitLevel = ToolkitSupportBP.getToolkitLevel(
            extToolkitId);
        final List<ICapPO> caps = getCaps(selectionList);
        for (ICapPO cap : caps) {
            final String capLevel = UsedToolkitBP.getInstance()
                .getToolkitLevel(cap);
            final boolean capLessConcrete = !ToolkitUtils
                .isToolkitMoreConcrete(capLevel, extToolkitLevel);
            final Component component = CapBP.getComponent(cap);
            final String capToolkitID = component.getToolkitDesriptor()
                .getToolkitID();
            if (!(capLessConcrete || capToolkitID.equals(extToolkitId))) {
                problemCaps.add(cap);
            }
        }
        return problemCaps;
    }

    /**
     * Gets all {@link ICapPO}s which are direct or indirect children of the 
     * given List of {@link GuiNode}s
     * @param selectionList a List of {@link GuiNode}s
     * @return a List of {@link ICapPO}s
     */
    private List<ICapPO> getCaps(List<GuiNode> selectionList) {
        List<ICapPO> caps = new ArrayList<ICapPO>();
        for (GuiNode guiNode : selectionList) {
            final INodePO nodePO = guiNode.getContent();
            CapBP.getCaps(nodePO, caps);
        }
        return caps;
    }
    
    
    
    /**
     * Displays the problems for a proposed move operation.
     * 
     * @param moveProblems Valid problems with the proposed move operation.
     */
    private void showProblems(ProblemSet moveProblems) {
        // Display info as to why TCs could not be moved
        StringBuilder sb = new StringBuilder();
        for (MoveProblem moveProblem : moveProblems.getProblems()) {
            sb.append(moveProblem.getCause().getName());
            sb.append("\n"); //$NON-NLS-1$
        }
        Utils.createMessageDialog(MessageIDs.I_CANNOT_MOVE_TC, 
            null, new String [] {
                I18n.getString("InfoDetail.CANNOT_MOVE_TC",  //$NON-NLS-1$
                    new String [] {sb.toString()})
            });
    }

    /**
     * 
     * @param selectionList All nodes that are to be moved.
     * @param newParent The new parent for the nodes.
     * @param extProject where selected nodes moved to
     * 
     * @return The commands necessary to move the given nodes.
     */
    private List<MultipleNodePM.AbstractCmdHandle> createCommands(
        List<GuiNode> selectionList, 
        ISpecObjContPO newParent, IProjectPO extProject) throws GDException {
        
        List<MultipleNodePM.AbstractCmdHandle> commands = 
            new ArrayList<MultipleNodePM.AbstractCmdHandle>();
        
        CompNameUsageMap usageMap = new CompNameUsageMap();
        final String projGuid = 
            GeneralStorage.getInstance().getProject().getGuid();
        final Long projId = GeneralStorage.getInstance().getProject().getId();
        for (GuiNode selNode : selectionList) {
            commands.add(new MultipleNodePM.MoveNodeHandle(
                selNode.getContent(), 
                selNode.getParentNode().getContent(), 
                newParent));
            
            List<GuiNode> specTcs = new ArrayList<GuiNode>();
            List<ISpecTestCasePO> specTcPOs = new ArrayList<ISpecTestCasePO>();
            addCatChildren(selNode, specTcs);
            for (GuiNode spec : specTcs) {
                ISpecTestCasePO specTestCasePo = 
                    (ISpecTestCasePO)spec.getContent();
                specTcPOs.add(specTestCasePo);
                CollectComponentNameUsersOp op = 
                    new CollectComponentNameUsersOp(projGuid, projId);
                TreeTraverser trav = 
                    new TreeTraverser(specTestCasePo, op, true, 2);
                trav.traverse();
                usageMap.addAll(op.getUsageMap());
                for (IExecTestCasePO execTc 
                    : NodePM.getInternalExecTestCases(
                        specTestCasePo.getGuid(), 
                        specTestCasePo.getParentProjectId())) {
                    
                    commands.add(new MultipleNodePM.UpdateTestCaseRefHandle(
                            execTc, specTestCasePo));
                }
            }
            commands.add(new MultipleNodePM.UpdateParamNamesHandle(
                specTcPOs, extProject));
        }
        commands.add(new MultipleNodePM.TransferCompNameHandle(
                usageMap, GeneralStorage.getInstance().getProject().getId(),
                extProject));
        
        return commands;
    }
    
    /**
     * Reparents the nodes in the given list to the given node.
     * 
     * @param selectionList List of nodes to reparent.
     * @param newParent The new parent for the given nodes.
     */
    private void reparentGuiNodes(List<GuiNode> selectionList, 
        GuiNode newParent) {
  
        for (GuiNode node : selectionList) {
            node.setEditable(false);
            GuiNode oldParent = node.getParentNode();
            oldParent.removeNode(node);
            if (newParent != null) {
                newParent.addNode(node);
            }
        }
    }

    /**
     * Indicates whether there is a problem with moving the given selection. If
     * there is a problem, it is described by the return value.
     * 
     * @param selectionList The elements that are to be moved
     * @return <code>null</code> if their is no problem with moving the given
     *         items. Otherwise, returns a <code>String</code> that represents
     *         the problem.
     */
    private ProblemSet getMoveProblem(List<GuiNode> selectionList) {
        ProblemSet problems = new ProblemSet(selectionList);
        getMoveProblem(selectionList, problems);
        return problems;
    }

    /**
     * Indicates whether there is a problem with moving the given selection. If
     * there is a problem, it is described by the return value.
     * 
     * @param selectionList The elements that are to be moved
     * @param problems All problems with moving the given nodes.
     */
    private void getMoveProblem(List<GuiNode> selectionList, 
        ProblemSet problems) {
        
        for (GuiNode node : selectionList) {
            if (node.getContent() instanceof IExecTestCasePO) {
                ISpecTestCasePO refTestCase = 
                    ((IExecTestCasePO)node.getContent()).getSpecTestCase();
                if (refTestCase != null) {
                    Long curProjectId = 
                        GeneralStorage.getInstance().getProject().getId();
                    if (refTestCase.getParentProjectId().equals(curProjectId)) {
                        problems.addProblem(node, 
                            (SpecTestCaseGUI)recursivlyfindNode(
                                refTestCase, getSpecView().getRootGuiNode()));
                        
                    }
                }
            } else {
                getMoveProblem(node.getChildren(), problems);
            }
        }
    }

    /**
     * @return instance of TestCaseBrowser, or null.
     */
    private TestCaseBrowser getSpecView() {
        IViewPart viewPart = Plugin.getView(Constants.TC_BROWSER_ID);
        if (viewPart != null) {
            return (TestCaseBrowser)viewPart;
        }
        return null;
    }
    
    /**
     * Adds all spec testcase descendants of the given node to the given 
     * list.
     * 
     * @param parentNode The parent node
     * @param nodeList The node list.
     */
    private static void addCatChildren(
        GuiNode parentNode, Collection<GuiNode> nodeList) {
        
        if (parentNode instanceof CategoryGUI) {
            for (GuiNode node : parentNode.getChildren()) {
                addCatChildren(node, nodeList);
            }
        } else if (parentNode instanceof SpecTestCaseGUI) {
            nodeList.add(parentNode);
        }
    }

    /**
     * recursivly find the SpecTestCase to an ExecTestCase and shows it in Specification View
     * @param spec the SpecTestCase you are looking for
     * @param current the current GuiNode
     * @return GuiNode  
     */
    private GuiNode recursivlyfindNode(ISpecTestCasePO spec, 
        GuiNode current) {
        if (current.getContent().equals(spec)) {
            return current;
        }
        for (GuiNode nextNode : current.getChildren()) {
            GuiNode result = recursivlyfindNode(spec, nextNode);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

}
