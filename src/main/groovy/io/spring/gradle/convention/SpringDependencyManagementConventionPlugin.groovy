package io.spring.gradle.convention

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * If the property springIoVersion is found will automatically apply the Spring IO Platform BOM. If
 * gradle/dependency-management.gradle is found, will automatically apply this file for configuring the dependencies.
 */
public class SpringDependencyManagementConventionPlugin implements Plugin<Project> {
	static final String DEPENDENCY_MANAGEMENT_RESOURCE = "gradle/dependency-management.gradle"

	@Override
	public void apply(Project project) {
		project.getPluginManager().apply(DependencyManagementPlugin)

		if(project.hasProperty("springIoVersion")) {
			project.dependencyManagement {
				imports {
					mavenBom("io.spring.platform:platform-bom:${project.springIoVersion}")
				}
			}
		}

		applyDependencyManagementWith(project, project.rootProject.file(DEPENDENCY_MANAGEMENT_RESOURCE))
		applyDependencyManagementWith(project, project.file(DEPENDENCY_MANAGEMENT_RESOURCE))
	}

	public void applyDependencyManagementWith(Project project, File dependencyManagementFile) {
		if(!dependencyManagementFile.exists()) {
			return
		}

		project.apply from: dependencyManagementFile.absolutePath
	}
}