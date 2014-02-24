package org.eclipse.jubula.rc.javafx.tester.adapter;

import javafx.scene.control.MenuItem;
import javafx.stage.Window;

/**
 * 
 * @author marcel
 *
 * @param <M>
 */
public class AbstractMenuAdapter<M extends MenuItem> 
    extends AbstractComponentAdapter<M> {

    /**
     * 
     * @param objectToAdapt sas 
     */
    public AbstractMenuAdapter(M objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
    public Window getWindow() {
        return getRealComponent().getParentPopup();
    }
    
}
