/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.toolkit;

import org.eclipse.jdt.annotation.NonNull;


/**
 * Information about a toolkit and its components
 * 
 * @author BREDEX GmbH
 * @created 15.10.2014
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ToolkitInfo {
    
    /**
     * Allows adding of a tester class for a component class into a toolkit
     * @param componentClassName name of the component class
     * @param testerClassName name of the tester class
     * @return previously registered tester class for the component class
     *         or <code>null</code> if there was none
     */
    public String registerTesterClass(@NonNull String componentClassName,
            @NonNull String testerClassName);
    
    /**
     * Allows removing of a tester class for a component class from a toolkit
     * @param componentClassName name of the component class
     * @return previously registered tester class for the component class
     *         or <code>null</code> if there was none
     */
    public String deregisterTesterClass(@NonNull String componentClassName);

}