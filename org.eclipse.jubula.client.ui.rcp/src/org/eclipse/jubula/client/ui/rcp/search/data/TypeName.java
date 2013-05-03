package org.eclipse.jubula.client.ui.rcp.search.data;

import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.tools.i18n.I18n;

/**
 * @author BREDEX GmbH
 * @created Aug 9, 2010
 */
public class TypeName extends SelectionState {

    /** The class of the search type.*/
    private Class<? extends IPersistentObject> m_type;

    /**
     * @param type The type.
     * @param isSelected True, if selected, otherwise false.
     */
    public TypeName(Class<? extends IPersistentObject> type,
            boolean isSelected) {
        super(isSelected);
        m_type = type;
    }

    /**
     * @return The type.
     */
    public Class<? extends IPersistentObject> getType() {
        return m_type;
    }

    /**
     * @return A human readable name.
     */
    public String getName() {
        return I18n.getString(m_type.getName());
    }
}