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
package org.eclipse.jubula.documentation.gen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jubula.tools.messagehandling.Message;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.utils.generator.IProcessor;


/**
 * @author BREDEX GmbH
 * @created Nov 21, 2005
 * @version $Revision: 12986 $
 */
public class ErrorProcessor implements IProcessor {
    
    /**
     * <code>m_messageMap</code>
     */
    private Map m_messageMap;
    
    /**
     * 
     */
    public ErrorProcessor() {
        m_messageMap = MessageIDs.getMessageMap();
    }    
    
    /**
     * @return Returns a list of all error infos
     */
    public List<ErrorInfo> getErrorInfos() {
        
        List<ErrorInfo> infos = new ArrayList<ErrorInfo>(m_messageMap.size());
        
        Iterator it = m_messageMap.keySet().iterator();
        while (it.hasNext()) {
            Integer key = (Integer) it.next();
            //get the error code
            if (key.intValue() >= 1000 && key.intValue() < 10000) {
                String errorCode = ((Message)m_messageMap.get(key))
                    .getMessage(null);
                infos.add(new ErrorInfo(key, errorCode));
            }
        }
        return infos;
    }

}
