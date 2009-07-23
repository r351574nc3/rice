/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kew.plugin;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.kew.util.Utilities;


/**
 * A factory for creating {@link PluginRegistry} instances based on the configured client protocol of the application.
 * 
 * @see PluginRegistry
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PluginRegistryFactory {

    public PluginRegistry createPluginRegistry() {

        ServerPluginRegistry registry = new ServerPluginRegistry();
        String pluginDir = ConfigContext.getCurrentContextConfig().getProperty(Config.PLUGIN_DIR);
        List<String> pluginDirectories = new ArrayList<String>();
        // TODO: maybe ensure that if these directories are the same, that
        // only one gets through
        if (!Utilities.isEmpty(pluginDir)) {
            pluginDirectories.add(pluginDir);
        }
        registry.setPluginDirectories(pluginDirectories);
        return registry;

    }

}
