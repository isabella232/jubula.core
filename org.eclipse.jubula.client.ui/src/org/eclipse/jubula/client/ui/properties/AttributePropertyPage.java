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
package org.eclipse.jubula.client.ui.properties;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jubula.client.core.model.IDocAttributeDescriptionPO;
import org.eclipse.jubula.client.core.model.IDocAttributePO;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.ui.attribute.IAttributeRenderer;
import org.eclipse.jubula.client.ui.factory.AttributeRendererFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


/**
 * @author BREDEX GmbH
 * @created 07.05.2008
 */
public class AttributePropertyPage extends AbstractProjectPropertyPage {

    /**
     * @param es The edit support
     */
    public AttributePropertyPage(EditSupport es) {
        super(es);
        noDefaultAndApplyButton();
    }

    /**
     * {@inheritDoc}
     */
    protected Control createContents(Composite parent) {
        parent.setLayoutData((new GridData(GridData.FILL_BOTH)));
        return createPropertyDisplay(parent);
    }

    /**
     * Creates the display of the used toolkits
     * @param parent the parent composite
     * @return the given composite with the new display.
     */
    private Composite createPropertyDisplay(Composite parent) {

        // we use a LinkedHashMap here because ordering is important
        Map<IDocAttributePO, IAttributeRenderer> renderMap = 
            new LinkedHashMap<IDocAttributePO, IAttributeRenderer>();

        // we use the List in order to insure that ordering is maintained
        for (IDocAttributeDescriptionPO attrDesc 
                : getProject().getProjectAttributeDescriptions()) {

            IDocAttributePO attr = getProject().getDocAttribute(attrDesc);
            // only try to render the attribute if it exists
            if (attr != null) {
                IAttributeRenderer renderer = 
                    AttributeRendererFactory.getRenderer(attrDesc);
                renderer.init(attrDesc, attr);
                renderMap.put(attr, renderer);
            }
        }
        
        for (IDocAttributePO attr : renderMap.keySet()) {
            IAttributeRenderer render = renderMap.get(attr);
            Composite composite = new Composite(parent, SWT.NONE);
            composite.setLayout(new GridLayout(1, false));
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));
            render.renderAttribute(composite);
        }
        
        return parent;
    }
}
