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
package org.eclipse.jubula.client.ui.controllers.propertysources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.jubula.client.core.businessprocess.CompletenessGuard;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.ui.businessprocess.WorkingLanguageBP;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.Assert;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;


/**
 * This is the abstract superclass of all PropertySources of Jubula.
 * 
 * @author BREDEX GmbH
 * @created 31.01.2005
 * @param <GUINODE_TYPE> type of node
 */
public abstract class AbstractPropertySource <GUINODE_TYPE>
    implements IPropertySource {
    /** The default image */
    public static final Image DEFAULT_IMAGE = null;
    
    /** Image for deprecated action or component*/
    public static final Image DEPRECATED_IMAGE = 
            IconConstants.DEPRECATED_IMAGE; 
    
    /** Image for readonly */
    public static final Image READONLY_IMAGE = IconConstants.READ_ONLY_IMAGE; 
    
    /** Image for incomplete data */
    public static final Image INCOMPL_DATA_IMAGE = IconConstants.
        INCOMPLETE_DATA_IMAGE;
    
    /** Image for warning */
    public static final Image WARNING_IMAGE = IconConstants.WARNING_IMAGE;
    
    /** List of <code>IPropertyDescriptors</code>  */
    private List<IPropertyDescriptor> m_propDescriptors = 
        new ArrayList<IPropertyDescriptor>();
    
    /** The GuiNode for this PropertySource*/
    private GUINODE_TYPE m_guiNode;
    
    
    
    /**
     * Constructor.
     * @param guiNode the GuiNode for this PropertySource.
     */
    public AbstractPropertySource(GUINODE_TYPE guiNode) {
        m_guiNode = guiNode;
    }
    
    
    /**
     * Adds a <code>IPropertyDescriptor</code> to the List of IPropertyDescriptors.
     * @param propDescr the IPropertyDescriptor to add.
     */
    protected void addPropertyDescriptor(IPropertyDescriptor propDescr) {
        m_propDescriptors.add(propDescr);
    }
    
    /**
     * Adds all IPropertyDescriptors of the given Collection.
     * @param propDescriptors the Collection to add.
     */
    protected void addPropertyDescriptor(Collection<IPropertyDescriptor> 
        propDescriptors) {
        m_propDescriptors.addAll(propDescriptors);
    }

    /**
     * Clears the List of <code>IPropertyDescriptor</code>s.
     *
     */
    protected void clearPropertyDescriptors() {
        m_propDescriptors.clear();
    }
    
 
    /**
     * Inits the PropertyDescriptors
     */
    protected abstract void initPropDescriptor();
    
    
    /**
     * {@inheritDoc}
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        initPropDescriptor();
        IPropertyDescriptor[] propdescr = 
            m_propDescriptors.toArray(
                new IPropertyDescriptor[m_propDescriptors.size()]);
        return propdescr;
    }
    
    /**
     * @return the <code>List</code> of <code>IPropertyDescriptor</code>s.
     */
    protected List<IPropertyDescriptor> getPropertyDescriptorList() {
        return m_propDescriptors;
    }
    
    
       
    /**
     * Gets a <code>IPropertyDescriptor</code> by the given ID.
     * @param id the ID of the searched Descriptor.
     * @return a IPropertyDescriptor or null if no descriptor found.
     */
    protected IPropertyDescriptor getPropertyDescriptorById(
        IPropertyController id) {
        Iterator<IPropertyDescriptor> iter = 
            getPropertyDescriptorList().iterator();
        while (iter.hasNext()) {
            IPropertyDescriptor descriptor = iter.next();
            if (id == descriptor.getId()) {
                return descriptor;
            }
        }
        return null;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void setPropertyValue(Object id, Object value) {
        if (id instanceof IPropertyController) {
            IPropertyController pc = (IPropertyController)id;
            pc.setProperty(value);
        } else {
            Assert.notReached(Messages.PropertyIDInexistent 
                + StringConstants.COLON + StringConstants.SPACE + id);
        }
        initPropDescriptor();
        DataEventDispatcher.getInstance().firePropertyChanged(false);
        DataEventDispatcher.getInstance().fireParamChangedListener();
    }
    
    
    /**
     * {@inheritDoc}
     */
    public Object getPropertyValue(Object id) {
        Object obj = null;
        if (id instanceof IPropertyController) {
            obj = ((IPropertyController)id).getProperty();
            return obj != null ? obj : StringConstants.EMPTY;
        }
        Assert.notReached(Messages.PropertyIDInexistent + StringConstants.COLON
                + StringConstants.SPACE + id);
        return obj;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public Object getEditableValue() {
        return this;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void resetPropertyValue(Object id) {
        // Reset not supported. Do nothing.
    }
    
    /**
     * Checks the entry sets
     * @param nodePo the node
     */
    protected void checkEntrySets(IParamNodePO nodePo) {
        final Locale locale = WorkingLanguageBP.getInstance()
            .getWorkingLanguage();
        
        boolean bool = nodePo.isTestDataComplete(locale);
        CompletenessGuard.setCompFlagForTD(nodePo, locale, bool);
    }
    
    /**
     * @return Returns the guiNode.
     */
    protected GUINODE_TYPE getGuiNode() {
        return m_guiNode;
    }
    
    /**
     * A general base class for all property controllers. It stores the
     * current SWT control the controller is associated with.
     */
    public abstract static class AbstractPropertyController implements
        IPropertyController {
        
        /**
         * parent propertysource
         */
        private AbstractGuiNodePropertySource m_propertySource;
        
        /**
         * contructor
         * @param s
         *      AbstractGuiNodePropertySource
         */
        public AbstractPropertyController(AbstractGuiNodePropertySource s) {
            setPropertySource(s);
        }
        /**
         * constructor
         */
        public AbstractPropertyController() {
            // do nothing
        }
        
        /**
         * @see AbstractPropertyController#getImage()
         * @param value the new value
         * @return an <code>Image</code> value. The Image.
         */
        public Image getImage(Object value) {
            if (value == null || StringConstants.EMPTY.equals(value)) {
                return INCOMPL_DATA_IMAGE;
            }
            return DEFAULT_IMAGE;
        }
        
        /**
         * {@inheritDoc}
         * calls getImage(getProperty())
         * if the depending value is available, call getImage(Object value)!
         */
        public Image getImage() {
            return getImage(getProperty());
        }
        
        /**
         * 
         * @return parent PropertySource
         */
        public AbstractGuiNodePropertySource getPropertySource() {
            return m_propertySource;
        }
        
        /**
         * 
         * @param propertySource
         * parent PropertySource
         */
        public void setPropertySource(AbstractGuiNodePropertySource 
            propertySource) {
            m_propertySource = propertySource;
        }
    }
    
    /**
     * A Dummy-Controller for an empty line in the Property View.
     * 
     * @author BREDEX GmbH
     * @created 11.02.2005
     */
    protected class DummyController extends AbstractPropertyController {
        /**
         * {@inheritDoc}
         */
        public boolean setProperty(Object value) {
            // do nothing
            return true;
        }
        
        /**
         * {@inheritDoc}
         */
        public Object getProperty() {
            return StringConstants.EMPTY;
        }
        
        /**
         * {@inheritDoc}
         */
        public Image getImage() {
            return DEFAULT_IMAGE;
        }
    }
}