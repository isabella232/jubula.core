/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.commons.codec.binary.Base64;


/**
 * Utility class for encoding/decoding serializable objects into/from Base64 Strings
 * @author BREDEX GmbH
 * @created Oct 09, 2014
 */
public class SerilizationUtils {
    
    /** private constructor */
    private SerilizationUtils() {
        // utility class
    }
    

    /** 
     * Encodes a serializable object to a Base64 string.
     * @param serializableObject the serializable object
     * @return a String representation of the serializable
     */
    public static String encode(Serializable serializableObject)
        throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(serializableObject);
        oos.close();
        return new String(
                Base64.encodeBase64(baos.toByteArray()));
    }

    /** 
     * Decodes a serializable from a base64 encoded string.
     * @param encodedString Base64 encoded String
     * @return the translated object
     */
    public static Object decode(String encodedString) throws IOException,
            ClassNotFoundException {
        byte [] data = Base64.decodeBase64(encodedString);
        ObjectInputStream ois = new ObjectInputStream(
                                        new ByteArrayInputStream(data));
        Object decodedObject = ois.readObject();
        ois.close();
        return decodedObject;
    } 

}
