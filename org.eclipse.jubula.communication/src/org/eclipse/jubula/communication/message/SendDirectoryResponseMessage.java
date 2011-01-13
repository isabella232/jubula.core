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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.tools.constants.CommandConstants;


/**
 * @author BREDEX GmbH
 * @created May 18, 2009
 */
/**
 * The @-attribute comments are configuration attributes for the .NET XML
 * serializer. They are not needed by the native Java classes. They are
 * defined here because the classes are shared on source code level.
 * Due to the way the attributes are set, the property variables need to be
 * public. Since these are pure data carrying properties this is acceptable.
 * 
 * @attribute System.Serializable()
 * */
public class SendDirectoryResponseMessage extends Message {
    /** dir marker */
    public static final String DIR_MARKER = "D"; //$NON-NLS-1$
    /** file marker */
    public static final String FILE_MARKER = "F"; //$NON-NLS-1$
    
    /** state */
    public static final int OK = 0;
    /** state */
    public static final int NOT_A_DIR = 1;
    /** state */
    public static final int IO_ERROR = 2;
    
    /** static version */
    private static final double VERSION = 1.0;
    

/* DOTNETDECLARE:BEGIN */

    /**
     * transmitted version of this message.
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__version") */
    public double m_version = VERSION;    
    
    /** @attribute System.Xml.Serialization.XmlElement("m__base") */
    public String m_base;
    
    /** @attribute System.Xml.Serialization.XmlElement("m__dirEntries") */
    public List m_dirEntries;
    
    /** @attribute System.Xml.Serialization.XmlElement("m__error") */
    public int m_error = OK;
    
    /** @attribute System.Xml.Serialization.XmlElement("m__separator") */
    public char m_separator = File.separatorChar;
    
    /** @attribute System.Xml.Serialization.XmlElement("m__roots") */
    public List m_roots;
    
/* DOTNETDECLARE:END */

    /**
     * basic constructor
     */
    public SendDirectoryResponseMessage() {
        super();
        m_dirEntries = new ArrayList(101);
        m_roots = new ArrayList(26);
    }

    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {        
        return CommandConstants.PROCESS_DIR_COMMAND;
    }

    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return m_version;
    }

    /**
     * add a file entry
     * @param name of the entry
     */
    public void addFile(String name) {
        m_dirEntries.add(FILE_MARKER + name);
    }

    /**
     * add a directory entry
     * @param name of the entry;
     */
    public void addDir(String name) {
        m_dirEntries.add(DIR_MARKER + name);
    }
    
    /**
     * add an entry to the roots list
     * this is set from File.listRoots()
     * @param absName the absolute path name of a root filesystem entry
     */
    public void addRoot(String absName) {
        m_roots.add(absName);
    }

    /**
     * @return the dirEntries
     */
    public List getDirEntries() {
        return m_dirEntries;
    }

    /**
     * @return the error
     */
    public int getError() {
        return m_error;
    }

    /**
     * @param error the error to set
     */
    public void setError(int error) {
        m_error = error;
    }

    /**
     * @return the base
     */
    public String getBase() {
        return m_base;
    }

    /**
     * @param base the base to set
     */
    public void setBase(String base) {
        m_base = base;
    }

}
