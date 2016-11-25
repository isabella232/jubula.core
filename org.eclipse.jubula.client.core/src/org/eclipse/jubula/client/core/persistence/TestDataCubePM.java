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
package org.eclipse.jubula.client.core.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.eclipse.jubula.client.core.model.IDataSetPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.utils.FunctionToken;
import org.eclipse.jubula.client.core.utils.IParamValueToken;
import org.eclipse.jubula.client.core.utils.LiteralToken;
import org.eclipse.jubula.client.core.utils.ParamValueConverter;
import org.eclipse.jubula.client.core.utils.SimpleStringConverter;
import org.eclipse.jubula.client.core.utils.SimpleValueToken;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;

/**
 * PM to handle all test data cube related Persistence (JPA / EclipseLink)
 * queries
 * 
 * @author BREDEX GmbH
 * @created Jul 21, 2010
 */
public class TestDataCubePM {
    /** hide */
    private TestDataCubePM() {
        // empty
    }

    /**
     * @param tdc
     *            the test data cube to search for reusage
     * @param session
     *            The session into which the test cases will be loaded.
     * @return list of ITestDataCubePO
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<ITestDataCubePO> computeReuser(IParameterInterfacePO tdc,
            EntityManager session) {

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery();
        Root from = query.from(PoMaker.getTestDataCubeClass());
        query.select(from)
                .where(builder.and(
                        builder.isNotNull(from.get("hbmReferencedDataCube")), //$NON-NLS-1$
                        builder.equal(from.get("hbmParentProjectId"), //$NON-NLS-1$
                                tdc.getParentProjectId())));

        List<ITestDataCubePO> queryResult =
                session.createQuery(query).getResultList();
        List<ITestDataCubePO> result = new ArrayList<ITestDataCubePO>();
        for (ITestDataCubePO pio : queryResult) {
            if (areEqual(pio.getReferencedDataCube(), tdc)) {
                result.add(pio);
            }
        }
        return result;
    }

    /**
     * @param pioToSearch
     *            the test data cube to search for reusage
     * @param session
     *            The session into which the test cases will be loaded.
     * @param proj
     *            the project to search in
     * @return list of param node po; without any ITestDataCubePOs themselves as
     *         they don't have a parent project id
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<IParamNodePO> computeParamNodeReuser(
            IParameterInterfacePO pioToSearch, EntityManager session,
            IProjectPO proj) {

        Query query = session.createQuery(
                "select paramPO from ParamNodePO paramPO where paramPO.hbmParentProjectId = :projId"); //$NON-NLS-1$
        query.setParameter("projId", proj.getId()); //$NON-NLS-1$

        List<IParameterInterfacePO> queryResult = query.getResultList();
        List<IParamNodePO> result = new ArrayList<IParamNodePO>();
        for (IParameterInterfacePO pio : queryResult) {
            search: if (pio instanceof IParamNodePO) {
                IParamNodePO pn = (IParamNodePO) pio;
                /*
                 * Start search for function call ?getCentralTestDataSetValue()
                 */
                for (IDataSetPO dataSet : pn.getDataManager().getDataSets()) {
                    for (int i = 0; i < dataSet.getColumnCount(); i++) {
                        String testData = dataSet.getValueAt(i);
                        ParamValueConverter conv = new SimpleStringConverter(
                                testData);
                        for (IParamValueToken token : conv.getTokens()) {
                            if (!(token instanceof FunctionToken)) {
                                continue;
                            }
                            FunctionToken funct = (FunctionToken) token;
                            if (!StringUtils.equals(funct.getFunctionName(),
                                    "getCentralTestDataSetValue") //$NON-NLS-1$
                                    || funct.getArguments().length < 1) {
                                continue;
                            }
                            try {
                                IParamValueToken[] args = funct.getArguments();
                                StringBuilder build = new StringBuilder();
                                int nextArg = 0;
                                while (nextArg < args.length
                                        && (args[nextArg]
                                            instanceof LiteralToken
                                        || args[nextArg]
                                            instanceof SimpleValueToken)) {
                                    build.append(args[nextArg]
                                            .getExecutionString(null));
                                    nextArg++;
                                }
                                /*
                                 * The CentralTestDataSet is used here. We stop
                                 * searching, because if it is also referenced
                                 * as a whole, it would be added twice.
                                 */
                                if (StringUtils.equals(build.toString(),
                                        pioToSearch.getName())) {
                                    result.add(pn);
                                    break search;
                                }
                            } catch (InvalidDataException e) {
                                // no success...
                            }
                        }
                    }
                }
                /*
                 * End search for function call ?getCentralTestDataSetValue()
                 */
                if (areEqual(pn.getReferencedDataCube(), pioToSearch)) {
                    result.add(pn);
                }
            }
        }
        return result;
    }

    /**
     * @param po1
     *            the first poi
     * @param po2
     *            the second poi
     * @return true if regarded as equal
     */
    private static boolean areEqual(IParameterInterfacePO po1,
            IParameterInterfacePO po2) {
        if (po1 != null && po2 != null) {
            EqualsBuilder eb = new EqualsBuilder();
            eb.append(po1.getId(), po2.getId());
            return eb.isEquals();
        }
        return false;
    }
}