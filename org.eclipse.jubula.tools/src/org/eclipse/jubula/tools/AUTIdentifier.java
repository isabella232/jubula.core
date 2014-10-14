package org.eclipse.jubula.tools;

/**
 * Exposes information to uniquely identify an AUT.
 *
 * @author BREDEX GmbH
 * @created Oct 13, 2014
 */
public interface AUTIdentifier {

    /**
     * @return the id of the executable used to start the AUT
     */
    String getID();
}
