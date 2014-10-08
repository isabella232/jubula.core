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
package org.eclipse.jubula.client.alm.mylyn.ui.propertysource;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author BREDEX GmbH
 */
public class ITaskPropertySource implements IPropertySource {
    /** the cached property descriptors */
    private IPropertyDescriptor[] m_descriptors = null;
    /** the task */
    private ITask m_task;

    /**
     * @param task
     *            the task which was selected
     */
    public ITaskPropertySource(ITask task) {
        m_task = task;
    }

    /** {@inheritDoc} */
    public Object getEditableValue() {
        return "editableValue"; //$NON-NLS-1$
    }

    /**
     * @return Each {@link PropertyDescriptor} will be renderd as an entry of
     *         PropertiesView
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {

        List<IPropertyDescriptor> tmpList = 
            new LinkedList<IPropertyDescriptor>();
        Iterator it = m_task.getAttributes().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            final String key = (String)pairs.getKey();
            PropertyDescriptor p = new PropertyDescriptor(key, key);
            tmpList.add(p);

        }
        m_descriptors = tmpList
                .toArray(new IPropertyDescriptor[tmpList.size()]);
        return m_descriptors;
    }

    /**
     * @return displays the value of the given object id, default is "empty"
     * @param id
     *            the id
     */
    public Object getPropertyValue(Object id) {
        return  m_task.getAttribute((String)id);

    }

    /**
     * {@inheritDoc}
     */
    public boolean isPropertySet(Object arg0) {
        // Do nothing
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void resetPropertyValue(Object arg0) {

        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void setPropertyValue(Object arg0, Object arg1) {
        // Do nothing

    }
}