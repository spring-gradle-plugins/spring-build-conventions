package io.spring.gradle.convention;

import org.gradle.api.Plugin
import org.gradle.api.Project

public class RepositoryConventionPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		String projectVersion = String.valueOf(project.getVersion());
		boolean isSnapshot = projectVersion.endsWith("-SNAPSHOT");
		boolean isRelease = projectVersion.endsWith(".RELEASE");
		boolean isMilestone = !isSnapshot && !isRelease;

		String mavenUrl
		if(isSnapshot) {
			mavenUrl = 'https://repo.spring.io/libs-snapshot'
		} else if(isMilestone) {
			mavenUrl = 'https://repo.spring.io/libs-milestone'
		} else {
			mavenUrl = 'https://repo.spring.io/libs-release';
		}

		project.repositories {
			maven { url mavenUrl }
		}
	}
}
