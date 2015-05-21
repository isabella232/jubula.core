/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.tester;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

import org.eclipse.jubula.rc.common.tester.AbstractTooltipTester;
import org.eclipse.jubula.rc.javafx.components.CurrentStages;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;



/**
 * Toolkit specific commands for the <code>Tooltip</code>.
 * 
 * @author BREDEX GmbH
 * @created 19.05.2015
 */
public class TooltipTester extends AbstractTooltipTester {

    @Override
    public String getTooltipText() {

        return EventThreadQueuerJavaFXImpl.invokeAndWait("getTooltipText", //$NON-NLS-1$
                new Callable<String>() {
                    private List<Tooltip> m_tooltips =
                            new ArrayList<Tooltip>();
            
                    public String call() {
                        Stage focusStage = CurrentStages.getfocusStage();
                        if (focusStage != null) {            
                            final Scene scene = focusStage.getScene();
                            if (scene != null) {
                                Parent root = scene.getRoot();
                                searchTooltips(root);
                            }
                        }
                        return m_tooltips.size() == 1
                                    ? m_tooltips.get(0).getText() : null;
                    }
                    
                    private void searchTooltips(Parent n) {
                        for (Node child : n.getChildrenUnmodifiable()) {
                            if (child instanceof Control) {
                                Tooltip tooltip =
                                        ((Control)child).getTooltip();
                                if (tooltip != null
                                        && tooltip.isShowing()) {
                                    m_tooltips.add(tooltip);
                                }
                            }
                            if (child instanceof Parent) {
                                searchTooltips((Parent)child);
                            }
                        }
                    }
                }
        );
    }
}
