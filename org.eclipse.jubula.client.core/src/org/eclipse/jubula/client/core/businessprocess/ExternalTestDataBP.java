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
package org.eclipse.jubula.client.core.businessprocess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.client.core.businessprocess.importfilter.DataTable;
import org.eclipse.jubula.client.core.businessprocess.importfilter.ExcelImportFilter;
import org.eclipse.jubula.client.core.businessprocess.importfilter.IDataImportFilter;
import org.eclipse.jubula.client.core.businessprocess.importfilter.exceptions.DataReadException;
import org.eclipse.jubula.client.core.businessprocess.importfilter.exceptions.NoSupportForLocaleException;
import org.eclipse.jubula.client.core.model.II18NStringPO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.ITDManagerPO;
import org.eclipse.jubula.client.core.model.ITestDataPO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.tools.exception.GDException;
import org.eclipse.jubula.tools.exception.IncompleteDataException;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;


/**
 * Business class for handling with external data sources (e.g. Excel files)
 *
 * @author BREDEX GmbH
 * @created Nov 3, 2005
 */
public class ExternalTestDataBP {

    /** the logger */
    private static final Log LOG = LogFactory.getLog(ExternalTestDataBP.class);

    /** this is where the datafile are stored */
    private static File globalDataDir = new File("."); //$NON-NLS-1$

    /** import fiter*/
    private List <IDataImportFilter> m_filter;
     
    /** 
     * Cache for the read in data file
     * Key = file name
     * Value = DataTable 
     */
    private Map<File, DataTable> m_dataTableCache = 
        new HashMap<File, DataTable>();
    
    /**
     * Cache for ITDmanagerPOs
     * Key = IParamNodePO
     * Value = ITDManagerPO
     */
    private Map<IParamNodePO, ITDManagerPO> m_tdManagerCache = 
        new HashMap<IParamNodePO, ITDManagerPO>();

    /**
     * Constructor
     *
     */
    public ExternalTestDataBP() {
        m_filter = new ArrayList <IDataImportFilter> ();
        m_filter.add(new ExcelImportFilter());
    }

    /**
     * Creates a new ITDManagerPO for the given IParamNodePO filled with 
     * the data of the given file or gets the ITDManagerPO from the cache.
     * @param dataDir
     *      directory for data files
     * @param file data source File
     * @param node ParamNode
     * @throws GDException error occured while reading data source
     * @return filled TestDataManager
     */
    private ITDManagerPO createFilledTDManager(File dataDir, String file, 
        IParamNodePO node) 
        throws GDException {
        
        ITDManagerPO tdManager = m_tdManagerCache.get(node);
        if (tdManager != null) {
            return tdManager;
        }
        
        tdManager = PoMaker.createTDManagerPO(node);
        Locale locale = TestExecution.getInstance().getLocale();
        // clear TDManager first
        node.clearTestData();
        // fill it again
        DataTable dataTable = createDataTable(dataDir, file, locale);
        tdManager = parseTable(dataTable, node, locale);
        m_tdManagerCache.put(node, tdManager);
        return tdManager;
    }

    /**
     * Creates and returns a Data Manager populated with test data corresponding
     * to the given arguments.
     * 
     * @param referencedDataCube
     *          The cube from which test data will be retrieved.
     * @param node
     *          The node containing the parameters that will use the generated
     *          data.
     * @return the created Test Data Manager.
     */
    private ITDManagerPO createFilledTDManager(
            IParameterInterfacePO referencedDataCube, IParamNodePO node) {
            
        ITDManagerPO tdManager = m_tdManagerCache.get(node);
        if (tdManager != null) {
            return tdManager;
        }

        tdManager = referencedDataCube.getDataManager();
        
        m_tdManagerCache.put(node, tdManager);
        return tdManager;
    }

