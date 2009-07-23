/*
 * Copyright 2005-2009 The Kuali Foundation
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
package org.kuali.rice.kew.dto;


/**
 * Transport object for the AuthenticationUserId
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NetworkIdDTO extends UserIdDTO {

	private static final long serialVersionUID = -5262644133374062911L;
	public NetworkIdDTO() {}
    
    public NetworkIdDTO(String networkId) {
        super(networkId);
    }
    
    public String getNetworkId() {
        return getId();
    }
    public void setNetworkId(String networkId) {
        setId(networkId);
    }
}
