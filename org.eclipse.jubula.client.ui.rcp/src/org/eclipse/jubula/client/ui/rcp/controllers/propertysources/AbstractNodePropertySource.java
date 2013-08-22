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
package org.eclipse.jubula.client.ui.rcp.controllers.propertysources;


import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.businessprocess.AbstractParamInterfaceBP;
import org.eclipse.jubula.client.core.businessprocess.IParamNameMapper;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameMapper;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.TestCaseParamBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.core.utils.GuiParamValueConverter;
import org.eclipse.jubula.client.core.utils.IParamValueValidator;
import org.eclipse.jubula.client.core.utils.NullValidator;
import org.eclipse.jubula.client.core.utils.StringHelper;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.WorkingLanguageBP;
import org.eclipse.jubula.client.ui.rcp.controllers.propertydescriptors.IVerifiable;
import org.eclipse.jubula.client.ui.rcp.controllers.propertysources.IParameterPropertyController.ParameterInputType;
import org.eclipse.jubula.client.ui.rcp.editors.IJBEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.xml.businessmodell.Param;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


/**
 * @author BREDEX GmbH
 * @created 04.08.2005
 */
public abstract class AbstractNodePropertySource 
    extends AbstractPropertySource<INodePO> {

    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_COMMENT =
        Messages.AbstractGuiNodePropertySourceComment;
          
    /**
     * The business process to handle the parameters and references.
     */
    private TestCaseParamBP m_testCaseParamBP;

    /**
     * a usable read only flag
     */
    private boolean m_readOnly;
    
    /** active input method for entering Parameter values */
    private ParameterInputType m_activeParameterInputType = 
        ParameterInputType.LOCAL;
    
    /** cached property descriptor for comment */
    private IPropertyDescriptor m_commentDesc = null;
    
    /** cached property descriptor for taskId */
    private IPropertyDescriptor m_taskIdPropDesc = null;
    
    /**
     * @param guiNode the depending GuiNode.
     */
    public AbstractNodePropertySource(INodePO guiNode) {
        super(guiNode);
        m_testCaseParamBP = new TestCaseParamBP();
    }

    /**
     * {@inheritDoc}
     */
    protected void initPropDescriptor() {
        if (m_commentDesc == null) {
            m_commentDesc = new TextPropertyDescriptor(
                    new CommentController(), P_ELEMENT_DISPLAY_COMMENT);
        }
        addPropertyDescriptor(m_commentDesc);
    }

    /**
     * @return The business process to handle the parameters and references
     */
    protected TestCaseParamBP getTestCaseParamBP() {
        return m_testCaseParamBP;
    }
    
    /**
     * Controller for the comment field.
     * @author BREDEX GmbH
     * @created 04.08.2005
     */
    protected class CommentController extends AbstractPropertyController {
        /**
         * {@inheritDoc}
         */
        public boolean setProperty(Object value) {
            if (value != null) {
                getPoNode().setComment(String.valueOf(value));
            } else {
                getPoNode().setComment(null);
            }
            return true;
        }

        /**
         * {@inheritDoc}
         */
        public Object getProperty() {
            if (getPoNode() != null && getPoNode().getComment() != null) {
                return getPoNode().getComment();
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
     * Controller for the taskId field.
     * @author BREDEX GmbH
     * @created 20.08.2013
     */
    protected class TaskIdController extends AbstractPropertyController {
        /** {@inheritDoc} */
        public boolean setProperty(Object value) {
            if (value != null) {
                getPoNode().setTaskId(String.valueOf(value));
            } else {
                getPoNode().setTaskId(null);
            }
            return true;
        }

        /** {@inheritDoc} */
        public Object getProperty() {
            if (getPoNode() != null && getPoNode().getTaskId() != null) {
                return getPoNode().getTaskId();
            }
            return StringConstants.EMPTY;
        }

        /** {@inheritDoc} */
        public Image getImage() {
            return DEFAULT_IMAGE;
        }
    }
    
    /**
     * Class to control the referenced taskId
     * 
     * @author BREDEX GmbH
     * @created 20.08.2013
     */
    protected class ReadOnlyTaskIdController extends TaskIdController {
        /** {@inheritDoc} */
        public boolean setProperty(Object value) {
            return true; // do nothing, read only
        }

        @Override
        public Object getProperty() {
            INodePO node = getPoNode();
            if (node instanceof IRefTestSuitePO) {
                IRefTestSuitePO refTS = (IRefTestSuitePO) node;
                return refTS.getTestSuite().getTaskId();
            }
            return super.getProperty();
        }

        /** {@inheritDoc} */
        public Image getImage() {
            return READONLY_IMAGE;
        }
    }
    
    /**
     * This class represents a controller for parameter values.
     */
    public abstract class AbstractParamValueController extends
        AbstractPropertyController implements IParameterPropertyController {
        
        /**
         * Parameter description
         */
        private IParamDescriptionPO m_paramDescr;
        
        /** used updating parameters */
        private IParamNameMapper m_paramNameMapper;
        
        /**
         * Constructor
         * @param paramDescr the Parameter description.
         * @param s 
         *     AbstractNodePropertySource 
         * @param paramNameMapper the param name mapper
         */
        public AbstractParamValueController(AbstractNodePropertySource s, 
            IParamDescriptionPO paramDescr, IParamNameMapper paramNameMapper) {
            super(s);
            m_paramDescr = paramDescr;
            m_paramNameMapper = paramNameMapper;
        }
        
        /**
         * @return an Iterator over the value set.<br>
         * The elements of the Iterator are of the type <code>ValueSetElement</code> <br>  
         * If there is no value set the Iterator has no elements!
         */
        protected Iterator getValueSetIterator() {
            INodePO node = getPoNode();
            if (!(Persistor.isPoSubclass(node, ICapPO.class))) {
                return IteratorUtils.EMPTY_ITERATOR;
            }
            Param param = ((ICapPO)node).getMetaAction().findParam(
                m_paramDescr.getUniqueId());
            return param.valueSetIterator();
        }
        
        /**
         * {@inheritDoc}
         */
        public String getProperty() {
            IParamNodePO paramNode = (IParamNodePO)getPoNode();
            String property = AbstractParamInterfaceBP
                    .getGuiStringForParamValue(paramNode, m_paramDescr, 0,
                            WorkingLanguageBP.getInstance()
                                    .getWorkingLanguage());
            return property == null ? StringConstants.EMPTY : property;
        }
        
        /**
         * {@inheritDoc}
         */
        public final boolean setProperty(Object value) {
            if (getPropertySource().isReadOnly()) {
                return true;
            }
            boolean propSet = false;

            if (value instanceof String) {
                IParamNodePO node = (IParamNodePO)getPoNode();
                Locale lang = 
                    WorkingLanguageBP.getInstance().getWorkingLanguage();
                GuiParamValueConverter conv = new GuiParamValueConverter(
                    (String)value, node, lang, getParamDesc(), 
                    getValidatorForConverter(getParamDesc()));
                if (conv.getErrors().isEmpty()) {
                    getTestCaseParamBP().startParameterUpdate(
                        conv, lang, 0, m_paramNameMapper);
                    DataEventDispatcher.getInstance().firePropertyChanged(
                            false);
                }
                checkEntrySets(node);
                propSet = true;
                return propSet;
            }
            
            Object val = ((List)value).get(0);

            IParamNodePO node = (IParamNodePO)getPoNode();
            IParamNameMapper mapper = 
                (IParamNameMapper)((List)value).get(1);
            Locale lang = WorkingLanguageBP.getInstance().getWorkingLanguage();
            GuiParamValueConverter conv = new GuiParamValueConverter(
                (String)val, node, lang, getParamDesc(), 
                getValidatorForConverter(getParamDesc()));
            if (conv.getErrors().isEmpty()) {
                getTestCaseParamBP().startParameterUpdate(
                    conv, lang, 0, mapper);
                DataEventDispatcher.getInstance().firePropertyChanged(false);
            }
            checkEntrySets(node);
            propSet = true;
            return propSet;
        }
        
        /**
         * @return The description of the parameter the controller operates on
         */
        public IParamDescriptionPO getParamDesc() {
            return m_paramDescr;
        }
        
        /**
         * @return the paramNode
         */
        public IParamNodePO getParamNode() {
            return (IParamNodePO)getPoNode();
        }

        /**
         * 
         * {@inheritDoc}
         */
        public boolean isPropertySet() {
            return StringUtils.isNotEmpty(getProperty());
        }
        
        /**
         * {@inheritDoc}
         */
        public ParameterInputType getInputType() {
            return ParameterInputType.LOCAL;
        }
    }
    
    /**
     * 
     * @return the readonly Flag
     */
    public boolean isReadOnly() {
        return m_readOnly;
    }

    /**
     * 
     * @param readOnly
     *  the read only flag
     */
    protected void setReadOnly(boolean readOnly) {
        m_readOnly = readOnly;
    }
    
    /**
     * 
     * @param inputType The active input type for entry of Parameter values.
     */
    public void setActiveParameterInputType(ParameterInputType inputType) {
        m_activeParameterInputType = inputType;
    }

    /**
     * 
     * @param parameterPropertyController The controller for which to check 
     *                                    the enablement.
     * @return <code>true</code> if the given controller should be enabled. 
     *         Otherwise <code>false</code>.
     */
    public boolean isParameterEntryEnabled(
            IParameterPropertyController parameterPropertyController) {
        
        return parameterPropertyController.getInputType().isEnabled(
                m_activeParameterInputType);
    }
    
    /**
     * @return the corresponding NodePO object
     */
    protected INodePO getPoNode() {
        return getGuiNode();
    }
    
    /**
     * 
     * @return the param name mapper for this property source.
     */
    protected IParamNameMapper getActiveParamNameMapper() {
        final IEditorPart activeEditor = Plugin.getActiveEditor();
        IParamNameMapper mapper = ParamNameBP.getInstance();
        if (activeEditor instanceof IJBEditor) {
            IParamNameMapper editorMapper = 
                ((IJBEditor)activeEditor).getEditorHelper()
                    .getEditSupport().getParamMapper();
            if (editorMapper != null) {
                mapper = editorMapper;
            }
        }
        return mapper;
    }
    
    /**
     * 
     * @return the component name mapper for this property source.
     */
    protected IWritableComponentNameMapper getActiveComponentNameMapper() {
        final IEditorPart activeEditor = Plugin.getActiveEditor();
        IWritableComponentNameMapper mapper = null;
        if (activeEditor instanceof IJBEditor) {
            mapper = ((IJBEditor)activeEditor).getEditorHelper()
                .getEditSupport().getCompMapper();
        }
        return mapper;
    }

    /**
     * Class to control the element name.
     * @author BREDEX GmbH
     * @created 06.09.2006
     */
    public class ElementNameController extends AbstractPropertyController {
        
        /**
         * {@inheritDoc}
         */
        public boolean setProperty(Object value) {
            String name = null;
            if (value != null) {
                name = String.valueOf(value);
            }
            getPoNode().setName(name);
            DataEventDispatcher.getInstance().firePropertyChanged(false);
            return true;
        }
        
        /**
         * {@inheritDoc}
         */
        public Object getProperty() {
            if (getPoNode() != null) {
                String name = getPoNode().getName();
                if (name != null) {
                    return name;
                }
            }
            return StringConstants.EMPTY;
        }
    }
    
    /**
     * @param desc parameter description
     * @return appropriate validator
     */
    protected IParamValueValidator getValidatorForConverter(
        IParamDescriptionPO desc) {
        List<IPropertyDescriptor> pdList = 
            getPropertyDescriptorList();
        IVerifiable descr = null;
        for (IPropertyDescriptor pd : pdList) {
            if (pd.getId() 
                instanceof AbstractParamValueController) {
                AbstractParamValueController ctrl = 
                    (AbstractParamValueController)pd.getId();
                if (ctrl.getParamDesc().equals(desc)) {
                    descr = (IVerifiable)pd;
                }
            }
        }
        return (descr != null) ? descr.getDataValidator() : new NullValidator();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isPropertySet(Object id) {
        if (id instanceof IParameterPropertyController) {
            return ((IParameterPropertyController)id).isPropertySet();
        }
        
        return false;
    }

    /**
     * Updates the active Parameter value input type.
     */
    public void updateParameterInputType() {
        setActiveParameterInputType(calculateActiveParameterInputType());
    }

    /**
     * 
     * @return the Parameter input type that is currently in use.
     */
    private ParameterInputType calculateActiveParameterInputType() {
        for (IPropertyDescriptor desc : getPropertyDescriptors()) {
            if (desc.getId() instanceof IParameterPropertyController) {
                IParameterPropertyController controller =
                    (IParameterPropertyController)desc.getId();
                if (controller.isPropertySet()) {
                    return controller.getInputType();
                }
            }
        }
        
        return ParameterInputType.LOCAL;
    }
    
    /**
     * @param desc
     *            the param description
     * @return a string representing the parameter name
     */
    protected String getParameterNameDescr(IParamDescriptionPO desc) {
        StringBuilder sb = new StringBuilder(desc.getName());
        sb.append(StringConstants.SPACE);
        sb.append(StringConstants.LEFT_BRACKET);
        sb.append(StringHelper.getInstance().getMap()
                .get(desc.getType()));
        sb.append(StringConstants.RIGHT_BRACKET);
        return sb.toString();
    }

    /**
     * @return the taskIdPropDesc
     */
    protected IPropertyDescriptor getTaskIdPropDesc() {
        return m_taskIdPropDesc;
    }

    /**
     * @param taskIdPropDesc the taskIdPropDesc to set
     */
    protected void setTaskIdPropDesc(IPropertyDescriptor taskIdPropDesc) {
        m_taskIdPropDesc = taskIdPropDesc;
    }
}