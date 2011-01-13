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
package org.eclipse.jubula.client.ui.constants;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * This class contains constants to be used for the layout of all components
 * which will be displayed graphical. (GUI)
 * 
 * @author BREDEX GmbH
 * @created 15.10.2004
 */
public class Layout {
    /** value for 0, use the detailed constant! */
    public static final int ZERO = 0;
    /** value for SMALL_XXX, use the detailed constant! */
    public static final int SMALL = 10;
    /** value for BIG_XXX, use the detailed constant! */
    public static final int BIG = 15;
    /** value for max. chars-width of a control, use the detailed constant! */
    public static final int WIDTH = 30;
    // constants for all layouts
    /** small margin width, for all layouts */
    public static final int SMALL_MARGIN_WIDTH = SMALL;
    /** big margin width, for all layouts */
    public static final int BIG_MARGIN_WIDTH = BIG;
    /** small margin height, for all layouts */
    public static final int SMALL_MARGIN_HEIGHT = SMALL;
    /** big margin height, for all layouts */
    public static final int BIG_MARGIN_HEIGHT = BIG;
    /** small horizontal spacing */
    public static final int SMALL_HORIZONTAL_SPACING = SMALL;
    /** big horizontal spacing */
    public static final int BIG_HORIZONTAL_SPACING = BIG;
    /** small vertical spacing */
    public static final int SMALL_VERTICAL_SPACING = SMALL;
    /** big vertical spacing */
    public static final int BIG_VERTICAL_SPACING = BIG;
    // constants for alignment
    /** the horizontal alignment for controls, except labels, m_text */
    public static final int HORIZONTAL_ALIGNMENT = SWT.FILL;
    /** the horizontal alignmemt for labels */
    public static final int LABEL_HORIZONTAL_ALIGNMENT = SWT.END;
    /**the horizontal alignmemt for labels in a GridLayout with more than two columns */
    public static final int MULTI_COLUMN_LABEL_HORIZONTAL_ALIGNMENT = 
        SWT.BEGINNING;
    /** the horizontal alignmemt for textfields */
    public static final int TEXT_HORIZONTAL_ALIGNMENT = SWT.BEGINNING;
    /** the horizontal alignmemt for multi line textfields in a GridLayout */
    public static final int MULTI_LINE_TEXT_HORIZONTAL_ALIGNMENT = SWT.FILL;
    /** the vertical algnment for all controls */
    public static final int VERTICAL_ALIGNMENT = SWT.CENTER;
    // style for controls 
    /** the style for a single - line m_text */
    public static final int SINGLE_TEXT_STYLE = SWT.SINGLE | SWT.BORDER;
    /** the style for a multi - line m_text */
    public static final int MULTI_TEXT_STYLE = SWT.MULTI | SWT.BORDER
            | SWT.V_SCROLL | SWT.H_SCROLL;
    /** the style for a multi - line m_text without border*/
    public static final int MULTI_TEXT = SWT.MULTI | SWT.WRAP;
    /** margin width = 2 */
    public static final int MARGIN_WIDTH = 2;    
    /** margin height = 2 */
    public static final int MARGIN_HEIGHT = 2;
    
    // -------------------------------------------------------------
    // Colors
    // -------------------------------------------------------------
    /** color white for gui elements */
    public static final Color WHITE_COLOR = Display.getDefault()
        .getSystemColor(SWT.COLOR_WHITE);
    /** color gray for disabled gui elements (foreground) */
    public static final Color GRAY_COLOR = new Color(
            Plugin.getDisplay(), new RGB(100, 100, 100));
    /** color gray for disabled gui elements (background) */
    public static final Color LIGHT_GRAY_COLOR = Display.getDefault()
        .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
    /** error color red for gui elements */
    public static final Color ERROR_COLOR = Display.getDefault()
        .getSystemColor(SWT.COLOR_RED);
    /** color for inactive gui elements */
    public static final Color INACTIVE_COLOR = Display.getDefault()
        .getSystemColor(SWT.COLOR_DARK_GREEN);
    /** <code>HAS_DETAILS_COLOR</code> */
    public static final Color HAS_DETAILS_COLOR = new Color(
            Plugin.getDisplay(), new RGB(170, 211, 255));
    /** default color (mostly black) for gui elements */
    public static final Color DEFAULT_OS_COLOR = null;
    /** light blue */
    public static final RGB GUIDANCER_BLUE = new RGB(129, 147, 255);
    /** light orange */
    public static final RGB GUIDANCER_ORANGE = new RGB(184, 128, 59);
    /** light red */
    public static final RGB GUIDANCER_LIGHT_RED = new RGB(255, 204, 230);
    /** red */
    public static final RGB GUIDANCER_RED = new RGB(255, 0, 0);
    /** Color constant for error tool tips */
    public static final Color TOOLTIP_COLOR = new Color(
            Plugin.getDisplay(), GUIDANCER_LIGHT_RED);   
    // -------------------------------------------------------------
    // Font
    // -------------------------------------------------------------
    /**
     * <code>FONT_NAME</code>
     */
    public static final String FONT_NAME = "Tahoma"; //$NON-NLS-1$

