package io.spring.gradle.convention;

import org.gradle.api.Project;

public class Utils {

	static String getProjectName(Project project) {
		String projectName = project.getRootProject().getName();
		if(projectName.endsWith("-build")) {
			projectName = projectName.substring(0, projectName.length() - "-build".length());
		}
		return projectName;
	}

	private Utils() {}
}
