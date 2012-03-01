package org.eclipse.jubula.client.analyze.internal.helper;

/**
 * Helperclass to manage wether the 
 * @author volker
 */
public class ProjectContextHelper {

    /** ObjContType **/
    private static String objContType;
    
    /**
     * contsructor
     */
    private ProjectContextHelper() {
        // hidden
    }
    
    /**
     * @return The ObjContType
     */
    public static String getObjContType() {
        return ProjectContextHelper.objContType;
    }
    
    /**
     * @param objCont The given objContType
     */
    public static void setObjContType(String objCont) {
        ProjectContextHelper.objContType = objCont;
    }
    
}
