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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.jubula.client.core.businessprocess.ITestResultEventListener;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;


/**
 * class for creation of resultNodes
 * @author BREDEX GmbH
 * @created 21.04.2005
 */
public class TestResultNode {
    /**
     * Status if not yet tested
     */
    public static final int NOT_YET_TESTED = 0;
    /**
     * Status if test is successful
     */
    public static final int SUCCESS = 1;
    /**
     * Status if test is not successful
     */
    public static final int ERROR = 2;
    /**
     * Status if no verify must do.
     */
    public static final int NO_VERIFY = 3;
    /**
     * Status if test is not tested
     */
    public static final int NOT_TESTED = 4;
    /**
     * Error in child
     */
    public static final int ERROR_IN_CHILD = 5;

    /**
     * Status if currently being tested
     */
    public static final int TESTING = 6;

    /**
     * Status if not successful, but will be retried later
     */
    public static final int RETRYING = 7;

    /**
     * Status if test is successful after 1 or more retries
     */
    public static final int SUCCESS_RETRY = 8;
    
    /**
     * Status if test was aborted due to internal AutServer errors
     */
    public static final int ABORT = 9;
    
    /**
     * index for Tree Tracker
     */
    private int m_childIndex = -1;
    
    /**
     * The status
     */
    private int m_status = 0;
    
    /**
     * Time of Teststep Execution
     */
    private Date m_timestamp = null;
    
    /**
     * <code>m_screenshot</code> the screenshot in case of a test error event
     */
    private byte[] m_screenshot = null; 
    
    /**
     * errorEvent, indicated from server 
     */
    private TestErrorEvent m_event;
    
    /**
     * <code>m_node</code> associated node in testexecution tree
     */
    private INodePO m_node;
    
    /** the Component Name name for this result node */
    private String m_componentName;

    /**
     * <code>m_resultNodeList</code> childList
     */
    private List < TestResultNode > m_resultNodeList  = 
        new ArrayList < TestResultNode > ();
    
    /**
     * <code>m_parent</code> parent resultNode
     */
    private TestResultNode m_parent;
    
    /**
     * values for parameter, if node is a cap
     */
    private List < String > m_paramValues = new ArrayList < String > ();
    
    /**
     * The listener
     */
    private List < ITestResultEventListener > m_listener = 
        new ArrayList < ITestResultEventListener > ();
    
    /** 
     * flag indicating whether the backing node / keyword for this result 
     * can be found
     */
    private boolean m_hasBackingNode;

    /**
     * <code>m_omHeuristicEquivalence</code>
     */
    private double m_omHeuristicEquivalence = -1.0d;
    
    /**
     * <code>m_noOfSimilarComponents</code>
     */
    private int m_noOfSimilarComponents = -1;
    
    /**
     * Constructor
     * 
     * @param node The Test Execution node (i.e. Test Suite, Test Case, 
     *             Test Step, etc.) associated with this result.
     * @param parent The parent Test Result node. May be <code>null</code>, in 
     *               which case this node is the root of a Test Result tree.
     */
    public TestResultNode(INodePO node, TestResultNode parent) {
        this(true, node, parent);
    }

    /**
     * Constructor
     * 
     * @param hasBackingNode <code>true</code> if the backing node for the 
     *                       result can be found. Otherwise, <code>false</code>.
     * @param node The Test Execution node (i.e. Test Suite, Test Case, 
     *             Test Step, etc.) associated with this result. If this value 
     *             is <code>null</code>, the <code>fallbackName</code>
     *             will be used for display purposes, and there will be no 
     *             reference to a Test Execution node.
     * @param parent The parent Test Result node. May be <code>null</code>, in 
     *               which case this node is the root of a Test Result tree.
     */
    public TestResultNode(boolean hasBackingNode, INodePO node, 
            TestResultNode parent) {
        
        m_node = node;
        m_parent = parent;
        if (m_parent != null) {
            m_parent.addChild(this);
        }
        m_status = NOT_YET_TESTED;
        m_event = null;
        m_hasBackingNode = hasBackingNode;
    }
    
