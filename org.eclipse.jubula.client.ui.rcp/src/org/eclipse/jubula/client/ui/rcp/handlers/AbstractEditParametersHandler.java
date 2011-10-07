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
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jubula.client.core.businessprocess.AbstractParamInterfaceBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.businessprocess.TestCaseParamBP;
import org.eclipse.jubula.client.core.model.IModifiableParameterInterfacePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.dialogs.AbstractEditParametersDialog.Parameter;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.Assert;
import org.eclipse.ui.IWorkbenchPart;


/**
 * @author BREDEX GmbH
 * @created Jul 14, 2010
 */
public abstract class AbstractEditParametersHandler extends AbstractHandler {
    
    /**
     * @return a valid AbstractGDEditor in the editable state; null if editing
     *         is not possible
     */
    protected AbstractJBEditor getEditorInEditableState() {
        final IWorkbenchPart activePart = Plugin.getActivePart();
        if (activePart == null) {
            return null;
        }
        final Object adapter = activePart.getAdapter(AbstractJBEditor.class);
        if (adapter != null) {
            final AbstractJBEditor editor = (AbstractJBEditor)adapter;
            final JBEditorHelper.EditableState state = editor.getEditorHelper()
                    .getEditableState();
            if (state == JBEditorHelper.EditableState.OK
                    || editor.getEditorHelper().requestEditableState() 
                    == JBEditorHelper.EditableState.OK) {
                return editor;
            }
        }
        return null;
    }
    
    
    /**
     * Gets the new index of the Parameter with the given paramDesc. 
     * @param paramDesc the paramDesc
     * @param paramList the List of Parameters
     * @return the zero based index or -1 if not found.
     */
    protected static int getNewParamIndex(IParamDescriptionPO paramDesc, 
            List<Parameter> paramList) {
        int index = 0;
        for (Parameter param : paramList) {
            if (param.getName().equals(paramDesc.getName())) {
                return index;
            }
            String paramGuid = param.getGuid();
            if ((paramGuid != null) 
                    && (paramGuid.equals(paramDesc.getUniqueId()))) {
                return index;
            }
            index++;
        }
        return -1;
    }
    
