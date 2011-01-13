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
package org.eclipse.jubula.communication.message;

import java.io.Serializable;

/**
 * This class is the parameter of the component action paramter tripel. 
 *
 * @author BREDEX GmbH
 * @created 14.10.2004
 *
 * 
 * The @-attribute comments are configuration attributes for the .NET XML
 * serializer. They are not needed by the native Java classes. They are
 * defined here because the classes are shared on source code level.
 * Due to the way the attributes are set, the property variables need to be
 * public. Since these are pure data carrying properties this is acceptable.
 * 
 * @attribute System.Serializable()
 * 
 *
 *
 *
 * */
public class MessageParam implements Serializable {
    
/* DOTNETDECLARE:BEGIN */

    /**
     * The value of the CAPParam
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__value") */
    public String m_value;
    
    /**
     * The type of CAPParam
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__type") */
    public String m_type;

/* DOTNETDECLARE:END */
    
    /**
     * The default constructor
     */
    public MessageParam() {
        super();
    }
    
    /**
     * 
     * @param value The value of parameter
     * @param type The type of parameter
     */
    public MessageParam(String value, String type) {
        m_value = value;
        m_type = type;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return m_type;
    }
    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        m_type = type;
    }
    /**
     * @return Returns the value.
     */
    public String getValue() {
        return m_value;
    }
    /**
     * @param value The value to set.
     */
    public void setValue(String value) {
        m_value = value;
    }
}
