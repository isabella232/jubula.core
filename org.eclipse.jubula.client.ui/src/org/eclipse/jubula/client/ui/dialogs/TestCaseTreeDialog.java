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
package org.eclipse.jubula.client.ui.dialogs;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.DependencyFinderOp;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.constants.Layout;
import org.eclipse.jubula.client.ui.filter.GDFilteredTree;
import org.eclipse.jubula.client.ui.filter.GDPatternFilter;
import org.eclipse.jubula.client.ui.model.CategoryGUI;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.model.SpecTestCaseGUI;
import org.eclipse.jubula.client.ui.model.TestCaseBrowserRootGUI;
import org.eclipse.jubula.client.ui.provider.labelprovider.GeneralGDLabelProvider;
import org.eclipse.jubula.client.ui.sorter.GuiNodeNameViewerSorter;
import org.eclipse.jubula.tools.exception.Assert;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.dialogs.FilteredTree;


/**
 * @author BREDEX GmbH
 * @since 12.10.2004
 */
public class TestCaseTreeDialog extends TitleAreaDialog {
        
    /** Add constant. */
    public static final int ADD = 9999;
    /** add a test case */
    public static final int TESTCASE = 10;
    /** add an event handler */
    public static final int EVENTHANDLER = 20;
    /** open an spec test case*/
    public static final int OPEN_TESTCASE = 30;
    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1;    
    /** vertical spacing = 2 */
    private static final int VERTICAL_SPACING = 2;    
    /** margin width = 2 */
    private static final int MARGIN_WIDTH = 2;    
    /** margin height = 2 */
    private static final int MARGIN_HEIGHT = 2;
    /** width hint = 300 */
    private static final int WIDTH_HINT = 300;
    /** <code>m_rootGUINode</code> */
    private TestCaseBrowserRootGUI m_rootGuiNode;
      
    /** the local tree viewer */
    private TreeViewer m_treeViewer;
    
    /** List of ISelectionListener */
    private List < ISelectionListener > m_selectionListenerList = 
        new ArrayList < ISelectionListener > ();
    
    /** the title */
    private String m_title = I18n.getString("TestCaseTableDialog.title"); //$NON-NLS-1$
    
    /** the message */
    private String m_message = I18n.getString("TestCaseTableDialog.message"); //$NON-NLS-1$
    
    /** the shell title */
    private String m_shellTitle = I18n.getString("TestCaseTableDialog.shellTitle"); //$NON-NLS-1$

    /** the add button text */
    private String m_addButtonText = I18n.getString("TestCaseTableDialog.Add"); //$NON-NLS-1$
    
    /** the type to add property */
    private int m_typeToAdd = TESTCASE;
    
    /** the TestCase which should be parent of the shown TestCases */
    private ISpecTestCasePO m_parentTestCase;
    
    /** the style of the tree */
    private int m_treeStyle = SWT.SINGLE;
    /** the add button */
    private Button m_addButton;
    /** the image of the title area */
    private Image m_image = IconConstants.ADD_TC_DIALOG_IMAGE; 
    /** a list with the item numbers of circular dependenced test cases */
    private Set < INodePO > m_circDependList = 
        new HashSet < INodePO > ();
    
    
    /**
     * Constructor.
     * @param shell The parent of the dialog.
     * @param parentTestCase the TestCase which should be parent of the shown TestCases.
     * <b>Can be null if parent is a Testsuite!</b>
     * @param treeStyle SWT.SINGLE or SWT.MULTI
     * @param typeToAdd TestCaseTreeDialog.TESTACSE or TestCaseTreeDialog.EVENTHANDLER, 
     * 
     */  
    public TestCaseTreeDialog(Shell shell, ISpecTestCasePO parentTestCase,
            int treeStyle, int typeToAdd) {
        super(shell);
        Assert.verify((typeToAdd == TESTCASE || typeToAdd == EVENTHANDLER
                || typeToAdd == OPEN_TESTCASE),
            "Parameter 'typeToAdd' must be 'TESTCASE', 'OPEN_TESTCASE' or 'EVENTHANDLER'!"); //$NON-NLS-1$
        setShellStyle(getShellStyle() | SWT.RESIZE);
        m_parentTestCase = parentTestCase;
        m_treeStyle = treeStyle;
        m_typeToAdd = typeToAdd;
    }
    
    /**
     * Constructor.
     * @param shell the parent shell.
     * @param title the title
     * @param message the message
     * @param parentTestCase the TestCase which should be parent of the shown TestCases.
     * <b>Can be null if parent is a Testsuite!</b>
     * @param treeStyle SWT.SINGLE or SWT.MULTI
     * @param shellTitle the shell title
     * @param image The title image.
     * @param typeToAdd TestCaseTreeDialog.TESTACSE or TestCaseTreeDialog.EVENTHANDLER
     */
    public TestCaseTreeDialog(Shell shell,
        String title, String message, ISpecTestCasePO parentTestCase, 
        String shellTitle, int treeStyle, Image image, int typeToAdd) {
        
        this(shell, parentTestCase, treeStyle, typeToAdd);
        m_title = title;
        m_message = message;
        m_shellTitle = shellTitle;
        m_image = image;
    }
    