    /**
     * @param paramIntObj
     *            the {@link IModifiableParameterInterfacePO} which is to modify.
     * @param parameters
     *            the
     *            {@link org.eclipse.jubula.client.ui.rcp.dialogs.EditParametersDialog.Parameter}
     *            .
     * @param isInterfaceLocked
     *            the Lock Interface flag
     * @param mapper
     *            for management of param names
     * @param paramInterfaceBP
     *            the param interface business process to use for model changes
     * @return if occurs any modification of parameters
     */
    public static boolean editParameters(
            IModifiableParameterInterfacePO paramIntObj,
            List<Parameter> parameters, boolean isInterfaceLocked,
            ParamNameBPDecorator mapper, 
            AbstractParamInterfaceBP paramInterfaceBP) {

        Map<String, IParamDescriptionPO> oldParams = 
            new HashMap<String, IParamDescriptionPO>();
        List<IParamDescriptionPO> paramList = paramIntObj.getParameterList();
        for (IParamDescriptionPO oldDesc : paramList) {
            oldParams.put(oldDesc.getUniqueId(), oldDesc);
        }
        // find new parameters
        List<Parameter> paramsToAdd = new ArrayList<Parameter>();
        List<Parameter> params = new ArrayList<Parameter>(parameters);
        for (Parameter parameter : parameters) {
            if (parameter.getGuid() == null) {
                paramsToAdd.add(parameter);
                params.remove(parameter);
            }
        }
        // find renamed parameters
        Map<IParamDescriptionPO, String> paramsToRename = 
            new HashMap<IParamDescriptionPO, String>();
        for (Parameter param : params) {
            IParamDescriptionPO paramDescr = oldParams.get(param.getGuid());
            if (paramDescr != null) {
                if (!(paramDescr.getName().equals(param.getName()))) {
                    paramsToRename.put(paramDescr, param.getName());
                }
            } else {
                Assert.notReached(Messages.UnexpectedError 
                    + StringConstants.COLON + StringConstants.SPACE
                    + Messages.ModificationOfNonExistingParameter 
                    + StringConstants.DOT);
            }
        }
        // find parameters to remove
        List<String> oldGuids = new ArrayList<String>(oldParams.keySet());
        for (Parameter parameter : parameters) {
            oldGuids.remove(parameter.getGuid());

        }
        List<IParamDescriptionPO> paramsToRemove = 
            new ArrayList<IParamDescriptionPO>();
        for (String oldGuid : oldGuids) {
            paramsToRemove.add(oldParams.get(oldGuid));
        }
        boolean isInterfaceLockedChanged = false;
        if (paramIntObj instanceof ISpecTestCasePO) {
            ISpecTestCasePO specTc = (ISpecTestCasePO)paramIntObj;
            isInterfaceLockedChanged = !((specTc).isInterfaceLocked() 
                    == isInterfaceLocked);
            TestCaseParamBP.setInterfaceLocked(specTc, isInterfaceLocked);
        }
        // update model
        for (Parameter addParam : paramsToAdd) {
            paramInterfaceBP.addParameter(addParam.getName(), addParam
                    .getType(), paramIntObj, mapper);
        }
        for (IParamDescriptionPO desc : paramsToRemove) {
            final List<Locale> projLangs = GeneralStorage.getInstance()
                    .getProject().getLangHelper().getLanguageList();
            for (Locale locale : projLangs) {
                paramInterfaceBP.removeParameter(desc, paramIntObj, locale);
            }
        }
        for (IParamDescriptionPO desc : paramsToRename.keySet()) {
            paramInterfaceBP.renameParameters(desc, paramsToRename.get(desc),
                    mapper);
        }
        final boolean moved = moveParameters(paramIntObj, parameters);
        return !paramsToRemove.isEmpty() || !paramsToAdd.isEmpty()
                || !paramsToRename.isEmpty() || isInterfaceLockedChanged
                || moved;
    }

    /**
     * Moves the Parameters of the given IModifiableParameterInterfacePO into
     * the order of the given parameter List (parameters).<br>
     * <b>Note: Call this method after all other model changes!</b>
     * 
     * @param paramIntObj
     *            the IParameterInterfacePO.
     * @param parameters
     *            the Parameter List with the new order.
     * @return true if oe or more parameters were moved, false otherwise.
     */
    private static boolean moveParameters(
            IModifiableParameterInterfacePO paramIntObj,
            List<Parameter> parameters) {

        boolean moved = false;
        final List<IParamDescriptionPO> paramList = 
            new LinkedList<IParamDescriptionPO>(
                paramIntObj.getParameterList());
        for (IParamDescriptionPO paramDesc : paramList) {
            final int currIdx = paramList.indexOf(paramDesc);
            final int newIdx = getNewParamIndex(paramDesc, parameters);
            if (currIdx != newIdx) {
                paramIntObj.moveParameter(paramDesc.getUniqueId(), newIdx);
                moved = true;
            }
        }
        return moved;
    }
    
    /**
     * @param paramIntObj
     *            the {@link IModifiableParameterInterfacePO} which is to
     *            modify.
     * @param parameters
     *            the
     *            {@link org.eclipse.jubula.client.ui.rcp.dialogs.EditParametersDialog.Parameter}
     *            .
     * @param mapper
     *            for management of param names
     * @param paramInterfaceBP
     *            the param interface business process to use for model changes
     * @return if occurs any modification of parameters
     */
    public static boolean editParameters(
            IModifiableParameterInterfacePO paramIntObj,
            List<Parameter> parameters, ParamNameBPDecorator mapper,
            AbstractParamInterfaceBP paramInterfaceBP) {
        return editParameters(paramIntObj, parameters, false, mapper,
                paramInterfaceBP);
    }
}