    /**
     * <code>FONT_HEIGHT</code>
     */
    public static final int FONT_HEIGHT = 9;

    /** bold tahoma font, size: 9 */
    public static final Font BOLD_TAHOMA = new Font(null, FONT_NAME,
            FONT_HEIGHT, SWT.BOLD);

    /** bold,italic tahoma font, size: 9 */
    public static final Font BOLD_ITALIC_TAHOMA = new Font(null, FONT_NAME,
            FONT_HEIGHT, SWT.BOLD | SWT.ITALIC);

    /** italic tahoma font, size: 9 */
    public static final Font ITALIC_TAHOMA = new Font(null, FONT_NAME,
            FONT_HEIGHT, SWT.ITALIC);

    /** tahoma font, size: 9 */
    public static final Font NORMAL_TAHOMA = new Font(null, FONT_NAME,
            FONT_HEIGHT, SWT.NORMAL);

    /**
     * do not instantiate this class
     */
    private Layout() {
        super();
    }
    
    /**
     * creates the default GridLayout to be used 
     * @param numColumns the number of the columns
     * @return a new instance of GridLayout
     */
    public static GridLayout createDefaultGridLayout(int numColumns) {
        GridLayout result = new GridLayout();
        result.numColumns = numColumns;
        result.horizontalSpacing = SMALL_HORIZONTAL_SPACING;
        result.verticalSpacing = SMALL_VERTICAL_SPACING;
        result.marginWidth = SMALL_MARGIN_WIDTH;
        result.marginHeight = SMALL_MARGIN_HEIGHT;
        return result;
    }
    
    /**
     * Sets a character limit (255) for the text field (incl. warning, if limit was reached)
     * @param textField The actual text field.
     */
    public static void setMaxChar(final Text textField) {
        setMaxChar(textField, 255);
    }
    
    /**
     * Sets a character limit for the text field (incl. warning, if limit was reached)
     * @param textField The actual text field.
     * @param maxLength the max size of input
     */
    public static void setMaxChar(final Text textField, final int maxLength) {
        if (textField == null) {
            return;
        }
        textField.setTextLimit(maxLength);
        textField.addModifyListener(new ModifyListener() {
            /**
             * <code>m_oldValue</code> the old value
             */
            private String m_oldValue = textField.getText();
            
            public void modifyText(ModifyEvent e) {
                Text theWidget = ((Text)e.widget);
                if (theWidget.getCharCount() >= maxLength) {
                    Utils.createMessageDialog(MessageIDs.W_MAX_CHAR, 
                            new Object[] {maxLength}, null);
                    theWidget.setText(m_oldValue);
                }
                m_oldValue = theWidget.getText();
            }
        });
    }

    /**
     * Defines a constant width for a control and shows a tool tip with the text 
     * of this control, when this text is longer than the constant width.
     * @param gridData The gridData object.
     * @param control The actual SWT control.
     */
    public static void addToolTipAndMaxWidth(GridData gridData, 
            final Control control) {
        
        gridData.widthHint = Dialog.convertWidthInCharsToPixels(
                Layout.getFontMetrics(control), Layout.WIDTH);
        control.addMouseTrackListener(new MouseTrackListener() {
            public void mouseEnter(MouseEvent e) {
                String toolTipText = StringConstants.EMPTY;
                if (control instanceof Combo) {
                    toolTipText = ((Combo)control).getText();
                } else if (control instanceof Text) {
                    toolTipText = ((Text)control).getText();
                } else {
                    return;
                }
                int width = Dialog.convertWidthInCharsToPixels(
                        Layout.getFontMetrics(control), toolTipText.length());
                if (width > control.getBounds().width) {
                    control.setToolTipText(toolTipText);
                } 
            }
            public void mouseExit(MouseEvent e) {
                control.setToolTipText(StringConstants.EMPTY);
            }
            public void mouseHover(MouseEvent e) {
                // do nothing
            }
        });
    }
    
    /**
     * creates a small skip for GridLayout 
     * @param parent the composite to creat the skip in
     * @param numColumns the number of the columns
     * @return a control representing a small skip
     */
    public static Control createGridSmallSkip(Composite parent,
            int numColumns) {
        
        Label result = new Label(parent, SWT.NONE);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        gd.heightHint = SMALL_VERTICAL_SPACING;
        result.setLayoutData(gd);
        return result;
    }
    
