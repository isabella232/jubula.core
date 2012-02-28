package org.eclipse.jubula.client.analyze.internal.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author volker
 *
 */
public class PortableNodeInformationHelper {
    
    /**
     * used to save the guid and the matching parentProjectID
     */
    private static Map<String, Long> nodeInformation = 
            new HashMap<String, Long>();
    
    /**
     * Empty constructor because of Util
     */
    private PortableNodeInformationHelper() {
        //empty
    }
    
    
    /**
     * @return The map that includes the NodeGUID an the matching parentProjectID
     */
    public static Map<String, Long> getNodeInformation() {
        return nodeInformation;
    }

}
