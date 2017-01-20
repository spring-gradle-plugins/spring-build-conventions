package io.spring.gradle.convention

import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.PluginManager
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.Plugin
import org.gradle.api.Project

public class SpringDocsConventionPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		PluginManager pluginManager = project.getPluginManager();
		pluginManager.apply("org.asciidoctor.convert");
		pluginManager.apply(DeployDocsPlugin);
		pluginManager.apply(JavadocApiPlugin);

		project.asciidoctorj {
			version = '1.5.4'
		}

		project.tasks.create('docsZip', Zip) {
			dependsOn 'api', 'asciidoctor'

			from(project.tasks.asciidoctor.outputs) {
				into 'reference'
			}
			from(project.tasks.api.outputs) {
				into 'api'
			}
			into 'docs'
		}

	}
}