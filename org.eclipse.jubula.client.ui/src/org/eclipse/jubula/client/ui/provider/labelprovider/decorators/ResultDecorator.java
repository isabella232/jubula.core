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
package org.eclipse.jubula.client.ui.provider.labelprovider.decorators;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.swt.graphics.Image;


/**
 * @author BREDEX GmbH
 * @created 21.10.2004
 */
public class ResultDecorator extends LabelProvider implements 
        ILabelDecorator {

    /**
     * Constructor
     */
    public ResultDecorator() {
        super();
        
    }

    /**
     * @param element Object
     * @param imageToDecorate Image
     * @return Image
     */
    private Image doDecorate(Object element, Image 
        imageToDecorate) {
        TestResultNode resultNode = 
            ((TestResultNode)element);
        int status = resultNode.getStatus();
        switch (status) {
            case TestResultNode.NOT_YET_TESTED:
                break;
            case TestResultNode.NO_VERIFY:
                return IconConstants.STEP_OK_IMAGE;
            case TestResultNode.TESTING:
                return IconConstants.STEP_TESTING_IMAGE;
            case TestResultNode.SUCCESS:
                return IconConstants.STEP_OK_IMAGE;
            case TestResultNode.ERROR:
                return IconConstants.STEP_NOT_OK_IMAGE;
            case TestResultNode.ERROR_IN_CHILD:
                return IconConstants.STEP_NOT_OK_IMAGE;
            case TestResultNode.NOT_TESTED:
                return IconConstants.STEP_FAILED_IMAGE;
            case TestResultNode.RETRYING:
                return IconConstants.STEP_RETRY_IMAGE;
            case TestResultNode.SUCCESS_RETRY:
                return IconConstants.STEP_RETRY_OK_IMAGE;
            case TestResultNode.ABORT:
                return IconConstants.STEP_NOT_OK_IMAGE;
            default:
                return imageToDecorate;
        }
        return imageToDecorate;
    }

    /**
     * {@inheritDoc}
     */
    public Image decorateImage(Image image, Object element) {
        if (((TestResultNode)element).
            getNode() instanceof IEventExecTestCasePO) {
            return image;
        }
        return doDecorate(element, image);
    }

    /**
     * {@inheritDoc}
     */
    public String decorateText(String text, Object element) {
        return null;
    }           
}