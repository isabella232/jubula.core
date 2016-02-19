package org.eclipse.jubula.client.core.rules;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * This is a rule which is implementing a semaphore. Only allowing one job to
 * run with the same {@link ISchedulingRule} object
 * 
 * @author BREDEX GmbH
 *
 */
public class SingleJobRule implements ISchedulingRule {
    /**
     * {@inheritDoc}
     */
    public boolean isConflicting(ISchedulingRule rule) {
        return rule == this;
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(ISchedulingRule rule) {
        return rule == this;
    }

}
