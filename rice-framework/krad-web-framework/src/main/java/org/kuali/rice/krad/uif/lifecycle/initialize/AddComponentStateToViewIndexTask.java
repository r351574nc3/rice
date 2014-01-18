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
package org.kuali.rice.krad.uif.lifecycle.initialize;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.LifecycleElementState;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;
import org.kuali.rice.krad.uif.view.View;

/**
 * Add the component's initial state to the view index.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AddComponentStateToViewIndexTask extends ViewLifecycleTaskBase<Component> {

    /**
     * Constructor.
     * 
     * @param phase The initialize phase for the component.
     */
    public AddComponentStateToViewIndexTask(ViewLifecyclePhase phase) {
        super(phase, Component.class);
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase#performLifecycleTask()
     */
    @Override
    protected void performLifecycleTask() {
        LifecycleElementState elementState = getElementState();
        View view = ViewLifecycle.getView();
        if (!(elementState.getElement() instanceof View)) {
            view.getViewIndex().addInitialComponentStateIfNeeded(
                    (Component) elementState.getElement());
        }
    }

}
