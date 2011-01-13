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
package org.eclipse.jubula.app.core;

import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;

/**
 * @author BREDEX GmbH
 * @created 23.08.2005
 */
public class JubulaActionBarAdvisor extends ActionBarAdvisor {

    /** Key to look up the action builder on the window configurer. */
    private static final String BUILDER_KEY = "builder"; //$NON-NLS-1$
    
    /** IWorkbenchWindowConfigurer */
    private IWorkbenchWindowConfigurer m_windowConfigurer;
    
    /**
     * @param configurer IActionBarConfigurer
     * @param windowConfigurer IWorkbenchWindowConfigurer
     */
    public JubulaActionBarAdvisor(IActionBarConfigurer configurer,
        IWorkbenchWindowConfigurer windowConfigurer) {            
        super(configurer);
        m_windowConfigurer = windowConfigurer;
    }
    
    /**
     * {@inheritDoc}
     */
    public void fillActionBars(int flags) {            
        if ((flags & ActionBarAdvisor.FILL_PROXY) != 0) {
            return;
        }
        ActionBuilder builder = new ActionBuilder(
            getActionBarConfigurer().getWindowConfigurer().getWindow());
        m_windowConfigurer.getWorkbenchConfigurer().getWindowConfigurer(
            getActionBarConfigurer().getWindowConfigurer().getWindow())
                .setData(BUILDER_KEY, builder);
        builder.fillActionBars(getActionBarConfigurer(), flags);
    }
}