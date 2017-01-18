/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.spring.gradle.convention;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginManager;
import org.gradle.plugins.ide.eclipse.EclipseWtpPlugin;
import org.gradle.plugins.ide.idea.IdeaPlugin;
import org.springframework.build.gradle.propdep.PropDepsEclipsePlugin;
import org.springframework.build.gradle.propdep.PropDepsIdeaPlugin;
import org.springframework.build.gradle.propdep.PropDepsPlugin;

/**
 * @author Rob Winch
 */
public class SpringModulePlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		PluginManager pluginManager = project.getPluginManager();
		pluginManager.apply(JavaPlugin.class);
		pluginManager.apply(EclipseWtpPlugin.class);
		pluginManager.apply(IdeaPlugin.class);
		pluginManager.apply(PropDepsPlugin.class);
		pluginManager.apply(PropDepsEclipsePlugin.class);
		pluginManager.apply(PropDepsIdeaPlugin.class);
		pluginManager.apply(SpringMavenPlugin.class);
		pluginManager.apply("io.spring.convention.springio");
	}
}
