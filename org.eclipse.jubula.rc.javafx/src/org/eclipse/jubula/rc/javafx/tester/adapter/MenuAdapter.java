package org.eclipse.jubula.rc.javafx.tester.adapter;

import java.util.List;
import java.util.concurrent.Callable;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuItemComponent;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;

/**
 * Adapter for a Menu. A Menu is a Subclass of MenuItem which represents a
 * sub-menu.
 * 
 * @author BREDEX GmbH
 * @created 10.2.2014
 */
public class MenuAdapter extends AbstractMenuAdapter<Menu> implements
        IMenuComponent {

    /**
     * Creates an adapter for a Menu.
     * 
     * @param objectToAdapt
     *            the object which needs to be adapted
     */
    public MenuAdapter(Menu objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
    public IMenuItemComponent[] getItems() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getItems", //$NON-NLS-1$
                new Callable<IMenuItemComponent[]>() {

                    @Override
                    public IMenuItemComponent[] call() throws Exception {
                        List<MenuItem> items = getRealComponent().getItems();
                        if (items.size() > 0) {
                            IMenuItemComponent[] itemAdapters = 
                                    new IMenuItemComponent[items.size()];
                            for (int i = 0; i < items.size(); i++) {
                                itemAdapters[i] = new MenuItemAdapter(items
                                        .get(i));
                            }
                            return itemAdapters;
                        }

                        return null;
                    }
                });
    }

    @Override
    public int getItemCount() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getItemCount", //$NON-NLS-1$
                new Callable<Integer>() {

                    @Override
                    public Integer call() throws Exception {
                        return getRealComponent().getItems().size();
                    }

                });
    }

}
