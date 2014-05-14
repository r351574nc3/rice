/*
 * Copyright 2007-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.service.impl;

import java.util.List;

import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.ParameterEvaluator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.RiceKeyConstants;

public class ParameterEvaluatorImpl implements ParameterEvaluator {
	private static final long serialVersionUID = -758645169354452022L;
	private Parameter parameter;
	private boolean constraintIsAllow;
	private String constrainedValue;
	private List<String> values;

	private static DataDictionaryService dataDictionaryService;
	
	/**
	 * If the constraint is allow and the constrainedValue is in the list of
	 * allowed values specified by the parameter this will return true, and if
	 * the constraint is deny and the constrainedValue is not in the list of
	 * denied values specified by the parameter this method will return true.
	 * 
	 * @return boolean indicating whether the constrained value adheres to the
	 *         restriction specified by the combination of the parameter
	 *         constraint and the parameter value
	 */
	public boolean evaluationSucceeds() {
		if (constraintIsAllow()) {
			return values.contains(constrainedValue);
		} else {
			return !values.contains(constrainedValue);
		}
	}

	/**
	 * @see org.kuali.kfs.sys.service.ParameterEvaluator#evaluateAndAddError(java.lang.Class
	 *      businessObjectOrDocumentClass, java.lang.String
	 *      constrainedPropertyName)
	 */
	public boolean evaluateAndAddError(Class<? extends Object> businessObjectOrDocumentClass, String constrainedPropertyName) {
		return evaluateAndAddError(businessObjectOrDocumentClass, constrainedPropertyName, constrainedPropertyName);
	}

	/**
	 * This method uses the evaluationSucceeds method to evaluate the
	 * constrainedValue. If evaluation does not succeed, it adds an error to
	 * GlobalVariables.getErrorMap(). The businessObjectOrDocumentClass,
	 * nameOfConstrainedProperty and userEditablePropertyName are used to
	 * retrieve the appropriate labels from the DataDictionary.
	 * 
	 * @param businessObjectOrDocumentClass
	 * @param userEditableFieldToHighlight
	 * @param nameOfconstrainedProperty
	 * @return boolean indicating whether evaluation succeeded (see
	 *         evaluationSucceeds)
	 */
	public boolean evaluateAndAddError(Class<? extends Object> businessObjectOrDocumentClass,
			String constrainedPropertyName, String userEditablePropertyName) {
		if (!evaluationSucceeds()) {
			GlobalVariables.getMessageMap().putError(
					userEditablePropertyName,
					constraintIsAllow() ? RiceKeyConstants.ERROR_DOCUMENT_INVALID_VALUE_ALLOWED_VALUES_PARAMETER : RiceKeyConstants.ERROR_DOCUMENT_INVALID_VALUE_DENIED_VALUES_PARAMETER,
					new String[] {
							getDataDictionaryService().getAttributeLabel( businessObjectOrDocumentClass, constrainedPropertyName),
							constrainedValue,
							toStringForMessage(),
							getParameterValuesForMessage(),
							getDataDictionaryService().getAttributeLabel( businessObjectOrDocumentClass, userEditablePropertyName) 
							} );
			return false;
		}
		return true;
	}

	/**
	 * @see org.kuali.kfs.sys.service.ParameterEvaluator#constraintIsAllow()
	 */
	public boolean constraintIsAllow() {
		return constraintIsAllow;
	}

	/**
	 * This method uses the List toString method and eliminates the [].
	 * 
	 * @return user-friendly String representation of Parameter values
	 */
	public String getParameterValuesForMessage() {
		return values.toString().replace("[", "").replace("]", "");
	}

	/**
	 * @see org.kuali.kfs.sys.service.ParameterEvaluator#getValue()
	 */
	public String getValue() {
		return parameter.getParameterValue();
	}

	public String toString() {
		return new StringBuffer("ParameterEvaluator").append("\n\tParameter: ")
				.append("module=").append(parameter.getParameterNamespaceCode())
				.append(", component=").append(parameter.getParameterDetailTypeCode())
				.append(", name=").append(parameter.getParameterName())
				.append(", value=").append(parameter.getParameterValue())
				.append("\n\tConstraint Is Allow: ").append(constraintIsAllow)
				.append("\n\tConstrained Value: ").append(constrainedValue)
				.append("\n\tValues: ").append(values.toString())
				.toString();
	}

	private String toStringForMessage() {
		return new StringBuffer("parameter: ").append(parameter.getParameterName())
				.append(", module: ").append(parameter.getParameterNamespaceCode())
				.append(", component: ").append(parameter.getParameterDetailTypeCode())
				.toString();
	}

	public String getModuleAndComponent() {
		return parameter.getParameterNamespaceCode() + ": " + parameter.getParameterDetailTypeCode();
	}

	public void setConstrainedValue(String constrainedValue) {
		this.constrainedValue = constrainedValue;
	}

	public void setConstraintIsAllow(boolean constraintIsAllow) {
		this.constraintIsAllow = constraintIsAllow;
	}

	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	/**
	 * @return the dataDictionaryService
	 */
	protected DataDictionaryService getDataDictionaryService() {
		if ( dataDictionaryService == null ) {
			dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
		}
		return dataDictionaryService;
	}
}