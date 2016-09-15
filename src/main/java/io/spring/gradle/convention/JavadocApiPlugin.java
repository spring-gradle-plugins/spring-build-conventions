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

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.javadoc.Javadoc;

/**
 * @author Rob Winch
 */
public class JavadocApiPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		Javadoc javadoc = project.getTasks().create("api", Javadoc.class);

		javadoc.setGroup("Documentation");
		javadoc.setDescription("Generates aggregated Javadoc API documentation.");

		for (Project subproject : project.getSubprojects()) {
			addJavaSourceSet(javadoc, subproject);
		}

		if (project.getSubprojects().isEmpty()) {
			addJavaSourceSet(javadoc, project);
		}

		javadoc.setMaxMemory("1024m");
		javadoc.setDestinationDir(new File(project.getBuildDir(), "api"));
	}

	private void addJavaSourceSet(final Javadoc javadoc, final Project project) {
		project.getPlugins().withType(JavaPlugin.class).all(new Action<JavaPlugin>() {
			@Override
			public void execute(JavaPlugin plugin) {
				JavaPluginConvention java = project.getConvention().getPlugin(JavaPluginConvention.class);
				SourceSet mainSourceSet = java.getSourceSets().getByName("main");
				javadoc.setSource(javadoc.getSource().plus(mainSourceSet.getAllJava()));
			}
		});
	}
}
