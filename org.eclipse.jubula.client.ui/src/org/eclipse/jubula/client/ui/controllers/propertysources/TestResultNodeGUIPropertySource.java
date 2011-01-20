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

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.propertydescriptors.JBPropertyDescriptor;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.constants.TestDataConstants;
import org.eclipse.jubula.tools.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.swt.graphics.Image;


/**
 * @author BREDEX GmbH
 * @created 21.04.2005
 */
@SuppressWarnings("synthetic-access")
public class TestResultNodeGUIPropertySource 
    extends AbstractPropertySource < TestResultNode > {
    /** Constant for Category Component */
    public static final String P_TESTSTEP_CAT = I18n.getString("TestResultNodeGUIPropertySource.ResultStep");  //$NON-NLS-1$
    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_STEPNAME = I18n.getString("TestResultNodeGUIPropertySource.StepName"); //$NON-NLS-1$
    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_STEPTYPE = I18n.getString("TestResultNodeGUIPropertySource.StepType"); //$NON-NLS-1$
    /** Constant for Category Component */
    public static final String P_TESTRESULT_CAT = I18n.getString("TestResultNodeGUIPropertySource.TestResult");  //$NON-NLS-1$
    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_TIMESTAMP = I18n.getString("TestResultNodeGUIPropertySource.TimeStamp"); //$NON-NLS-1$
    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_STEPSTATUS = I18n.getString("TestResultNodeGUIPropertySource.StepStatus"); //$NON-NLS-1$
    /** Constant for Category Component */
    public static final String P_TESTERROR_CAT = I18n.getString("TestResultNodeGUIPropertySource.ErrorDetail");  //$NON-NLS-1$
    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_ERRORTYPE = I18n.getString("TestResultNodeGUIPropertySource.ErrorType"); //$NON-NLS-1$

    // CAP Details
    /** Constant for Category Component */
    public static final String P_CAP_CAT = I18n.getString("TestResultNodeGUIPropertySource.CapDetail"); //$NON-NLS-1$
    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_CAPCOMMENT = I18n.getString("TestResultNodeGUIPropertySource.Comment"); //$NON-NLS-1$
    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_COMPNAME = I18n.getString("CapGUIPropertySource.ComponentName"); //$NON-NLS-1$
    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_ACTIONTYPE = I18n.getString("CapGUIPropertySource.ActionType"); //$NON-NLS-1$
    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_COMPTYPE = I18n.getString("CapGUIPropertySource.CompType"); //$NON-NLS-1$
    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_PARAMETERNAME = I18n.getString("CapGUIPropertySource.ParamName"); //$NON-NLS-1$
    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_PARAMETERVALUE = I18n.getString("CapGUIPropertySource.ParamValue"); //$NON-NLS-1$
    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_PARAMETERTYPE = I18n.getString("CapGUIPropertySource.ParamType"); //$NON-NLS-1$

    /**
     * Constructor 
     * @param node
     *      TestResultNodeGUI
     */
    public TestResultNodeGUIPropertySource(TestResultNode node) {
        super(node);
        initPropDescriptor();
    }

    /**
     * Inits the PropertyDescriptors
     */
    protected void initPropDescriptor() {
        clearPropertyDescriptors();
        final INodePO node = getGuiNode().getNode();        
        JBPropertyDescriptor propDes = new JBPropertyDescriptor(
            new ComponentController() {
                public Object getProperty() {
                    final TestResultNode cap = getGuiNode();
                    Date time = cap.getTimeStamp();
                    if (time != null) {
                        String timeStamp = time.toString();
                        return timeStamp;
                    }
                    return StringConstants.EMPTY;
                }
            }, P_ELEMENT_DISPLAY_TIMESTAMP);
        propDes.setCategory(P_TESTSTEP_CAT);
        addPropertyDescriptor(propDes);
        propDes = new JBPropertyDescriptor(
            new ComponentController() {
                public Object getProperty() {
                    return getGuiNode().getName();
                } 
            } , P_ELEMENT_DISPLAY_STEPNAME);
        propDes.setCategory(P_TESTSTEP_CAT);
        addPropertyDescriptor(propDes);
        propDes = new JBPropertyDescriptor(new ComponentController() {
            public Object getProperty() {
                if (Hibernator.isPoSubclass(node, IEventExecTestCasePO.class)) {
                    return I18n
                        .getString("TestResultNodeGUIPropertySource.EventTestCase"); //$NON-NLS-1$
                } else if (Hibernator.isPoSubclass(node, ITestCasePO.class)) {
                    return I18n
                        .getString("TestResultNodeGUIPropertySource.TestCase"); //$NON-NLS-1$
                } else if (Hibernator.isPoSubclass(node, ICapPO.class)) {
                    return I18n
                        .getString("TestResultNodeGUIPropertySource.TestStep"); //$NON-NLS-1$
                } else if (Hibernator.isPoSubclass(node, ITestSuitePO.class)) {
                    return I18n
                        .getString("TestResultNodeGUIPropertySource.TestSuite"); //$NON-NLS-1$
                }
                return I18n
                    .getString("TestResultNodeGUIPropertySource.UnknownElement"); //$NON-NLS-1$
            }
            public Image getImage() {
                return getImageForNode(node);
            }
            
        }, P_ELEMENT_DISPLAY_STEPTYPE);
        propDes.setCategory(P_TESTSTEP_CAT);
        addPropertyDescriptor(propDes);
        propDes = new JBPropertyDescriptor(
            new ComponentController() {
                public Object getProperty() {
                    return node == null || node.getComment() == null 
                        ? StringUtils.EMPTY : node.getComment();
                }
            } , P_ELEMENT_DISPLAY_CAPCOMMENT);
        propDes.setCategory(P_TESTSTEP_CAT);
        addPropertyDescriptor(propDes);
        insertEmptyRow(P_TESTSTEP_CAT);

        initResultDetailsPropDesc();
        
        if (Hibernator.isPoSubclass(node, IEventExecTestCasePO.class)) {
            initEventTestCasePropDescriptor(node);

        }

        if (getGuiNode().getEvent() != null) { 
            initErrorEventPropDescriptor();
        }
        if (Hibernator.isPoSubclass(node, ICapPO.class)) {
            initCapDetailsPropDescriptor();
        }
    }

    /**
     * retrieves the Image for NodePO
     * @param node
     *      NodePO
     * @return
     *      Image
     */
    private Image getImageForNode(final INodePO node) {
        Image image = null;
        if (Hibernator.isPoSubclass(node, ITestSuitePO.class)) {
            image = IconConstants.TS_IMAGE; 
        }
        if (Hibernator.isPoSubclass(node, IExecTestCasePO.class)) {
            if (Hibernator.isPoSubclass(node, 
                IEventExecTestCasePO.class)) {
                image = IconConstants.RESULT_EH_IMAGE; 
            } else {
                image = IconConstants.TC_IMAGE; 
            }
        } 
        if (Hibernator.isPoSubclass(node, ICapPO.class)) {
            TestResultNode parent = getGuiNode().getParent();
            if (Hibernator.isPoSubclass(parent.getNode(),
                IEventExecTestCasePO.class)) {
                image = IconConstants.EH_CAP_IMAGE;
            } else {
                image = IconConstants.CAP_IMAGE; 
            }
        }
        return image;
    }

    /**
     * 
     *
     */
    private void initResultDetailsPropDesc() {
        JBPropertyDescriptor propDes = new JBPropertyDescriptor(
            new ComponentController() {
                public Object getProperty() {
                    return getGuiNode().getStatusString();
                }
                public Image getImage() {
                    int status = getGuiNode().getStatus();
                    switch (status) {
                        case TestResultNode.SUCCESS:
                            return IconConstants.STEP_OK_IMAGE;
                        case TestResultNode.ERROR:
                        case TestResultNode.ERROR_IN_CHILD:
                            return IconConstants.STEP_NOT_OK_IMAGE;
                        case TestResultNode.RETRYING:
                            return IconConstants.STEP_RETRY_IMAGE;
                        case TestResultNode.SUCCESS_RETRY:
                            return IconConstants.STEP_RETRY_OK_IMAGE;
                        case TestResultNode.ABORT:
                            return IconConstants.STEP_NOT_OK_IMAGE;
                        case TestResultNode.NOT_YET_TESTED:
                        case TestResultNode.NOT_TESTED:
                        case TestResultNode.TESTING:
                        case TestResultNode.NO_VERIFY:
                        default:
                            return null;
                    }

                }
            } , P_ELEMENT_DISPLAY_STEPSTATUS);
        propDes.setCategory(P_TESTRESULT_CAT);
        addPropertyDescriptor(propDes);
        insertEmptyRow(P_TESTRESULT_CAT);
    }

    /**
     * 
     */
    private void initCapDetailsPropDescriptor() {
        final ICapPO node = (ICapPO)getGuiNode().getNode();
        JBPropertyDescriptor propDes = new JBPropertyDescriptor(
            new ComponentController() {
                public Object getProperty() {
                    return getGuiNode().getComponentName();
                }
            } , P_ELEMENT_DISPLAY_COMPNAME);
        propDes.setCategory(P_CAP_CAT);
        addPropertyDescriptor(propDes);
        propDes = new JBPropertyDescriptor(
            new ComponentController() {
                public Object getProperty() {
                    return CompSystemI18n.getString(
                            node.getComponentType(), true);
                }
            } , P_ELEMENT_DISPLAY_COMPTYPE);
        propDes.setCategory(P_CAP_CAT);
        addPropertyDescriptor(propDes);
        insertEmptyRow(P_CAP_CAT);
        propDes = new JBPropertyDescriptor(
            new ComponentController() {
                public Object getProperty() {
                    return CompSystemI18n.getString(node.getActionName());
                }
            } , P_ELEMENT_DISPLAY_ACTIONTYPE);
        propDes.setCategory(P_CAP_CAT);
        addPropertyDescriptor(propDes);
        int index = 0;
        insertEmptyRow(P_CAP_CAT);
        for (final IParamDescriptionPO desc : node.getParameterList()) {
            propDes = new JBPropertyDescriptor(
                new ComponentController() {
                    public Object getProperty() {
                        return CompSystemI18n.getString(desc
                            .getUniqueId(), true);
                    }
                } , P_ELEMENT_DISPLAY_PARAMETERNAME);
            propDes.setCategory(P_CAP_CAT);
            addPropertyDescriptor(propDes);
            
            propDes = new JBPropertyDescriptor(
                new ComponentController() {
                    public Object getProperty() {
                        return CompSystemI18n.getString(desc.getType());
                    }
                } , P_ELEMENT_DISPLAY_PARAMETERTYPE);
            propDes.setCategory(P_CAP_CAT);
            addPropertyDescriptor(propDes);
            propDes = createPropDescriptorForParamValue(index);
            addPropertyDescriptor(propDes);
            index++;
            insertEmptyRow(P_CAP_CAT);
        }
    }

    /**
     * @param index of parameter value
     * @return property descriptor
     */
    private JBPropertyDescriptor createPropDescriptorForParamValue(
        final int index) {
        
        JBPropertyDescriptor propDes = new JBPropertyDescriptor(
            new ComponentController() {
                public Object getProperty() {
                    if (getGuiNode().
                            getParamValues().size() >= index + 1) {
                        String returnVal = getGuiNode().
                            getParamValues().get(index);
                        if (returnVal != null
                            && returnVal.length() == 0) {
                            return 
                                TestDataConstants.EMPTY_SYMBOL;
                        } 
                        return returnVal == null 
                            ? StringConstants.EMPTY : returnVal;
                    }
                    return StringConstants.EMPTY;
                }
            } , P_ELEMENT_DISPLAY_PARAMETERVALUE); 
        propDes.setCategory(P_CAP_CAT);
        return propDes;
    }
    
    /**
     * inserts an empty row into a cat
     * @param cat
     *      String
     */
    private void insertEmptyRow(String cat) {
        JBPropertyDescriptor propDes = new JBPropertyDescriptor(
                new ComponentController() {
                    
                    public Object getProperty() {
                        return StringConstants.EMPTY;
                    }
                }, StringConstants.EMPTY);
        propDes.setCategory(cat);
        addPropertyDescriptor(propDes);
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    private void initErrorEventPropDescriptor() {
        JBPropertyDescriptor propDes = new JBPropertyDescriptor(
            new ComponentController() {
                public Object getProperty() {
                    return I18n.getString(getGuiNode()
                        .getEvent().getId());
                }
            } , P_ELEMENT_DISPLAY_ERRORTYPE);
        propDes.setCategory(P_TESTERROR_CAT);
        addPropertyDescriptor(propDes);
        final TestErrorEvent event = getGuiNode().getEvent();
        if (event.getId().equals(TestErrorEvent.ID.VERIFY_FAILED)) {
            Set keys = event.getProps().keySet();
            for (final Object key : keys) {
                propDes = new JBPropertyDescriptor(new ComponentController() {
                    public Object getProperty() {
                        return event.getProps().get(key);
                    }
                }, I18n.getString((String)key));
                propDes.setCategory(P_TESTERROR_CAT);
                addPropertyDescriptor(propDes);
            }
        } else if (event.getId().equals(
                TestErrorEvent.ID.IMPL_CLASS_ACTION_ERROR)
            && event.getProps().keySet().contains(
                TestErrorEvent.Property.DESCRIPTION_KEY)) {
            
            propDes = new JBPropertyDescriptor(new ComponentController() {
                public Object getProperty() {
                    String key = (String)event.getProps().get(
                        TestErrorEvent.Property.DESCRIPTION_KEY);
                    Object[] args = (Object[])event.getProps().get(
                        TestErrorEvent.Property.PARAMETER_KEY);
                    if (args != null) {
                        return I18n.getString(key, args);
                    }
                    
                    return I18n.getString(key, true);
                }
            }, I18n.getString(TestErrorEvent.Property.DESCRIPTION_KEY));
            propDes.setCategory(P_TESTERROR_CAT);
            addPropertyDescriptor(propDes);
        }
        insertEmptyRow(P_TESTERROR_CAT);
    }

    /**
     * @param node
     *            NodePO
     * 
     */
    private void initEventTestCasePropDescriptor(final INodePO node) {
        
        final IEventExecTestCasePO tc = (IEventExecTestCasePO) node;
        JBPropertyDescriptor propDes = new JBPropertyDescriptor(
            new ComponentController() {
                public Object getProperty() {
                    return I18n.getString(tc.getEventType());
                }
            } , I18n.getString("TestResultNodeGUIPropertySource.ErrorType")); //$NON-NLS-1$
        propDes.setCategory(I18n.getString("TestResultNodeGUIPropertySource.Eventhandler")); //$NON-NLS-1$
        addPropertyDescriptor(propDes);
        propDes = new JBPropertyDescriptor(
            new ComponentController() {
                public Object getProperty() {
                    return tc.getReentryProp();
                }
            } , I18n.getString("TestResultNodeGUIPropertySource.Reentry")); //$NON-NLS-1$
        propDes.setCategory(I18n.getString("TestResultNodeGUIPropertySource.Eventhandler")); //$NON-NLS-1$
        addPropertyDescriptor(propDes);
        insertEmptyRow(I18n.getString("TestResultNodeGUIPropertySource.Eventhandler")); //$NON-NLS-1$
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
    private abstract class ComponentController extends
        AbstractPropertyController {
        /**
         * {@inheritDoc}
         */
        
        public boolean setProperty(Object value) {
            // readonly
            return true;
        }
        
        /**
         * {@inheritDoc}
         */
        public abstract Object getProperty();
        
        /**
         * {@inheritDoc}
         */
        public Image getImage() {
            return null;
        }
    }
}
