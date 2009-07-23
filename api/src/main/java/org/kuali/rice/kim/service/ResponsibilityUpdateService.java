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
package org.kuali.rice.kim.service;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.kuali.rice.kim.bo.types.dto.AttributeSet;


/**
 * This service provides operations for creating and updating responsibilities. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@WebService(name = "ResponsibilityUpdateService", targetNamespace = "http://org.kuali.rice/kim/responsibilityUpdate")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ResponsibilityUpdateService {

	void saveResponsibility( @WebParam(name="responsibilityId") String responsibilityId, 
							 @WebParam(name="responsibilityTemplateId") String responsibilityTemplateId, 
							 @WebParam(name="namespaceCode") String namespaceCode,
							 @WebParam(name="name") String name,
							 @WebParam(name="description") String description,
							 @WebParam(name="active") boolean active,
							 @WebParam(name="responsibilityDetails") AttributeSet responsibilityDetails );
}
