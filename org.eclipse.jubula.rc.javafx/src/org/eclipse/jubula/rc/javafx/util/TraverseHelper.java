package org.eclipse.jubula.rc.javafx.util;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * Utility class for finding instances of a certain type in the
 * hierarchy below a given node 
 * @author BREDEX GmbH
 * @created 25.03.2014
 * @param <T> the type
 */
public class TraverseHelper<T extends Node> {
    
    /** The result where all instances of the given type are stored */
    private List<T> m_result = new ArrayList<T>();
    
    /**
     * Finds instances of a certain type in the hierarchy below a given node
     * @param parent the parent
     * @param type the type
     */
    private void findInstancesOf(Parent parent, Class<T> type) {
        for (Node object : parent.getChildrenUnmodifiable()) {
            if (type.isAssignableFrom(object.getClass())) {
                m_result.add((T) object);
            }
            if (object instanceof Parent) {                
                findInstancesOf((Parent)object, type);
            }
        }
    }
    
    /**
     * Gives instances of a certain type in the hierarchy below a given node
     * @param parent the parent
     * @param type the type
     * @return returns all instances of the given type which are below 
     *             the parent in the hierarchy
     */
    public List<T> getInstancesOf(Parent parent, Class<T> type) {
        m_result.clear();
        findInstancesOf(parent, type);
        return m_result;
    }
}
