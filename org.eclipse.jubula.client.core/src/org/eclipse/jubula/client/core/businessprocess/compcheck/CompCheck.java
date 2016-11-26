/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.businessprocess.compcheck;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jubula.client.core.businessprocess.CompNameManager;
import org.eclipse.jubula.client.core.businessprocess.problems.IProblem;
import org.eclipse.jubula.client.core.businessprocess.problems.ProblemFactory;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IConditionalStatementPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.LogicComponentNotManagedException;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ConcreteComponent;

/**
 * Class responsible for collecting completeness information for AUTs
 * @author BREDEX GmbH
 * @created Aug 18, 2016
 */
public class CompCheck {

    /** Map Node Id => Set of Component Name guids which must be mapped in order to use the node */
    private Map<Long, Set<String>> m_mustMap;
    
    /** The original Test Suites */
    private List<ITestSuitePO> m_suites;
    
    /** The current AUT for Problem Collecting */
    private IAUTMainPO m_aut;
    
    /** A unique problem for the current AUT */
    private IProblem m_autProblem;
    
    /** The ID => unique problem for the AUTs */
    private Map<Long, IProblem> m_autProblems;
    
    /** 
     * Constructor
     * @param suites the suites
     **/
    public CompCheck(List<ITestSuitePO> suites) {
        m_mustMap = new HashMap<>();
        m_suites = suites;
    }

    /**
     * Collects the Component Name usage information for a list of Test Suites
     */
    public void traverse() {
        long start = System.currentTimeMillis();
        IAUTMainPO aut;
        IComponentIdentifier id;
        Set<String> problems;
        for (ITestSuitePO ts : m_suites) {
            aut = ts.getAut();
            if (aut != null) {
                traverseImpl(ts);
                problems = getProblematicGuids(ts); 
            }
        }
    }
    
    /**
     * The traverse implementation, bottom - up
     * @param node the current node, can be ITestSuitePO or IExecTestCasePO
     */
    private void traverseImpl(INodePO node) {
        // first we collect all used Component Names for all children of a node
        // and after this we calculate the usage for the node's SpecTC (not the node itself!)
        INodePO next;
        for (Iterator<INodePO> it = node.getAllNodeIter(); it.hasNext();) {
            next = it.next();
            // if m_mustMap contains the Id, that means we have already traversed the child
            if (getId(next) != null && !m_mustMap.containsKey(getId(next))
                    && !(next instanceof ICapPO)) {
                // if we have not yet dealt with the node (in case of an ExecTC the corresponding SpecTC!)
                // then we deal with it
                traverseImpl(next);
            }
        }
        // Finished with all children (Node and Event), next step is to fill the node's guid set
        Set<String> nodeGuids = new HashSet<>();
        Long id = getId(node);
        String guid;
        for (Iterator<INodePO> it = node.getAllNodeIter(); it.hasNext();) {
            handleNext(nodeGuids, it.next());
        }
        m_mustMap.put(id, nodeGuids);
    }
    
    /**
     * Puts the used guids to the given set
     * @param nodeGuids the set of used Guids
     * @param child the child of the SpecTC
     */
    private void handleNext(Set<String> nodeGuids, INodePO child) {
        if (!child.isActive()) {
            return;
        }
        String guid;
        if (child instanceof IExecTestCasePO) {
            handleExecTestCasePO(nodeGuids, (IExecTestCasePO) child);
        } else if (child instanceof ICapPO && isRelevant((ICapPO) child)) {
            guid = ((ICapPO) child).getComponentName();
            if (guid != null) {
                nodeGuids.add(CompNameManager.getInstance().resolveGuid(guid));
            }
        } else if (child instanceof IConditionalStatementPO) {
            for (Iterator<INodePO> it = child.getAllNodeIter(); it.hasNext();) {
                handleNext(nodeGuids, it.next());
            }
        }
    }
    
    /**
     * Handles an ExecTestCase child: we have to alter the Comp Names by the child's Comp Names Pairs
     * @param guids the guids
     * @param child the child
     */
    private void handleExecTestCasePO(Set<String> guids,
            IExecTestCasePO child) {
        // We designed the traverse such that the corresponding SpecTC must have been traversed before
        ISpecTestCasePO childSpecTC = child.getSpecTestCase();
        if (childSpecTC != null) {
            Set<String> childSpecGuids = m_mustMap.get(childSpecTC.getId());
            ICompNamesPairPO pair;
            for (String guid : childSpecGuids) {
                pair = child.getCompNamesPair(guid);
                if (pair != null) {
                    guids.add(CompNameManager.getInstance().
                            resolveGuid(pair.getSecondName()));
                } else {
                    guids.add(guid);
                }
            }
        }
    }
    
    /**
     * Returns the Id of the SpecTC for ExecTCs and the normal id for other nodes
     * @param node the node
     * @return the id or null if 
     */
    private Long getId(INodePO node) {
        if (node instanceof IExecTestCasePO) {
            ISpecTestCasePO specTC = ((IExecTestCasePO) node).getSpecTestCase();
            if (specTC != null) {
                return specTC.getId();
            }
            return null;
        }
        return node.getId();
    }
    
    /**
     * Adds Problem markers to Nodes: exactly those paths are marked
     * which start in a problematic TS and end at the last node where there
     * is still a chance to correct the map by changing a corresponding CNPair 
     */
    public void addProblems() {
        long time = System.currentTimeMillis();
        m_autProblems = new HashMap<>();
        for (ITestSuitePO ts : m_suites) {
            m_aut = ts.getAut();
            if (m_aut != null) {
                m_autProblem = m_autProblems.get(m_aut.getId());
                if (m_autProblem == null) {
                    m_autProblem = ProblemFactory.
                            createIncompleteObjectMappingProblem(m_aut);
                    m_autProblems.put(m_aut.getId(), m_autProblem);
                }
                Set<String> problems = getProblematicGuids(ts);
                if (!problems.isEmpty()) {
                    addProblemsImpl(ts, problems);
                }
            }
        }
    }
    
