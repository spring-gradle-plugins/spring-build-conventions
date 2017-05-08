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

		File rootDir = project.rootDir
		List<File> dependencyManagementFiles = [project.rootProject.file(DEPENDENCY_MANAGEMENT_RESOURCE)]
		for(File dir = project.projectDir;dir != rootDir;dir = dir.parentFile) {
			dependencyManagementFiles.add(new File(dir, DEPENDENCY_MANAGEMENT_RESOURCE))
		}

		dependencyManagementFiles.each { f->
			applyDependencyManagementWith(project, f)
		}
	}

	public void applyDependencyManagementWith(Project project, File dependencyManagementFile) {
		if(!dependencyManagementFile.exists()) {
			return
		}

		project.apply from: dependencyManagementFile.absolutePath
	}
}