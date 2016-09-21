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
package org.eclipse.jubula.client.core.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;


/**
 * @author BREDEX GmbH
 * @created 07.06.2005
 */
@Entity
@DiscriminatorValue(value = "T")
abstract class TestCasePO extends ParamNodePO implements ITestCasePO {
    
    /**
     * <code>m_eventExecTcMap</code><br>
     * eventhandler for each eventType (key)
     */
    private Map<String, IEventExecTestCasePO> m_eventExecTcMap = 
        new HashMap<String, IEventExecTestCasePO>(
            IEventHandlerContainer.MAX_NUMBER_OF_EVENT_HANDLER);

    /**
     * @param name name of testcase
     * @param isGenerated indicates whether this node has been generated
     */
    public TestCasePO(String name, boolean isGenerated) {
        super(name, isGenerated);
    }
    
    /**
     * @param name name of testcase
     * @param guid the GUID of the testcase
     * @param isGenerated indicates whether this node has been generated
     */
    public TestCasePO(String name, String guid, boolean isGenerated) {
        super(name, guid, isGenerated);
    }

    /**
     * only for Persistence (JPA / EclipseLink)
     */
    TestCasePO() {
        // only for Persistence (JPA / EclipseLink)
    }

   

    /**
     * 
     * @return Returns the eventExecTestCaseMap.
     */
    @OneToMany(targetEntity = EventExecTestCasePO.class, 
               fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "EVENT_HANDLERS", 
               inverseJoinColumns = @JoinColumn(name = "TEST_CASE_ID"),
               joinColumns = @JoinColumn(name = "EVENT_HANDLER_ID"))
    @MapKeyColumn(name = "EVENT_TYPE_KEY", nullable = false, 
                  table = "EVENT_HANDLERS")
    @BatchFetch(value = BatchFetchType.JOIN)
    public Map<String, IEventExecTestCasePO> getEventExecTcMap() {
        return m_eventExecTcMap;
    }

    /**
     * only for Persistence (JPA / EclipseLink)
     * @param eventExecTestCaseList
     *            The eventExecTestCaseList to set.
     */
    @SuppressWarnings("unused")
    private void setEventExecTcMap(
            Map<String, IEventExecTestCasePO> eventExecTestCaseList) {
        m_eventExecTcMap = eventExecTestCaseList;
    }

    /**
     * @param eventTC eventExecTestCase to add
     * @throws InvalidDataException if an eventTestCase is already 
     * existent for the associated event
     */
    public void addEventTestCase(IEventExecTestCasePO eventTC) 
        throws InvalidDataException {
        
        if (!getEventExecTcMap().containsKey(eventTC.getEventType())) {
            getEventExecTcMap().put(eventTC.getEventType(), eventTC);
            eventTC.setParentProjectId(getParentProjectId());
            eventTC.setParentNode(this);
        } else {
            throw new InvalidDataException(
                    Messages.DoubleEventTestCaseForTheSameEvent 
                    + StringConstants.SPACE + StringConstants.SLASH
                    + eventTC.getEventType() + StringConstants.SLASH,
                    MessageIDs.E_DOUBLE_EVENT); 
        }
    }


    /**
     * get the eventExecTC for a given eventType
     * @param eventType eventType for wanted eventExecTC
     * @return the eventExecTC for given eventType or null
     */
    public IEventExecTestCasePO getEventExecTC(String eventType) {
        setParents(getEventExecTcMap().values());
        return getEventExecTcMap().get(eventType);
    }

    /**
     * @return all EventExecTestCases of this SpecTestCasePO.
     */
    @Transient
    public Collection<IEventExecTestCasePO> getAllEventEventExecTC() {
        Collection<IEventExecTestCasePO> evHandlers = 
                getEventExecTcMap().values();
        setParents(evHandlers);
        return Collections.unmodifiableCollection(evHandlers);
    }
    

    /**
     * Private class implementing an Iterator over all child nodes
     * @author BREDEX GmbH
     *
     */
    private class AllNodeIterator implements Iterator<INodePO> {
        /** Iterator over normal nodes */
        private Iterator<INodePO> m_normals;
        
        /** Iterator over event handlers */
        private Iterator<IEventExecTestCasePO> m_events;
        
        /**
         * Private constructor
         * @param tc the test case
         */
        private AllNodeIterator(ITestCasePO tc) {
            super();
            m_normals = tc.getNodeListIterator();
            m_events = tc.getAllEventEventExecTC().iterator();
        }

        /** {@inheritDoc} */
        public boolean hasNext() {
            if (m_normals != null) {
                if (m_normals.hasNext()) {
                    return true;
                }
                m_normals = null;
            }
            return m_events.hasNext();
        }

        /** {@inheritDoc} */
        public INodePO next() {
            if (!hasNext()) {
                return null;
            }
            
            if (m_normals != null) {
                return m_normals.next();
            }
            
            return m_events.next();
        }
        
        /** {@inheritDoc} */
        public void remove() {
            throw new UnsupportedOperationException(
                    "This iterator does not support remove."); //$NON-NLS-1$
        }
    }

    /** {@inheritDoc} */
    public Iterator<INodePO> getAllNodeIter() {
        return new AllNodeIterator(this);
    }

    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        super.setParentProjectId(projectId);
        for (IEventExecTestCasePO eh : getAllEventEventExecTC()) {
            eh.setParentProjectId(projectId);
        }
    }
    
    /**
     * @param evHandlers the event handlers to set the parent for
     */
    private void setParents(Collection<IEventExecTestCasePO> evHandlers) {
        for (IEventExecTestCasePO evTc : evHandlers) {
            evTc.setParentNode(this);
        }
    }
}
