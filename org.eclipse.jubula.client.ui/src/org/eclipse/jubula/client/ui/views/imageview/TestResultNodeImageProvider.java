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
package org.eclipse.jubula.client.ui.views.imageview;

import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.ui.utils.ImageUtils;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;


/**
 * @author BREDEX GmbH
 * @created Apr 14, 2010
 */
public class TestResultNodeImageProvider implements ImageProvider {
    /**
     * <code>m_testResultNode</code>
     */
    private TestResultNode m_testResultNode;

    /**
     * @param testresultnode
     *            the test result node
     */
    public TestResultNodeImageProvider(TestResultNode testresultnode) {
        m_testResultNode = testresultnode;
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage(Device target) {
        if (m_testResultNode.getScreenshot() != null) {
            return new Image(target, ImageUtils.getImageData(m_testResultNode
                    .getScreenshot()));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void disposeImage(Image image) {
        image.dispose();
    }
}
