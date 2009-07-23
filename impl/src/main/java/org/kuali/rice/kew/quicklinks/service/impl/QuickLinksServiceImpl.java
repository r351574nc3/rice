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
package org.kuali.rice.kew.quicklinks.service.impl;

import java.util.List;

import org.kuali.rice.kew.quicklinks.ActionListStats;
import org.kuali.rice.kew.quicklinks.InitiatedDocumentType;
import org.kuali.rice.kew.quicklinks.WatchedDocument;
import org.kuali.rice.kew.quicklinks.dao.QuickLinksDAO;
import org.kuali.rice.kew.quicklinks.service.QuickLinksService;
import org.kuali.rice.kew.web.KeyValue;


public class QuickLinksServiceImpl implements QuickLinksService {

    private QuickLinksDAO quickLinksDAO;

    public List<ActionListStats> getActionListStats(String principalId) {
        return getQuickLinksDAO().getActionListStats(principalId);
    }

    public List<InitiatedDocumentType> getInitiatedDocumentTypesList(String principalId) {
        return getQuickLinksDAO().getInitiatedDocumentTypesList(principalId);
    }

    public List<KeyValue> getNamedSearches(String principalId) {
        return getQuickLinksDAO().getNamedSearches(principalId);
    }

    public List<KeyValue> getRecentSearches(String principalId) {
        return getQuickLinksDAO().getRecentSearches(principalId);
    }

    public List<WatchedDocument> getWatchedDocuments(String principalId) {
        return getQuickLinksDAO().getWatchedDocuments(principalId);
    }

    // BELOW ARE SPRING MANAGED PROPERTIES OF THIS BEAN
    public QuickLinksDAO getQuickLinksDAO() {
        return quickLinksDAO;
    }
    public void setQuickLinksDAO(QuickLinksDAO quickLinksDAO) {
        this.quickLinksDAO = quickLinksDAO;
    }

}
