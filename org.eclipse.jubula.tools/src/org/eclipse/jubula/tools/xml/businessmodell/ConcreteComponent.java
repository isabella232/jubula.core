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
package org.eclipse.jubula.tools.xml.businessmodell;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.AssertException;


/**
 * This class represents concrete graphic components which can be tested by the
 * GuiDancer. The tester class which is associated to the component performs
 * the operations on the component.
 * 
 * @author BREDEX GmbH
 * @created 08.07.2004
 */

public class ConcreteComponent extends Component {

    /* DOTNETDECLARE:BEGIN */

    /**
     * the component class
     */
    public List m_compClass = new ArrayList();
    
    /**
     * The testerClass of the component.
     */
    public String m_testerClass;
    /**
     * The default mapping.
     */
    public DefaultMapping m_defaultMapping;

    /* DOTNETDECLARE:END */
    
    
    /**
     * @return Returns the testerClass.
     */
    public String getTesterClass() {
        return m_testerClass;
    }
    /**
     * @param testerClass The testerClass to set.
     */
    public void setTesterClass(String testerClass) {
        m_testerClass = testerClass;
    }
    /**
     * Returns a string representation of the component object.
     * @return The string
     */
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append(
            "TesterClass", m_testerClass).toString(); //$NON-NLS-1$
    }
    /**
     * Compares the <code>type</code> and <code>testerClass</code>.
     * 
     * @param object
     *            The object to compare.
     * @return <code>true</code> if the objects are equal.
     */
    public boolean equals(Object object) {
        if (!(object instanceof ConcreteComponent)) {
            return false;
        }
        ConcreteComponent rhs = (ConcreteComponent)object;
        return new EqualsBuilder().appendSuper(super.equals(object)).append(
            getTesterClass(), rhs.getTesterClass()).isEquals();
    }
    /**
     * @return The hash code build from <code>type</code> and
     *         <code>testerClass</code>.
     */
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(
            getTesterClass()).toHashCode();
    }    
    /**
     * @return <code>true</code> if this Component is of type
     *         <code>ConcreteComponent</code>.
     */
    public boolean isConcrete() {
        return true;
    }
    /**
     * @return The default mapping or <code>null</code>, if the component has
     *         not default mapping.
     */
    public DefaultMapping getDefaultMapping() {
        return m_defaultMapping;
    }
    /**
     * @return <code>true</code> if the component has a default mapping
     */
    public boolean hasDefaultMapping() {
        return m_defaultMapping != null;
    }
    /**
     * @return Returns the componentClass.
     */
    public List getCompClass() {
        return m_compClass;
    }
    /**
     * @param componentClass The componentClass to set.
     */
    public void setCompClass(List componentClass) {
        m_compClass = componentClass;
    }
    
    /**
     * use the name of the component class instead
     * @return Returns the componentClassName.
     */
    public String getComponentClass() {
        List compClassList = getCompClass();
        if (!compClassList.isEmpty()) {
            return ((ComponentClass)compClassList.get(0)).getName();
        }
        return StringConstants.EMPTY;
    }

    /**
     * @param componentClass
     *            The componentClass to set.
     */
    public void setComponentClass(String componentClass) {
        if (StringUtils.isBlank(componentClass)) {
            throw new AssertException("component class must point to a valid identifier"); //$NON-NLS-1$
        }
        List compClassList = getCompClass();
        if (!compClassList.isEmpty()) {
            ((ComponentClass)compClassList.get(0)).setName(componentClass);
        } else {
            compClassList.add(new ComponentClass(componentClass));
        }
    }
}