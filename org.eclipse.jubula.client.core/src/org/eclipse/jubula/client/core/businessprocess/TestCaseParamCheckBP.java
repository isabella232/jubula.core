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
package org.eclipse.jubula.client.core.businessprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestDataPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.TDCell;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.utils.ModelParamValueConverter;
import org.eclipse.jubula.client.core.utils.ParamValueConverter;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.JBException;
import org.eclipse.jubula.tools.exception.TestCaseParamCheckException;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.osgi.util.NLS;


/**
 * This business process performs checks whenever a parameter value which
 * is a reference will be removed or added. The <code>check()</code> method
 * throws a <code>TestCaseParamCheckException</code> if the checks determines
 * that the operation on the reference(s) is not allowed.
 *
 * @author BREDEX GmbH
 * @created 23.08.2005
 */
public class TestCaseParamCheckBP {
    
    /** The logger */
    private static final Log LOG = 
        LogFactory.getLog(TestCaseParamCheckBP.class);
    
    
    /**
     * This class collects all check errors during a check.
     */
    private static class CheckErrors {
        /**
         * The map of error codes.
         */
        private Map < String, List > m_errorCodes =
            new HashMap < String, List > ();
        /**
         * Adds an error code with the values that will fill the place holders
         * of the internationalized error message.
         * 
         * @param errorCode
         *            The error code
         * @param params
         *            The message parameters
         */
        public void add(String errorCode, Object[] params) {
            if (!m_errorCodes.containsKey(errorCode)) {
                m_errorCodes.put(errorCode, new ArrayList < Object > ());
            }
            if (params != null) {
                List<Object> paramList = m_errorCodes.get(errorCode);
                paramList.addAll(Arrays.asList(params));
            }
        }
        /**
         * @return <code>true</code> if this class has errors
         */
        public boolean hasErrors() {
            return !m_errorCodes.isEmpty();
        }
        /**
         * Builds the full error message from all stored error codes.
         * 
         * @return The error message
         */
        public String buildErrorMessage() {
            StringBuffer msg = new StringBuffer();
            
            for (String key : m_errorCodes.keySet()) {
                Object[] object = new Object[] {m_errorCodes.get(key)};
                if (m_errorCodes.get(key) instanceof ArrayList) {
                    List l = m_errorCodes.get(key);
                    if (l.isEmpty()) {
                        object = new Object[]{};
                    }
                }
                msg.append(I18n.getString(
                    key, object));
                msg.append('\n');
            }
            return msg.toString();
        }
    }
    /**
     * This interface represents a parameter check.
     */
    public static interface IParamCheck {
        /**
         * This method is called to perform the check.
         * 
         * @param node
         *            The node with the parameters to check
         * @param ref
         *            The references to check
         * @param locale currently used locale
         * @param errors
         *            The error collection
         */
        public void execute(IParamNodePO node, List<String> ref, 
            Locale locale, CheckErrors errors);
        /**
         * Sets the utility to format/parse a parameter reference.
         * 
         * @param format The utility
         */
    }
    /**
     * The base class for parameter checks. It holds an <code>IParamCheck</code>
     * instance (optionally) to follow the decorator pattern.
     */
    public abstract static class ParamCheck implements IParamCheck {
        /**
         * The check instance (optional).
         */
        private IParamCheck m_check;
        /**
         * Constructor.
         * 
         * @param check Another check
         */
        protected ParamCheck(IParamCheck check) {
            m_check = check;
        }
        /**
         * Default constructor.
         */
        protected ParamCheck() {
            // Nothing to be done
        }
        /**
         * @return Another check
         */
        protected IParamCheck getCheck() {
            return m_check;
        }

