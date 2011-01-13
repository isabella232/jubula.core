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
import java.util.Collections;
import java.util.List;

import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.propertydescriptors.GDPropertyDescriptor;
import org.eclipse.jubula.client.ui.provider.labelprovider.PropertyControllerLabelProvider;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.objects.IComponentIdentifier;
import org.eclipse.swt.graphics.Image;


/**
 * @author BREDEX GmbH
 * @created 21.04.2005
 */
public class OMTechNameGUIPropertySource 
    extends AbstractPropertySource<IObjectMappingAssoziationPO> {

    
    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_COMP = 
        I18n.getString("OMTechNameGUIPropertySource.Component"); //$NON-NLS-1$

    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_COMPADDINFO = 
        I18n.getString("OMTechNameGUIPropertySource.ComponentAddInfo"); //$NON-NLS-1$

    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_COMPNAME = 
        I18n.getString("OMTechNameGUIPropertySource.ComponentName"); //$NON-NLS-1$
    
    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_COMPCLASS = 
        I18n.getString("OMTechNameGUIPropertySource.CompClass");  //$NON-NLS-1$

    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_COMPSUPPCLASS = 
        I18n.getString("OMTechNameGUIPropertySource.CompSuppClass");  //$NON-NLS-1$

    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_HIERARCHY = 
        I18n.getString("OMTechNameGUIPropertySource.Hierarchy");  //$NON-NLS-1$

    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_CONTEXT = 
        I18n.getString("OMTechNameGUIPropertySource.Context");  //$NON-NLS-1$


    /**
     * Constructor
     * 
     * @param assoc The association from which properties are obtained.
     */
    public OMTechNameGUIPropertySource(IObjectMappingAssoziationPO assoc) {
        super(assoc);
        initPropDescriptor();
    }

    /**
     * Inits the PropertyDescriptors
     */
    @SuppressWarnings("synthetic-access")
    protected void initPropDescriptor() {
        clearPropertyDescriptors();
        GDPropertyDescriptor propDes = null;
        // Component Name
        propDes = new GDPropertyDescriptor(
            new ComponentNameController(), P_ELEMENT_DISPLAY_COMPNAME);
        propDes.setCategory(P_ELEMENT_DISPLAY_COMP);
        propDes.setLabelProvider(new PropertyControllerLabelProvider());
        addPropertyDescriptor(propDes);

        // Component Class
        propDes = new GDPropertyDescriptor(
            new ComponentClassController(), P_ELEMENT_DISPLAY_COMPCLASS);
        propDes.setCategory(P_ELEMENT_DISPLAY_COMP);
        propDes.setLabelProvider(new PropertyControllerLabelProvider());
        addPropertyDescriptor(propDes);
        
        // Component SuppClass
        propDes = new GDPropertyDescriptor(
            new ComponentSuppClassController(), 
                P_ELEMENT_DISPLAY_COMPSUPPCLASS);
        propDes.setCategory(P_ELEMENT_DISPLAY_COMPADDINFO);
        propDes.setLabelProvider(new PropertyControllerLabelProvider());
        addPropertyDescriptor(propDes);
        
        initHierarchy();
        initContext();
    }

    /**
     * initalizes the hierarchy
     *
     */
    @SuppressWarnings("unchecked")
    private void initHierarchy() {
        GDPropertyDescriptor propDes = null;
        IComponentIdentifier compId = getGuiNode().getTechnicalName();
        if (compId != null) {
            List hierarchy = compId.getHierarchyNames();
            for (int i = 0; i < hierarchy.size(); i++) {
                if (i == 0) {
                    propDes = new GDPropertyDescriptor(
                            new ComponentHierarchyController(i), 
                            P_ELEMENT_DISPLAY_HIERARCHY);
                } else {
                    propDes = new GDPropertyDescriptor(
                            new ComponentHierarchyController(i), 
                            StringConstants.EMPTY);
                }
                propDes.setCategory(P_ELEMENT_DISPLAY_COMPADDINFO);
                addPropertyDescriptor(propDes);
            }
        }
    }
    /**
     * initalizes the context
     *
     */
    @SuppressWarnings("unchecked")
    private void initContext() {
        GDPropertyDescriptor propDes = null;
        IComponentIdentifier compId = getGuiNode().getTechnicalName();
        if (compId != null) {
            List context = compId.getNeighbours();
            for (int i = 0; i < context.size(); i++) {
                if (i == 0) {
                    propDes = new GDPropertyDescriptor(
                            new ComponentContextController(i), 
                            P_ELEMENT_DISPLAY_CONTEXT);
                } else {
                    propDes = new GDPropertyDescriptor(
                            new ComponentContextController(i), 
                            StringConstants.EMPTY);
                }
                propDes.setCategory(P_ELEMENT_DISPLAY_COMPADDINFO);
                addPropertyDescriptor(propDes);
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    public boolean isPropertySet(Object id) {
        boolean isPropSet = false;
        return isPropSet;
    }

    /**
     * Class to control component name.
     *
     * @author BREDEX GmbH
     * @created 07.01.2005
     */
    private class ComponentNameController extends AbstractPropertyController {
        /**
         * {@inheritDoc}
         */
        public boolean setProperty(Object value) {
            return true;
        }
        
        /**
         * {@inheritDoc}
         */
        public Object getProperty() {
            IComponentIdentifier compId = getGuiNode().getTechnicalName();
            if (compId != null) {
                if (compId.getComponentName() != null) {
                    return compId.getComponentName();
                }
            }
            return StringConstants.EMPTY;
        }  
        
        /**
         * {@inheritDoc}
         */
        public Image getImage() {
            return IconConstants.TECHNICAL_NAME_IMAGE;
        }
    }

    /**
     * Class to control component name.
     *
     * @author BREDEX GmbH
     * @created 07.01.2005
     */
    private class ComponentClassController extends AbstractPropertyController {
        /**
         * {@inheritDoc}
         */
        public boolean setProperty(Object value) {
            return true;
        }
        
        /**
         * {@inheritDoc}
         */
        public Object getProperty() {
            IComponentIdentifier compId = getGuiNode().getTechnicalName();
            if (compId != null) {
                if (compId.getComponentClassName() != null) {
                    return compId.getComponentClassName();
                }
            }
            return StringConstants.EMPTY;
        }  
        
        /**
         * {@inheritDoc}
         */
        public Image getImage() {
            return DEFAULT_IMAGE;
        }
    }

    /**
     * Class to control component name.
     *
     * @author BREDEX GmbH
     * @created 07.01.2005
     */
    private class ComponentSuppClassController extends
        AbstractPropertyController {
        /**
         * {@inheritDoc}
         */
        public boolean setProperty(Object value) {
            return true;
        }
        
        /**
         * {@inheritDoc}
         */
        public Object getProperty() {
            IComponentIdentifier compId = getGuiNode().getTechnicalName();
            if (compId != null) {
                if (compId.getSupportedClassName() != null) {
                    return compId.getSupportedClassName();
                }
            }
            return StringConstants.EMPTY;
        }  
        
        /**
         * {@inheritDoc}
         */
        public Image getImage() {
            return DEFAULT_IMAGE;
        }
    }

    /**
     * Class to control component name.
     *
     * @author BREDEX GmbH
     * @created 07.01.2005
     */
    private class ComponentHierarchyController extends
        AbstractPropertyController {
        
        /** 
         * index of array
         */
        private int m_index = 0;
        
        /**
         * constructor
         * 
         * @param i
         *      int
         */
        public ComponentHierarchyController(int i) {
            m_index = i;
        }

        /**
         * {@inheritDoc}
         */
        public boolean setProperty(Object value) {
            return true;
        }
        
        /**
         * {@inheritDoc}
         */
        public Object getProperty() {
            IComponentIdentifier compId = getGuiNode().getTechnicalName();
            if (compId != null) {
                if (compId.getSupportedClassName() != null
                        && compId.getHierarchyNames().get(m_index) != null) {
                    return compId.getHierarchyNames().get(m_index);
                }
            }
            return StringConstants.EMPTY;
        }  
        
        
        /**
         * {@inheritDoc}
         */
        public Image getImage() {
            return DEFAULT_IMAGE;
        }
    }

    /**
     * Class to control component context.
     *
     * @author BREDEX GmbH
     * @created 11.03.2008
     */
    private class ComponentContextController extends
        AbstractPropertyController {
        
        /** 
         * index of array
         */
        private int m_index = 0;
        
        /**
         * constructor
         * 
         * @param i
         *      int
         */
        public ComponentContextController(int i) {
            m_index = i;
        }

        /**
         * {@inheritDoc}
         */
        public boolean setProperty(Object value) {
            return true;
        }
        
        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        public Object getProperty() {
            IComponentIdentifier compId = getGuiNode().getTechnicalName();
            if (compId != null) {
                if (compId.getSupportedClassName() != null
                        && compId.getNeighbours().get(m_index) != null) {
                    List context = new ArrayList(compId.getNeighbours());
                    Collections.sort(context);
                    return context.get(m_index);
                }
            }
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
