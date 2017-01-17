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

import java.io.File;
import java.util.Set;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rob Winch
 */
public class JavadocApiPlugin implements Plugin<Project> {
	Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void apply(Project project) {
		logger.info("Applied");
		Javadoc api = project.getTasks().create("api", Javadoc.class);

		api.setGroup("Documentation");
		api.setDescription("Generates aggregated Javadoc API documentation.");

		Set<Project> subprojects = project.getRootProject().getSubprojects();
		for (Project subproject : subprojects) {
			addJavaSourceSet(api, subproject);
		}

		if (subprojects.isEmpty()) {
			addJavaSourceSet(api, project);
		}

		api.setMaxMemory("1024m");
		api.setDestinationDir(new File(project.getBuildDir(), "api"));
	}

	private void addJavaSourceSet(final Javadoc api, final Project project) {
		logger.info("Try add sources for {}", project);
		project.getPlugins().withType(JavaPlugin.class).all(new Action<JavaPlugin>() {
			@Override
			public void execute(JavaPlugin plugin) {
				logger.info("Added sources for {}", project);

				JavaPluginConvention java = project.getConvention().getPlugin(JavaPluginConvention.class);
				SourceSet mainSourceSet = java.getSourceSets().getByName("main");

				api.setSource(api.getSource().plus(mainSourceSet.getAllJava()));
			}
		});
	}
}
