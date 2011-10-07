package org.eclipse.jubula.client.ui.rcp.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.ui.rcp.controllers.propertysources.MonitoringValuePropertySource;

/**
 * 
 * @author marc
 * 
 */
public class MonitoringSourceAdapterFactory implements IAdapterFactory {

    /** types for which adapters are available */
    private final Class[] m_types = { ITestResultSummaryPO.class };

    /**
     * {@inheritDoc}
     */
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        
        if (adaptableObject instanceof ITestResultSummaryPO) {            
            return new MonitoringValuePropertySource(
                    (ITestResultSummaryPO)adaptableObject);
        }  
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Class[] getAdapterList() {

        return m_types;
    }

}
