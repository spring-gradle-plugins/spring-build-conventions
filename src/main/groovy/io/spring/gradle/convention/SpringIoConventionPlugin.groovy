package io.spring.gradle.convention

import io.spring.gradle.springio.SpringIoPlugin

import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.Plugin
import org.gradle.api.Project

public class SpringIoConventionPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.ext['JDK8_HOME'] = System.getProperty("java.home");
		project.getPluginManager().apply(SpringIoPlugin)

		String platformBomVersion = project.springIoVersion
		if(project.hasProperty('platformVersion')) {
			platformBomVersion = project.findProperty("platformVersion");
		}
		project.dependencyManagement {
			springIoTestRuntime {
				imports {
					mavenBom("io.spring.platform:platform-bom:${platformBomVersion}")
				}
			}
		}
		project.tasks.create("dependencyManagementExport", DependencyManagementExportTask);
	}
}