        /**
         * Checks if the test data in the test data manager of the passed node
         * has no references, e.g. <code>=FOO</code> as parameter value set
         * for the parameter with the userdefined name <code>ref</code>.
         * 
         * @param ref
         *            The reference (will be converted to the parameter name
         * @param errors
         *            The error collection
         * @param parent of node to check
         * @param locale currently used locale
         */
        protected void checkTDHasNoRefAsValue(String ref, 
            CheckErrors errors, IParamNodePO parent, Locale locale) {
            try {
                IParamDescriptionPO desc = parent.getParameterForName(ref);
                ITestDataPO td = parent.getDataManager().getCell(0, desc);
                ParamValueConverter conv = new ModelParamValueConverter(
                    td, parent, locale, desc);
                List<String> refsForTD = conv.getNamesForReferences();
                if (!refsForTD.isEmpty() 
                    && !hasMoreThanOneChildrenWithRef(parent, ref, locale)) {

                    errors.add(NLS.bind(
                            Messages.TestCaseParamCheckBPTestCaseDefinesRefs,
                            new String[] { parent.getName(), ref }),
                            null);
                }
            } catch (IndexOutOfBoundsException e) { // NOPMD by al on 3/19/07 1:24 PM
                // Nothing to be done
            }
        }
        
        /**
         * Checks if the given node has more than one children which has the
         * given reference.
         * @param node the parent whose children to check
         * @param ref the reference to check
         * @param locale currently used locale
         * @return true if there are more than one children with the given 
         * reference, false otherwise.
         */
        private boolean hasMoreThanOneChildrenWithRef(IParamNodePO node, 
                String ref, Locale locale) {
            
            Iterator<INodePO> childIter = node.getNodeListIterator();
            int refCount = 0;
            while (refCount < 2 && childIter.hasNext()) {
                INodePO child = childIter.next();
                if (child instanceof IParamNodePO) {
                    Iterator<TDCell> refIter = 
                        ((IParamNodePO)child).getParamReferencesIterator(
                                locale);
                    while (refIter.hasNext()) {
                        TDCell cell = refIter.next();
                        List<String> refs = cell.getReferences(node, locale);
                        if (refs.contains(ref)) {
                            refCount++;
                            break;
                        }
                    }
                }
            }
            return refCount > 1;
        }
        
    }
    /**
     * This class checks if test execution nodes have a referenced test data
     * manager. The passed node is expected to be a test case node, and the
     * check iterates over all associated test execution nodes and calls
     * {@link ExecTestCasePO#getHasReferencedTD()}.
     */
    private static class ExecTcHaveReferencedTDCheck extends ParamCheck {
        /**
         * {@inheritDoc}
         */
        public void execute(IParamNodePO node, List<String> ref, 
            Locale locale, CheckErrors errors) {
            ISpecTestCasePO parent = (ISpecTestCasePO)node.getParentNode();
            List<IExecTestCasePO> execTestCases = 
                new ArrayList<IExecTestCasePO>(0);
            try {
                execTestCases = NodePM.getAllExecTestCases(
                    parent.getGuid(), parent.getParentProjectId());
            } catch (JBException e) {
                LOG.error(e);
            }
            List <String> paramNames = parent.getParamNames();
            for (String refName : ref) {
                if (refName != null && !paramNames.contains(refName)) {
                    List<String> checkList = new ArrayList<String>();
                    for (IExecTestCasePO execTc : execTestCases) {
                        if (!execTc.getHasReferencedTD()) {
                            String name = execTc.getParentNode() != null 
                                ? execTc.getParentNode().getName() 
                                    : execTc.getName();
                            if (!checkList.contains(name)) {
                                errors.add(Messages
                                    .TestCaseParamCheckBPTestExecHasOwnTestData,
                                    new String[] { name });
                                checkList.add(name);
                            }
                        }
                    }
                    return;
                }
            }
            
        }
    }
    /**
     * This class checks the test step node.
     */
    public static class CapParamRefCheck extends ParamCheck {
        /**
         * Flag to differentiate between the removal and insertion of a
         * reference.
         */
        private boolean m_insertParamRef;
        /**
         * The test case node check
         */
        private SpecTcParamRefCheck m_specTcParamRefCheck;
        /**
         * Constructor.
         * 
         * @param insertParamRef
         *            If <code>true</code>, the insertion of a reference is
         *            checked, otherwise the removal
         */
        public CapParamRefCheck(boolean insertParamRef) {
            super(new ExecTcHaveReferencedTDCheck());
            m_insertParamRef = insertParamRef;
            m_specTcParamRefCheck = new SpecTcParamRefCheck();
        }
        /**
         * {@inheritDoc}
         */
        public void execute(IParamNodePO node, List <String> ref, 
            Locale locale, CheckErrors errors) {

            if (!m_insertParamRef) {
                ISpecTestCasePO parent = (ISpecTestCasePO)node
                .getParentNode();
                IParamDescriptionPO desc = null;
                for (String refName : ref) {
                    if (parent.getParamNames().contains(refName)) {
                        desc = parent.getParameterForName(refName);
                    }
                    if (desc != null) {
                        try {
                            ITestDataPO td = parent.getDataManager().getCell(0,
                                desc);
                            String value = td.getValue().getValue(locale);
                            if (value != null) {
                                ParamValueConverter conv = 
                                    new ModelParamValueConverter(
                                        value, parent, locale, desc);
                                if (!conv.getNamesForReferences().isEmpty()) {
                                    m_specTcParamRefCheck.execute(
                                        parent, ref, locale, errors);
                                }
                            }
                        } catch (IndexOutOfBoundsException e) { // NOPMD by al on 3/19/07 1:24 PM
                            // Nothing to be done
                        }
                    }
                }
            }
            getCheck().execute(node, ref, locale, errors);
        }
    }
    /**
     * This class validates, if the specTC is already reused and creates an
     * error message in this case
     */
    public static class SpecTcParamRefCheck extends ParamCheck {
        /** false if editor should be dirty (e.g. after not allowed d&d) */
        private static boolean editorShouldBeDirty = true;
        
