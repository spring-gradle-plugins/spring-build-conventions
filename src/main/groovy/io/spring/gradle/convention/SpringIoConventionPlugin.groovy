package io.spring.gradle.convention

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import io.spring.gradle.propdeps.PropDepsPlugin
import io.spring.gradle.springio.SpringIoPlugin

import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.Plugin
import org.gradle.api.Project

public class SpringIoConventionPlugin implements Plugin<Project> {
	static final String DEPENDENCY_MANAGEMENT_RESOURCE = "gradle/springio-dependency-management.properties"

	@Override
	public void apply(Project project) {
		project.ext['JDK8_HOME'] = System.getProperty("java.home");
		project.getPluginManager().apply(SpringIoPlugin)

		project.plugins.withType(SpringIoPlugin) {
			project.configurations.withType(Object.class) { c->
				if(c.getName() == 'springIoTestRuntime') {
					println c.getClass()
					applySpringIoConfiguration(project)
				}
			}
		}
	}

	private void applySpringIoConfiguration(Project project) {
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

		project.plugins.withType(PropDepsPlugin) {
			project.configurations {
				springIoTestRuntime {
					extendsFrom optional, provided
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
			springIoTestRuntime {
				dependencies {
					for (Map.Entry<Object, Object> entry : dependencyMap.entrySet()) {
						String managedDependency = entry.getKey() + ":" + entry.getValue();
						dependency managedDependency
					}
				}
			}
		}
	}
}