package io.spring.gradle.convention;

import org.gradle.api.Plugin
import org.gradle.api.Project

public class RepositoryConventionPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		boolean isSnapshot = Utils.isSnapshot(project)
		boolean isMilestone = Utils.isMilestone(project);

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
