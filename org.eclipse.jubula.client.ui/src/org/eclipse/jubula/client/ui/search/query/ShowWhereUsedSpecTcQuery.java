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
package org.eclipse.jubula.client.ui.search.query;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.search.result.BasicSearchResult.NodeSearchResultElementAction;
import org.eclipse.jubula.client.ui.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.jubula.tools.exception.JBException;
import org.eclipse.jubula.tools.i18n.I18n;


/**
 * @author BREDEX GmbH
 * @created Jul 27, 2010
 */
public class ShowWhereUsedSpecTcQuery 
    extends AbstractShowWhereUsedQuery {
    /**
     * <code>m_specTC</code>
     */
    private ISpecTestCasePO m_specTC;

    /**
     * @param specTC
     *            the spec tc to search the reuse for
     */
    public ShowWhereUsedSpecTcQuery(ISpecTestCasePO specTC) {
        setSpecTC(specTC);
    }

    /**
     * {@inheritDoc}
     */
    public IStatus run(IProgressMonitor monitor) {
        calculateReuseOfSpecTestCase(getSpecTC(), monitor);
        return Status.OK_STATUS;
    }
    
    /**
     * calculates and show the places of reuse for a spectestcase
     * 
     * @param specTC
     *            ISpecTestCasePO
     * @param monitor
     *            the progress monitor
     */
    protected void calculateReuseOfSpecTestCase(ISpecTestCasePO specTC, 
            IProgressMonitor monitor) {
        IProjectPO currentProject = GeneralStorage.getInstance().getProject();
        List<Long> parentProjectIDs = new LinkedList<Long>();
        parentProjectIDs.add(currentProject.getId());
        for (IReusedProjectPO rp : currentProject.getUsedProjects()) {
            try {
                Long projID = ProjectPM.findProjectId(rp.getProjectGuid(), rp
                        .getMajorNumber(), rp.getMinorNumber());
                if (projID != null) {
                    parentProjectIDs.add(projID);
                }
            } catch (JBException e) {
                // ignore
            }
        }
        List<IExecTestCasePO> reuse = NodePM.getExecTestCases(specTC.getGuid(),
                parentProjectIDs);
        final List<SearchResultElement> reuseLoc = 
            new ArrayList<SearchResultElement>(
                reuse.size());
        List<INodePO> parentList = new ArrayList<INodePO>();
        
        monitor.beginTask("Searching for reusage of Test Case", reuse.size()); //$NON-NLS-1$
        for (IExecTestCasePO execTC : reuse) {
            INodePO parent = execTC.getParentNode();
            if (parent != null) {
                Long id = execTC.getId();
                String name = execTC.getName();
                parentList.add(parent);
                String resultName = parent.getName() + " / " + name; //$NON-NLS-1$
                reuseLoc.add(new SearchResultElement<Long>(resultName, id,
                        getImageForNode(execTC),
                        new NodeSearchResultElementAction(), execTC
                                .getComment()));
            }
            monitor.worked(1);
        }
        
        setSearchResult(reuseLoc);
        monitor.done();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("nls")
    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTimestamp());
        sb.append(": ");
        sb.append(I18n.getString("UIJob.searchingTestCases"));
        sb.append(" \"");
        sb.append(getSpecTC().getName());
        sb.append("\"");
        return sb.toString();
    }
    
    /**
     * @param specTC
     *            the specTC to set
     */
    private void setSpecTC(ISpecTestCasePO specTC) {
        m_specTC = specTC;
    }

    /**
     * @return the specTC
     */
    private ISpecTestCasePO getSpecTC() {
        return m_specTC;
    }
}