    /**
     * The recursive method adding the problems to the nodes
     * @param node the starting node
     * @param problemGuids the problematic guids at this node
     *      - these are not mapped, should be, and still has a chance to be corrected
     */
    private void addProblemsImpl(INodePO node, Set<String> problemGuids) {
        // ExecTestCasePOs are marked problematic even if the problemGuids is empty
        
        if (problemGuids.size() == 0 || !node.isActive()) {
            return;
        }

        node.addProblem(m_autProblem);
        
        INodePO child;
        for (Iterator<INodePO> it = node.getAllNodeIter(); it.hasNext();) {
            // adding problematic guids to Node children
            child = it.next();
            addProblemsImpl(child, problemHandleChild(child, problemGuids));
        }
    }
    
    /**
     * Calculates the problematic guids for a node
     * @param child the node
     * @param guids the problematic guids of the parent
     *        (these are not mapped by the AUT at the top of the current tree path)
     * @return the Set of problematic guids for the Node
     */
    private Set<String> problemHandleChild(INodePO child, Set<String> guids) {
        Set<String> result = new HashSet<String>(guids.size());
        if (child instanceof ICapPO && isRelevant((ICapPO) child)) {
            String guid = CompNameManager.getInstance().
                    resolveGuid(((ICapPO) child).getComponentName());
            if (guids.contains(guid)) {
                result.add(guid);
            }
        } else if (child instanceof IExecTestCasePO) {
            problemHandleExecTC((IExecTestCasePO) child, guids, result);
        } else if (child instanceof IConditionalStatementPO) {
            for (Iterator<INodePO> iterator = child.getAllNodeIter(); iterator
                    .hasNext();) {
                INodePO node = iterator.next();
                if (node instanceof IExecTestCasePO) {
                    problemHandleExecTC((IExecTestCasePO) node, guids, result);
                }
            }
        }
        return result;
    }
    
    /**
     * Collecting the problematic guids for an ExecTC
     * @param child the ExecTC
     * @param problemGuids the problematic guids of the parent
     *        (these are not mapped by the AUT at the top of the current tree path)
     * @param result the problematic guids for the ExecTC
     *        these are guids that are mapped to the problemGuids Set by the CompName
     *        pairs of the ExecTC
     */
    private void problemHandleExecTC(IExecTestCasePO child,
            Set<String> problemGuids, Set<String> result) {
        ISpecTestCasePO spec = child.getSpecTestCase();
        if (spec == null) {
            return;
        }
        for (String guid : m_mustMap.get(spec.getId())) {
            ICompNamesPairPO pair = child.getCompNamesPair(guid); 
            if (pair == null
                    && problemGuids.contains(guid)
                    && !specTCHasAutoGenPair(spec, guid)) {
                result.add(guid);
            } else if (pair != null 
                    && problemGuids.contains(pair.getSecondName())) {
                child.addProblem(m_autProblem);
            }
        }
    }
    
    /**
     * Decides whether the SpecTestCasePO has an auto-generated Comp Names Pair for the guid
     * @param spec the SpecTestCasePO
     * @param guid the guid
     * @return whether there is a Comp Names Pair
     */
    private boolean specTCHasAutoGenPair(ISpecTestCasePO spec, String guid) {
        for (Iterator<INodePO> it = spec.getAllNodeIter(); it.hasNext(); ) {
            INodePO node = it.next();
            if (node instanceof ICapPO) {
                String cNGuid = ((ICapPO) node).getComponentName();
                if (cNGuid != null && cNGuid.equals(guid)) {
                    return true;
                }
            } else if (node instanceof IExecTestCasePO) {
                for (ICompNamesPairPO pair : ((IExecTestCasePO) node)
                        .getCompNamesPairs()) {
                    if (pair.isPropagated() && guid.equals(
                            pair.getSecondName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Collecting those guids of a TS which should be mapped, but aren't
     * @param ts the TS
     * @return the set of unmapped guids
     */
    private Set<String> getProblematicGuids(ITestSuitePO ts) {
        Set<String> problemGuids = new HashSet<>();
        IComponentIdentifier id;
        IAUTMainPO aut = ts.getAut();
        for (String guid : m_mustMap.get(ts.getId())) {
            id = null;
            try {
                id = aut.getObjMap().getTechnicalName(guid);
            } catch (LogicComponentNotManagedException e) {
                // Nothing
            }
            if (id == null) {
                problemGuids.add(guid);
            }
        }
        return problemGuids;
    }      
    
    /**
     * Decides if the Component Name of a CAP needs mapping
     * @param cap the cap
     * @return the result
     */
    private boolean isRelevant(ICapPO cap) {
        Component metaComponentType = cap.getMetaComponentType();
        if (metaComponentType instanceof ConcreteComponent
                && ((ConcreteComponent) metaComponentType)
                        .hasDefaultMapping()) {
            return false;
        }
        return true;
    }
    
    /**
     * Returns the Suit ID => Set of guids of Component Names to map Map
     * @return the Map
     */
    public Map<Long, Set<String>> getCompNamesToMap() {
        Map<Long, Set<String>> result = new HashMap<>(m_suites.size());
        for (ITestSuitePO ts : m_suites) {
            if (ts.getAut() != null) {
                result.put(ts.getId(), m_mustMap.get(ts.getId()));
            }
        }
        return result;
    }
}