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
package org.eclipse.jubula.client.qa.caa.gen.ui.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jubula.client.core.businessprocess.db.TestCaseBP;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.ISpecObjContPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.model.ReentryProperty;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMSaveException;
import org.eclipse.jubula.client.core.utils.StringHelper;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ConcreteComponent;

/**
 * @author BREDEX GmbH
 */
public class GenerateAllHandler extends AbstractHandler {
    /** prefix */
    private static final String REQ_PREFIX = "[] [] [] [] - ";
    /** eh */
    private ISpecTestCasePO m_aeEH;
    /** eh */
    private ISpecTestCasePO m_cfEH;
    /** eh */
    private ISpecTestCasePO m_cnfEH;
    /** eh */
    private ISpecTestCasePO m_ceEH;

    /** {@inheritDoc} */
    protected Object executeImpl(ExecutionEvent event) 
        throws ExecutionException {
        final String toolkitID = "ui.toolkit.mobile.IOSToolkitPlugin";
        List<ConcreteComponent> toolkitComponents = 
            getToolkitComponentList(toolkitID);

        StringHelper sh = StringHelper.getInstance();

        ICategoryPO category = NodeMaker.createCategoryPO("GENERATED");
        try {
            NodePM.addAndPersistChildNode(ISpecObjContPO.TCB_ROOT_NODE, 
                category, null);
        
            ISpecTestCasePO tkTC = TestCaseBP.createNewSpecTestCase(toolkitID,
                category, null);

            createEventHandler(category);
            
            for (ConcreteComponent c : toolkitComponents) {
                String visibleTypeName = sh.get(c.getType(), true);

                String testcaseName = visibleTypeName;

                final String componentClass = c.getComponentClass();
                if (StringUtils.isNotBlank(componentClass)) {
                    testcaseName += " - "
                        + StringUtils.substringAfterLast(componentClass, ".");
                }

                ICategoryPO typeCat = NodeMaker.createCategoryPO(testcaseName);
                NodePM.addAndPersistChildNode(category, typeCat, null);

                ISpecTestCasePO typeTC =  NodeMaker.createSpecTestCasePO(
                    REQ_PREFIX + testcaseName);
                
                addEH(typeTC);

                NodePM.addAndPersistChildNode(typeCat, typeTC, null);
                
                TestCaseBP.addReferencedTestCase(tkTC,
                    NodeMaker.createExecTestCasePO(typeTC), null);
                
                createActionTestCases(typeCat, typeTC, c);
            }
            CommandHelper.executeCommand("org.eclipse.ui.file.refresh");
        } catch (Throwable e) {
            throw new ExecutionException("error", e);
        }

        return null;
    }

    /**
     * @param category
     *            category
     * @throws PMSaveException
     *             PMSaveException
     * @throws PMAlreadyLockedException
     *             PMAlreadyLockedException
     * @throws PMException
     *             PMException
     * @throws ProjectDeletedException
     *             ProjectDeletedException
     */
    private void createEventHandler(ICategoryPO category)
        throws PMSaveException, PMAlreadyLockedException, PMException,
        ProjectDeletedException {
        m_aeEH = TestCaseBP.createNewSpecTestCase("EH - Action Error",
            category, null);
        m_cfEH = TestCaseBP.createNewSpecTestCase("EH - Check Failed",
            category, null);
        m_cnfEH = TestCaseBP.createNewSpecTestCase("EH - Component not found",
            category, null);
        m_ceEH = TestCaseBP.createNewSpecTestCase("EH - Configuration Error",
            category, null);
    }

