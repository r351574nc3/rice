/**
 * Copyright 2005-2012 The Kuali Foundation
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
package edu.sampleu.travel.service;

import edu.sampleu.travel.bo.TravelAccount;
import edu.sampleu.travel.dto.TravelAccountInfo;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kns.inquiry.KualiInquirableImpl;

import java.util.Map;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TravelAccountInfoInquiryViewHelperServiceImpl extends KualiInquirableImpl {

    @Override
    public TravelAccountInfo retrieveDataObject(Map fieldValues) {
        TravelAccountService service = GlobalResourceLoader.getService("travelAccountService");

        return service.retrieveTravelAccount((String) fieldValues.get("number"));
    }
}
