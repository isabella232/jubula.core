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
package org.eclipse.jubula.client.core.model;

import java.util.List;

/**
 * @author BREDEX GmbH
 * @created 20.12.2005
 */
public interface ITestJobContPO extends IPersistentObject {

    /**
     * @return an unmodifiable list of TestJobs
     */
    public abstract List<ITestJobPO> getTestJobList();

    /**
     * @param ts
     *            TestJob to add
     */
    public abstract void addTestJob(ITestJobPO ts);

    /**
     * @param position
     *            position of TestJob to add in TestJobList
     * @param ts
     *            TestJob to add
     */
    public abstract void addTestJob(int position, ITestJobPO ts);

    /**
     * @param ts
     *            TestJob to remove
     */
    public abstract void removeTestJob(ITestJobPO ts);
}