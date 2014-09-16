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
package org.eclipse.jubula.documentation.gen;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jubula.tools.internal.utils.generator.IProcessor;
import org.eclipse.jubula.tools.internal.utils.generator.Info;


/**
 * This factory creates generator instances by names. The generator has
 * to implement {@link org.eclipse.jubula.documentation.gen.IGenerator}.
 *
 * @author BREDEX GmbH
 * @created 16.09.2005
 * @version $Revision: 12986 $
 */
public class GeneratorFactory {
    /**
     * The constructor.
     */
    private GeneratorFactory() {
        // OK
    }
    /**
     * @param name
     *            The name of the generator class
     * @param processor
     *            The processor for the generator
     * @param info
     *            The info for the generator
     * @param group
     *            the configuration group for the generator
     * @return The generator instance
     * @throws IllegalArgumentException
     *             If the creation fails
     */
    public static Generator create(String name, IProcessor processor, Info info,
        ConfigGroup group)
        throws IllegalArgumentException {
        try {
            return (Generator)Class.forName(name)
                .getConstructor(IProcessor.class, Info.class, ConfigGroup.class)
                    .newInstance(processor, info, group);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        } catch (SecurityException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
