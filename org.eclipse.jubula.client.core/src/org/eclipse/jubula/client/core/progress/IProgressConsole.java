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
package org.eclipse.jubula.client.core.progress;

/**
 * Presents an interface for writing text messages to a console.
 *
 * @author BREDEX GmbH
 * @created Jun 4, 2010
 */
public interface IProgressConsole {

    /**
     * Writes a single line to the console.
     * 
     * @param line The text to write to the console.
     */
    public void writeLine(String line);
    
    /**
     * Writes a single error line to the console.
     * 
     * @param line The text to write to the console as an error.
     */
    public void writeErrorLine(String line);
}
