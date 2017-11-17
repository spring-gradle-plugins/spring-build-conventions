package io.spring.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager

public class RootProjectPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		PluginManager pluginManager = project.getPluginManager()
		pluginManager.apply(SchemaPlugin)
		pluginManager.apply("org.sonarqube")


		String projectName = Utils.getProjectName(project);
		project.sonarqube {
			properties {
				property "sonar.java.coveragePlugin", "jacoco"
				property "sonar.projectName", projectName
				property "sonar.jacoco.reportPath", "${project.buildDir.name}/jacoco.exec"
				property "sonar.links.homepage", "https://spring.io/${projectName}"
				property "sonar.links.ci", "https://jenkins.spring.io/job/${projectName}/"
				property "sonar.links.issue", "https://github.com/spring-projects/${projectName}/issues"
				property "sonar.links.scm", "https://github.com/spring-projects/${projectName}"
				property "sonar.links.scm_dev", "https://github.com/spring-projects/${projectName}.git"
				property "sonar.java.coveragePlugin", "jacoco"
			}
		}

		project.tasks.create("dependencyManagementExport", DependencyManagementExportTask);

		def finalizePublishArtifacts = project.task("finalizePublishArtifacts")
		if(Utils.isRelease(project)) {
			project.getPluginManager().apply("io.codearte.nexus-staging");
			finalizePublishArtifacts.dependsOn project.tasks.closeAndReleaseRepository
		}
	}
}