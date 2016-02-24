/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.businessprocess.compcheck;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jubula.client.core.Activator;
import org.eclipse.jubula.client.core.businessprocess.problems.ProblemFactory;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.rules.SingleJobRule;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.client.core.utils.TreeTraverser;

/**
 * @author BREDEX GmbH
 * @created 27.10.2011
 */
public enum ProblemPropagator {
    /** Singleton */
    INSTANCE;
    
    /** the rule for the propagation Job */
    private static final ISchedulingRule PROPAGATIONRULE = new SingleJobRule();

    /** {@inheritDoc} */
    public void propagate() {
        final IProjectPO project = GeneralStorage.getInstance().getProject();
        if (project != null) {
            Job pp = new ProblemPropagationJob(
                    Messages.ProblemPropagationJobName, project);
            pp.setRule(PROPAGATIONRULE);
            pp.schedule(1000);
            
            for (Job job : Job.getJobManager().find(pp)) {
                if (job != pp) {
                    job.cancel();
                }
            }
        }
    }
    
    /**
     * @author BREDEX GmbH
     */
    private final class ProblemPropagationJob extends Job {
        /** the project */
        private final IProjectPO m_project;

        /**
         * @param name the name of the job
         * @param project the project to use
         */
        private ProblemPropagationJob(String name, IProjectPO project) {
            super(name);
            m_project = project;
        }

        /** {@inheritDoc} */
        public boolean belongsTo(Object family) {
            if (family instanceof ProblemPropagationJob) {
                return true;
            }
            return super.belongsTo(family);
        }
        
        /** {@inheritDoc} */
        protected IStatus run(IProgressMonitor monitor) {
            int status = IStatus.OK;
            try {
                TreeTraverser treeTraverser = new TreeTraverser(m_project,
                        new ProblemCleanupOperation(), false, true);
                treeTraverser.addOperation(
                        new ProblemPropagationOperation());
                treeTraverser.setMonitor(monitor);
                treeTraverser.traverse(true);
                
            } finally {
                if (monitor.isCanceled()) {
                    status = IStatus.CANCEL;
                } else {
                    DataEventDispatcher.getInstance()
                        .fireProblemPropagationFinished();
                }
                monitor.done();
            }
            
            return new Status(status, Activator.PLUGIN_ID, getName());
        }
    }

    /**
     * @author BREDEX GmbH
     */
    public static class ProblemCleanupOperation 
        extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {
        /** {@inheritDoc} */
        public boolean operate(ITreeTraverserContext<INodePO> ctx, 
            INodePO parent, INodePO node, boolean alreadyVisited) {
            if (alreadyVisited) {
                return false;
            }
            boolean hasHadProblems = false;
            hasHadProblems |= node.removeProblem(
                    ProblemFactory.ERROR_IN_CHILD);
            hasHadProblems |= node.removeProblem(
                    ProblemFactory.WARNING_IN_CHILD);
            return hasHadProblems;
        }
    }
    
    /**
     * @author BREDEX GmbH
     */
    public static class ProblemPropagationOperation 
        implements ITreeNodeOperation<INodePO> {
        /** {@inheritDoc} */
        public boolean operate(ITreeTraverserContext<INodePO> ctx, 
            INodePO parent, INodePO node, boolean alreadyVisited) {
            return node.isActive();
        }

        /** {@inheritDoc} */
        public void postOperate(ITreeTraverserContext<INodePO> ctx, 
            INodePO parent, INodePO node, boolean alreadyVisited) {
            if (ProblemFactory.hasProblem(node)) {
                setProblem(parent, ProblemFactory.getWorstProblem(
                        node.getProblems()).getStatus().getSeverity());
            }
        }

    }
    /**
     * @param node
     *            the node where the problem should be added.
     * @param severity
     *            severity of which the problem should be set
     */
    public static void setProblem(INodePO node, int severity) {
        switch (severity) {
            case IStatus.ERROR:
                node.addProblem(ProblemFactory.ERROR_IN_CHILD);
                break;
            case IStatus.WARNING:
                node.addProblem(ProblemFactory.WARNING_IN_CHILD);
                break;
            case IStatus.INFO:
            default:
                break;
        }
    }
}
