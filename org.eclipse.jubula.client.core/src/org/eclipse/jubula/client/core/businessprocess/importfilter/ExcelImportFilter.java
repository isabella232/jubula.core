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
package org.eclipse.jubula.client.core.businessprocess.importfilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.eclipse.jubula.client.core.businessprocess.importfilter.exceptions.DataReadException;
import org.eclipse.jubula.client.core.businessprocess.importfilter.exceptions.NoSupportForLocaleException;


/**
 * @author BREDEX GmbH
 * @created Nov 8, 2005
 */
public class ExcelImportFilter implements IDataImportFilter {
    
    /**
     * supported files extension
     */
    private static String[] fileExtensions = {"xls"}; //$NON-NLS-1$

    /**
     * @return a String Array of supported file extensions
     */
    public String[] getFileExtensions() {
        return fileExtensions;
    }
    
    /**
     * parses a file and returns the data as DataTable structure
     * 
     * @param dataDir
     *      directory for data files
     * @param file
     *      data source File
     * @param locale
     *      data source locale
     * @return
     *      filled TestDataManager with new data
     * @throws IOException
     *      error occured while reading data source
     * @throws NoSupportForLocaleException
     *      no support for selected locale
     */
    public DataTable parse(File dataDir, String file, Locale locale) 
        throws IOException, NoSupportForLocaleException, DataReadException {
        
        DataTable filledDataTable;
        final FileInputStream inStream = findDataFile(dataDir, file);
        try {
            POIFSFileSystem fs = new POIFSFileSystem(inStream);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            // first data sheet is the only supported so far
            final int index = wb.getSheetIndex(locale.toString());
            if (index == -1) {
                throw new NoSupportForLocaleException();
            }
            HSSFSheet sheet = wb.getSheetAt(index);
            final int lastRowNum = sheet.getLastRowNum();
            final int firstRowNum = sheet.getFirstRowNum();
            // iterate over rows
            if (sheet.getRow(firstRowNum) == null) {
                return new DataTable(0, 0);
            }
            final int height = lastRowNum - firstRowNum + 1;
            final int width = sheet.getRow(firstRowNum).getLastCellNum() 
                - sheet.getRow(firstRowNum).getFirstCellNum();
            filledDataTable = new DataTable(height, width);
            for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {
                HSSFRow row = sheet.getRow(rowNum);
                final short lastCellNum = row.getLastCellNum();
                final short firstCellNum = row.getFirstCellNum();
                for (int cellNr = firstCellNum; cellNr < lastCellNum; 
                    cellNr++) {
                    
                    HSSFCell cell = row.getCell(cellNr);
                    String cellString = getExcelCellString(cell);
                    filledDataTable.updateDataEntry(rowNum, cellNr, cellString);
                }
            }
        } catch (IOException e) {
            throw e; // just pass on, don't fall through to Throwable
        } catch (Throwable t) {
            throw new DataReadException(t);
        } finally {
            inStream.close();
        }
        
        /* fix issues with documents saved via open office 
         * if the document has been saved via open office it contains one ore many
         * "null" columns at the end of the data table; these columns are truncated 
         */
        while ((filledDataTable.getColumnCount() > 0)
                && (StringUtils.isBlank(filledDataTable.getData(0,
                        filledDataTable.getColumnCount() - 1)))) {
            int newHeight = filledDataTable.getRowCount();
            int newWidth = filledDataTable.getColumnCount() - 1;
            DataTable cleanedFilledDataTable = new DataTable(newHeight,
                    newWidth);
            for (int i = 0; i < newHeight; i++) {
                for (int j = 0; j < newWidth; j++) {
                    cleanedFilledDataTable.updateDataEntry(i, j,
                            filledDataTable.getData(i, j));
                }
            }
            filledDataTable = cleanedFilledDataTable;
        }
        
        return filledDataTable;
    }

    
    
    
    /**
     * Open a data file for reading
     * @param dataDir the data directory
     * @param file the filename
     * @return an opened FIleInputStream for the filename
     * @throws FileNotFoundException guess when!
     */
    private FileInputStream findDataFile(File dataDir, String file) 
        throws FileNotFoundException {
        File dataFile = new File(file);
        File infile;
        if (dataFile.isAbsolute()) {
            infile = dataFile; 
        } else {
            infile = new File(dataDir, file);
        }
        return new FileInputStream(infile);
    }

    /**
     * returns a String for an excel cell
     * @param cell
     *      HSSFCell
     * @return
     *      String
     */
    private String getExcelCellString(HSSFCell cell) {
        if (cell == null) {
            return null;
        }
        int type = cell.getCellType();
        switch (type) {
            case Cell.CELL_TYPE_STRING:
                return cell.getRichStringCellValue().getString();
            case Cell.CELL_TYPE_NUMERIC:
                return getDoubleString(cell.getNumericCellValue());
            case Cell.CELL_TYPE_FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case Cell.CELL_TYPE_STRING:
                        return cell.getRichStringCellValue().getString();
                    case Cell.CELL_TYPE_NUMERIC:
                        return getDoubleString(cell.getNumericCellValue());
                    default:
                        break;
                }
            default :
                break;
        }
        return null;
    }

    /**
     * returns the double as string
     * trims any tailing 0
     * @param value double
     * @return String
     */
    private String getDoubleString(double value) {
        if (Math.round(value) - value == 0) {
            return String.valueOf(Math.round(value));
        }
        return String.valueOf(value);
    }

}
