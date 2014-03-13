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
package org.eclipse.jubula.rc.javafx.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.rc.common.exception.ComponentNotFoundException;
import org.eclipse.jubula.rc.common.exception.ComponentNotManagedException;
import org.eclipse.jubula.rc.common.exception.NoIdentifierForComponentException;
import org.eclipse.jubula.rc.common.listener.BaseAUTListener;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.javafx.components.AUTJavaFXHierarchy;
import org.eclipse.jubula.rc.javafx.components.CurrentStages;
import org.eclipse.jubula.rc.javafx.components.FindJavaFXComponentBP;
import org.eclipse.jubula.rc.javafx.components.JavaFXComponent;
import org.eclipse.jubula.rc.javafx.util.NodeBounds;
import org.eclipse.jubula.tools.constants.TimingConstantsServer;
import org.eclipse.jubula.tools.exception.InvalidDataException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.xml.businessmodell.ComponentClass;

/**
 * This class is responsible for handling the components of the AUT. <br>
 *
 * The static methods for fetching an identifier for a component and getting the
 * component for an identifier delegates to this AUTHierarchy.
 *
 * @author BREDEX GmbH
 * @created 10.10.2013
 */
public class ComponentHandler implements ListChangeListener<Stage>,
        BaseAUTListener {
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            ComponentHandler.class);

    /** the Container hierarchy of the AUT */
    private static AUTJavaFXHierarchy hierarchy = new AUTJavaFXHierarchy();

    /** Businessprocess for getting components */
    private static FindJavaFXComponentBP findBP = new FindJavaFXComponentBP();

    /**
     * Constructor. Adds itself as ListChangeListener to the Stages-List
     */
    public ComponentHandler() {
        CurrentStages.addStagesListener(this);
    }

    @Override
    public void onChanged(Change<? extends Stage> change) {
        change.next();
        List<? extends Stage> changedStages = change.getAddedSubList();
        for (final Stage stage : changedStages) {
            stage.setOnShown(new EventHandler<WindowEvent>() {

                @Override
                public void handle(WindowEvent event) {
                    hierarchy.createHierarchyFrom(stage);
                    stage.setOnShown(null);
                }
            });

        }
        changedStages = change.getRemoved();
        for (final Stage stage : changedStages) {
            hierarchy.removeComponentFromHierarchy(stage);
        }
    }

    @Override
    public long[] getEventMask() {
        return null;
    }

    /**
     * @return the Container hierarchy of the AUT
     */
    public static AUTJavaFXHierarchy getAutHierarchy() {
        return hierarchy;
    }

    /**
     * Searches the hierarchy-map for components that are assignable from the
     * given type
     *
     * @param type
     *            the type to look for
     *            
     * @param <T> component type
     * @return List
     */
    public static <T> List<? extends T> getAssignableFromType(Class<T> type) {
        Set<JavaFXComponent> keys = hierarchy.getHierarchyMap().keySet();
        List<T> result = new ArrayList<T>();
        for (JavaFXComponent object : keys) {
            if (type.isAssignableFrom(object.getRealComponentType())) {
                result.add(type.cast(object.getRealComponent()));
            }
        }
        return result;
    }

    /**
     * Searches the hierarchy-map for components of the given type
     *
     * @param type
     *            the type to look for
     * @return List
     */
    public static List<Object> getInstancesOfType(Class<?> type) {
        Set<JavaFXComponent> keys = hierarchy.getHierarchyMap().keySet();
        List<Object> result = new ArrayList<Object>();
        for (JavaFXComponent object : keys) {
            if (type.isAssignableFrom(object.getRealComponentType())) {
                result.add(object.getRealComponent());
            }
        }
        return result;
    }

    /**
     * Returns the node under the given point
     *
     * @param pos
     *            the point
     * @return the component
     */
    public static Node getComponentByPos(Point2D pos) {
        List<? extends Node> comps = getAssignableFromType(Node.class);
        List<Node> matches = new ArrayList<Node>();
        for (Node n : comps) {
            Set supportetTypes = AUTServerConfiguration.getInstance().
                    getSupportedTypes();
            for (Object object : supportetTypes) {
                if (((ComponentClass)object).getName().
                        equals(n.getClass().getName()) 
                        && NodeBounds.checkIfContains(pos, n)) {
                    matches.add(n);
                }
            }
        }
        
        if (matches.size() == 0) {
            return null;
        }
        if (matches.size() == 1) {
            return matches.get(0);
        }

        // multiple matches, try filtering
        matches = filterMatches(matches);
        if (matches.size() ==  1) {
            // only one match left
            return matches.get(0);
        }
        
        // still multiple matches, guess
        return guessBestPick(matches);
    }

    /**
     * Guesses which Node might be topmost in a list
     * @param matches the list
     * @return the guess for the topmost node
     */
    private static Node guessBestPick(List<Node> matches) {
        if (matches.size() == 0) {
            return null;
        }
        Parent parent = matches.get(0).getParent();
        // in a StackPane the last child has highest z-coordinate
        if (parent instanceof StackPane) {
            ObservableList<Node> children = parent.getChildrenUnmodifiable();
            ArrayList<Node> revertedChildren = new ArrayList<Node>(children);
            Collections.reverse(revertedChildren);
            // Start by checking if the last child of the StackPane is among the matches
            for (Node child : revertedChildren) {
                if (matches.contains(child)) {
                    return child;
                }
            }
        }
        // FALLBACK: Guess randomly (So every match has a chance to get picked at some try)
        int randomNumber = (int)(matches.size() * Math.random());
        return matches.get(randomNumber);
    }

    /**
     * Filters out all parent in a list of matches
     * @param matches the matches
     * @return the filtered list
     */
    private static List<Node> filterMatches(List<Node> matches) {
        List<Node> filteredMatches = new ArrayList<Node>();
        
    checkMatches:
        for (Node match : matches) {
            if (match instanceof Parent) {
                for (Node otherMatch : matches) {
                    if (!otherMatch.equals(match)) {
                        if (isDescendant(otherMatch, match)) {
                            // If match has another match as a descendant then it
                            // should not be considered.
                            // i.e. some kind of mappable container containing a
                            // mappable component gets filtered out here.
                            continue checkMatches;
                        }
                    }
                }
            }
            // No descendants found, so the match can still be considered.
            filteredMatches.add(match);
        }
        return filteredMatches;
    }

    /**
     * Checks whether a node is a descendant of a node
     * @param candidate the possible descendant
     * @param node the node
     * @return whether the candidate is a descendant of the other node
     */
    private static boolean isDescendant(Node candidate, Node node) {
        if (node instanceof Parent) {
            List<Node> children = ((Parent) node).getChildrenUnmodifiable();
            // Check if the candidate is a direct child of the node
            if (children.contains(candidate)) {
                return true;
            }
            // Check all of the node's children
            for (Node child : children) {
                if (isDescendant (candidate, child)) {
                    return true;
                }
            }
        }
        // The candidate is no descendant of the node
        return false;
    }

    /**
     * Investigates the given <code>component</code> for an identifier. It must
     * be distinct for the whole AUT. To obtain this identifier the AUTHierarchy
     * is queried.
     *
     * @param node
     *            the node to get an identifier for
     * @throws NoIdentifierForComponentException
     *             if an identifier could not created for <code>component</code>
     *             .
     * @return the identifier, containing the identification
     */
    public static IComponentIdentifier getIdentifier(Node node)
        throws NoIdentifierForComponentException {

        try {
            return hierarchy.getComponentIdentifier(node);
        } catch (ComponentNotManagedException cnme) {
            log.warn(cnme);
            throw new NoIdentifierForComponentException(
                    "unable to create an identifier for '" //$NON-NLS-1$
                            + node + "'", //$NON-NLS-1$
                    MessageIDs.E_COMPONENT_ID_CREATION);
        }
    }

    /**
     * Finds a Node by id
     *
     * @param id
     *            the id
     * @return the node ore null if there is nothing or something else than a
     *         node found
     */
    public static Node findNodeByID(IComponentIdentifier id) {
        Object comp = findBP.findComponent(id, hierarchy);
        if (comp != null && comp instanceof Node) {
            return (Node) comp;
        }
        return null;
    }

    /**
     * Searchs the component in the AUT, which belongs to the given
     * <code>componentIdentifier</code>.
     *
     * @param componentIdentifier
     *            the identifier of the component to search for
     * @param retry
     *            number of tries to get object
     * @param timeout
     *            timeout for retries
     * @throws ComponentNotFoundException
     *             if no component is found for the given identifier.
     * @throws IllegalArgumentException
     *             if the identifier is null or contains invalid data
     *             {@inheritDoc}
     * @return the found component
     */
    public static Object findComponent(
        IComponentIdentifier componentIdentifier, boolean retry, int timeout)
        throws ComponentNotFoundException, IllegalArgumentException {

        long start = System.currentTimeMillis();
        ReentrantLock lock = hierarchy.getLock();
        try {
            return hierarchy.findComponent(componentIdentifier);
        } catch (ComponentNotManagedException cnme) {
            if (retry) {

                while (System.currentTimeMillis() - start < timeout) {
                    try {
                        lock.lock();
                        return hierarchy.findComponent(componentIdentifier);
                    } catch (ComponentNotManagedException e) { // NOPMD by zeb
                                                               // on 10.04.07
                                                               // 15:25
                        // OK, we will throw a corresponding exception later
                        // if we really can't find the component
                        lock.unlock();
                        try {
                            Thread.sleep(TimingConstantsServer.
                                    POLLING_DELAY_FIND_COMPONENT);
                        } catch (InterruptedException e1) {
                            // ok
                        }
                    } catch (InvalidDataException ide) { // NOPMD by zeb on
                                                         // 10.04.07 15:25
                        // OK, we will throw a corresponding exception later
                        // if we really can't find the component
                    }
                }
            }
            throw new ComponentNotFoundException(cnme.getMessage(),
                    MessageIDs.E_COMPONENT_NOT_FOUND);
        } catch (IllegalArgumentException iae) {
            log.error(iae);
            throw iae;
        } catch (InvalidDataException ide) {
            log.error(ide);
            throw new ComponentNotFoundException(ide.getMessage(),
                    MessageIDs.E_COMPONENT_NOT_FOUND);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }

        }
    }
}
