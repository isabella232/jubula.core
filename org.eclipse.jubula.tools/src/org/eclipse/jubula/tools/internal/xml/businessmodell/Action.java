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
package org.eclipse.jubula.tools.internal.xml.businessmodell;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This class represents an action which belongs to a component.
 * For example an action is a click on a button or a check for a value.
 * An action has got one name, one method, one changed version number
 * one deprecated status, no or one returnValue and no or many params.
 * {@inheritDoc}
 * @author BREDEX GmbH
 * @created 08.07.2004
 */
public class Action {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(Action.class);

    /** parameter list of action */
    private List<Param> m_params = new ArrayList<Param>();
    
    /** Name of action */
    private String m_name;
    
    /** Method name */
    private String m_method;
    
    /** 
     * full qualified name of the command class to execute after
     * test execution in TestExecution 
     */
    private String m_postExecutionCommand;
    
    /** Whether this Action is deprecated or not */
    private boolean m_deprecated = false;
    
    /**
     *  Whether this action is executed in the client or server 
     *  (default = server (false)).
     */
    private boolean m_clientAction = false;
    
    /** The version number of the last change of this action */
    private String m_changed;

    /** Default constructor. Do nothing. */
    public Action() {
        super();
    }
    
    /**
     * @return Returns the list of params.
     */
    public List<Param> getParams() {
        return m_params;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return m_name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        m_name = name;
    }
    
    /**
     * Returns the size of param list.
     * @return an <code>int</code> value.
     */
    public int getParamsSize() {
        return m_params != null ? m_params.size() : 0;
    }

    /**
     * Returns a string representation of the action object.
     * @return String
     */
    public String toString() {
        if (getParamsSize() == 0) {
            return new ToStringBuilder(this)
                .append("Name", m_name) //$NON-NLS-1$
                    .append("Method", m_method) //$NON-NLS-1$
                        .toString();
        }
        return new ToStringBuilder(this)
            .append("Name", m_name) //$NON-NLS-1$
                .append("Method", m_method) //$NON-NLS-1$
                    .append("Params", m_params.toArray()) //$NON-NLS-1$
                        .toString();
    }
    /**
     * Compares this <code>Action</code> to the specified object.
     * The param list is not be compared, only the name
     * and the method.
     * @param object Object
     * @return <code>true</code> if both actions are equal.
     */
    public boolean equals(Object object) {
        if (object instanceof Action) {
            Action theOther = (Action)object;
            return new EqualsBuilder().append(m_name, theOther.m_name)
                .append(m_method, theOther.m_method)
                    .isEquals();
        }
        
        
        return false;
    }
    /**
     * @return the hashcode
     */
    public int hashCode() {
        return new HashCodeBuilder().append(m_name)
            .append(m_method)
                .toHashCode();
    }
    /**
     * Returns the param with the specified name.
     * @param name The name of the specified action.
     * @return The specified param.

     */
    public Param findParam(String name) {
        Validate.notNull(name);
        List list = getParams();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Param param = (Param)it.next();
            if (name.equals(param.getName())) {
                return param;
            }
        }
        String message = "Param " + name //$NON-NLS-1$
                + " does not exist"; //$NON-NLS-1$
        log.error(message);
        return new InvalidParam();
    }
    /**
     * 
     * @return Returns a list with names af the params or null
     */
    public String[] getParamNames() {
        int count = 0;
        String[] names = new String[getParamsSize()];
        List params = getParams();
        if (params != null) {
            Iterator it = params.iterator();
            while (it.hasNext()) {
                Param param = (Param)it.next();
                names[count] = param.getName();
                count++;
            }
            return names;
        }
        return null;
    }
    /**
     * @return Returns the method.
     */
    public String getMethod() {
        return m_method;
    }
    /**
     * @param method The method to set.
     */
    public void setMethod(String method) {
        m_method = method;
    }
    
    
    /**
     * @return The version number of the last change of this action
     */
    public Float getChanged() {
        return new Float(m_changed);
    }

    /**
     * Sets the changed status
     * @param changed the status to set
     */
    public void setChanged(String changed) {
        m_changed = changed;
    }

    /**
     * @return whether this Action is deprecated or not
     */
    public boolean isDeprecated() {
        return m_deprecated;
    }

    /**
     * Sets the deprecated status
     * @param deprecated the status to set
     */
    public void setDeprecated(boolean deprecated) {
        m_deprecated = deprecated;
    }

    /**
     * @return full qualified name of the command class to execute after
     *  test execution in TestExecution 
     */
    public String getPostExecutionCommand() {
        return m_postExecutionCommand;
    }

    /**
     * @param postExecutionCommand full qualified name of the command class 
     * to execute after test execution in TestExecution 
     */
    public void setPostExecutionCommand(String postExecutionCommand) {
        m_postExecutionCommand = postExecutionCommand;
    }

    /**
     * @return Returns Whether this action is executed in the client or server 
     *  (default = server).
     */
    public boolean isClientAction() {
        return m_clientAction;
    }

    /**
     * @param clientAction true if action is to be executed in the client, 
     * false in the server
     */
    public void setClientAction(boolean clientAction) {
        m_clientAction = clientAction;
    }
}