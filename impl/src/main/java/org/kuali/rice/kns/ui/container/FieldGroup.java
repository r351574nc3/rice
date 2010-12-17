/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.ui.container;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kns.ui.ComponentBase;
import org.kuali.rice.kns.web.ui.Field;

/**
 * This is a description of what this class does - jkneal don't forget to fill this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class FieldGroup extends ComponentBase {

	public FieldGroup() {

	}

	/**
	 * @see org.kuali.rice.krad.web.view.container.ContainerBase#getSupportedComponents()
	 */
	public List<Class> getSupportedComponents() {
		List<Class> supportedComponents = new ArrayList<Class>();
		supportedComponents.add(Field.class);

		return supportedComponents;
	}
}
