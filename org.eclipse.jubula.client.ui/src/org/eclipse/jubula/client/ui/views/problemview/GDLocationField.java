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
package org.eclipse.jubula.client.ui.views.problemview;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.markers.internal.ConcreteMarker;
import org.eclipse.ui.views.markers.internal.IField;
import org.eclipse.ui.views.markers.internal.TableComparator;


/**
 * @author BREDEX GmbH
 * @created 27.07.2005
 */
public class GDLocationField implements IField {

    /** the description of the column + text of the column header*/
    private String m_description;

    /** the column header image */
    private Image m_image;

    /**
     * The constructor. 
     */
    public GDLocationField() {
        m_description = I18n.getString("GDLocationField.ColumnHeader"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return m_description;
    }

    /**
     * {@inheritDoc}
     */
    public Image getDescriptionImage() {
        return m_image;
    }

    /**
     * {@inheritDoc}
     */
    public String getColumnHeaderText() {
        return m_description;
    }

    /**
     * {@inheritDoc}
     */
    public Image getColumnHeaderImage() {
        return m_image;
    }

    /**
     * {@inheritDoc}
     */
    public String getValue(Object obj) {
        if (obj == null || !(obj instanceof ConcreteMarker)) {
            return StringConstants.EMPTY; 
        }
        ConcreteMarker marker = (ConcreteMarker)obj;
        return marker.getConcreteRepresentative().getMarker()
            .getAttribute(IMarker.LOCATION, StringConstants.EMPTY); 
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage(Object obj) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int compare(Object obj1, Object obj2) {
        
        if (obj1 == null || obj2 == null || !(obj1 instanceof ConcreteMarker)
                || !(obj2 instanceof ConcreteMarker)) {
            
            return 0;
        }
        final String emptyString = StringConstants.EMPTY;
        ConcreteMarker marker1 = (ConcreteMarker)obj1;
        ConcreteMarker marker2 = (ConcreteMarker)obj2;
        try {
            String attribute1 = (String)marker1.getMarker().getAttribute(
                    IMarker.LOCATION);
            if (attribute1 == null) {
                attribute1 = emptyString;
            }
            String attribute2 = (String)marker2.getMarker().getAttribute(
                    IMarker.LOCATION);
            if (attribute2 == null) {
                attribute2 = emptyString;
            }
            return attribute1.compareTo(attribute2);
        } catch (CoreException e) {
            return 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultDirection() {
        return TableComparator.ASCENDING;
    }

    /**
     * {@inheritDoc}
     */
    public int getPreferredWidth() {
        return 100;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isShowing() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void setShowing(boolean showing) {
        // do nothing
    }
}