package org.eclipse.jubula.client.core.testresult.export;

import org.eclipse.jubula.client.core.model.ITestResult;

/**
 * @author Bredex Gmbh
 */
public interface ITestResultExporter {

    /**
     * Initiliazing the Exporter (used to replace Constructor with parameters)
     * 
     * @param result the result of the test which is to be exported
     */
    public void initiliaze(ITestResult result);
    
    
    /**
     * writes the content into a file
     * 
     * @param path the targetpath for the file
     * @param filename the filename
     */
    public void writeTestResult(String path, String filename);
}
