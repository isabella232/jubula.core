package org.eclipse.jubula.toolkit.api.gen.internal.genmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all necessary information for toolkit information generation
 * @author BREDEX GmbH
 * @created 17.10.2014
 */
public class ToolkitGenInfo {

    /** list with component infos */
    private List<CompInfoForToolkitGen> m_compInfoList =
            new ArrayList<CompInfoForToolkitGen>();
    
    /**
     * @return list with component infos
     */
    public List<CompInfoForToolkitGen> getCompInformation() {
        return m_compInfoList;
    }
    
    /**
     * adds a component information to the list
     * @param compInfo the comp info
     */
    public void addCompInformation (CompInfoForToolkitGen compInfo) {
        m_compInfoList.add(compInfo);
    }
}
