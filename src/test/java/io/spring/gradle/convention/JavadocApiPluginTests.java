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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Rob Winch
 */
public class JavadocApiPluginTests {
	Project rootProject;

	@After
	public void cleanup() throws Exception {
		if (rootProject != null) {
			FileUtils.deleteDirectory(rootProject.getProjectDir());
		}
	}

	@Test
	public void applyWhenNotOverrideThenPropertiesDefaulted() {
		rootProject = ProjectBuilder.builder().build();
		rootProject.getPlugins().apply(JavadocApiPlugin.class);

		Javadoc apiTask = (Javadoc) rootProject.getTasks().getByPath("api");

		assertThat(apiTask).isNotNull();
		assertThat(apiTask.getGroup()).isEqualTo("Documentation");
		assertThat(apiTask.getDescription()).isEqualTo("Generates aggregated Javadoc API documentation.");
		assertThat(apiTask.getMaxMemory()).isEqualTo("1024m");
		assertThat(apiTask.getDestinationDir()).isEqualTo(new File(rootProject.getBuildDir(), "api"));
	}

	@Test
	public void applyWhenOnlyRootProjectNoJavaPluginThenSourceEmpty() {
		rootProject = ProjectBuilder.builder().build();
		rootProject.getPlugins().apply(JavadocApiPlugin.class);

		Javadoc apiTask = (Javadoc) rootProject.getTasks().getByPath("api");
		assertThat(apiTask.getSource()).isEmpty();
	}

	@Ignore
	@Test
	public void applyWhenOnlyRootProjectThenSourcePopulated() throws Exception {
		rootProject = ProjectBuilder.builder().build();
		FileUtils.touch(rootProject.file("src/main/java/File.java"));
		rootProject.getPlugins().apply(JavadocApiPlugin.class);
		rootProject.getPlugins().apply(JavaPlugin.class);

		Javadoc apiTask = (Javadoc) rootProject.getTasks().getByPath("api");
		JavaPluginConvention java = rootProject.getConvention().getPlugin(JavaPluginConvention.class);
		SourceDirectorySet javaSourceSets = java.getSourceSets().getByName("main").getAllJava();

		assertThat(apiTask.getSource()).isNotEmpty();
		assertThat(apiTask.getSource().getFiles()).isEqualTo(javaSourceSets.getFiles());
	}

	@Test
	public void applyWhenChildProjectThenSourcePopulated() throws Exception {
		rootProject = ProjectBuilder.builder().build();

		Project api = ProjectBuilder.builder().withName("api").withParent(rootProject).build();

		Project impl = ProjectBuilder.builder().withName("impl").withParent(rootProject).build();

		rootProject.getPlugins().apply(JavadocApiPlugin.class);

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		for (Project p : rootProject.getSubprojects()) {
			File file = p.file("src/main/java/File.java");
			FileUtils.touch(file);
			InputStream input = loader.getResourceAsStream("File.java.txt");
			IOUtils.copy(input,
					new FileOutputStream(file));
			p.getPlugins().apply(JavaPlugin.class);
		}

		final Javadoc apiTask = (Javadoc) rootProject.getTasks().getByPath("api");
		SourceDirectorySet apiJavaSourceSets = getJavaSourceSets(api);
		SourceDirectorySet implJavaSourceSets = getJavaSourceSets(impl);

		for(Action<? super Task> a : apiTask.getActions()) {
			a.execute(apiTask);
		}

		assertThat(apiTask.getSource()).isNotEmpty();
		assertThat(apiTask.getSource().getFiles()).containsAll(apiJavaSourceSets.getFiles());
		assertThat(apiTask.getSource().getFiles()).containsAll(implJavaSourceSets.getFiles());
	}

	public void applyWhenChildNotJavaThenNoError() throws Exception {
		rootProject = ProjectBuilder.builder().build();

		Project api = ProjectBuilder.builder().withName("api").withParent(rootProject).build();

		Project doc = ProjectBuilder.builder().withName("doc").withParent(rootProject).build();

		rootProject.getPlugins().apply(JavadocApiPlugin.class);

		FileUtils.touch(api.file("src/main/java/File.java"));
		api.getPlugins().apply(JavaPlugin.class);

		Javadoc apiTask = (Javadoc) rootProject.getTasks().getByPath("api");
		SourceDirectorySet apiJavaSourceSets = getJavaSourceSets(api);

		assertThat(apiTask.getSource()).isNotEmpty();
		assertThat(apiTask.getSource().getFiles()).containsAll(apiJavaSourceSets.getFiles());
	}

	public void applyWhenRootProjectJavaThenRootNotPopulated() throws Exception {
		rootProject = ProjectBuilder.builder().build();

		Project api = ProjectBuilder.builder().withName("api").withParent(rootProject).build();

		rootProject.getPlugins().apply(JavadocApiPlugin.class);

		FileUtils.touch(rootProject.file("src/main/java/File.java"));
		rootProject.getPlugins().apply(JavaPlugin.class);

		FileUtils.touch(api.file("src/main/java/File.java"));
		api.getPlugins().apply(JavaPlugin.class);

		Javadoc apiTask = (Javadoc) rootProject.getTasks().getByPath("api");
		SourceDirectorySet apiJavaSourceSets = getJavaSourceSets(api);

		assertThat(apiTask.getSource()).isNotEmpty();
		assertThat(apiTask.getSource().getFiles()).containsExactlyElementsOf(apiJavaSourceSets.getFiles());
	}

	private SourceDirectorySet getJavaSourceSets(Project p) {
		JavaPluginConvention java = p.getConvention().getPlugin(JavaPluginConvention.class);
		return java.getSourceSets().getByName("main").getAllJava();
	}
}