    /**
     * Creates a DataTable of the given file name with the given Locale or 
     * gets it from the cache if available.
     * @param dataDir
     *      directory for data files
     * @param fileName the name of the data source
     * @param locale the local of the  data
     * @return a DataTable
     * @throws GDException id data source is not supported
     */
    public DataTable createDataTable(File dataDir, String fileName, 
        Locale locale) throws GDException {
        
        File dataFile = new File(dataDir, fileName);
        DataTable dataTable = m_dataTableCache.get(dataFile);
        if (dataTable != null) {
            return dataTable;
        }
        String dataFileName = String.valueOf(dataFile);
        try {
            IDataImportFilter filter = getFilterFromFileType(fileName);
            if (filter != null) {
                dataTable = filter.parse(dataDir, fileName, locale);
                m_dataTableCache.put(dataFile, dataTable);
                return dataTable;
            } 
            LOG.error("DataSource: '" + dataFileName + "' not supported");  //$NON-NLS-1$//$NON-NLS-2$
            throw new GDException(I18n.getString(
                    "ErrorMessage.NOT_SUPP_DATASOURCE", //$NON-NLS-1$
                    new Object[] { dataFileName }),
                MessageIDs.E_NOT_SUPP_DATASOURCE);
        } catch (IOException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Error reading file: " + dataFileName, e); //$NON-NLS-1$                
            }
            throw new GDException(I18n.getString("ErrorMessage.DATASOURCE_FILE_IO",  //$NON-NLS-1$
                new Object[] {dataFileName}), 
                MessageIDs.E_DATASOURCE_FILE_IO);
        } catch (DataReadException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Error reading file: " + dataFileName, e); //$NON-NLS-1$                
            }
            throw new GDException(I18n.getString("ErrorMessage.DATASOURCE_READ_ERROR",  //$NON-NLS-1$
                new Object[] {dataFileName}), 
                MessageIDs.E_DATASOURCE_READ_ERROR);
        } catch (NoSupportForLocaleException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Locale not supported: " + String.valueOf(locale) //$NON-NLS-1$
                    + " in data source: " + dataFileName); //$NON-NLS-1$                
            }
            throw new GDException(I18n.getString("ErrorMessage.DATASOURCE_LOCALE_NOTSUPPORTED",  //$NON-NLS-1$
                new Object[] {locale.toString(), locale.getDisplayLanguage()}), 
                MessageIDs.E_DATASOURCE_LOCALE_NOTSUPPORTED);
        }
    }

    /**
     * @param file file we will use as data source or null if file type is not
     * supported.
     * @return the filter type
     */
    private IDataImportFilter getFilterFromFileType(String file) {
        for (IDataImportFilter filter : m_filter) {
            String[] extensions = filter.getFileExtensions();
            for (String extension : extensions) {
                if (extension != null && extension.length() > 0) {
                    if (file.endsWith(extension)) { // NOPMD by al on 3/19/07 1:22 PM
                        return filter;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * update a given TestDataManager with data
     * 
     * @param filledTable
     *      data extracted from File
     * @param paramPo
     *      Parameter po we would like to update
     * @param locale
     *      What Locale the TestData should be
     * @return
     *      filled TestDataManager with new data
     * @throws GDException
     *      error occured while reading data source
     */
    private ITDManagerPO parseTable(DataTable filledTable,
        IParameterInterfacePO paramPo, Locale locale) throws GDException {
        return parseTable(filledTable, paramPo, locale, false);
    }
    
    /**
     * update a given TestDataManager with data
     * 
     * @param filledTable
     *      data extracted from File
     * @param paramPo
     *      Parameter po we would like to update
     * @param locale
     *      What Locale the TestData should be
     * @param updateCellValues
     *      whether the
     * @return
     *      filled TestDataManager with new data
     * @throws GDException
     *      error occured while reading data source
     */
    public ITDManagerPO parseTable(DataTable filledTable,
        IParameterInterfacePO paramPo, Locale locale,
        boolean updateCellValues) throws GDException {
        
        // iterate over rows
        List<String> paramNamesExcel = 
            new ArrayList<String>();
        List <IParamDescriptionPO> paramNamesNode = 
            paramPo.getParameterList();
        final int rowCount = filledTable.getRowCount();
        for (int row = 0; row < rowCount; row++) {
            final int columnCount = filledTable.getColumnCount();
            for (int cellNr = 0; cellNr < columnCount; cellNr++) {
                final String cellString = getTestDataForTDManager(filledTable, 
                        row, cellNr);
                if (row == 0) {
                    paramNamesExcel.add(cellString);
                } else {
                    IParamDescriptionPO desc = paramPo
                            .getParameterForName(paramNamesExcel.get(cellNr));
                    if (desc != null) {
                        int dataSetNo = row - 1;
                        II18NStringPO i18nString;
                        ITestDataPO testData;
                        if (updateCellValues) {
                            testData = paramPo.getDataManager().getCell(
                                    dataSetNo, desc);
                            i18nString = testData.getValue();
                            i18nString.setValue(locale, cellString,
                                    GeneralStorage.getInstance().getProject());
                        } else {
                            testData = TestDataBP.instance()
                                    .createEmptyTestData();
                            i18nString = PoMaker.createI18NStringPO(locale,
                                    cellString, GeneralStorage.getInstance()
                                            .getProject());
                        }
                        testData.setValue(i18nString);
                        paramPo.getDataManager().updateCell(testData,
                                dataSetNo, desc.getUniqueId());
                    }
                }
            }
        }
        for (IParamDescriptionPO desc : paramNamesNode) {
            if (!paramNamesExcel.contains(desc.getName())) {
                
                throw new GDException(I18n.getString(
                        "ErrorMessage.DATASOURCE_MISSING_PARAMETER", //$NON-NLS-1$
                        new Object[] { desc.getName(),
                                paramPo.getName(),
                                paramPo.getDataFile()}),
                        MessageIDs.E_DATASOURCE_MISSING_PARAMETER);
            }
        }
        if (rowCount == 1) {
            throw new GDException(I18n.getString("ErrorMessage.DATASOURCE_MISSING_VALUES"),  //$NON-NLS-1$
                MessageIDs.E_DATASOURCE_MISSING_VALUES);
        }
        return paramPo.getDataManager();
    }

    /**
     * Gets the value for the TestDataManager from the given DataTable.
     * Any internal GUIdancer-Symbols (References, Variables, etc.) will be
     * converted into the right format.
     * @param filledTable The DataTable of the Excel-file
     * @param row the row number
     * @param column the column number
     * @return the value for the TDManager
     * @throws IncompleteDataException if data are incomplete
     */
    private String getTestDataForTDManager(DataTable filledTable, 
            int row, int column) throws IncompleteDataException {
        
        String cellString = filledTable.getData(row, column);
        if (cellString == null || cellString.length() == 0) {
            
            MessageIDs.getMessageObject(
                    MessageIDs.E_DATASOURCE_CONTAIN_EMPTY_DATA).setDetails(
                            new String[] {});
            throw new IncompleteDataException(MessageIDs.getMessage(
                    MessageIDs.E_DATASOURCE_CONTAIN_EMPTY_DATA) + "\n"  //$NON-NLS-1$
                        + I18n.getString("ErrorDetail.DATASOURCE_CONTAIN_EMPTY_DATA",  //$NON-NLS-1$
                                new Object[] {row + 1, column + 1}), 
                MessageIDs.E_DATASOURCE_CONTAIN_EMPTY_DATA);
        }
        return cellString;
    }
    
    /**
     * Gets the ITDManagerPO for the given IParamNodePO which has a file as
     * data source.<br>
     * If the given IParamNodePO has regular data,its ITDManagerPO 
     * will be returned.
     * @param paramNode ParamNode
     * @return the usable TDManager
     * @throws GDException
     *      occuring Exception while creating TDManager
     */
    public ITDManagerPO getExternalCheckedTDManager(IParamNodePO paramNode)
        throws GDException {
        
        boolean isTestRunning = 
            TestExecution.getInstance().getStartedTestSuite() != null
            && TestExecution.getInstance().getStartedTestSuite().isStarted()
            && TestExecution.getInstance().getConnectedAut() != null;
        
        return getExternalCheckedTDManager(paramNode, 
                TestExecution.getInstance().getLocale(), isTestRunning);
    }
    
    /**
     * Retrieves or generates a Test Data Manager for the given arguments. If 
     * the provided <code>paramNode</code> manages its own data, then its 
     * Test Data Manager is retrieved and returned. Otherwise, a Test Data 
     * Manager corresponding to:
     * <ul>
     *   <li>the external data referenced by <code>paramNode</code></li>
     *   <li>the provided locale</li>
     * </ul>
     * is generated and returned.
     * 
     * @param paramNode
     *              The node for which to retrieve / generate a Data Manager.
     *              Must not be <code>null</code>.
     * @param locale
     *              The locale for which to retrieve the external test data 
     *              if external test data is required.
     *              Must not be <code>null</code>.
     * @param retrieveExternalData 
     *              Flag for forcing retrieval of external test data. If the 
     *              provided node requires external data and this flag is set to 
     *              <code>false</code>, then an empty Data Manager will be 
     *              returned.
     * 
     * @return the retrieved or generated Test Data Manager.
     * @throws GDException 
     *              if an error occurs while reading an external data source.
     */
    public ITDManagerPO getExternalCheckedTDManager(
            IParamNodePO paramNode, Locale locale, 
            boolean retrieveExternalData) throws GDException {
        
        Validate.notNull(paramNode);
        Validate.notNull(locale);
        
        boolean usesExternalDataFile = 
            StringUtils.isNotEmpty(paramNode.getDataFile());
        boolean usesReferencedDataCube = 
            paramNode.getReferencedDataCube() != null;
                
        if (!usesExternalDataFile && !usesReferencedDataCube) {
            return paramNode.getDataManager();
        }

        if (!retrieveExternalData) {
            ITDManagerPO tdManager = PoMaker.createTDManagerPO(paramNode);
            return tdManager;
        }

        if (usesExternalDataFile) {
            return createFilledTDManager(globalDataDir, paramNode.getDataFile(),
                    paramNode);
        } else if (usesReferencedDataCube) {
            return createFilledTDManager(
                    paramNode.getReferencedDataCube(), paramNode);
        }

        LOG.error("Unknown data source type for: " + paramNode.getName()); //$NON-NLS-1$
        return null;
    }
    
    /**
     * Clears this ExternalTestDataBP (e.g. the caches) after TestExecution 
     * has finished.
     */
    public void clearExternalData() {
        m_dataTableCache.clear();
        m_tdManagerCache.clear();
    }
    
    /**
     * @return the dataDir
     */
    public static File getDataDir() {
        return globalDataDir;
    }

    /**
     * @param dataDir the dataDir to set
     */
    public static void setDataDir(File dataDir) {
        globalDataDir = dataDir;
    }

}