    /**
     * @param node associated node in testexecution tree
     * @param parent parent resultNode (in case of testsuite null)
     * @param pos
     *      int
     */
    public TestResultNode(INodePO node, TestResultNode parent, int pos) {
        m_node = node;
        m_parent = parent;
        if (m_parent != null) {
            m_parent.addChildAtPosition(pos, this);
        }
        m_status = NOT_YET_TESTED;
        m_event = null;
        m_hasBackingNode = true;
    }

    /**
     * add a child to resultNode and set actual resultNode as parent
     * @param resultNode resultNode to add
     */
    public void addChild(TestResultNode resultNode) {
        m_resultNodeList.add(resultNode);
        resultNode.m_parent = this;
    }

    /**
     * add a child to resultNode and set actual resultNode as parent
     * @param resultNode resultNode to add
     * @param pos
     *      position where to add
     */
    public void addChildAtPosition(int pos, TestResultNode resultNode) {
        m_resultNodeList.add(pos, resultNode);
        resultNode.m_parent = this;
    }

    /**
     * @return Returns the node.
     */
    public INodePO getNode() {
        return m_node;
    }
    /**
     * @return Returns the parent.
     */
    public TestResultNode getParent() {
        return m_parent;
    }
    /**
     * @return Returns the resultNodeList.
     */
    public List < TestResultNode > getResultNodeList() {
        return m_resultNodeList;
    }
    
    /**
     * @return Return the name of the execTestCase 
     */
    public String getName() {
        if (m_node != null) {
            return m_node.getName();
        }
        
        return StringConstants.LEFT_INEQUALITY_SING 
            + Messages.TestResultNodeGUINoNode 
            + StringConstants.RIGHT_INEQUALITY_SING;
    }
    
    
    /**
     * Adds the listener
     * @param listener ITestResultEventListener
     */
    public void addTestResultChangedListener(
        ITestResultEventListener listener) {
        if (!m_listener.contains(listener)) {
            m_listener.add(listener);
        }
    }

    /**
     * Remove the listener from ResultCap
     * @param listener to be removed
     */
    public void removeTestResultChangedListener(
        ITestResultEventListener listener) {
        m_listener.remove(listener);
    }
    
