package io.spring.gradle.convention

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin

import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.Plugin
import org.gradle.api.Project

public class SpringDependencyManagementConventionPlugin implements Plugin<Project> {
	static final String DEPENDENCY_MANAGEMENT_RESOURCE = "gradle/dependency-management.properties"

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
			return;
		}

		Properties dependencyMap = new Properties();
		dependencyMap.load(new FileInputStream(dependencyManagementFile));

		project.dependencyManagement {
			dependencies {
				for(Map.Entry<Object,Object> entry : dependencyMap.entrySet()) {
					String managedDependency = entry.getKey() + ":" + entry.getValue();
					dependency managedDependency
				}
			}
		}
	}
}