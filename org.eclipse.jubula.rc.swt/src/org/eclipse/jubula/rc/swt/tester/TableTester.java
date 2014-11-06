/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.swt.tester;

import java.awt.Point;
import java.awt.Rectangle;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.tester.AbstractTableTester;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITableComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITextInputComponent;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.rc.swt.driver.DragAndDropHelperSwt;
import org.eclipse.jubula.rc.swt.tester.adapter.StyledTextAdapter;
import org.eclipse.jubula.rc.swt.tester.adapter.TableAdapter;
import org.eclipse.jubula.rc.swt.tester.adapter.TextComponentAdapter;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.toolkit.enums.ValueSets.InteractionMode;
import org.eclipse.jubula.tools.internal.constants.SwtToolkitConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
/**
 * Toolkit specific commands for the <code>Table</code>
 *
 * @author BREDEX GmbH
 */
public class TableTester extends AbstractTableTester {
    /**
     *  Gets the real table component
     * @return the table
     */
    private Table getTable() {
        return (Table)getComponent().getRealComponent();
    }
    
    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        final String[] componentTextArray;
        Item[] itemArray = getTable().getColumns();
        componentTextArray = getTextArrayFromItemArray(itemArray);         
        return componentTextArray;
    }
    
    /**
     * {@inheritDoc}
     */
    protected Object setEditorToReplaceMode(Object editor, boolean replace) {

        if (replace) {
            ITextInputComponent textEditor = null;
            if (editor instanceof Text) {
                textEditor = new TextComponentAdapter(editor);
            }
            if (editor instanceof StyledText) {
                textEditor = new StyledTextAdapter(editor);
            }
            if (EnvironmentUtils.isMacOS()) {
                getRobot().clickAtCurrentPosition(editor, 3, 
                        InteractionMode.primary.rcIntValue());
            } else {
                getRobot().keyStroke(getRobot().getSystemModifierSpec() + " A"); //$NON-NLS-1$
            }
            if (textEditor != null) {
                if (!textEditor.getSelectionText()
                        .equals(textEditor.getText())) {
                    // if the whole text is not selected, select programmatic
                    textEditor.selectAll();
                }
            }
        } else {
            getRobot().clickAtCurrentPosition(editor, 2, 
                    InteractionMode.primary.rcIntValue());
        }
        return editor;
    }

    /**
     * {@inheritDoc}
     */
    protected Object activateEditor(Cell cell, Rectangle rectangle) {
        TableAdapter table = (TableAdapter) getComponent();
        return table.activateEditor(cell);
    }

    /**
     * {@inheritDoc}
     */
    protected int getExtendSelectionModifier() {
        return SWT.MOD1;
    }

    /**
     * {@inheritDoc}
     */
    protected Cell getCellAtMousePosition() throws StepExecutionException {
        
        final Table table = getTable();
        final Point awtMousePos = getRobot().getCurrentMousePosition();
        Cell returnvalue = (Cell) getEventThreadQueuer().invokeAndWait(
                "getCellAtMousePosition",  //$NON-NLS-1$
                new IRunnable() {
                    public Object run() throws StepExecutionException {
                        Cell cell = null;
                        final int itemCount = table.getItemCount();
                        for (int rowCount = table.getTopIndex(); 
                                rowCount < itemCount; rowCount++) {
                            if (cell != null) {
                                break;
                            }
                            final int columnCount = table.getColumnCount();
                            if (columnCount > 0) {
                                for (int col = 0; col < columnCount; col++) {
                                    checkRowColBounds(rowCount, col);
                                    final Rectangle itemBounds = getCellBounds(
                                            getEventThreadQueuer(), getTable(),
                                            rowCount, col);
                                    final org.eclipse.swt.graphics.Point 
                                        absItemBounds = table
                                            .toDisplay(itemBounds.x,
                                                    itemBounds.y);
                                    final Rectangle absRect = new Rectangle(
                                            absItemBounds.x, absItemBounds.y,
                                            itemBounds.width,
                                            itemBounds.height);
                                    if (absRect.contains(awtMousePos)) {
                                        cell = new Cell(rowCount, col);
                                        break;
                                    }
                                }
                            } else {
                                checkRowColBounds(rowCount, 0);
                                final Rectangle itemBounds = getCellBounds(
                                        getEventThreadQueuer(), getTable(),
                                        rowCount, 0);
                                final org.eclipse.swt.graphics.Point 
                                    absItemBounds = table
                                        .toDisplay(itemBounds.x, itemBounds.y);
                                final Rectangle absRect = new Rectangle(
                                        absItemBounds.x, absItemBounds.y,
                                        itemBounds.width, itemBounds.height);
                                if (absRect.contains(awtMousePos)) {
                                    cell = new Cell(rowCount, 0);
                                }
                            }
                        }
                        if (cell == null) {
                            throw new StepExecutionException(
                                    "No cell under mouse position found!", //$NON-NLS-1$
                                    EventFactory
                                            .createActionError(
                                                    TestErrorEvent.NOT_FOUND));
                        }
                        return cell;
                    }
                });
        return returnvalue;
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isMouseOnHeader() {
        final Table table = getTable();
        final ITableComponent adapter = (ITableComponent)getComponent();
        Boolean isVisible;
        isVisible = (Boolean)getEventThreadQueuer().invokeAndWait(
                "isMouseOnHeader", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        return new Boolean(table.getHeaderVisible());
                    }
                });
        
        if (!(isVisible.booleanValue())) {
            return false;
        }
        
        Boolean isOnHeader = new Boolean(false);
        isOnHeader = (Boolean)getEventThreadQueuer().invokeAndWait(
                "isMouseOnHeader", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        final Point awtMousePos = getRobot()
                            .getCurrentMousePosition();
                        org.eclipse.swt.graphics.Point mousePos =
                            new org.eclipse.swt.graphics.Point(
                                awtMousePos.x, awtMousePos.y);

                        for (int j = 0; j < table.getColumnCount(); j++) {
                            final Rectangle constraints = 
                                    adapter.getHeaderBounds(j);
                            
                            org.eclipse.swt.graphics.Rectangle bounds = 
                                    SwtUtils.getWidgetBounds(
                                    table);
                            
                            if (constraints != null) {
                                // Use SWT's mapping function, if possible, as it is more
                                // multi-platform than simply adding the x and y values.
                                org.eclipse.swt.graphics.Point
                                convertedLocation = getConvertedLocation(
                                        constraints);
                                bounds.x = convertedLocation.x;
                                bounds.y = convertedLocation.y;
                                
                                bounds.height = constraints.height;
                                bounds.width = constraints.width;
                            }

                            if (bounds.contains(mousePos)) {
                                return new Boolean(true);
                            }
                        }      
                        return new Boolean(false);
                    }
                });                  
        
        return isOnHeader.booleanValue();
    }
    
    /**
     * Returns an array of representation strings that corresponds to the given
     * array of items or null if the given array is null;
     * @param itemArray the item array whose item texts have to be read
     * @return array of item texts corresponding to the given item array
     */
    protected final String[] getTextArrayFromItemArray(Item[] itemArray) {
        final String[] itemTextArray;
        if (itemArray == null) {
            itemTextArray = null;
        } else {
            itemTextArray = new String[itemArray.length];
            for (int i = 0; i < itemArray.length; i++) {
                Item item = itemArray[i];
                if (item == null) {
                    itemTextArray[i] = null;
                } else {
                    String fallback = SwtUtils.removeMnemonics(item.getText());
                    itemTextArray[i] = CAPUtil.getWidgetText(item, fallback);
                }
            }
        }
        
        return itemTextArray;
    }
        
    /**
     * @param constraints Rectangle
     * @return converted Location of table
     */
    private org.eclipse.swt.graphics.Point getConvertedLocation(
            final Rectangle constraints) {
        org.eclipse.swt.graphics.Point convertedLocation =
            (org.eclipse.swt.graphics.Point)getEventThreadQueuer()
                .invokeAndWait("toDisplay", new IRunnable() { //$NON-NLS-1$
                    public Object run() throws StepExecutionException {
                        return getTable().toDisplay(
                                constraints.x, constraints.y);
                    }
                });
        return convertedLocation;
    }
        
    /**
     * @param etq
     *            the EventThreadQueuer to use
     * @param table
     *            the table to use
     * @param row
     *            The row of the cell
     * @param col
     *            The column of the cell
     * @return The bounding rectangle for the cell, relative to the table's
     *         location.
     */
    public static Rectangle getCellBounds(IEventThreadQueuer etq,
        final Table table, final int row, final int col) {
        Rectangle cellBounds = (Rectangle)etq.invokeAndWait(
                "getCellBounds", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        TableItem ti = table.getItem(row); 
                        int column = (table.getColumnCount() > 0 || col > 0) 
                            ? col : 0;
                        org.eclipse.swt.graphics.Rectangle r = 
                                ti.getBounds(column);
                        String text = CAPUtil.getWidgetText(ti,
                                SwtToolkitConstants.WIDGET_TEXT_KEY_PREFIX
                                        + column, ti.getText(column));
                        Image image = ti.getImage(column);
                        if (text != null && text.length() != 0) {
                            GC gc = new GC(table);
                            int charWidth = 0; 
                            try {
                                FontMetrics fm = gc.getFontMetrics();
                                charWidth = fm.getAverageCharWidth();
                            } finally {
                                gc.dispose();
                            }
                            r.width = text.length() * charWidth;
                            if (image != null) {
                                r.width += image.getBounds().width;
                            }
                        } else if (image != null) {
                            r.width = image.getBounds().width;
                        }
                        if (column > 0) {
                            TableColumn tc = table.getColumn(column);
                            int alignment = tc.getAlignment();
                            if (alignment == SWT.CENTER) {
                                r.x += ((double)tc.getWidth() / 2) 
                                        - ((double)r.width / 2);
                            }
                            if (alignment == SWT.RIGHT) {
                                r.x += tc.getWidth() - r.width;
                            }
                        }
                        
                        return new Rectangle(r.x, r.y, r.width, r.height);
                    }
                });
        return cellBounds;
    }
    /**
     * {@inheritDoc}
     */
    protected Object getSpecificRectangle(Rectangle rectangle) {
        return new org.eclipse.swt.graphics.Rectangle(rectangle.x, rectangle.y,
                rectangle.width, rectangle.height);
    }
    
    /**
     * {@inheritDoc}
     */
    public void rcClickDirect(int count, int button, int xPos, String xUnits, 
        int yPos, String yUnits) throws StepExecutionException {
        
        int correctedYPos = correctYPos(yPos, yUnits);
        super.rcClickDirect(count, button, xPos, xUnits, correctedYPos, yUnits);
    }
    
    /**
     * Corrects the given Y position based on the height of the table's header.
     * This ensures, for example, that test steps don't try to click within the
     * table header (where we receive no confirmation events).
     * 
     * @param pos The Y position to correct.
     * @param units The units used for the Y position.
     * @return The corrected Y position.
     */
    private int correctYPos(int pos, String units) {
        int correctedPos = pos;
        int headerHeight = ((Integer)getEventThreadQueuer().invokeAndWait(
                "getHeaderHeight", new IRunnable() { //$NON-NLS-1$

                    public Object run() throws StepExecutionException {
                        return new Integer(
                            ((Table)getComponent().getRealComponent())
                            .getHeaderHeight());
                    }
            
                })).intValue();

        if (ValueSets.Unit.pixel.rcValue().equalsIgnoreCase(units)) {
            // Pixel units
            correctedPos += headerHeight;
        } else {
            // Percentage units
            int totalHeight = ((Integer)getEventThreadQueuer().invokeAndWait(
                    "getWidgetBounds", new IRunnable() { //$NON-NLS-1$

                        public Object run() throws StepExecutionException {
                            return new Integer(
                                SwtUtils.getWidgetBounds(
                                    (Widget) getComponent().
                                        getRealComponent()).height);
                        }
            
                    })).intValue();
            long targetHeight = totalHeight - headerHeight;
            long targetPos = Math.round((double)targetHeight * (double)pos
                / 100.0);
            targetPos += headerHeight;
            double heightPercentage = 
                (double)targetPos / (double)totalHeight * 100.0;
            correctedPos = (int)Math.round(heightPercentage);
            if (correctedPos > 100) { // rounding error
                correctedPos = 100;
            }
        }
        return correctedPos;
    }
    
    /**
     * Drags the cell of the Table.<br>
     * With the xPos, yPos, xunits and yUnits the click position inside the 
     * cell can be defined.
     * 
     * @param mouseButton the mouseButton.
     * @param modifier the modifier.
     * @param row The row of the cell.
     * @param rowOperator the row header operator
     * @param col The column of the cell.
     * @param colOperator the column header operator
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @throws StepExecutionException
     *             If the row or the column is invalid
     */
    public void rcDragCell(final int mouseButton, final String modifier, 
            final String row, String rowOperator, final String col,
            final String colOperator, final int xPos, final String xUnits,
            final int yPos, final String yUnits) 
        throws StepExecutionException {
        
        final DragAndDropHelperSwt dndHelper = DragAndDropHelperSwt
            .getInstance();
        dndHelper.setMouseButton(mouseButton);
        dndHelper.setModifier(modifier);
        dndHelper.setDragComponent(null);
        
        rcSelectCell(row, rowOperator, col, colOperator, 0, xPos,
                xUnits, yPos, yUnits, 
                ValueSets.BinaryChoice.no.rcValue(), 1);
    }
    
    /**
     * Drops on the cell of the JTable.<br>
     * With the xPos, yPos, xunits and yUnits the click position inside the 
     * cell can be defined.
     * 
     * @param row The row of the cell.
     * @param rowOperator The row operator
     * @param col The column of the cell.
     * @param colOperator The column operator
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @param delayBeforeDrop the amount of time (in milliseconds) to wait
     *                        between moving the mouse to the drop point and
     *                        releasing the mouse button                       
     * @throws StepExecutionException
     *             If the row or the column is invalid
     */
    public void rcDropCell(final String row, final String rowOperator,
            final String col, final String colOperator, final int xPos, 
            final String xUnits, final int yPos, final String yUnits,
            int delayBeforeDrop) throws StepExecutionException {
        
        final DragAndDropHelperSwt dndHelper = DragAndDropHelperSwt
            .getInstance();
        final IRobot robot = getRobot();
        
        pressOrReleaseModifiers(dndHelper.getModifier(), true);
        try {
            getEventThreadQueuer().invokeAndWait("rcDropCell", new IRunnable() { //$NON-NLS-1$

                public Object run() throws StepExecutionException {
                    // drag
                    robot.mousePress(dndHelper.getDragComponent(), null, 
                            dndHelper.getMouseButton());

                    CAPUtil.shakeMouse();

                    // drop
                    rcSelectCell(row, rowOperator, col, colOperator, 0, xPos,
                            xUnits, yPos, yUnits, 
                            ValueSets.BinaryChoice.no.rcValue(), 1);
                    return null;
                }            
            });

            waitBeforeDrop(delayBeforeDrop);
            
        } finally {
            robot.mouseRelease(dndHelper.getDragComponent(), null, 
                    dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }
    }
    
    /**
     * Finds the first row which contains the value <code>value</code>
     * in column <code>col</code> and drags this row.
     * 
     * @param mouseButton the mouse button
     * @param modifier the modifier
     * @param col the column
     * @param colOperator the column header operator
     * @param value the value
     * @param regexOp the regex operator
     * @param searchType Determines where the search begins ("relative" or "absolute")
     */
    public void rcDragRowByValue(int mouseButton, String modifier, String col,
            String colOperator, final String value, final String regexOp,
            final String searchType) {
        
        final DragAndDropHelperSwt dndHelper = DragAndDropHelperSwt
            .getInstance();
        dndHelper.setMouseButton(mouseButton);
        dndHelper.setModifier(modifier);
        dndHelper.setDragComponent(null);
        
        rcSelectRowByValue(col, colOperator, value, regexOp, 1, 
                ValueSets.BinaryChoice.no.rcValue(),
                searchType, 1);
    }
    
    /**
     * Finds the first row which contains the value <code>value</code>
     * in column <code>col</code> and drops on this row.
     * 
     * @param col the column
     * @param colOperator the column operator
     * @param value the value
     * @param regexOp the regex operator
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @param delayBeforeDrop the amount of time (in milliseconds) to wait
     *                        between moving the mouse to the drop point and
     *                        releasing the mouse button                       
     */
    public void rcDropRowByValue(final String col, final String colOperator,
            final String value, final String regexOp, final String searchType, 
            int delayBeforeDrop) {
        
        final DragAndDropHelperSwt dndHelper = DragAndDropHelperSwt
            .getInstance();
        final IRobot robot = getRobot();
        pressOrReleaseModifiers(dndHelper.getModifier(), true);
        
        try {
            getEventThreadQueuer().invokeAndWait("rcDropRowByValue", new IRunnable() { //$NON-NLS-1$

                public Object run() throws StepExecutionException {
                    // drag
                    robot.mousePress(dndHelper.getDragComponent(), null, 
                            dndHelper.getMouseButton());

                    CAPUtil.shakeMouse();

                    // drop
                    selectRowByValue(col, colOperator, value, regexOp,
                            ValueSets.BinaryChoice.no.rcValue(), 
                            searchType, 
                            ClickOptions.create().setClickCount(0));
                    return null;
                }            
            });
            
            waitBeforeDrop(delayBeforeDrop);
            
        } finally {
            robot.mouseRelease(dndHelper.getDragComponent(), null, 
                    dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }
    }
    
    /**
     * Finds the first column which contains the value <code>value</code>
     * in the given row and drags the cell.
     * 
     * @param mouseButton the mouse button
     * @param modifier the modifiers
     * @param row the row
     * @param rowOperator the row header operator
     * @param value the value
     * @param regex search using regex
     * @param searchType Determines where the search begins ("relative" or "absolute")
     */
    public void rcDragCellByColValue(int mouseButton, String modifier,
            String row, String rowOperator, final String value,
            final String regex, final String searchType) {
        
        final DragAndDropHelperSwt dndHelper = DragAndDropHelperSwt
            .getInstance();
        dndHelper.setMouseButton(mouseButton);
        dndHelper.setModifier(modifier);
        dndHelper.setDragComponent(null);
        selectCellByColValue(row, rowOperator, value, regex, 
                ValueSets.BinaryChoice.no.rcValue(),
                searchType, ClickOptions.create().setClickCount(0));
    }
    
    /**
     * Finds the first column which contains the value <code>value</code>
     * in the given row and drops on the cell.
     * 
     * @param row the row
     * @param rowOperator the row header operator
     * @param value the value
     * @param regex search using regex
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @param delayBeforeDrop the amount of time (in milliseconds) to wait
     *                        between moving the mouse to the drop point and
     *                        releasing the mouse button                       
     */
    public void rcDropCellByColValue(final String row, final String rowOperator,
            final String value, final String regex, final String searchType,
            int delayBeforeDrop) {
        
        final DragAndDropHelperSwt dndHelper = DragAndDropHelperSwt
            .getInstance();
        final IRobot robot = getRobot();
        pressOrReleaseModifiers(dndHelper.getModifier(), true);

        try {
            getEventThreadQueuer().invokeAndWait("rcDropCellByColValue", new IRunnable() { //$NON-NLS-1$

                public Object run() throws StepExecutionException {
                    // drag
                    robot.mousePress(dndHelper.getDragComponent(), null, 
                            dndHelper.getMouseButton());

                    CAPUtil.shakeMouse();

                    // drop
                    selectCellByColValue(row, rowOperator, value, regex,
                            ValueSets.BinaryChoice.no.rcValue(), 
                            searchType, 
                            ClickOptions.create().setClickCount(0));
                    return null;
                }            
            });

            waitBeforeDrop(delayBeforeDrop);
        
        } finally {
            robot.mouseRelease(dndHelper.getDragComponent(), null, 
                    dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }
    }
    
    /**
     * Verifies whether the checkbox in the row of the selected cell 
     * is checked
     * 
     * @param checked true if checkbox in cell should be selected, false otherwise
     * @throws StepExecutionException If no cell is selected or the verification fails.
     */
    public void rcVerifyCheckboxInSelectedRow(boolean checked)
        throws StepExecutionException {
        int row = ((ITableComponent) getComponent()).getSelectedCell().getRow();
        verifyCheckboxInRow(checked, row);
    }
    
    /**
     * Verifies whether the checkbox in the row under the mouse pointer is checked
     * 
     * @param checked true if checkbox in cell is selected, false otherwise
     */
    public void rcVerifyCheckboxInRowAtMousePosition(boolean checked) {
        int row = getCellAtMousePosition().getRow();
        verifyCheckboxInRow(checked, row);
    }
    
    /**
     * Verifies whether the checkbox in the row with the given
     * <code>index</code> is checked
     * 
     * @param checked true if checkbox in cell is selected, false otherwise
     * @param row the row-index of the cell in which the checkbox-state should be verified
     */
    private void verifyCheckboxInRow(boolean checked, final int row) {
        Boolean checkIndex = ((Boolean)getEventThreadQueuer().invokeAndWait(
                "rcVerifyTableCheckboxIndex", new IRunnable() { //$NON-NLS-1$
                    public Object run() throws StepExecutionException {
                        Table table = getTable();
                        if ((table.getStyle() & SWT.CHECK) == 0) {
                            throw new StepExecutionException(
                                    "No checkbox found", //$NON-NLS-1$
                                    EventFactory.createActionError(
                                            TestErrorEvent.CHECKBOX_NOT_FOUND));
                        }
                        return new Boolean(table.getItem(row).
                                getChecked());
                    }
                }));
        Verifier.equals(checked, checkIndex.booleanValue());
    }

    /**
     * Toggles the checkbox in the row under the Mouse Pointer 
     */
    public void rcToggleCheckboxInRowAtMousePosition() {
        toggleCheckboxInRow(getCellAtMousePosition().getRow());
    }
    
    /**
     * Toggles the checkbox in the selected row
     */
    public void rcToggleCheckboxInSelectedRow() {
        int row = ((Integer) getEventThreadQueuer().invokeAndWait(
                "get Selection index", new IRunnable() { //$NON-NLS-1$
                    public Object run() throws StepExecutionException {
                        return new Integer(getTable().getSelectionIndex());
                    }
                })).intValue();
        toggleCheckboxInRow(row);
    }

    /**
     * Toggles the checkbox in the row with the given index
     * @param row the index
     */
    private void toggleCheckboxInRow(final int row) {

        if (row == -1) {
            getEventThreadQueuer().invokeAndWait(
                "No Selection", new IRunnable() { //$NON-NLS-1$
                    public Object run() throws StepExecutionException {
                        throw new StepExecutionException(
                            "No Selection found ", //$NON-NLS-1$
                            EventFactory.createActionError(
                                TestErrorEvent.NO_SELECTION));
                    }
                });
        }
        
        ((ITableComponent) getComponent()).scrollCellToVisible(row, 0);

        final Table table = getTable();
        
        org.eclipse.swt.graphics.Rectangle itemBounds =
            (org.eclipse.swt.graphics.Rectangle) getEventThreadQueuer().
                invokeAndWait(
                    "getTableItem",  //$NON-NLS-1$
                    new IRunnable() {
                        public Object run() throws StepExecutionException {
                            return table.getItem(row).getBounds();
                        }
                    });
        
        int itemHeight = itemBounds.height;
        
        // Creates a Rectangle with bounds around the checkbox of the row
        org.eclipse.swt.graphics.Rectangle cbxBounds = 
                new org.eclipse.swt.graphics.Rectangle(0,
                        itemBounds.y, itemBounds.x, itemHeight);
        
        // Performs a click in the middle of the Rectangle
        getRobot().click(table, cbxBounds, ClickOptions.create().left().
                setScrollToVisible(false),
                itemBounds.x / 2, true, itemHeight / 2, true);
    }
}
