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
package org.kuali.rice.krad.uif.util;

import java.io.Serializable;
import java.util.Map;
import java.util.Queue;

import org.kuali.rice.krad.datadictionary.Copyable;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.ComponentDefaultInitializeTask;

/**
 * Interface to be implemented by objects that participates in the view lifecycle.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface LifecycleElement extends Serializable, Copyable {

    /**
     * The unique id (within a given tree) for the element.
     *
     * <p>The id is used to identify an element instance within the tree, and
     * will be used by renderers to set the HTML element id. This gives a way to find various elements
     * for scripting. If the id is not given, a default will be generated by the framework.
     * </p>
     *
     * <p></p>
     *
     * @return A unique ID for this lifecycle element.
     */
    String getId();

    /**
     * Setter for the unique id (within a given tree) for the component
     *
     * @param id - string to set as the component id
     */
    void setId(String id);
    
    /**
     * Gets a property for referring to this component from the view, relative to the view, as
     * assigned by the current or most recent lifecycle.
     * 
     * @return property path
     */
    String getViewPath();

    /**
     * Setter for {@link #getViewPath()}.
     * 
     * @param viewPath The property path.
     */
    void setViewPath(String viewPath);
    
    /**
     * Gets the path relative the preceding state's component.
     * 
     * @return path relative the preceding state's component
     */
    String getPath();

    /**
     * Setter for {@link #getPath()}.
     * 
     * @param path The property path.
     */
    void setPath(String path);
    
    /**
     * Determine if this lifecycle element is mutable.
     * 
     * <p>
     * Most lifecycle element are immutable, and all are immutable expect during initialization and
     * the during the view lifecycle. Those that have been copied within the view lifecycle,
     * however, may be modified during the same lifecycle.
     * </p>
     * @param legalBeforeConfiguration true if the current operation may be called before the
     *        lifecycle element has been cached, for example while being initialized as part of a
     *        Spring context.
     * 
     * @return True if the component is mutable.
     */
    boolean isMutable(boolean legalBeforeConfiguration);

    /**
     * Check for mutability on the element before modifying state.
     *
     * @param legalDuringInitialization True if the operation is legal during view initialization,
     *        false if the operation is only allowed during the component lifecycle.
     * @throws IllegalStateException If the component is not mutable and the lifecycle is operating
     *         in strict mode.
     * @see ViewLifecycle#isStrict()
     */
    void checkMutable(boolean legalDuringInitialization);

    /**
     * Initialize the lifecycle task queue for custom tasks specific to this component on the given
     * lifecycle phase.
     * 
     * <p>
     * Any tasks added to the queue by this method will be performed after the default lifecycle
     * phase processing method {@link #performInitialization(Object)},
     * {@link #performApplyModel(Object, LifecycleElement)}, or
     * {@link #performFinalize(Object, LifecycleElement)} has been called.
     * </p>
     * 
     * @param phase The lifecycle phase to queue pending tasks for.
     * @param pendingTasks The pending task queue.
     */
    void initializePendingTasks(ViewLifecyclePhase phase, Queue<ViewLifecycleTask<?>> pendingTasks);
    
    /**
     * Get the view lifecycle processing status for this component.
     * 
     * @return The view lifecycle processing status for this component.
     * @see org.kuali.rice.krad.uif.UifConstants.ViewStatus
     */
    String getViewStatus();

    /**
     * Set the view lifecycle processing status for this component.
     * 
     * @param phase The phase that has just finished processing the component.
     */
    void setViewStatus(ViewLifecyclePhase phase);
    
    /**
     * Indicates whether the component has been initialized.
     *
     * @return True if the component has been initialized, false if not.
     */
    boolean isInitialized();

    /**
     * Indicates whether the component has been updated from the model.
     *
     * @return True if the component has been updated, false if not.
     */
    boolean isModelApplied();

    /**
     * Indicates whether the component has been updated from the model and final
     * updates made.
     *
     * @return True if the component has been updated, false if not.
     */
    boolean isFinal();

    /**
     * Receive notification that a lifecycle phase, and all successor phases, have been completed on
     * this component.
     * @param phase The completed view lifecycle phase
     */
    void notifyCompleted(ViewLifecyclePhase phase);

    /**
     * Places the given object into the context Map for the component with the given name
     * 
     * <p>
     * Note this also will push context to property replacers configured on the component. To place
     * multiple objects in the context, you should use #pushAllToContext since that will call this
     * method for each and update property replacers. Using {@link Component#getContext()}{@link
     * Map#putAll(Map) .putAll()} will bypass property replacers.
     * </p>
     * 
     * @param objectName - name the object should be exposed under in the context map
     * @param object - object instance to place into context
     */
    void pushObjectToContext(String objectName, Object object);

    /**
     * Places each entry of the given Map into the context for the component
     *
     * <p>
     * Note this will call #pushObjectToContext for each entry which will update any configured property
     * replacers as well. This should be used in place of getContext().putAll()
     * </p>
     *
     * @param objects - Map<String, Object> objects to add to context, where the entry key will be the context key
     * and the entry value will be the context value
     */
    void pushAllToContext(Map<String, Object> objects);

    /**
     * Initializes the component
     *
     * <p>
     * Where components can set defaults and setup other necessary state. The initialize method
     * should only be called once per component lifecycle and is invoked within the initialize phase
     * of the view lifecylce.
     * </p>
     *
     * @param model - object instance containing the view data
     * @see ComponentDefaultInitializeTask
     * @deprecated Special processing within this method should be replaced by
     *             {@link ViewLifecycleTask} and initialized by
     *             {@link #initializePendingTasks(ViewLifecyclePhase, Queue)}.
     */
    @Deprecated
    void performInitialization(Object model);

    /**
     * Called after the initialize phase to perform conditional logic based on the model data
     *
     * <p>
     * Where components can perform conditional logic such as dynamically generating new fields or setting field state
     * based on the given data
     * </p>
     *
     * @param model - Top level object containing the data (could be the form or a
     * top level business object, dto)
     * @param parent parent lifecycle element
     * @deprecated Special processing within this method should be replaced by
     *             {@link ViewLifecycleTask} and initialized by
     *             {@link #initializePendingTasks(ViewLifecyclePhase, Queue)}.
     */
    @Deprecated
    void performApplyModel(Object model, LifecycleElement parent);

    /**
     * The last phase before the view is rendered
     *
     * <p>
     * Here final preparations can be made based on the updated view state.
     * </p>
     *
     * @param model - top level object containing the data
     * @param parent - parent component
     * @deprecated Special processing within this method should be replaced by
     *             {@link ViewLifecycleTask} and initialized by
     *             {@link #initializePendingTasks(ViewLifecyclePhase, Queue)}.
     */
    @Deprecated
    void performFinalize(Object model, LifecycleElement parent);

}