        /**
         * {@inheritDoc}
         */
        public void execute(IParamNodePO node, List <String> ref, 
            Locale locale, CheckErrors errors) {
            ISpecTestCasePO specNode = (ISpecTestCasePO)node;
            List<IExecTestCasePO> execTestCases;
            try {
                execTestCases = NodePM.getAllExecTestCases(specNode.getGuid(), 
                    specNode.getParentProjectId());
                for (IExecTestCasePO execTc : execTestCases) {
                    if (execTc.getParentNode() instanceof ITestSuitePO) {
                        continue;
                    }
                    String name = execTc.getParentNode() != null ? execTc
                        .getParentNode().getName() : execTc.getName();
                    errors.add(Messages.TestCaseParamCheckBPTestCaseIsUsed1
                            + specNode.getName() 
                            + Messages.TestCaseParamCheckBPTestCaseIsUsed2,
                            new String[] { name });
                    editorShouldBeDirty = false;
                }   
            } catch (JBException e) {
                errors.add(Messages.ErrorMessageDATABASE_GENERAL, null);
            }
        }

        /**
         * @return Returns the editorShouldBeDirty.
         */
        public static boolean editorShouldBeDirty() {
            return editorShouldBeDirty;
        }

        /**
         * @param dirty The editorShouldBeDirty to set.
         */
        public static void setEditorShouldBeDirty(boolean dirty) {
            SpecTcParamRefCheck.editorShouldBeDirty = dirty;
        }
    }
    /**
     * This class checks the test execution node.
     */
    public static class ExecTcParamRefCheck extends ParamCheck {
        /**
         * Flag to differentiate between the removal and insertion of a
         * reference.
         */
        private boolean m_insertParamRef;
        /**
         * Constructor.
         * 
         * @param insertParamRef
         *            If <code>true</code>, the insertion of a reference is
         *            checked, otherwise the removal
         */
        public ExecTcParamRefCheck(boolean insertParamRef) {
            m_insertParamRef = insertParamRef;
        }
        /**
         * {@inheritDoc}
         */
        public void execute(IParamNodePO node, List <String> ref, 
            Locale locale, CheckErrors errors) {
            IExecTestCasePO execNode = (IExecTestCasePO)node;
            INodePO parent = execNode.getParentNode();
            Class parentPoClass = Hibernator.getClass(parent);
            if (m_insertParamRef) {
                if (Hibernator.isPoClassSubclass(
                        parentPoClass, ITestSuitePO.class)) {
                    errors.add(
                        Messages.TestCaseParamCheckBPTestExecHasTestSuiteParent,
                        new String[] { execNode.getName() });
                }
            } else {
                if (Hibernator.isPoClassSubclass(
                        parentPoClass, IParamNodePO.class)) {
                    for (String refName : ref) {
                        checkTDHasNoRefAsValue(refName, errors, 
                            (IParamNodePO)parent, locale);
                    }
                }
            }
        }
    }
    /**
     * This class checks the removal and insertion of a reference in a cell of
     * the test data manager.
     */
    public static class TestDataParamCheck extends ParamCheck {
        /**
         * {@inheritDoc}
         */
        public void execute(IParamNodePO node, List <String> ref, 
            Locale locale, CheckErrors errors) {
            for (Iterator<TDCell> it = 
                        node.getParamReferencesIterator(0, locale); 
                    it.hasNext();) {

                List<String> refNames = it.next().getReferences(node, locale);
                String notAllowedRef = StringConstants.EMPTY;
                if (!refNames.containsAll(ref)) {
                    for (String refInput : ref) {                        
                        if (!refNames.contains(refInput)) {
                            notAllowedRef = refInput;
                            break;
                        }
                    }
                    errors.add(Messages
                            .TestCaseParamCheckBPInvalidRefInTestData,
                        new String[] { notAllowedRef });
                }
            }
            if (!node.getParamReferencesIterator(0, locale).hasNext()) {
                for (String refInput : ref) {
                    errors.add(Messages
                            .TestCaseParamCheckBPInvalidRefInTestData,
                        new String[] { refInput });
                }
            }
        }
    }
    /**
     * This check is a decorator to check all parameters of a node. This is for
     * example useful if a node is checked before it is removed or added.
     */
    public static class AllNodesParamCheck extends ParamCheck {
        /**
         * Constructor.
         * 
         * @param check Another check
         */
        public AllNodesParamCheck(IParamCheck  check) {
            super(check);
        }
        /**
         * {@inheritDoc}
         */
        public void execute(IParamNodePO node, List<String> ref, 
            Locale locale, CheckErrors errors) {
            for (Iterator<TDCell> it = node.getParamReferencesIterator(locale); 
                    it.hasNext();) {
                List<String> references = it.next().getReferences(node, locale);
                getCheck().execute(node, references, locale, errors);
            }
        }
    }
    
    /**
     * Checks the passed parameter node by calling the <code>execute()</code>
     * method of <code>check</code>. The reference to check is passed to
     * <code>execute()</code>.
     * 
     * @param toCheck
     *            The node to check
     * @param refToCheck
     *            The references to check
     * @param check
     *            The check that will be called
     * @param locale currently used locale
     * @throws TestCaseParamCheckException
     *             If the check fails and the <code>execute</code> method of
     *             the check adds an error to the error collection
     */
    public void check(IParamNodePO toCheck, List<String> refToCheck, 
        Locale locale, IParamCheck check)
        throws TestCaseParamCheckException {

        CheckErrors errors = new CheckErrors();
        check.execute(toCheck, refToCheck, locale, errors);
        
        if (errors.hasErrors()) {
            throw new TestCaseParamCheckException(errors.buildErrorMessage(),
                    MessageIDs.E_PARAMETER_ERROR);
        }
    }
    
    
}