    /**
     * @param typeCat
     *            typeCat
     * @param typeTC
     *            typeTC
     * @param c
     *            c
     * @throws ProjectDeletedException
     *             ProjectDeletedException
     * @throws PMException
     *             PMException
     * @throws PMAlreadyLockedException
     *             PMAlreadyLockedException
     * @throws PMSaveException
     *             PMSaveException
     * @throws InvalidDataException 
     */
    private void createActionTestCases(ICategoryPO typeCat,
        ISpecTestCasePO typeTC, ConcreteComponent c) throws PMSaveException,
        PMAlreadyLockedException, PMException, ProjectDeletedException, 
        InvalidDataException {

        ICategoryPO actionsCat = NodeMaker.createCategoryPO("Actions");
        NodePM.addAndPersistChildNode(typeCat, actionsCat, null);

        final StringHelper sh = StringHelper.getInstance();
        final List<Action> actions = c.getActions();
        
        Collections.sort(actions, new Comparator<Action>() {
            public int compare(Action o1, Action o2) {
                String val1 = sh.get(o1.getName(), false);
                String val2 = sh.get(o2.getName(), false);
                return val1.compareTo(val2);
            }
        });
        
        for (Action action : actions) {
            final String actionName = sh.get(
                action.getName(), true);
            ICategoryPO actionCat = NodeMaker.createCategoryPO(actionName);
            NodePM.addAndPersistChildNode(actionsCat, actionCat, null);
            
            ISpecTestCasePO actionTC =  NodeMaker.createSpecTestCasePO(
                REQ_PREFIX + actionName);
            
            addEH(actionTC);

            NodePM.addAndPersistChildNode(actionCat, actionTC, null);
            
            TestCaseBP.addReferencedTestCase(typeTC,
                NodeMaker.createExecTestCasePO(actionTC), null);
            
            ISpecTestCasePO actionTC01 = TestCaseBP.createNewSpecTestCase(
                REQ_PREFIX + actionName + " - 01", actionCat, null);

            TestCaseBP.addReferencedTestCase(actionTC,
                NodeMaker.createExecTestCasePO(actionTC01), null);
        }
    }

    /**
     * @param actionTC
     *            actionTC
     * @throws InvalidDataException
     *             InvalidDataException
     */
    private void addEH(ISpecTestCasePO actionTC) throws InvalidDataException {
        final IEventExecTestCasePO ae = NodeMaker.createEventExecTestCasePO(
            m_aeEH, actionTC);
        ae.setEventType("TestErrorEvent.Action");
        ae.setReentryProp(ReentryProperty.RETURN);
        actionTC.addEventTestCase(ae);
        
        final IEventExecTestCasePO cf = NodeMaker.createEventExecTestCasePO(
            m_cfEH, actionTC);
        cf.setEventType("TestErrorEvent.VerifyFailed");
        cf.setReentryProp(ReentryProperty.CONTINUE);
        actionTC.addEventTestCase(cf);
        
        final IEventExecTestCasePO cnf = NodeMaker.createEventExecTestCasePO(
            m_cnfEH, actionTC);
        cnf.setEventType("TestErrorEvent.CompNotFound");
        cnf.setReentryProp(ReentryProperty.RETURN);
        actionTC.addEventTestCase(cnf);
        
        final IEventExecTestCasePO ce = NodeMaker.createEventExecTestCasePO(
            m_ceEH, actionTC);
        ce.setEventType("TestErrorEvent.Config");
        ce.setReentryProp(ReentryProperty.RETURN);
        actionTC.addEventTestCase(ce);
    }

    /**
     * @param string
     *            the id of the toolkit to return the components for
     * @return a list of components that belong to the given toolkit
     */
    private List<ConcreteComponent> getToolkitComponentList(String string) {
        CompSystem compSystem = ComponentBuilder.getInstance().getCompSystem();
        List<ConcreteComponent> allComponents = compSystem
            .getConcreteComponents();
        final StringHelper sh = StringHelper.getInstance();
        Collections.sort(allComponents, new Comparator<ConcreteComponent>() {
            public int compare(ConcreteComponent o1, ConcreteComponent o2) {
                String val1 = sh.get(o1.getType(), false);
                String val2 = sh.get(o2.getType(), false);
                return val1.compareTo(val2);
            }
        });

        List<ConcreteComponent> toolkitComponents = 
            new ArrayList<ConcreteComponent>();
        for (ConcreteComponent component : allComponents) {
            if (component.getToolkitDesriptor()
                    .getToolkitID().matches(string)) {
                toolkitComponents.add(component);
            }
        }

        return toolkitComponents;
    }
}
