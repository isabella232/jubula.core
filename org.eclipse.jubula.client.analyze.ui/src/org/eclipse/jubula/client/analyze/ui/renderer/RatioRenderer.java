package org.eclipse.jubula.client.analyze.ui.renderer;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jubula.client.analyze.internal.AnalyzeResult;
import org.eclipse.jubula.client.analyze.ui.definition.IResultRendererUI;
import org.eclipse.jubula.client.analyze.ui.i18n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
/**
 * 
 * @author volker
 *
 */
public class RatioRenderer implements IResultRendererUI {
    /**
     * 
     */
    private AnalyzeResult m_analyzeResult;

    /** The TopLevelControl of this Renderer */
    private Control m_topControl;

    /**
     * The RatioRenderer is able to display the AnalyzeResult as a Table
     */
    public RatioRenderer() {
    }

    /** @return The TopControl of this Renderer */
    public Control getTopControl() {
        return m_topControl;
    }

    /**
     * @param control
     *            The Control that is to be set as TopControl of this Renderer
     */
    public void setTopControl(Control control) {
        this.m_topControl = control;
    }

    /**
     * @param res
     *            The given AnalyzeResult
     * @param composite
     *            The given Composite
     */
    public void renderResult(AnalyzeResult res, Composite composite) {
        m_analyzeResult = res;

        if (m_analyzeResult.getResult() instanceof Map) {
            Map<String, String> resultMap = (Map<String, String>) 
                    m_analyzeResult.getResult();
            ArrayList<TableResult> resultList = new ArrayList<TableResult>();

            for (Map.Entry<String, String> e : resultMap.entrySet()) {

                TableResult tr = new TableResult();

                tr.setCol1(e.getKey());
//                tr.setCol2(String.valueOf(e.getValue()));
                tr.setCol2(e.getValue());
                resultList.add(tr);
            }
            createViewer(composite, resultList);
        }
    }

    /**
     * creates the TableViewer
     * 
     * @param parent
     *            The parent Composite
     * @param resultList
     *            The Given resultList
     */
    private void createViewer(Composite parent, List<TableResult> resultList) {
        TableViewer tv = new TableViewer(parent);
        createColumns(parent, tv);

        Table table = tv.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        tv.setContentProvider(new ArrayContentProvider());
        // set the Sorter

        tv.setSorter(new TableResultSorter());

        tv.setInput(resultList);
        // set the crontrol of the TreeViewer as the TopControl of this Renderer
        setTopControl(tv.getControl());
    }

    /**
     * 
     * @param cmp
     *            the parent composite
     * @param viewer
     *            The TableViewer
     */
    private void createColumns(Composite cmp, TableViewer viewer) {
        String[] titles = { Messages.Ratio, Messages.Value};
        int[] colType = { 0, 1 };

        // set the bounds of the table to the aspect ratio 1/3 and 2/3
        int c1 = (((cmp.getBounds().width) / 3) * 2);
        int c2 = ((cmp.getBounds().width) / 3);
        int[] bounds = { c1, c2 };
        // First column
        TableViewerColumn col = createTabelViewerColumn(titles[0], bounds[0],
                viewer, colType[0]);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                TableResult tr = (TableResult) element;
                return tr.getCol1();
            }
        });
        // Second column
        col = createTabelViewerColumn(titles[1], bounds[1], viewer, colType[1]);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {

                TableResult tr = (TableResult) element;
                return tr.getCol2();
            }
        });
    }

    /**
     * @param title
     *            The columnTitle
     * @param bounds
     *            The bounds of the Column
     * @param viewer
     *            The parent Viewer
     * @param colType
     *            The columnType
     * @return The TableViewerColumn
     */
    private TableViewerColumn createTabelViewerColumn(String title, int bounds,
            TableViewer viewer, int colType) {

        final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
                SWT.NONE);

        final int c = colType;

        final TableColumn column = viewerColumn.getColumn();
        final TableViewer tv = viewer;

        column.setText(title);
        column.setWidth(bounds);
        column.setResizable(true);
        column.setMoveable(true);
        column.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                ((TableResultSorter) tv.getSorter()).doSort(c);
                tv.refresh();
            }
        });
        return viewerColumn;
    }

    /**
     * This class is used to sort the TableResults
     * 
     * @author volker
     */
    class TableResultSorter extends ViewerSorter {
        /** */
        private static final int ASCENDING = 0;
        /** */
        private static final int DESCENDING = 1;
        /** */
        private int m_column;
        /** */
        private int m_direction;

        /**
         * checks if the direction ascending/descending
         * 
         * @param column
         *            the column that has to be sorted
         */
        public void doSort(int column) {
            if (column == this.m_column) {
                m_direction = 1 - m_direction;
            } else {
                this.m_column = column;
                m_direction = ASCENDING;
            }
        }

        /** {@inheritDoc} */
        public int compare(Viewer viewer, Object e1, Object e2) {
            int rc = 0;
            TableResult tr1 = (TableResult) e1;
            TableResult tr2 = (TableResult) e2;

            // create a new Collator for the comparison
            Collator col = Collator.getInstance();
            switch (m_column) {
                case 0:
                    rc = col.compare(tr1.getCol1(), tr2.getCol1());
                    break;
                case 1:
                    if (new Integer(tr1.getCol2()) 
                        < (new Integer(tr2.getCol2()))) {
                        rc = -1;
                    } else if (new Integer(tr1.getCol2()) == (new Integer(
                        tr2.getCol2()))) {
                        rc = 0;
                    } else if (new Integer(tr1.getCol2()) > (new Integer(
                        tr2.getCol2()))) {
                        rc = 1;
                    }
                    break;
                default:
                    break;
            }
            if (m_direction == DESCENDING) {
                rc = -rc;
            }
            return rc;
        }
    }

    /**
     * This class is used to provide a Result, that could be used from
     * TableRenderer with two columns
     * 
     * @author volker
     * 
     */
    public class TableResult {
        /**
         * the value which will be shown in the first column
         */
        private String m_col1;
        /**
         * the value which will be shown in the second column
         */
        private String m_col2;

        /**
         * the value which will be shown in the first column
         * 
         * @return the value which will be shown in the first column
         */
        public String getCol1() {
            return m_col1;
        }

        /**
         * the value which will be shown in the first column
         * 
         * @param col1
         *            the value which will be shown in the first column
         */
        public void setCol1(String col1) {
            this.m_col1 = col1;
        }

        /**
         * @return the value which will be shown in the second column
         */
        public String getCol2() {
            return m_col2;
        }

        /**
         * @param col2
         *            the value which will be shown in the second column
         */
        public void setCol2(String col2) {
            this.m_col2 = col2;
        }
    }
}
