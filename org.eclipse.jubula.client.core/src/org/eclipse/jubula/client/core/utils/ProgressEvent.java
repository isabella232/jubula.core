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
package org.eclipse.jubula.client.core.utils;


/**
 * @author BREDEX GmbH
 * @created 25.08.2005
 *
 */
public class ProgressEvent {
    /** Default ID : value = 1 */
    public static final Integer LOGIN = 1;
        
    /** ID for open progress bar : value = 2 */
    public static final Integer OPEN_PROGRESS_BAR = 2;
    
    /** ID for close progress bar : value = 3 */
    public static final Integer CLOSE_PROGRESS_BAR = 3;

    /** Show an error message, code is supplied in constructor : value = 4 */
    public static final Integer SHOW_MESSAGE = 4;
    
    /** The DB scheme has just been created : value = 5*/
    public static final Integer DB_SCHEME_CREATE = 5;
    
    /** The ID of the event */
    private Integer m_id;
    /** The ErrorMessage code to be used */
    private Integer m_msgId;
    /** The text of the progress dialog */
    private String m_progressText;
    
    /**
     * The long running Thread.
     */
    private Thread m_thread;

    /**
     * The constructor.
     * @param id the id of this Event.
     * @param msgId id of the error message to display if id is SHOW_MESSAGE (<code>null</code> otherwise)
     * @param text text of the progress dialog if id is OPEN_PROGRESS_BAR (<code>null</code> otherwise)
     */
    public ProgressEvent(Integer id, Integer msgId, String text) {
        
        m_id = id;
        m_msgId = msgId;
        m_progressText = text;
    }
   
    /**
     * @return Returns the ID of the Event.
     */
    public Integer getId() {
        return m_id;
    }

    /**
     * @return Returns the msgId.
     */
    public Integer getMsgId() {
        return m_msgId;
    }

    /**
     * @return the progressText
     */
    public String getProgressText() {
        return m_progressText;
    }

    /**
     * @return the long running Thread.<br>
     * <b> Can be null!</b>
     */
    public Thread getThread() {
        return m_thread;
    }
}