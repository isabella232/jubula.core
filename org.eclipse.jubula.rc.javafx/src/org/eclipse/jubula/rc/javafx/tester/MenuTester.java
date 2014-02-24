package org.eclipse.jubula.rc.javafx.tester;

import javafx.scene.control.MenuItem;

import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.tester.AbstractMenuTester;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuItemComponent;
import org.eclipse.jubula.rc.javafx.tester.adapter.MenuItemAdapter;
/**
 * Toolkit specific commands for the <code>Menu</code>
 *
 * @author BREDEX GmbH
 * @created 17.2.2014
 */
public class MenuTester extends AbstractMenuTester {
    
    /**
     * The logging.
     */
    private static AutServerLogger log = new AutServerLogger(
            MenuTester.class);
    

    @Override
    public String[] getTextArrayFromComponent() {
        return null;
    }

    @Override
    protected IMenuItemComponent newMenuItemAdapter(Object component) {
        return new MenuItemAdapter((MenuItem) component);
    }

    @Override
    protected void closeMenu(IMenuComponent menu, String[] textPath,
            String operator) {
        log.error("exeClose1"); //$NON-NLS-1$
        getRobot().activateApplication("TITLEBAR"); //$NON-NLS-1$
    }

    @Override
    protected void closeMenu(IMenuComponent menu, int[] path) {
        log.error("exeClose2"); //$NON-NLS-1$
        getRobot().activateApplication("TITLEBAR"); //$NON-NLS-1$
    }

}
