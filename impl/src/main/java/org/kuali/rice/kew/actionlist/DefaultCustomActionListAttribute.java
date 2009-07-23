/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.actionlist;

import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actions.ActionSet;
import org.kuali.rice.kew.web.session.UserSession;



/**
 * The default implementation of a CustomActionListAttribute.  Shows only FYI actions.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DefaultCustomActionListAttribute implements CustomActionListAttribute {

	private static final long serialVersionUID = 6776164670024486696L;

	/**
	 * Sets up the default ActionSet which includes only FYIs.
	 */
	private static ActionSet DEFAULT_LEGAL_ACTIONS = new ActionSet();
	static {
		DEFAULT_LEGAL_ACTIONS.addFyi();
	}
	
	public DefaultCustomActionListAttribute() {}
    
    public ActionSet getLegalActions(UserSession userSession, ActionItem actionItem) throws Exception {
    	return DEFAULT_LEGAL_ACTIONS;
	}
    
    public DisplayParameters getDocHandlerDisplayParameters(UserSession userSession, ActionItem actionItem) throws Exception {
		return null;
	}
    
}
