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
		pluginManager.apply('docbook-reference')

		project.asciidoctorj {
			version = '1.5.4'
		}

		project.tasks.create('docsZip', Zip) {
			dependsOn 'api', 'reference'
			group = 'Distribution'
			baseName = project.rootProject.name
			classifier = 'docs'
			description = "Builds -${classifier} archive containing all " +
				"Docs for deployment at docs.spring.io"

			from(project.tasks.asciidoctor.outputs) {
				into 'reference'
				include 'html5/**'
			}
			from(project.tasks.reference.outputs) {
				into 'reference'
			}
			from(project.tasks.api.outputs) {
				into 'api'
			}
			into 'docs'
			duplicatesStrategy 'exclude'
		}

		String projectName = Utils.getProjectName(project);

		project.tasks.asciidoctor {
			backends = ['docbook5','html5']
			def ghTag = 'master'//snapshotBuild ? 'master' : project.version
			def ghUrl = "https://github.com/spring-projects/${projectName}/tree/$ghTag"
			options = [
			  eruby: 'erubis',
			  attributes: [
				  copycss : '',
				  icons : 'font',
				  'source-highlighter': 'prettify',
				  sectanchors : '',
				  toc2: '',
				  idprefix: '',
				  idseparator: '-',
				  doctype: 'book',
				  numbered: '',
				  '${projectName}-version' : project.version,
				  revnumber : project.version,
				  'gh-url': ghUrl,
				  'gh-samples-url': "$ghUrl/samples",
				  docinfo : ""
			  ]
			]
		}

		project.reference {
			dependsOn 'asciidoctor'
			sourceDir = new File(project.asciidoctor.outputDir , 'docbook5')
			pdfFilename = "${projectName}-reference.pdf"
			epubFilename = "${projectName}-reference.epub"
			expandPlaceholders = ""
		}
	}
}