    /**
     * Trigger the listeners
     * @param res the changed TestResultNode
     */
    private void fireTestResultChanged(TestResultNode res) {
        Iterator<ITestResultEventListener> iter = m_listener.iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            ITestResultEventListener item = (ITestResultEventListener) obj;
            item.testResultChanged(res);
        }
    }
    
    /**
     * Updates the parent;
     * 
     * @param pos
     *      index
     * @param node
     *      TestResultNode
     */
    public void updateResultNode(int pos, TestResultNode node) {
        for (ITestResultEventListener item : m_listener) {
            item.testResultNodeUpdated(this, pos, node);
        }
    }
    /**
     * @return Returns the status.
     */
    public int getStatus() {
        return m_status;
    }
    /**
     * @return Returns the event.
     */
    public TestErrorEvent getEvent() {
        return m_event;
    }

    /**
     * Set the results from an execution
     * 
     * @param status
     *            Specifies the kind of result
     * @param event
     *            On a CAP this may be the event from the server.
     *            <code>null</code> is allowed.
     */
    public void setResult(int status, TestErrorEvent event) {
        boolean changed = m_status != status
            || (m_event != null && !m_event.equals(event))
            || (m_event == null && event != null);
        if (changed) { 
            m_status = status;
            m_event = event;
            if (m_status == TESTING && m_timestamp == null) {
                m_timestamp = new Date();
            }
        }
        
        if (isError(status)) {
            if (getParent() != null) { // NOPMD by al on 3/19/07 1:37 PM
                getParent().setResult(ERROR_IN_CHILD, null);
            }
        }               
        if (changed) {
            fireTestResultChanged(this);
        }
    }

    /**
     * @param status to be checked
     * @return true if the status means an error condition
     */
    private boolean isError(int status) {        
        return status == ERROR || status == ERROR_IN_CHILD
            || status == NO_VERIFY || status == NOT_TESTED || status == ABORT;
    }
    /**
     * {@inheritDoc}
     */
    public String toString() {        
        return new ToStringBuilder(this).append(getName()).append(getStatus())
            .toString();
    }

    /**
     * @return Returns the childIndex.
     */
    public int getNextChildIndex() {
        m_childIndex++;
        return m_childIndex;
    }


    /**
     *
     * @return  String to a status
     */
    public String getStatusString() {
        switch (m_status) {
            case TestResultNode.ERROR : 
                return Messages.TestResultNodeStepfailed;
            case TestResultNode.ERROR_IN_CHILD :
                return Messages.TestResultNodeErrorInChildren;
            case TestResultNode.NOT_YET_TESTED :
                return Messages.TestResultNodeNotYetTested;
            case TestResultNode.SUCCESS :
                return Messages.TestResultNodeSuccessfullyTested;
            case TestResultNode.TESTING :
                return Messages.TestResultNodeTesting;
            case TestResultNode.RETRYING :
                return Messages.TestResultNodeRetrying;
            case TestResultNode.SUCCESS_RETRY :
                return Messages.TestResultNodeSuccessRetry;
            case TestResultNode.ABORT : 
                return Messages.TestResultNodeAbort;
            default : 
                break;
        }
        return Messages.TestResultNodeUnknown;
    }

    /**
     * 
     * @return Parameter values used in CapPo
     */
    public List < String > getParamValues() {
        return m_paramValues;
    }

    /**
     * Parameter values used in CapPo
     * 
     * @param value String[]
     */
    public void addParamValue(String value) {
        m_paramValues.add(value);
    }
    
    /**
     * 
     * @param componentName The Component Name name to set. This must be a name,
     *                      <b>not</b> a GUID.
     */
    public void setComponentName(String componentName) {
        m_componentName = componentName;
    }
    
    /**
     * 
     * @return the Component Name name for this result node. 
     *         This is a name, <b>not</b> a GUID.
     */
    public String getComponentName() {
        return m_componentName;
    }
    
    /**
     * Gets the actual Time during Testexecution
     * 
     * @return The Timestamp
     */
    public Date getTimeStamp() {
        return m_timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Date timestamp) {
        m_timestamp = timestamp;
    }

    /**
     * @param screenshot the screenshot to set
     */
    public void setScreenshot(byte[] screenshot) {
        m_screenshot = screenshot;
    }

    /**
     * @return the screenshot
     */
    public byte[] getScreenshot() {
        return m_screenshot;
    }

    /**
     * 
     * @return <code>true</code> if the backing node for the 
     *         result can be found. Otherwise, <code>false</code>.
     */
    public boolean hasBackingNode() {
        return m_hasBackingNode;
    }

    /**
     * @param omHeuristicEquivalence the omHeuristicEquivalence to set
     */
    public void setOmHeuristicEquivalence(double omHeuristicEquivalence) {
        m_omHeuristicEquivalence = omHeuristicEquivalence;
    }

    /**
     * @return the omHeuristicEquivalence
     */
    public double getOmHeuristicEquivalence() {
        return m_omHeuristicEquivalence;
    }

    /**
     * @param noOfSimilarComponents the noOfSimilarComponents to set
     */
    public void setNoOfSimilarComponents(int noOfSimilarComponents) {
        m_noOfSimilarComponents = noOfSimilarComponents;
    }

    /**
     * @return the noOfSimilarComponents
     */
    public int getNoOfSimilarComponents() {
        return m_noOfSimilarComponents;
    }
}
