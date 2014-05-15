/**
 * Copyright 2005-2014 The Kuali Foundation
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
package edu.sampleu.kew;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class QuickLinksAft extends WebDriverLegacyITBase {

    /**
     * AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Quicklinks&channelUrl="
     * + WebDriverUtils.getBaseUrlString() + "/kew/QuickLinks.do";
     */
    public static final String BOOKMARK_URL =
            AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Quicklinks&channelUrl=" + WebDriverUtils
                    .getBaseUrlString() + "/kew/QuickLinks.do";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickMainMenu();
        waitAndClickByLinkText("Quicklinks");
    }

    protected void testQuickLinks() throws Exception {
        selectFrameIframePortlet();
        assertTextPresent(new String[] {"Quick Action List", "ENROUTE", "Route Log"});
    }

    @Test
    public void testQuickLinksBookmark() throws Exception {
        testQuickLinks();
        passed();
    }

    @Test
    public void testQuickLinksNav() throws Exception {
        testQuickLinks();
        passed();
    }
}