/*
 * Copyright 2016-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.spring.gradle.convention;

import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RepositoryConventionPlugin}.
 */
public class RepositoryConventionPluginTests {

	private Project project = ProjectBuilder.builder().build();

	@Before
	public void setUp() {
		this.project.getProperties().clear();
	}

	@Test
	public void applyWhenIsReleaseThenShouldIncludeReleaseRepo() {
		this.project.setVersion("1.0.0.RELEASE");
		this.project.getPluginManager().apply(RepositoryConventionPlugin.class);

		RepositoryHandler repositories = this.project.getRepositories();
		assertRepositoriesWithSpring(repositories, "release");
	}

	private void assertRepositoriesWithSpring(RepositoryHandler repositories, String repositoryName) {
		assertThat(repositories).hasSize(3);
		assertThat(((MavenArtifactRepository) repositories.get(0)).getUrl().toString())
				.isEqualTo("https://repo.maven.apache.org/maven2/");
		assertThat(((MavenArtifactRepository) repositories.get(1)).getUrl().toString())
				.isEqualTo("https://jcenter.bintray.com/");
		assertThat(((MavenArtifactRepository) repositories.get(2)).getUrl().toString())
				.isEqualTo("https://repo.spring.io/" + repositoryName + "/");
	}

	private void assertRepositoriesWithLocalAndSpring(RepositoryHandler repositories, String repositoryName) {
		assertThat(repositories).hasSize(4);
		assertThat((repositories.get(0)).getName()).isEqualTo("MavenLocal");
	}

	@Test
	public void applyWhenIsMilestoneThenShouldIncludeMilestoneRepo() {
		this.project.setVersion("1.0.0.M1");
		this.project.getPluginManager().apply(RepositoryConventionPlugin.class);

		RepositoryHandler repositories = this.project.getRepositories();
		assertRepositoriesWithSpring(repositories, "milestone");
	}

	@Test
	public void applyWhenIsSnapshotThenShouldIncludeSnapshotRepo() {
		this.project.setVersion("1.0.0.BUILD-SNAPSHOT");
		this.project.getPluginManager().apply(RepositoryConventionPlugin.class);

		RepositoryHandler repositories = this.project.getRepositories();
		assertRepositoriesWithSpring(repositories, "snapshot");
	}

	@Test
	public void applyWhenIsSnapshotWithForceReleaseThenShouldOnlyIncludeReleaseRepo() {
		this.project.getExtensions().getByType(ExtraPropertiesExtension.class)
				.set("forceMavenRepositories", "release");
		this.project.setVersion("1.0.0.RELEASE");
		this.project.getPluginManager().apply(RepositoryConventionPlugin.class);

		RepositoryHandler repositories = this.project.getRepositories();
		assertRepositoriesWithSpring(repositories, "release");
	}

	@Test
	public void applyWhenIsReleaseWithForceMilestoneThenShouldIncludeMilestoneRepo() {
		this.project.getExtensions().getByType(ExtraPropertiesExtension.class)
				.set("forceMavenRepositories", "milestone");
		this.project.setVersion("1.0.0.RELEASE");
		this.project.getPluginManager().apply(RepositoryConventionPlugin.class);

		RepositoryHandler repositories = this.project.getRepositories();
		assertRepositoriesWithSpring(repositories, "milestone");
	}

	@Test
	public void applyWhenIsReleaseWithForceSnapshotThenShouldIncludeSnapshotRepo() {
		this.project.getExtensions().getByType(ExtraPropertiesExtension.class)
				.set("forceMavenRepositories", "snapshot");
		this.project.setVersion("1.0.0.RELEASE");
		this.project.getPluginManager().apply(RepositoryConventionPlugin.class);

		RepositoryHandler repositories = this.project.getRepositories();
		assertRepositoriesWithSpring(repositories, "snapshot");
	}

	@Test
	public void applyWhenIsReleaseWithForceLocalThenShouldIncludeReleaseAndLocalRepos() {
		this.project.getExtensions().getByType(ExtraPropertiesExtension.class)
				.set("forceMavenRepositories", "local");
		this.project.setVersion("1.0.0.RELEASE");
		this.project.getPluginManager().apply(RepositoryConventionPlugin.class);

		RepositoryHandler repositories = this.project.getRepositories();
		assertRepositoriesWithLocalAndSpring(repositories, "release");
	}

	@Test
	public void applyWhenIsReleaseWithForceMilestoneAndLocalThenShouldIncludeMilestoneAndLocalRepos() {
		this.project.getExtensions().getByType(ExtraPropertiesExtension.class)
				.set("forceMavenRepositories", "milestone,local");
		this.project.setVersion("1.0.0.RELEASE");
		this.project.getPluginManager().apply(RepositoryConventionPlugin.class);

		RepositoryHandler repositories = this.project.getRepositories();
		assertRepositoriesWithLocalAndSpring(repositories, "milestone");
	}

}
