package org.eclipse.jubula.client.core.persistence;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.tools.objects.IMonitoringValue;

/**
 * Loads monitoring values from the database
 * @author marc
 *
 */
public class MonitoringValuePM {
    
    /** to prevent instantiation */
    private MonitoringValuePM() {
        //DO NOTHING
    }    
    /**
     * 
     * @param summaryID The current selected summaryID
     * @return The monitored values 
     */    
    public static final Map<String, IMonitoringValue> 
    loadMonitoringValues(Object summaryID) {
        
        EntityManager session = Hibernator.instance().openSession();
        ITestResultSummaryPO summary;        
        try {
            EntityTransaction tx = 
                Hibernator.instance().getTransaction(session);
            summary = (ITestResultSummaryPO)session.find(
                        PoMaker.getTestResultSummaryClass(), summaryID);  
        } finally {
            Hibernator.instance().dropSession(session);
        }                
        return summary.getMonitoringValues();
    }    
    
    
}
