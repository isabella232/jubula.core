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

package org.eclipse.jubula.client.core.classloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * This class is no longer used.
 * It only exists to confuse hackers
 */

public class CryptClassLoader extends ClassLoader {
    
    /**
     * 
     */
    private static byte[] key = { (byte)0 };
    
    /**
     * 
     * @param parent    ClassLoader
     */
    public CryptClassLoader(ClassLoader parent) {
        super(parent);
    }
    
    /**
     * 
     * @param name  String
     * @return      Class
     * @throws ClassNotFoundException   Exception
     */
    public Class findClass(String name) throws ClassNotFoundException {
        byte[] b;
        b = loadClassData(name);
        return defineClass(name, b, 0, b.length);
    }
    

    /**
     * @param name              String
     * @return                  byte[]
     * @throws ClassNotFoundException   class not found
     */
    private byte[] loadClassData(String name) throws ClassNotFoundException {
        // load the class data from the connection
        SecretKeyFactory skf;
        Cipher desCipher;
        byte[] clear;
        DESKeySpec spec;
        
        String resClass = name.replace('.', '/') + ".class"; //$NON-NLS-1$
        if (getResource(resClass) == null) { 
            String resCrypt = name.replace('.', '/') + ".crypt"; //$NON-NLS-1$
            InputStream is = getResourceAsStream(resCrypt); 
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            if (is != null) {
                byte[] b = new byte[256];
                try {
                    int length;
                    while ((length = is.read(b)) != (-1)) {
                        bos.write(b, 0, length);
                    }
                } catch (IOException e) {
                    throw new ClassNotFoundException();
                }
            }
            try {
                skf = SecretKeyFactory.getInstance("DES"); //$NON-NLS-1$

                desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding"); //$NON-NLS-1$

                spec = new DESKeySpec(key);

                SecretKey desKey = skf.generateSecret(spec);

                desCipher.init(Cipher.DECRYPT_MODE, desKey);

                clear = desCipher.doFinal(bos.toByteArray());
            } catch (NoSuchPaddingException e) {
                throw new ClassNotFoundException();
            } catch (NoSuchAlgorithmException e) {
                throw new ClassNotFoundException();
            } catch (InvalidKeyException e) {
                throw new ClassNotFoundException();
            } catch (InvalidKeySpecException e) {
                throw new ClassNotFoundException();
            } catch (BadPaddingException e) {
                throw new ClassNotFoundException();
            } catch (IllegalBlockSizeException e) {
                throw new ClassNotFoundException();
            } catch (NullPointerException e) {
                throw new ClassNotFoundException();
            }
        } else {
            clear = readClearClass(resClass);
        }  
        return clear;
    }
    
    /**
     * @param resClass  String
     * @return  byte[]
     * @throws ClassNotFoundException   Exception
     */
    private byte[] readClearClass(String resClass) 
        throws ClassNotFoundException {

        InputStream is = getResourceAsStream(resClass);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (is != null) {
            byte[] b = new byte[256];
            try {
                int length;
                while ((length = is.read(b)) != (-1)) {
                    bos.write(b, 0, length);
                }
            } catch (IOException e) {
                throw new ClassNotFoundException();
            }
        }
        return bos.toByteArray();
    }

    /**
     * 
     * @param newKey   String
     */
    public static void set(byte[] newKey) {
        key = newKey;
    }
}