    /**
     * Constructor.
     * @param shell the parent shell.
     * @param title the title
     * @param message the message
     * @param parentTestCase the TestCase which should be parent of the shown TestCases.
     * <b>Can be null if parent is a Testsuite!</b>
     * @param treeStyle SWT.SINGLE or SWT.MULTI
     * @param shellTitle the shell title
     * @param image The title image.
     * @param typeToAdd TestCaseTreeDialog.TESTACSE or TestCaseTreeDialog.EVENTHANDLER
     * @param addButtonText the text for the add / ok button
     */
    public TestCaseTreeDialog(Shell shell, String title, String message,
            ISpecTestCasePO parentTestCase, String shellTitle, int treeStyle,
            Image image, int typeToAdd, String addButtonText) {
        this(shell, title, message, parentTestCase, shellTitle, treeStyle,
                image, typeToAdd);
        m_addButtonText = addButtonText;
    }
    
    
    /**
     * {@inheritDoc}
     * createDialogArea(org.eclipse.swt.widgets.Composite) Hier wird der
     * mittlere Bereich des Dialogs erzeugt
     */
    protected Control createDialogArea(Composite parent) {
        setTitle(m_title);
        setMessage(m_message);
        getShell().setText(m_shellTitle);
        setTitleImage(m_image); 
        // new Composite as container
        final GridLayout gridLayoutParent = new GridLayout();
        gridLayoutParent.numColumns = NUM_COLUMNS_1;
        gridLayoutParent.verticalSpacing = VERTICAL_SPACING;
        gridLayoutParent.marginWidth = MARGIN_WIDTH;
        gridLayoutParent.marginHeight = MARGIN_HEIGHT;
        parent.setLayout(gridLayoutParent);
        
        Plugin.createSeparator(parent);

        final Composite area = new Composite(parent, SWT.NULL);
        // use Gridlayout
        final GridLayout gridLayout = new GridLayout();

        area.setLayout(gridLayout);

        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;

        area.setLayoutData(gridData);

        final FilteredTree ft = new GDFilteredTree(
                area, m_treeStyle, new GDPatternFilter(), true);
        
        m_treeViewer = ft.getViewer();

        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.horizontalAlignment = GridData.FILL;
        layoutData.verticalAlignment = GridData.FILL;
        layoutData.heightHint = WIDTH_HINT;
        Layout.addToolTipAndMaxWidth(layoutData, m_treeViewer.getControl());
        m_treeViewer.getControl().setLayoutData(layoutData);
        m_treeViewer.setUseHashlookup(true);
        getInitialInput();
        m_treeViewer.setLabelProvider(new LabelProvider());
        m_treeViewer.setContentProvider(new ContentProvider());
        m_treeViewer.setInput(m_rootGuiNode);
        m_treeViewer.setSorter(new GuiNodeNameViewerSorter());
        Plugin.createSeparator(parent);
        return area;
    }
    
    /**
     * gets a list of all test cases
     */
    private void getInitialInput() {
        m_rootGuiNode = (TestCaseBrowserRootGUI)Plugin.getDefault()
            .getTestCaseBrowserRootGUI().getChildren().get(0);
        
        if (m_parentTestCase != null) {
            DependencyFinderOp op = new DependencyFinderOp(m_parentTestCase);
            TreeTraverser traverser = new TreeTraverser(GeneralStorage.
                getInstance().getProject(), op, true);
            traverser.traverse(true);
            m_circDependList = op.getDependentNodes();
        }
    }

