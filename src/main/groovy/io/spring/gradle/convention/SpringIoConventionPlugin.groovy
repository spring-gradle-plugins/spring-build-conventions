package io.spring.gradle.convention

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import io.spring.gradle.dependencymanagement.dsl.GeneratedPomCustomizationHandler
import io.spring.gradle.propdeps.PropDepsPlugin
import io.spring.gradle.springio.SpringIoPlugin
import org.gradle.api.Action
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * This will add the Spring IO Plugin if it sees either springIoVersion or platformVersion property defined. If platformVersion
 * is specified and the project is missing a snapshot or milestone repository the repository is automatically added.
 * The springIoVersion does not automatically add the property since it is typically included in the project vs
 * externalized.
 */
public class SpringIoConventionPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.ext['JDK8_HOME'] = System.getProperty("java.home");
		project.getPluginManager().apply(SpringIoPlugin)

		project.plugins.withType(SpringIoPlugin) {
			project.configurations.withType(Object.class) { c->
				if(c.getName() == 'springIoTestRuntime') {
					applySpringIoConfiguration(project)
				}
			}
		}
	}

	private void applySpringIoConfiguration(Project project) {
		String platformBomVersion = project.findProperty('springIoVersion')
		if(project.hasProperty('platformVersion')) {
			platformBomVersion = project.findProperty("platformVersion");
		}
		if(platformBomVersion == null) {
			return
		}
		addMissingSpringRepository(project, project.findProperty("platformVersion"))
		project.dependencyManagement {
			springIoTestRuntime {
				imports {
					mavenBom("io.spring.platform:platform-bom:${platformBomVersion}")
				}
			}
		}
		project.tasks.create("dependencyManagementExport", DependencyManagementExportTask);

		project.plugins.withType(PropDepsPlugin) {
			project.configurations {
				springIoTestRuntime {
					extendsFrom optional, provided
				}
			}
		}
	}

	private void addMissingSpringRepository(Project project, String platformBomVersion) {
		if(!platformBomVersion) {
			return
		}
		boolean isRelease = platformBomVersion.endsWith('.RELEASE') || platformBomVersion.matches('^.*?\\.SR\\d+$')
		boolean isSnapshot = platformBomVersion.endsWith('-SNAPSHOT')
		boolean isMilestone = !(isRelease || isSnapshot)
		if(isSnapshot) {
			addMavenRepositoryIfMissing(project,'https://repo.spring.io/libs-snapshot')
		}
		if(isMilestone) {
			addMavenRepositoryIfMissing(project,'https://repo.spring.io/libs-milestone')
		}
	}

	private void addMavenRepositoryIfMissing(Project project, String repositoryUrl) {
		boolean found = project.repositories.findAll { it instanceof MavenArtifactRepository }.collect { it.url.toString() }.find { it.startsWith(repositoryUrl)}
		if(found) {
			return
		}
		project.repositories {
			maven { url repositoryUrl }
		}
	}
}