package org.eclipse.jubula.rc.javafx.tester.adapter;

import javafx.scene.Node;
import javafx.scene.control.MenuButton;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.AbstractMenuTester;

/**
 * Adapter class for MenuButtons.
 * 
 * @author BREDEX GmbH
 * @created 20.10.2014
 *
 */
public class MenuButtonAdapter extends ButtonBaseAdapter {
    /**
     * Creates an object with the adapted MenuButton.
     *
     * @param objectToAdapt
     *            this must be an object of the Type <code>MenuButton</code>
     */
    public MenuButtonAdapter(MenuButton objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
    public AbstractMenuTester showPopup(int xPos, String xUnits, int yPos,
            String yUnits, int button) throws StepExecutionException {
        Node n = getRealComponent();
        return openContextMenu(xPos, xUnits, yPos, yUnits, button, n);
    }
}
