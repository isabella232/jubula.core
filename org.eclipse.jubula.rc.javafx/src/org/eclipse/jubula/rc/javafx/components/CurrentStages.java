/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.components;

import java.lang.reflect.Field;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides access to all instantiated Stages, by accessing a private field in
 * the <code>Stage</code> class with reflection. Whenever a <code>Stage</code>
 * is instantiated or closed a reference is stored in this field automatically
 * by JavaFX.
 *
 * @author BREDEX GmbH
 * @created 10.10.2013
 *
 */
public class CurrentStages {

    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(CurrentStages.class);

    /** The Stage list **/
    private static ObservableList<Stage> stages;

    /** private Constructor **/
    private CurrentStages() {
        // private Constructor
    }

    static {
        Class<Stage> stageC = Stage.class;

        Field stagesField;
        try {
            stagesField = stageC.getDeclaredField("stages"); //$NON-NLS-1$
            stagesField.setAccessible(true);
            Object fieldObj = stagesField.get(null);
            if (fieldObj instanceof ObservableList<?>) {
                stages = (ObservableList<Stage>) fieldObj;
            } else {
                throw new NoSuchFieldException(
                        "The type of the field is not the expected type"); //$NON-NLS-1$
            }
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            LOG.error(e.getMessage());
        }
    }

    /**
     * Gets the first Stage in the list
     *
     * @return the Stage
     */
    public static Stage getfirstStage() {
        return stages.get(0);
    }

    /**
     * Gets the Stage with focus in the list
     *
     * @return the Stage
     */
    public static Stage getfocusStage() {
        Stage fStage = null;
        for (Stage stage : stages) {
            if (stage.isFocused()) {
                fStage = stage;
            }
        }
        return fStage;
    }

    /**
     * Adds a <code>ListChangeListener</code> to the Stages-List
     *
     * @param listener
     *            the listener
     */
    public static void addStagesListener(ListChangeListener<Stage> listener) {
        stages.addListener(listener);
    }

}