    /**
     * {@inheritDoc}
     *      createButtonsForButtonBar(org.eclipse.swt.widgets.Composite) 
     */
    protected void createButtonsForButtonBar(Composite parent) {
        // Add-Button
        m_addButton = createButton(parent, ADD , m_addButtonText , true);
        m_addButton.setEnabled(false);
        m_addButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                notifyListener();
                setReturnCode(ADD);
                close();
            }
        });
        m_treeViewer.addSelectionChangedListener(
            new ISelectionChangedListener() {
                public void selectionChanged(SelectionChangedEvent e) {
                    if (e.getSelection() != null) {
                        m_addButton.setEnabled(true);
                    }
                }
            });
        
        m_treeViewer.getTree().addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                for (TreeItem item : ((Tree) e.getSource()).getSelection()) {
                    Object itemData = item.getData();
                    if (itemData != null) {
                        if (m_circDependList.contains(((GuiNode) itemData)
                                .getContent())
                                || itemData instanceof CategoryGUI) {
                            m_addButton.setEnabled(false);
                        }
                    }
                }
            }
            
            /**
             * {@inheritDoc}
             */
            public void widgetDefaultSelected(SelectionEvent e) {
                if (!m_addButton.getEnabled()) {
                    return;
                }
                notifyListener();
                setReturnCode(ADD);
                close();
            }
        });
        // Cancel-Button
        Button cancelButton = 
            createButton(parent, CANCEL , I18n.getString(
                    "TestCaseTableDialog.Cancel") , false); //$NON-NLS-1$
        cancelButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                setReturnCode(CANCEL);
                close();
            }
        });
    }       
    
    /**
     * Adds the given ISelectionListener to this dialog
     * @param listener the listener to set.
     */
    public void addSelectionListener(ISelectionListener listener) {
        if (!m_selectionListenerList.contains(listener)) {
            m_selectionListenerList.add(listener);
        }
    }
    
    /**
     * Removes the given IselectionListener from this dialog.
     * @param listener the listener to be removed.
     */
    public void removeSelectionListener(ISelectionListener listener) {
        m_selectionListenerList.remove(listener);
    }
    
    /**
     * Notifies the listeners about the selected TestCases when the Add-button
     * is pressed. <br>
     * Note: The IWorkbenchPart-Parameter of the listener is set to null!
     */
    void notifyListener() {
        for (ISelectionListener listener : m_selectionListenerList) {
            listener.selectionChanged(null, m_treeViewer.getSelection());
        }
    }
    
    /**
     * LabelProvider for m_treeViewer
     *
     * @author BREDEX GmbH
     * @created 14.06.2005
     */
    private class LabelProvider implements ILabelProvider {

        /**
         * @param item the current item
         * @param o the object to compare with
         * @return the item that equals with o
         */
        private TreeItem getItem(TreeItem item, Object o) {
            TreeItem returnItem = null;
            if (o.equals(item.getData())) {
                return item;
            }
            for (TreeItem ti : item.getItems()) {
                returnItem = getItem(ti, o);
                if (returnItem != null) {
                    break;
                }
            }
            return returnItem;
        }
        
        /**
         * {@inheritDoc}
         */
        public Image getImage(Object element) {
            TreeItem currentItem = null;
            for (TreeItem item : m_treeViewer.getTree().getItems()) {
                currentItem = getItem(item, element);
                if (currentItem != null) {
                    break;
                }
            }
            if (element instanceof SpecTestCaseGUI) {
                if (m_circDependList.contains(((SpecTestCaseGUI)element).
                    getContent())) {
                    if (currentItem != null) {
                        currentItem.setForeground(Layout.GRAY_COLOR);
                    }
                    return (IconConstants.TC_DISABLED_IMAGE); 
                } 
                if (currentItem != null) {
                    currentItem.setForeground(Layout.DEFAULT_OS_COLOR);
                }
                return ((GuiNode)element).getImage();
            }
            if (currentItem != null) {
                // Attention: grayed text for categories
                currentItem.setForeground(Layout.GRAY_COLOR);
            }
            // Attention: no grayed icon for categories
            return ((GuiNode)element).getImage();
        }

        /**
         * {@inheritDoc}
         */
        public String getText(Object element) {
            return GeneralGDLabelProvider.getGDText(element);
        }

        /**
         * {@inheritDoc}
         */
        public void addListener(ILabelProviderListener listener) {
            // do nothing
        }

        /**
         * {@inheritDoc}
         */
        public void dispose() {
            // do nothing
        }

        /**
         * {@inheritDoc}
         */
        public boolean isLabelProperty(Object element, String property) {
            // do nothing
            return false;
        }

        /**
         * {@inheritDoc}
         */
        public void removeListener(ILabelProviderListener listener) {
            // do nothing
        }        
    }
    
    /**
     * @author BREDEX GmbH
     * @created 26.06.2006
     */
    private class ContentProvider implements ITreeContentProvider {
        /**
         * {@inheritDoc}
         */
        public void dispose() {
            // do nothing
        }

        /**
         * {@inheritDoc}
         */
        public void inputChanged(Viewer viewer, Object oldInput, 
                Object newInput) {
            
            // do nothing
        }

        /**
         * {@inheritDoc}
         */
        public Object[] getElements(Object inputElement) {
            List<GuiNode> children = ((GuiNode)inputElement).getChildren();
            List<GuiNode> childrenToDisplay = new ArrayList<GuiNode>();
            if (m_typeToAdd == OPEN_TESTCASE) {
                for (GuiNode gn : children) {
                    if (gn.isEditable()) {
                        childrenToDisplay.add(gn);
                    }
                }
            } else {
                childrenToDisplay.addAll(children);
            }
            return childrenToDisplay.toArray();
        }

        /**
         * {@inheritDoc}
         */
        public Object[] getChildren(Object parentElement) {
            if ((parentElement instanceof TestCaseBrowserRootGUI 
                    || parentElement instanceof CategoryGUI)) {
                List<GuiNode> children = ((GuiNode)parentElement).getChildren();
                return children.toArray();
            }
            return ArrayUtils.EMPTY_OBJECT_ARRAY;
        }

        /**
         * {@inheritDoc}
         */
        public Object getParent(Object element) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public boolean hasChildren(Object element) {
            return (element instanceof CategoryGUI && !((CategoryGUI)element)
                    .getChildren().isEmpty());
        }
    }
}