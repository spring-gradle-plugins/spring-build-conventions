package io.spring.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.MavenPlugin
import org.gradle.plugins.signing.SigningPlugin
import org.sonarqube.gradle.SonarQubePlugin

public class MavenBomPlugin implements Plugin<Project> {
	static String MAVEN_BOM_TASK_NAME = "mavenBom"

	public void apply(Project project) {
		apply plugin: 'io.spring.convention.artifactory'
		apply plugin: 'io.spring.convention.maven'
		project.plugins.apply(JavaPlugin)
		project.plugins.apply(MavenPlugin)
		project.plugins.apply(SigningPlugin)
		project.plugins.apply("io.spring.convention.ossrh")

		project.group = project.rootProject.group
		project.task(MAVEN_BOM_TASK_NAME, type: MavenBomTask, group: 'Generate', description: 'Configures the pom as a Maven Build of Materials (BOM)')
		project.install.dependsOn project.mavenBom
		project.tasks.uploadArchives.dependsOn project.mavenBom

		project.plugins.withType(SonarQubePlugin) {
			project.sonarqube.skipProject = true
		}

		project.rootProject.allprojects.each { p ->
			p.plugins.withType(io.spring.gradle.convention.SpringMavenPlugin) {
				project.mavenBom.projects.add(p)
			}
		}
	}
}
