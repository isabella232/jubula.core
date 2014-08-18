/**
 * 
 */
package org.eclipse.jubula.client.alm.mylyn.core.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jubula.client.core.model.IALMReportingRulePO;
import org.eclipse.jubula.client.core.model.TestResultNode;

/**
 * @author BREDEX GmbH
 *
 */
public class FieldUpdate extends ALMChange {
    
    /** map containing all attribute changes which have to be reported */
    private Map<String, Object> m_attributesToChange =
            new HashMap<String, Object>();

    /**
     * Constructor
     * @param resultNode the node
     * @param dashboardURL the dashboardURL
     * @param summaryId the summary id
     * @param nodeNumber the node number
     * @param rules the list of reporting rules
     */
    public FieldUpdate(TestResultNode resultNode, String dashboardURL,
            String summaryId, Long nodeNumber,
            List<IALMReportingRulePO> rules) {
        super(resultNode, dashboardURL, summaryId, nodeNumber);
        
        for (IALMReportingRulePO rule : rules) {
            m_attributesToChange.put(rule.getAttributeID(), rule.getValue());
        }
    }
    
    /**
     * @return the map containing all attribute changes
     */
    public Map<String, Object> getAttributesToChange() {
        return m_attributesToChange;
    }

}