    /**
     * creates a big skip for GridLayout
     * @param parent the composite to creat the skip in
     * @param numColumns the number of the columns
     * @return a control representing a big skip
     */
    public static Control createGridBigSkip(Composite parent, int numColumns) {
        Label result = new Label(parent, SWT.NONE);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        gd.heightHint = BIG_VERTICAL_SPACING;
        result.setLayoutData(gd);
        return result;
    }
    
    /**
     * creates a GridData for labels with default layout
     * @return a new instance of GridData
     */
    public static GridData createDefaultLabelGridData() {
        GridData result = new GridData();
        result.horizontalAlignment = LABEL_HORIZONTAL_ALIGNMENT;
        result.verticalAlignment = VERTICAL_ALIGNMENT;
        result.horizontalSpan = 1;
        result.verticalSpan = 1; 
        return result;
    }
     
    /**
     * creates a GridData for labels in a GridLayout with more than two columns
     *  @return a new instance of GridData
     */
    public static GridData createMultiColumnLabelGridData() {
        GridData result = new GridData();
        result.horizontalAlignment = MULTI_COLUMN_LABEL_HORIZONTAL_ALIGNMENT;
        result.verticalAlignment = VERTICAL_ALIGNMENT;
        result.horizontalSpan = 1;
        result.verticalSpan = 1; 
        result.horizontalIndent = BIG_HORIZONTAL_SPACING;

        return result;
    }
    
    /**
     * creates a GridData for labels which are aligned at the top of the cell
     * <br>
     * use it for labels for control which take more space as usual, e.g. a multi line textfield.
     * @return a new instance of GridData
     */
    public static GridData createTopLabelGridData() {
        GridData result = new GridData();
        result.horizontalAlignment = LABEL_HORIZONTAL_ALIGNMENT;
        result.verticalAlignment = SWT.BEGINNING;
        result.horizontalSpan = 1;
        result.verticalSpan = 1; 
        return result;
    }
    
    /**
     * creates a GridData for textfields with default layout
     * @return a new instance of GridData
     */
    public static GridData createDefaultTextGridData() {
        return createDefaultTextGridData(1);
    }
     
    /**
     * creates a GridData for textfields with default layout (fills one column)
     * @param numColumns number of columns to span
     * @return a new instance of GridData
     */
    public static GridData createDefaultTextGridData(int numColumns) {
        GridData result = createTextGridData();
        result.horizontalSpan = numColumns; 
        return result;
    }
    
    /**
     * creates a GridData for textfields with default layout, give a number of expected characters as a hint
     * @param control the m_text control, used to determine the FontMetrics
     * @param numChars number of characters textfield should contain
     * @return a new instance of GridData
     */
    public static GridData createDefaultTextGridData(Control control,
            int numChars) {
        
        GridData result = new GridData();
        result.horizontalAlignment = TEXT_HORIZONTAL_ALIGNMENT;
        result.verticalAlignment = VERTICAL_ALIGNMENT;
        result.horizontalSpan = 1;
        result.verticalSpan = 1;
        result.widthHint = Dialog.convertWidthInCharsToPixels(
                getFontMetrics(control), numChars); 
        return result;
    } 
    
    /**
     * creates a GridData for multi line textfields, give a number of lines as a hint
     * @param control the m_text control, used to determine the FontMetrics
     * @param numLines number of lines the textfield should contain
     * @return a new instance of GridData
     */
    public static GridData createMultiLineTextGridData(Control control,
            int numLines) {
        GridData result = new GridData();
        result.horizontalAlignment = MULTI_LINE_TEXT_HORIZONTAL_ALIGNMENT;
        result.verticalAlignment = VERTICAL_ALIGNMENT;
        result.horizontalSpan = 1;
        result.verticalSpan = 1;
        result.heightHint = Dialog.convertHeightInCharsToPixels(
                getFontMetrics(control), numLines); 
        return result;
    }
         
    /** creates a m_text grid data for textfields
     * @return a new instance of GridData
     */
    private static GridData createTextGridData() {
        GridData result = new GridData();
        result.horizontalAlignment = HORIZONTAL_ALIGNMENT;
        result.verticalAlignment = VERTICAL_ALIGNMENT;
        result.verticalSpan = 1;
        return result;
    }
    
    /**
     * returns the FontMetrics of <code>control</code> 
     * @param control the control to get the font metrics from
     * @return the FontMetrics
     */
    public static FontMetrics getFontMetrics(Control control) {
        GC gc = new GC(control);
        gc.setFont(control.getFont());
        FontMetrics fontMetrics = gc.getFontMetrics();
        gc.dispose();
        return fontMetrics;
    }
}