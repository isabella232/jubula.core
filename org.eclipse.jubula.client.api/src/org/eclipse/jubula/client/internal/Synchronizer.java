package org.eclipse.jubula.client.internal;

import java.util.concurrent.Exchanger;

/** @author BREDEX GmbH */
public class Synchronizer extends Exchanger<Object> {
    /** the singleton instance */
    private static Synchronizer instance = null;

    /** Constructor */
    private Synchronizer() {
        // currently empty
    }

    /** @return singleton instance */
    public static synchronized Synchronizer instance() {
        if (instance == null) {
            instance = new Synchronizer();
        }

        return instance;
    }
}
