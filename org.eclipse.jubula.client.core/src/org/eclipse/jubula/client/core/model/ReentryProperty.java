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

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.Assert;
import org.eclipse.jubula.tools.exception.InvalidDataException;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;



/**
 * class for enumeration of reentry properties for resuming of 
 * testexecution after execution of errorhandler
 *
 * @author BREDEX GmbH
 * @created 04.04.2005
 */
public final class ReentryProperty {
    
    
    /**
     * <code>CONTINUE</code> continue testexecution with next step
     */
    public static final ReentryProperty CONTINUE = 
        new ReentryProperty(1);

    /**
     * <code>REPEAT</code> continue testexecution with actual step (which
     * caused the error)
     */
    public static final ReentryProperty REPEAT = new ReentryProperty(2);

    /**
     * <code>BREAK</code> continue testexecution with next testcase
     */
    public static final ReentryProperty BREAK = new ReentryProperty(3);

    /**
     * <code>GOTO</code> continue testexecution with next nested testcase
     */
    public static final ReentryProperty GOTO = new ReentryProperty(4);

    /**
     * <code>RETURN</code> continue testexecution with next step (Cap or testcase)
     * in testcase, which contains the actual eventhandler
     */
    public static final ReentryProperty RETURN = new ReentryProperty(5);

    /**
     * <code>STOP</code> stop testexecution after execution of
     * errorHandler
     */
    public static final ReentryProperty STOP = new ReentryProperty(6);

    /**
     * <code>EXIT</code> cancel testexecution
     */
    public static final ReentryProperty EXIT = new ReentryProperty(7);
    
    /**
     * <code>RETRY</code> retry test step
     */
    public static final ReentryProperty RETRY = new ReentryProperty(8);

    /** Array of existing Reentry Properties */
    public static final ReentryProperty[] REENTRY_PROP_ARRAY = 
    {BREAK, CONTINUE, EXIT, RETURN, STOP, RETRY};

    /** Array of Reentry Properties available for test suites */
    public static final ReentryProperty[] TS_REENTRY_PROP_ARRAY = 
    {CONTINUE, EXIT, STOP};
    
    /** The logger */
    private static final Log LOG = LogFactory.getLog(TestExecution.class);

    /**
     * <code>m_value</code> reentry property
     */
    private int m_value = 0;

    /**
     * @param value status for Reentry Property
     */
    private ReentryProperty(int value) {
        m_value = value;
    }
    
    /**
     * empty constructor
     */
    private ReentryProperty() {
        // nothing
    }
    
    /**
     * get the Proeprty for an specific int value, used by persistence layer.
     * @param reentryPropValue int value from DB
     * @return the property for the supplied int value
     * {@inheritDoc}
     */
    public static ReentryProperty getProperty(Integer reentryPropValue)
        throws InvalidDataException {
        int val = reentryPropValue == null ? 0 : reentryPropValue.intValue();
        validateValue(val);
        switch (val) {
            case 1:
                return CONTINUE;
            case 2:
                return REPEAT;
            case 3:
                return BREAK;
            case 4:
                return GOTO;
            case 5:
                return RETURN;
            case 6:
                return STOP;
            case 7:
                return EXIT;
            case 8:
                return RETRY;
            default:
                return null; // can not happen, values are validated
        }
    }
    
    /**
     * get the Proeprty for an specific int value, used by persistence layer.
     * @param reentryPropValue string value
     * @return the property(Integer) for the supplied string value
     * {@inheritDoc}
     */
    public static Integer getProperty(String reentryPropValue) 
        throws InvalidDataException {
        String val = reentryPropValue == null 
            ? StringConstants.EMPTY : reentryPropValue;
        if (val.equals(I18n.getString("EventExecTestCasePO.CONTINUE"))) { //$NON-NLS-1$
            return 1;
        } else if (val.equals(I18n.getString("EventExecTestCasePO.REPEAT"))) { //$NON-NLS-1$
            return 2;
        } else if (val.equals(I18n.getString("EventExecTestCasePO.BREAK"))) { //$NON-NLS-1$
            return 3;
        } else if (val.equals(I18n.getString("EventExecTestCasePO.GOTO"))) { //$NON-NLS-1$
            return 4;
        } else if (val.equals(I18n.getString("EventExecTestCasePO.RETURN"))) { //$NON-NLS-1$
            return 5;
        } else if (val.equals(I18n.getString("EventExecTestCasePO.STOP"))) { //$NON-NLS-1$
            return 6;
        } else if (val.equals(I18n.getString("EventExecTestCasePO.EXIT"))) { //$NON-NLS-1$
            return 7;
        } else if (val.equals(I18n.getString("EventExecTestCasePO.RETRY"))) { //$NON-NLS-1$
            return 8;
        } 
        LOG.error("Unsupported reentry property " //$NON-NLS-1$
            + "with value " + val); //$NON-NLS-1$
        throw new InvalidDataException("Unsupported reentry property with value "  //$NON-NLS-1$ 
            + val, MessageIDs.E_UNSUPPORTED_REENTRY);
    }

    /**
     * @param value
     *            value for reentry property
     * {@inheritDoc}
     *  
     */
    private static void validateValue(int value) throws InvalidDataException {
        for (int i = 0; i < REENTRY_PROP_ARRAY.length; i++) {
            ReentryProperty prop = REENTRY_PROP_ARRAY[i];
            if (prop.getValue() == value) {
                return;
            }
        }
        LOG.error("Unsupported reentry property with value " + value); //$NON-NLS-1$
        throw new InvalidDataException("Unsupported reentry property with value " //$NON-NLS-1$  
            + value, MessageIDs.E_UNSUPPORTED_REENTRY);       
    }

    /**
     * @return the hashcode
     */
    public int hashCode() {
        return new HashCodeBuilder().append(m_value).toHashCode();
    }
    
    
    /**
     * only to prevent problems in case of loading this class with different
     * classloaders
     * @param obj the object to equal
     * @return if equals or not
     */
    public boolean equals(Object obj) {
        return (obj instanceof ReentryProperty) 
            ? ((ReentryProperty)obj).getValue() == m_value : false;
    }
    
    /**
     * @return Returns the value for reentryProperty.
     */
    public int getValue() {
        return m_value;
    }
    /**
    * @return a String representation of this object.
    */
    public String toString() {
        switch(m_value) {
            case 1:
                return I18n.getString("EventExecTestCasePO.CONTINUE"); //$NON-NLS-1$
            case 2:
                return I18n.getString("EventExecTestCasePO.REPEAT"); //$NON-NLS-1$
            case 3:
                return I18n.getString("EventExecTestCasePO.BREAK"); //$NON-NLS-1$
            case 4:
                return I18n.getString("EventExecTestCasePO.GOTO"); //$NON-NLS-1$
            case 5:
                return I18n.getString("EventExecTestCasePO.RETURN"); //$NON-NLS-1$
            case 6:
                return I18n.getString("EventExecTestCasePO.STOP"); //$NON-NLS-1$
            case 7:
                return I18n.getString("EventExecTestCasePO.EXIT"); //$NON-NLS-1$
            case 8:
                return I18n.getString("EventExecTestCasePO.RETRY"); //$NON-NLS-1$
            default:
                Assert.notReached("Wrong type of ReentryProperty!"); //$NON-NLS-1$
                return StringConstants.EMPTY;
        }
    }
    
}
